/*
 * Copyright yz 2016-01-16  Email:admin@javaweb.org.
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import freemarker.core.Environment;
import freemarker.template.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * freemarker 自定义标签工具类 修改了jeecms的DirectiveUtils
 *
 * @author yz
 */
public class DirectiveUtils {

	/**
	 * 从自定义标签中获取String类型参数值
	 *
	 * @param name
	 * @param params
	 * @return
	 * @throws TemplateException
	 */
	public static String getString(String name, Map<String, TemplateModel> params) throws TemplateException {
		TemplateModel model = params.get(name);

		if (model != null) {
			if (model instanceof TemplateScalarModel) {
				return ((TemplateScalarModel) model).getAsString();
			} else if ((model instanceof TemplateNumberModel)) {
				return ((TemplateNumberModel) model).getAsNumber().toString();
			} else {
				throw new TemplateModelException(name);
			}
		}

		return null;
	}

	/**
	 * 从自定义标签中获取boolean类型参数值 如果值为1也自动转换为true
	 *
	 * @param name
	 * @param params
	 * @return
	 * @throws TemplateException
	 */
	public static Boolean getBoolean(String name, Map<String, TemplateModel> params) throws TemplateException {
		String str = getString(name, params);

		if (StringUtils.isNotEmpty(str)) {
			try {
				return "1".equals(str.trim()) || "true".equals(str.trim());
			} catch (Exception e) {
				throw new TemplateModelException(name);
			}
		} else {
			return null;
		}
	}

	public static Integer getInt(String name, Map params) throws TemplateException {
		Number number = getNumber(name, params);

		if (number != null) {
			return number.intValue();
		}

		return null;
	}

	public static Long getLong(String name, Map params) throws TemplateException {
		Number number = getNumber(name, params);

		if (number != null) {
			return number.longValue();
		}

		return null;
	}

	public static Number getNumber(String name, Map params) throws TemplateException {
		String str = getString(name, params);

		try {
			if (StringUtils.isNotEmpty(str) && StringUtils.isNum(str)) {
				return NumberFormat.getInstance().parse(str);
			}
		} catch (Exception e) {
			// 忽略类型转换异常
		}

		return null;
	}

	/**
	 * 将params的值复制到variable中
	 *
	 * @param env
	 * @param params
	 * @return 原Variable中的值
	 * @throws TemplateException
	 */
	public static Map<String, TemplateModel> addParamsToVariable(
			Environment env, Map<String, TemplateModel> params) throws TemplateException {

		Map<String, TemplateModel> origMap = new HashMap<String, TemplateModel>();

		if (!params.isEmpty()) {
			Set<Map.Entry<String, TemplateModel>> entrySet = params.entrySet();

			for (Map.Entry<String, TemplateModel> entry : entrySet) {
				String        key   = entry.getKey();
				TemplateModel value = env.getVariable(key);

				if (value != null) {
					origMap.put(key, value);
				}

				env.setVariable(key, entry.getValue());
			}
		}

		return origMap;
	}

	/**
	 * 将variable中的params值移除
	 *
	 * @param env
	 * @param params
	 * @param origMap
	 */
	public static void removeParamsFromVariable(
			Environment env, Map<String, TemplateModel> params,
			Map<String, TemplateModel> origMap) {

		if (!params.isEmpty()) {
			for (String key : params.keySet()) {
				env.setVariable(key, origMap.get(key));
			}
		}
	}

