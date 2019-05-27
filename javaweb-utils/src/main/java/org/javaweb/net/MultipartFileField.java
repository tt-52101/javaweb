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
package org.javaweb.net;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class MultipartFileField {

	/**
	 * 表单域name
	 */
	private String fieldName;

	/**
	 * 表单域值
	 */
	private String fieldValue;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 文件流
	 */
	private InputStream fileInputStream;

	/**
	 * 内容类型
	 */
	private String contentType;

	private MultipartFileField() {

	}

	/**
	 * 创建文件上传表单域对象
	 *
	 * @param fieldName  表单域name
	 * @param fieldValue 表单域值
	 */
	public MultipartFileField(String fieldName, String fieldValue) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	/**
	 * 创建文件上传表单域对象
	 *
	 * @param fieldName       表单域name
	 * @param fileName        文件名称
	 * @param fileInputStream 文件流
	 */
	public MultipartFileField(String fieldName, String fileName, InputStream fileInputStream) {
		this.fieldName = fieldName;
		this.fileName = fileName;
		this.fileInputStream = fileInputStream;
	}

	/**
	 * 创建文件上传表单域对象
	 *
	 * @param fieldName       表单域name
	 * @param fileName        文件名称
	 * @param fileInputStream 文件流
	 * @param contentType     内容类型
	 */
	public MultipartFileField(String fieldName, String fileName, InputStream fileInputStream, String contentType) {
		this.fieldName = fieldName;
		this.fileName = fileName;
		this.fileInputStream = fileInputStream;
		this.contentType = contentType;
	}

	/**
	 * 创建文件上传表单域对象
	 *
	 * @param fieldName 表单域name
	 * @param fileName  文件名称
	 * @param bytes     文件内容的字节数组
	 */
	public MultipartFileField(String fieldName, String fileName, byte[] bytes) {
		this.fieldName = fieldName;
		this.fileName = fileName;
		this.fileInputStream = new ByteArrayInputStream(bytes);
	}

	/**
	 * 创建文件上传表单域对象
	 *
	 * @param fieldName   表单域name
	 * @param fileName    文件名称
	 * @param bytes       文件内容的字节数组
	 * @param contentType 内容类型
	 */
	public MultipartFileField(String fieldName, String fileName, byte[] bytes, String contentType) {
		this.fieldName = fieldName;
		this.fileName = fileName;
		this.fileInputStream = new ByteArrayInputStream(bytes);
		this.contentType = contentType;
	}

	/**
	 * 创建文件上传表单域对象
	 *
	 * @param fileName        文件名称
	 * @param fileInputStream 文件流
	 */
	public MultipartFileField(String fileName, InputStream fileInputStream) {
		this.fieldName = "file";
		this.fileName = fileName;
		this.fileInputStream = fileInputStream;
	}

	/**
	 * 创建文件上传表单域对象
	 *
	 * @param fileName        文件名称
	 * @param fileInputStream 文件流
	 * @param contentType     内容类型
	 */
	public MultipartFileField(String fileName, InputStream fileInputStream, String contentType) {
		this.fieldName = "file";
		this.fileName = fileName;
		this.fileInputStream = fileInputStream;
		this.contentType = contentType;
	}

	/**
	 * 创建文件上传表单域对象
	 *
	 * @param fileName 文件名称
	 * @param bytes    文件内容的字节数组
	 */
	public MultipartFileField(String fileName, byte[] bytes) {
		this.fieldName = "file";
		this.fileName = fileName;
		this.fileInputStream = new ByteArrayInputStream(bytes);
	}

	/**
	 * 创建文件上传表单域对象
	 *
	 * @param fileName    文件名称
	 * @param bytes       文件内容的字节数组
	 * @param contentType 内容类型
	 */
	public MultipartFileField(String fileName, byte[] bytes, String contentType) {
		this.fieldName = "file";
		this.fileName = fileName;
		this.fileInputStream = new ByteArrayInputStream(bytes);
		this.contentType = contentType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}