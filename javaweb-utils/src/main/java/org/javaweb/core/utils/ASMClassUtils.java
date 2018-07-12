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

import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.springframework.asm.Opcodes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yz on 2017/1/22.
 *
 * @author yz
 */
public class ASMClassUtils {

	public byte[] createClassWriter(byte[] classfileBuffer, Class<?> targetClass) {
		ClassReader cr = new ClassReader(classfileBuffer);

		if (!ClassUtils.isInterface(cr)) {
			ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
			try {
				ClassVisitor classVisitor = (ClassVisitor) targetClass.getConstructor(Integer.class, ClassWriter.class).newInstance(Opcodes.ASM5, cw);
				cr.accept(classVisitor, ClassReader.EXPAND_FRAMES);

				return cw.toByteArray();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		return classfileBuffer;
	}

	/**
	 * ASM获取某个类中的所有的注解,如果用反射去读取注解会无法避免的初始化目标class
	 *
	 * @param classFileBuffer 类文件字节码数组
	 * @return
	 */
	public Map<String, List<AnnotationNode>> getClassAnnotation(byte[] classFileBuffer) {
		final Map<String, List<AnnotationNode>> annotationNodeMap        = new LinkedHashMap<String, List<AnnotationNode>>();
		final List<AnnotationNode>              classAnnotationNodeList  = new ArrayList<AnnotationNode>();
		final List<AnnotationNode>              methodAnnotationNodeList = new ArrayList<AnnotationNode>();
		final List<AnnotationNode>              fieldAnnotationNodeList  = new ArrayList<AnnotationNode>();
		final ClassReader                       cr                       = new ClassReader(classFileBuffer);

		if (!ClassUtils.isInterface(cr)) {

			ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);

			cr.accept(new ClassVisitor(Opcodes.ASM5, cw) {
				@Override
				public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
					AnnotationNode an = new AnnotationNode(desc);
					classAnnotationNodeList.add(an);

					return an;
				}

				@Override
				public MethodVisitor visitMethod(int access, String methodName, String argTypeDesc, String signature, String[] exceptions) {
					final MethodVisitor mv = super.visitMethod(access, methodName, argTypeDesc, signature, exceptions);
					return new MethodVisitor(Opcodes.ASM5, mv) {

						@Override
						public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
							AnnotationNode an = new AnnotationNode(desc);
							methodAnnotationNodeList.add(an);

							return an;
						}
					};
				}

				@Override
				public FieldVisitor visitField(int access, String methodName, String argTypeDesc, String signature, final Object value) {
					final FieldVisitor fv = super.visitField(access, methodName, argTypeDesc, signature, value);
					return new FieldVisitor(Opcodes.ASM5, fv) {
						@Override
						public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
							AnnotationNode an = new AnnotationNode(desc);
							fieldAnnotationNodeList.add(an);

							return an;
						}
					};
				}
			}, ClassReader.EXPAND_FRAMES);
		}

		annotationNodeMap.put("class", classAnnotationNodeList);
		annotationNodeMap.put("method", methodAnnotationNodeList);
		annotationNodeMap.put("field", fieldAnnotationNodeList);

		return annotationNodeMap;
	}

}