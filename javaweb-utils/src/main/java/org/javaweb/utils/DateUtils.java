/*
 * Copyright sky 2017-12-05 Email:sky@03sec.com.
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

	/**
	 * 获取当天的开始时间
	 *
	 * @return
	 */
	public static Date getDayBegin() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/**
	 * 获取当天的结束时间
	 *
	 * @return
	 */
	public static Date getDayEnd() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		return cal.getTime();
	}

	/**
	 * 获取昨天的开始时间
	 *
	 * @return
	 */
	public static Date getBeginDayOfYesterday() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayBegin());
		cal.add(Calendar.DAY_OF_MONTH, -1);

		return cal.getTime();
	}

	/**
	 * 获取昨天的结束时间
	 *
	 * @return
	 */
	public static Date getEndDayOfYesterDay() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayEnd());
		cal.add(Calendar.DAY_OF_MONTH, -1);

		return cal.getTime();
	}

	/**
	 * 获取明天的开始时间
	 *
	 * @return
	 */
	public static Date getBeginDayOfTomorrow() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayBegin());
		cal.add(Calendar.DAY_OF_MONTH, 1);

		return cal.getTime();
	}

	/**
	 * 获取明天的结束时间
	 *
	 * @return
	 */
	public static Date getEndDayOfTomorrow() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayEnd());
		cal.add(Calendar.DAY_OF_MONTH, 1);

		return cal.getTime();
	}

	/**
	 * 获取本周的开始时间
	 *
	 * @return
	 */
	public static Date getBeginDayOfWeek() {
		Date date = new Date();
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK);

		if (dayofweek == 1) {
			dayofweek += 7;
		}

		cal.add(Calendar.DATE, 2 - dayofweek);

		return getDayStartTime(cal.getTime());
	}

	/**
	 * 获取本周的结束时间
	 *
	 * @return
	 */
	public static Date getEndDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getBeginDayOfWeek());
		cal.add(Calendar.DAY_OF_WEEK, 6);
		Date weekEndSta = cal.getTime();

		return getDayEndTime(weekEndSta);
	}

	/**
	 * 获取本月的开始时间
	 *
	 * @return
	 */
	public static Date getBeginDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);

		return getDayStartTime(calendar.getTime());
	}

	/**
	 * 获取本月的结束时间
	 *
	 * @return
	 */
	public static Date getEndDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);
		int day = calendar.getActualMaximum(5);
		calendar.set(getNowYear(), getNowMonth() - 1, day);

		return getDayEndTime(calendar.getTime());
	}

	/**
	 * 获取某个日期的开始时间
	 *
	 * @param d
	 * @return
	 */
	public static Date getDayStartTime(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d) calendar.setTime(d);

		calendar.set(
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0
		);

		calendar.set(Calendar.MILLISECOND, 0);

		return new Timestamp(calendar.getTimeInMillis());
	}

	/**
	 * 获取某个日期的结束时间
	 *
	 * @param d
	 * @return
	 */
	public static Date getDayEndTime(Date d) {
		Calendar calendar = Calendar.getInstance();

		if (null != d) {
			calendar.setTime(d);
		}

		calendar.set(
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59
		);

		calendar.set(Calendar.MILLISECOND, 999);

		return new Timestamp(calendar.getTimeInMillis());
	}

	/**
	 * 获取今年是哪一年
	 *
	 * @return
	 */
	public static Integer getNowYear() {
		Date              date = new Date();
		GregorianCalendar gc   = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);

		return Integer.valueOf(gc.get(1));
	}

	/**
	 * 获取本月是哪一月
	 *
	 * @return
	 */
	public static int getNowMonth() {
		Date              date = new Date();
		GregorianCalendar gc   = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);

		return gc.get(2) + 1;
	}

	/**
	 * 日期转时间戳
	 *
	 * @param date
	 * @return
	 */
	public static Long dateToTimestamp(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String           time   = date.toLocaleString();

		try {
			return format.parse(time).getTime();
		} catch (ParseException e) {
			return null;
		}
	}


	/**
	 * 获取当前时间
	 *
	 * @return
	 */
	public static Date getNowTime() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	/**
	 * 获取今年开始时间
	 *
	 * @return
	 */
	public static Date getBeginDayOfYear() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY),
				calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0
		);

		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.YEAR));
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * 获取今年结束时间
	 *
	 * @return
	 */
	public static Date getEndDayOfYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getBeginDayOfYear());
		calendar.add(Calendar.YEAR, 1);

		return calendar.getTime();
	}

}
