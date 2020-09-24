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
package org.javaweb.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

import static org.objectweb.asm.Opcodes.*;

public class ClassUtils extends org.apache.commons.lang.ClassUtils {

	private static final Logger LOG = Logger.getLogger("info");

	/**
	 * Java 9大原始类型包括Void
	 */
	private static final int[] PRIMITIVE_TYPE = new int[]{
			Type.INT, Type.BOOLEAN, Type.BYTE, Type.CHAR,
			Type.SHORT, Type.DOUBLE, Type.FLOAT, Type.LONG, Type.VOID
	};

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
	 * 获取用于ASM调用的类名称
	 *
	 * @param clazz
	 * @return
	 */
	public static String toAsmClassName(Class clazz) {
		return clazz.getName().replace(".", "/");
	}

	/**
	 * 获取用于ASM调用的类名称
	 *
	 * @param className
	 * @return
	 */
	public static String toAsmClassName(String className) {
		return className.replace(".", "/");
	}

	/**
	 * 转换成Java内部命名方式
	 *
	 * @param className
	 * @return
	 */
	public static String toJavaName(String className) {
		return className.replace("/", ".");
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

	/**
	 * 判断JDK版本是否大于1.4,确定是否需要计算Frames
	 *
	 * @param cr
	 * @return
	 */
	public static boolean shouldComputeFrames(ClassReader cr) {
		return getClassVersion(cr) >= 50;
	}

	/**
	 * 获取默认的上下文ClassLoader
	 * 依次查找当前线程上下文ClassLoader->当前类上下文ClassLoader->系统上下文ClassLoader
	 *
	 * @return
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader loader = null;

		try {
			loader = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// 忽略
		}

		if (loader == null) {
			loader = ClassUtils.class.getClassLoader();
		}

		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}

		return loader;
	}

	/**
	 * 获取参数描述符,接收参数类型的全类名，如果是参数是数组类型的那么直接在类型后面加上一对"[]"就可以了,
	 * 如"java.lang.String[]",如果是基础类型直接写就行了，如参数类型是int,那么直接传入:"int"就行了。
	 * 需要特别注意的是类名一定不能写错,"[]"也一定不能加错，否则无法正常匹配。
	 *
	 * @param classes 参数类型
	 * @return
	 */
	public static String getDescriptor(final String... classes) {
		StringBuilder sb = new StringBuilder();

		for (String name : classes) {
			int length = name.split("\\[\\]", -1).length;// 统计数组[]出现次数

			for (int i = 0; i < length - 1; i++) {
				sb.append("[");
			}

			String className = ClassUtils.toAsmClassName(name.replace("[]", ""));// 移除所有[]

			if (Byte.TYPE.getName().equals(className)) {
				sb.append('B');
			} else if (Boolean.TYPE.getName().equals(className)) {
				sb.append('Z');
			} else if (Short.TYPE.getName().equals(className)) {
				sb.append('S');
			} else if (Character.TYPE.getName().equals(className)) {
				sb.append('C');
			} else if (Integer.TYPE.getName().equals(className)) {
				sb.append('I');
			} else if (Long.TYPE.getName().equals(className)) {
				sb.append('J');
			} else if (Double.TYPE.getName().equals(className)) {
				sb.append('D');
			} else if (Float.TYPE.getName().equals(className)) {
				sb.append('F');
			} else if (Void.TYPE.getName().equals(className)) {
				sb.append('V');
			} else {
				sb.append("L").append(className).append(";");
			}
		}

		return sb.toString();
	}

	/**
	 * 获取返回类型OpCode
	 *
	 * @param returnType
	 * @return
	 */
	public static int getReturnOpCode(Type returnType) {
		int sort = returnType.getSort();

		if (isPrimitive(returnType)) {
			if (Type.LONG == sort) {
				return LRETURN;
			} else if (Type.DOUBLE == sort) {
				return DRETURN;
			} else if (Type.FLOAT == sort) {
				return FRETURN;
			} else {
				return IRETURN;
			}
		}

		return Type.VOID == sort ? RETURN : ARETURN;
	}


	/**
	 * 获取ASM load变量opcode
	 *
	 * @param type ASM方法类型
	 * @return opcode
	 */
	public static int getLoadOpCode(Type type) {
		int sort = type.getSort();

		if (ClassUtils.isPrimitive(type)) {
			if (Type.LONG == sort) {
				return LLOAD;
			} else if (Type.DOUBLE == sort) {
				return DLOAD;
			} else if (Type.FLOAT == sort) {
				return FLOAD;
			} else {
				return ILOAD;
			}
		}

		return ALOAD;
	}

	/**
	 * 获取类类型描述符
	 *
	 * @param classes
	 * @return
	 */
	public static String getDescriptor(final Class<?>... classes) {
		StringBuilder buf = new StringBuilder();

		for (Class<?> clazz : classes) {
			getDescriptor(buf, clazz);
		}

		return buf.toString();
	}

	/**
	 * 获取类类型描述符
	 *
	 * @param sb
	 * @param clazz
	 */
	public static void getDescriptor(final StringBuilder sb, final Class<?> clazz) {
		Class<?> typeClass = clazz;

		while (true) {
			if (typeClass.isPrimitive()) {
				char car;

				if (typeClass == Integer.TYPE) {
					car = 'I';
				} else if (typeClass == Void.TYPE) {
					car = 'V';
				} else if (typeClass == Boolean.TYPE) {
					car = 'Z';
				} else if (typeClass == Byte.TYPE) {
					car = 'B';
				} else if (typeClass == Character.TYPE) {
					car = 'C';
				} else if (typeClass == Short.TYPE) {
					car = 'S';
				} else if (typeClass == Double.TYPE) {
					car = 'D';
				} else if (typeClass == Float.TYPE) {
					car = 'F';
				} else {
					car = 'J';
				}

				sb.append(car);
				return;
			} else if (typeClass.isArray()) {
				sb.append('[');
				typeClass = typeClass.getComponentType();
			} else {
				sb.append('L').append(toAsmClassName(typeClass.getName())).append(";");
				return;
			}
		}
	}

	/**
	 * 获取一个类的所有父类和实现的接口
	 *
	 * @param className
	 * @param classLoader
	 * @return
	 */
	public static Set<String> getSuperClassListByAsm(String className, ClassLoader classLoader) {
		Set<String> superClassList  = new LinkedHashSet<String>();
		String      objectClassName = Object.class.getName();

		try {
			getSuperClassListByAsm(className, classLoader, superClassList);

			// 把Object的位置放到最后,方便父类检测
			for (Iterator<String> it = superClassList.iterator(); it.hasNext(); ) {
				String name = it.next();

				if (objectClassName.equals(name)) {
					it.remove();
				}
			}

			superClassList.add(objectClassName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return superClassList;
	}

	/**
	 * 获取一个类的所有父类和实现的接口
	 *
	 * @param clazz
	 * @return
	 */
	public static Set<Class> getSuperClassList(Class clazz) {
		Set<Class> superClassList = new LinkedHashSet<Class>();

		try {
			Class<Object> objectClass = Object.class;

			getSuperClassList(clazz, superClassList);

			// 把Object的位置放到最后,方便父类检测
			for (Iterator<Class> it = superClassList.iterator(); it.hasNext(); ) {
				Class name = it.next();

				if (objectClass.equals(name)) {
					it.remove();
				}
			}

			superClassList.add(objectClass);
		} catch (Exception e) {
			LOG.info("获取" + clazz.getName() + "类信息异常:" + e);
		}

		return superClassList;
	}

	/**
	 * 获取一个类的所有父类和实现的接口
	 *
	 * @param className
	 * @param loader
	 * @param superClassList
	 */
	public static void getSuperClassListByAsm(String className, ClassLoader loader, Set<String> superClassList) {
		if (className != null && loader != null) {
			try {
				superClassList.add(className);
				byte[] classBytes = getClassBytes(className, loader);

				// TODO: 需要找出为什么无法获取class文件，IDEA Debug Agent类无法获取
				// 忽略无法找到类字节码的class
				if (classBytes != null) {
					ClassReader classReader = new ClassReader(classBytes);

					String   superClass = classReader.getSuperName();// 父类
					String[] interfaces = classReader.getInterfaces();// 父接口

					List<String> ls = new ArrayList<String>();

					// 添加父类
					if (superClass != null) {
						ls.add(superClass);
					}

					// 添加父类的所有接口
					for (String clazz : interfaces) {
						ls.add(clazz);
					}

					// 遍历所有父类和接口
					for (String clazz : ls) {
						getSuperClassListByAsm(toJavaName(clazz), loader, superClassList);
					}
				}
			} catch (Exception e) {
				LOG.info("获取" + className + "类的父类异常:" + e);
			}
		}
	}

	/**
	 * 获取一个类的所有父类和实现的接口
	 *
	 * @param targetClass
	 * @param superClassList
	 */
	public static void getSuperClassList(Class targetClass, Set<Class> superClassList) {
		if (targetClass != null) {
			superClassList.add(targetClass);

			if (Object.class.equals(targetClass)) {
				return;
			}

			// 父类
			Class superClass = targetClass.getSuperclass();

			// 父接口
			Class[] interfaces = targetClass.getInterfaces();

			List<Class> ls = new ArrayList<Class>();

			// 添加父类
			if (superClass != null) {
				ls.add(superClass);
			}

			// 添加父类的所有接口
			for (Class clazz : interfaces) {
				ls.add(clazz);
			}

			// 遍历所有父类和接口
			for (Class clazz : ls) {
				getSuperClassList(clazz, superClassList);
			}
		}
	}

	/**
	 * 查找类对象，获取类字节码
	 *
	 * @param className
	 * @param classLoader
	 * @return
	 */
	public static byte[] getClassBytes(String className, ClassLoader classLoader) {
		InputStream in = null;

		try {
			String classRes = toAsmClassName(className) + ".class";

			in = ClassLoader.getSystemResourceAsStream(classRes);

			if (in == null) {
				in = classLoader.getResourceAsStream(classRes);
			}

			if (in != null) {
				return IOUtils.toByteArray(in);
			}

			return null;
		} catch (IOException e) {
			return null;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * 检查是否是基础类型
	 *
	 * @param type
	 * @return
	 */
	public static boolean isPrimitive(Type type) {
		for (int primitiveType : PRIMITIVE_TYPE) {
			if (primitiveType == type.getSort()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检测类注解是否匹配,只要匹配上任意一个注解就返回true
	 *
	 * @param clazz
	 * @param classAnnotations
	 * @return
	 */
	public static boolean classAnnotationsMatcher(Class clazz, String[] classAnnotations) {
		Annotation[] annotations = clazz.getDeclaredAnnotations();

		for (Annotation annotation : annotations) {
			for (String className : classAnnotations) {
				if (annotation.equals(className)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 检测方法的注解是否一致,只要检测任意一个注解匹配都返回true
	 *
	 * @param clazz
	 * @param methodAnnotations
	 * @return
	 */
	public static boolean methodAnnotationsMatcher(Class clazz, String[] methodAnnotations) {
		Method[] methods = clazz.getDeclaredMethods();

		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();

			for (Annotation annotation : annotations) {
				for (String className : methodAnnotations) {
					if (annotation.equals(className)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 获取类的构造器
	 *
	 * @param obj
	 * @param classes
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Constructor getConstructor(Object obj, Class... classes) throws NoSuchMethodException {
		Constructor constructor = null;
		Class<?>    clazz       = obj.getClass();

		try {
			constructor = clazz.getDeclaredConstructor(classes);
		} catch (NoSuchMethodException e) {
			constructor = clazz.getConstructor(classes);
		}

		constructor.setAccessible(true);

		return constructor;
	}

	/**
	 * 反射获取类方法(包括本类所有方法和父类非private方法)
	 *
	 * @param obj
	 * @param methodName
	 * @param classes
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getMethod(Object obj, String methodName, Class... classes) throws NoSuchMethodException {
		Method   method = null;
		Class<?> clazz  = obj.getClass();

		try {
			method = clazz.getDeclaredMethod(methodName, classes);
		} catch (NoSuchMethodException e) {
			method = clazz.getMethod(methodName, classes);
		}

		method.setAccessible(true);

		return method;
	}

	/**
	 * 获取目标类方法
	 *
	 * @param obj        目标类实例
	 * @param methodName 方法名称
	 * @param methodDesc 方法描述法
	 * @return 反射获取到的Method对象
	 */
	public static Method getMethod(Object obj, String methodName, String methodDesc) {
		Class<?> clazz   = obj.getClass();
		Method[] methods = clazz.getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				String descriptor = org.objectweb.asm.commons.Method.getMethod(method).getDescriptor();

				if (descriptor.equals(methodDesc)) {
					method.setAccessible(true);
					return method;
				}
			}
		}

		return null;
	}

	/**
	 * 反射获取类成员变量
	 *
	 * @param obj
	 * @param fieldName
	 * @param <T>
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static <T> T getFieldValue(Object obj, String fieldName)
			throws NoSuchFieldException, IllegalAccessException {

		Field field = null;
		Class clazz = obj.getClass();

		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			field = clazz.getField(fieldName);
		}

		field.setAccessible(true);

		return (T) field.get(obj);
	}

}