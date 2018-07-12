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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class ManagementUtils {

	private static final RuntimeMXBean RUNTIME_MX_BEAN = ManagementFactory.getRuntimeMXBean();

	private static int PID = -1;

	/**
	 * 获取当前应用运行时的进程PID
	 *
	 * @return
	 */
	public static int getRuntimeProcessID() {
		if (PID < 1) {
			try {
				String pidString = RUNTIME_MX_BEAN.getName().split("@")[0];
				PID = Integer.parseInt(pidString);
			} catch (Throwable e) {
				PID = -1;
			}
		}

		return PID;
	}

	/**
	 * 获取JVM名称 (如:Java HotSpot(TM) 64-Bit Server VM)
	 *
	 * @return
	 */
	public static String getJVMName() {
		return RUNTIME_MX_BEAN.getVmName();
	}

	/**
	 * 获取JVM版本号 (如:24.80-b11)
	 *
	 * @return
	 */
	public static String getJVMVersion() {
		return RUNTIME_MX_BEAN.getVmVersion();
	}

	/**
	 * 获取JVM提供商 (如:Oracle Corporation)
	 *
	 * @return
	 */
	public static String getJVMVendor() {
		return RUNTIME_MX_BEAN.getVmVendor();
	}

}
