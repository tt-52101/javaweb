/*
 * Copyright yz 2016-01-14  Email:admin@javaweb.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javaweb.core.utils;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils extends org.apache.commons.lang.ClassUtils {

	/**
	 * 查找指定的文件夹下的获取所有的类文件
	 *
	 * @param f
	 * @param path
	 * @param ls
	 */
	public static void findAllClassFile(File f, String path, Set<String> ls) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				findAllClassFile(file, path, ls);
			}
		} else {
			String fileString = f.toString();

			// 查找所有的class文件
			if (fileString.endsWith(".class")) {
				String classPath = fileString.substring(path.length());
				classPath = classPath.replaceAll("\\\\+", "/").replaceAll("/+", "/").replaceAll("^/", "");
				ls.add(classPath.substring(0, classPath.length() - ".class".length()));
			}
		}
	}

	public static void getAllJarClass(File file, Set<String> classList) throws IOException {
		if (file.isDirectory()) {
			getAllJarClass(file, classList);
		} else {
			if (file.getName().endsWith(".jar")) {
				getAllJarClass(file.toURI().toURL(), classList);
			}
		}
	}

	/**
	 * 获取jar包所有class
	 *
	 * @param url
	 * @param classList
	 * @throws IOException
	 */
	public static void getAllJarClass(URL url, Set<String> classList) throws IOException {
		if (url != null) {
			JarURLConnection      juc = (JarURLConnection) url.openConnection();
			JarFile               jf  = juc.getJarFile();
			Enumeration<JarEntry> je  = jf.entries();

			while (je.hasMoreElements()) {
				JarEntry jar = je.nextElement();

				if (jar.getName().endsWith(".class")) {
					String classPath = jar.getName().replaceAll("\\\\", "/").replaceAll("/+", ".");
					classList.add(classPath.substring(0, classPath.length() - ".class".length()));
				}
			}
		}
	}

	/**
	 * 获取当前jar包或者class目录下所有的类文件
	 *
	 * @return classList 所有的类的完整包名加类名
	 * @throws IOException
	 */
	public static Set<String> getAllClass() throws IOException {
		Set<String> classList  = new LinkedHashSet<String>();
		ClassUtils  classUtils = new ClassUtils();
		URL         url        = classUtils.getClass().getProtectionDomain().getCodeSource().getLocation();

		try {
			if (!"http".equalsIgnoreCase(url.toURI().getScheme()) && new File(url.toURI()).isDirectory()) {
				File f = new File(url.toURI());
				findAllClassFile(f, f.toString(), classList);
			} else {
				getAllJarClass(url, classList);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw e;
		}

		return classList;
	}

	/**
	 * 类对象是否是接口
	 *
	 * @param cr
	 * @return
	 */
	public static boolean isInterface(ClassReader cr) {
		return (cr.getAccess() & Opcodes.ACC_INTERFACE) != 0;
	}

	/**
	 * 获取java类编译版本
	 *
	 * @param cr
	 * @return
	 */
	public static int getClassVersion(ClassReader cr) {
		return cr.readUnsignedShort(6);
	}

	private static boolean shouldComputeFrames(ClassReader cr) {
		return getClassVersion(cr) >= 50;
	}

	public static ClassWriter getClassWriter(ClassReader cr, ClassLoader classLoader) {
		int writerFlags = 1;

		if (shouldComputeFrames(cr)) {
			writerFlags = 2;
		}

		return new ClassWriter(cr, writerFlags);
	}

	/**
	 * 序列化java类成Map对象
	 *
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> serializeClassToMap(Object obj) {
		Map<String, Object> params = new HashMap<String, Object>(0);

		try {
			PropertyUtilsBean    propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors       = propertyUtilsBean.getPropertyDescriptors(obj);

			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();

				if (!"class".equals(name)) {
					params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return params;
	}

}