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

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Java Bean对象验证工具类
 *
 * @author yz 2016-01-16
 */
public class BeanValidatorUtils {

	public static <T> ValidationResult beanValidatorSingle(T beanClass, String... propertyName) {
		return beanValidator(beanClass, true, propertyName);
	}

	/**
	 * Java Bean对象成员变量合法性验证
	 *
	 * @param <T>
	 * @param singlePropertyResult 验证单个属性错误立即返回error
	 * @param beanClass
	 * @param propertyName
	 * @return
	 */
	public static <T> ValidationResult beanValidator(T beanClass, boolean singlePropertyResult, String... propertyName) {
		ValidationResult    result           = new ValidationResult();
		ValidatorFactory    validatorFactory = Validation.buildDefaultValidatorFactory();
		Validator           validator        = validatorFactory.getValidator();
		Map<String, String> errorMsg         = new HashMap<String, String>();

		loop:
		if (propertyName.length > 0) {
			for (String str : propertyName) {
				Set<ConstraintViolation<T>> constraintViolations = validator.validateProperty(beanClass, str);

				for (ConstraintViolation<T> constraintViolation : constraintViolations) {
					errorMsg.put(str, constraintViolation.getMessage());

					if (singlePropertyResult) {
						break loop;
					}
				}
			}
		} else {
			Set<ConstraintViolation<T>> constraintViolations = validator.validate(beanClass);

			for (ConstraintViolation<T> constraintViolation : constraintViolations) {
				errorMsg.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());

				if (singlePropertyResult) {
					break loop;
				}
			}
		}

		result.setErrorMsg(errorMsg);
		result.setHasErrors(!errorMsg.isEmpty());

		return result;
	}

	/**
	 * Java Bean验证结果
	 */
	public static class ValidationResult {

		// 验证结果中是否包含了错误
		private boolean hasErrors;

		// 验证结果中的错误信息
		private Map<String, String> errorMsg;

		public boolean hasErrors() {
			return hasErrors;
		}

		public void setHasErrors(boolean hasErrors) {
			this.hasErrors = hasErrors;
		}

		public Map<String, String> getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(Map<String, String> errorMsg) {
			this.errorMsg = errorMsg;
		}

		@Override
		public String toString() {
			if (hasErrors) {
				StringBuilder sb = new StringBuilder();

				for (String key : errorMsg.keySet()) {
					sb.append(errorMsg.get(key));

					if (errorMsg.size() > 1) {
						sb.append("\r\n");
					}
				}

				return sb.toString();
			} else {
				return "验证通过,格式正确.";
			}
		}
	}

}
