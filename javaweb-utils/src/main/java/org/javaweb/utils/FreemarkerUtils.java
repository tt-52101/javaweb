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

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerUtils {

	/**
	 * 序列化java类成Freemarker的TemplateModelMap对象
	 *
	 * @param clazz
	 * @return
	 */
	public static Map<String, TemplateModel> classToTemplateModelMap(Object clazz, Map<String, TemplateModel> paramsMap) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, TemplateModelException {
		PropertyUtilsBean    propertyUtilsBean = new PropertyUtilsBean();
		PropertyDescriptor[] descriptors       = propertyUtilsBean.getPropertyDescriptors(clazz);

		for (int i = 0; i < descriptors.length; i++) {
			String name = descriptors[i].getName();

			if (!"class".equals(name)) {
				Object        obj   = propertyUtilsBean.getNestedProperty(clazz, name);
				TemplateModel model = DirectiveUtils.getDefaultObjectWrapper().wrap(obj);
				paramsMap.put(name, model);
			}
		}

		return paramsMap;
	}

	/**
	 * Freemarker渲染单个Bean对象
	 *
	 * @param clazz
	 * @param env
	 * @param body
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws TemplateException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public static void singleBeanRender(Object clazz, Environment env, TemplateDirectiveBody body)
			throws InvocationTargetException, NoSuchMethodException, TemplateException,
			IllegalAccessException, IOException {

		Map<String, TemplateModel> paramsMap = new HashMap<String, TemplateModel>();

		if (clazz != null) {
			classToTemplateModelMap(clazz, paramsMap);
		} else {
			paramsMap.put("__EMPTY_RESULT__", DirectiveUtils.getDefaultObjectWrapper().wrap(true));
		}

		Map<String, TemplateModel> origMap = DirectiveUtils.addParamsToVariable(env, paramsMap);
		body.render(env.getOut());
		DirectiveUtils.removeParamsFromVariable(env, paramsMap, origMap);
	}

}