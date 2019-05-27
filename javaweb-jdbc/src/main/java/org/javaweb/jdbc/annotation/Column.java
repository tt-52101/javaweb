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
package org.javaweb.jdbc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 数据库列映射
 *
 * @author yz
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Column {

	/**
	 * 数据库表主键ID
	 *
	 * @return
	 */
	boolean id() default false;

	/**
	 * 数据库表字段名
	 *
	 * @return
	 */
	String name() default "";

	/**
	 * 数据库表名
	 *
	 * @return
	 */
	String table() default "";

	/**
	 * 数据库表字段是否允许更新
	 *
	 * @return
	 */
	boolean updatable() default true;

}