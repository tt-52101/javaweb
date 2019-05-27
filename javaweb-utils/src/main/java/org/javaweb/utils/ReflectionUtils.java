/*
 * Copyright yz 2016-01-17  Email:admin@javaweb.org.
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java反射工具类
 *
 * @author yz 2016-01-17
 */
public class ReflectionUtils {

	/**
	 * 反射获取某类的所有成员变量名和成员变量(Field[])
	 *
	 * @param <T>
	 * @param entityClass
	 * @return
	 */
	public static <T> Map<String, Field> getFieldsMap(Class<T> entityClass) {
		Map<String, Field> fieldMap = new ConcurrentHashMap<String, Field>();
		Field[]            fields   = entityClass.getDeclaredFields();

		for (Field field : fields) {
			fieldMap.put(field.getName().toLowerCase(), field);
		}

		return fieldMap;
	}

	/**
	 * 反射获取某类及其父类的所有成员变量名和成员变量(Field[])
	 *
	 * @param <T>
	 * @param entityClass
	 * @return
	 */
	public static <T> Map<String, Field> getAllFieldsMap(Class<T> entityClass) {
		Map<String, Field> map = new ConcurrentHashMap<String, Field>();
		Class<?>           c   = entityClass;
		map.putAll(getFieldsMap(entityClass));

		while (c.getSuperclass() != null) {
			c = c.getSuperclass();
			map.putAll(getFieldsMap(c));
		}

		return map;
	}

	/**
	 * 反射获取某类的所有方法名和方法Method[]
	 *
	 * @param <T>
	 * @param entityClass
	 * @return
	 */
	public static <T> Map<String, Method> getMethodsMap(Class<T> entityClass) {
		Method[]            methods = entityClass.getMethods();
		Map<String, Method> map     = new ConcurrentHashMap<String, Method>();

		for (Method method : methods) {
			map.put(method.getName().toLowerCase(), method);
		}

		return map;
	}

	/**
	 * 反射获取某类及其父类的所有方法名和方法Method[]
	 *
	 * @param <T>
	 * @param entityClass
	 * @return
	 */
	public static <T> Map<String, Method> getAllMethodsMap(Class<T> entityClass) {
		Method[]            methods = entityClass.getDeclaredMethods();
		Map<String, Method> map     = new ConcurrentHashMap<String, Method>();

		for (Method method : methods) {
			map.put(method.getName().toLowerCase(), method);
		}

		return map;
	}

}