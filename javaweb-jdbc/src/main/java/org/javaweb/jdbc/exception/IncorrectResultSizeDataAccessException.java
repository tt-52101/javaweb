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
package org.javaweb.jdbc.exception;

import java.sql.SQLException;

/**
 * SQL查询异常，查询结果数量与期待的查询结果数不一致
 *
 * @author yz
 */
public class IncorrectResultSizeDataAccessException extends SQLException {

	private static final long serialVersionUID = -4579851381128020945L;

	public IncorrectResultSizeDataAccessException(long size) {
		super("Data access exception thrown when a result was not of the expected size,for example when expecting a single row but getting 0 or more than 1 rows.it's " + size + " rows.");
	}

}