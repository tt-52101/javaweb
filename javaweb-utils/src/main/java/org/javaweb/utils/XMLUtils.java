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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * java 对象和XML工具类
 *
 * @author yz
 */
public class XMLUtils {

	/**
	 * java bean 序列化成xml
	 *
	 * @param bean
	 * @return
	 */
	public static String bean2XML(Object bean) {
		StringWriter sw = new StringWriter();

		try {
			JAXBContext jaxbContext    = JAXBContext.newInstance(bean.getClass());
			Marshaller  jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 格式化输出
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 忽略XML头声明
			jaxbMarshaller.marshal(bean, sw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return sw.toString();
	}

}
