package org.javaweb.jdbc;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

	/**
	 * 查询的结果数组
	 */
	private List<T> result;

	/**
	 * 当前页
	 */
	private int pageNum;

	/**
	 * 每页显示数量
	 */
	private int pageSize;

	/**
	 * 总记录数
	 */
	private long recordCount;

	/**
	 * 分页总数
	 */
	private int pageCount;

	/**
	 * 开始页
	 */
	private int pageBegin;

	/**
	 * 结束页
	 */
	private int pageEnd;

	public Page() {

	}

	/**
	 * 分页计算
	 *
	 * @param pageNum
	 * @param recordCount
	 * @param pageSize
	 */
	public Page(int pageNum, int pageSize, List<T> result, long recordCount) {
		this.setPageNum(pageNum);
		this.setPageSize(pageSize);
		this.setResult(result);

		double dd = (double) recordCount / this.getPageSize();
		this.pageCount = (int) Math.ceil(dd);
		this.pageBegin = this.getPageNum() - 5 <= 0 ? 1 : this.getPageNum() - 5;
		this.pageEnd = pageCount - this.getPageNum() <= 4 ? pageCount : this.getPageNum() + 4;
		this.setRecordCount(recordCount);
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPageBegin() {
		return pageBegin;
	}

	public void setPageBegin(int pageBegin) {
		this.pageBegin = pageBegin;
	}

	public int getPageEnd() {
		return pageEnd;
	}

	public void setPageEnd(int pageEnd) {
		this.pageEnd = pageEnd;
	}

	public static String getPageSql(String sql, int currentPage, int pageSize) {
		currentPage = currentPage < 1 ? 1 : currentPage;
		pageSize = pageSize < 1 ? 1 : pageSize;
		return sql + " limit " + (currentPage - 1) * pageSize + "," + pageSize;
	}

	public static String getResultCountSql(String sql, Object... obj) {
		return "SELECT count(*) FROM ( " + sql + " ) rs";
	}

}