	/**
	 * DefaultObjectWrapper 对象已经过时了
	 * http://freemarker.incubator.apache.org/docs/versions_2_3_21.html
	 * http://freemarker.incubator.apache.org/docs/pgui_datamodel_objectWrapper.html
	 *
	 * @return
	 */
	public static DefaultObjectWrapper getDefaultObjectWrapper() {
		return new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23).build();
	}

	/**
	 * Freemarker模板参数转Map对象
	 *
	 * @param params
	 * @param data
	 * @throws TemplateException
	 */
	public static void templateParameterToMap(Map<String, TemplateModel> params,
	                                          Map<String, String> data) throws TemplateException {

		Set<Map.Entry<String, TemplateModel>> entrySet = params.entrySet();

		for (Map.Entry<String, TemplateModel> entry : entrySet) {
			String key   = entry.getKey();
			String value = getString(key, params);

			data.put(key, value);
		}
	}

	/**
	 * Http请求参数、Freemarker模板参数转Map对象
	 *
	 * @param request
	 * @param params
	 * @return
	 * @throws TemplateException
	 */
	public static Map<String, String> getRequestParameter(
			HttpServletRequest request, Map<String, TemplateModel> params) throws TemplateException {

		Map<String, String> data = new HashMap<String, String>();

		if (request != null) {
			Enumeration<String> enumeration = request.getParameterNames();

			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();
				data.put(key, request.getParameter(key));
			}
		}

		templateParameterToMap(params, data);

		return data;
	}

	/**
	 * Http请求参数、Freemarker模板参数转实体对象
	 *
	 * @param params
	 * @param entityClass
	 * @param <T>
	 * @return
	 * @throws TemplateException
	 */
	public static <T> T parameterMap2Bean(Map<String, TemplateModel> params,
	                                      Class<T> entityClass) throws TemplateException {

		return parameterMap2Bean(null, params, entityClass);
	}

	/**
	 * Http请求参数、Freemarker模板参数转实体对象
	 *
	 * @param params
	 * @param entityClass
	 * @param <T>
	 * @return
	 * @throws TemplateException
	 */
	public static <T> T parameterMap2Bean(HttpServletRequest request, Map<String, TemplateModel> params,
	                                      Class<T> entityClass) throws TemplateException {

		Map<String, String> data = getRequestParameter(request, params);
		return JSONObject.parseObject(JSON.toJSONString(data), entityClass);
	}

	/**
	 * params 参数反射映射到bean 只支持基础类型及其包装类型、String、StringBuffer、
	 * StringBuilder、Date类型 其中Date类型只允许传入long类型的日期
	 *
	 * @param <T>
	 * @param params
	 * @return
	 * @throws TemplateException
	 */
	public static <T> T paramsMap2Bean(Map params, Class<T> entityClass) throws TemplateException {
		Set<Map.Entry<String, TemplateModel>> entrySet  = params.entrySet();
		Map<String, Method>                   methodMap = ReflectionUtils.getAllMethodsMap(entityClass);
		Map<String, Field>                    fieldMap  = ReflectionUtils.getAllFieldsMap(entityClass);
		T                                     instance  = null;

		try {
			instance = entityClass.newInstance();

			for (Map.Entry<String, TemplateModel> entry : entrySet) {
				String key           = entry.getKey();
				String field         = key.toLowerCase().replaceAll("_", "");
				String setColumnName = "set" + field;

				// 检测类方法和类成员变量是否包含了需要处理的field
				if (methodMap.containsKey(setColumnName) && fieldMap.containsKey(field)) {
					Method method = methodMap.get(setColumnName);

					// 只设值一个参数的method
					if (method.getParameterTypes().length == 1) {
						try {
							String valueStr  = getString(key, params);
							Object value     = null;
							String className = method.getParameterTypes()[0].getName();

							// 不处理空值
							if (StringUtils.isNotEmpty(valueStr)) {
								if (StringUtils.isNum(valueStr)) {
									try {
										Number number = NumberFormat.getInstance().parse(valueStr);

										if (className.equals("java.lang.Integer") || className.equals("int")) {
											method.invoke(instance, number.intValue());
										} else if (className.equals("java.lang.Long") || className.equals("long")) {
											method.invoke(instance, number.longValue());
										} else if (className.equals("java.lang.Double") || className.equals("double")) {
											method.invoke(instance, number.doubleValue());
										} else if (className.equals("java.lang.Float") || className.equals("float")) {
											method.invoke(instance, number.floatValue());
										} else if (className.equals("java.lang.Byte") || className.equals("byte")) {
											method.invoke(instance, number.byteValue());
										} else if (className.equals("java.lang.Short") || className.equals("short")) {
											method.invoke(instance, number.shortValue());
										} else if (className.equals("java.lang.Boolean") || className.equals("boolean")) {
											method.invoke(instance, 1 == number.intValue());
										} else if (className.equals("java.util.Date")) {
											method.invoke(instance, new Date(number.longValue()));
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
								} else {
									if (className.equals("java.lang.Boolean") || className.equals("boolean")) {
										method.invoke(instance, "true".equalsIgnoreCase(valueStr));
									} else if (className.equals("java.lang.Char") || className.equals("char")) {
										method.invoke(instance, valueStr.charAt(0));
									} else if (className.equals("java.lang.String")) {
										method.invoke(instance, valueStr);
									} else if (className.equals("java.lang.StringBuffer")) {
										method.invoke(instance, new StringBuffer(valueStr));
									} else if (className.equals("java.lang.StringBuilder")) {
										method.invoke(instance, new StringBuilder(valueStr));
									}
								}
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return instance;
	}

}