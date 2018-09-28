package com.dc.f01.common;



import java.util.List;
import java.util.Map;

import com.dc.f01.utils.DataUtil;
import com.dc.f01.utils.HttpUtils;
import com.dc.f01.utils.StringTools;





public class VIDBPager {
	
	private Integer				pageIndex		= 1;	// 当前页码
	private Integer				pageSize		= 10;	// 每页记录数
	private Long				totalCount		= 0L;	// 总记录数
	private Integer				pageCount		= 0;	// 总页数
	private String              url             = null;
	private List list;
	private Map<String, String[]>  paramMap;

	public VIDBPager(){
		//this.queryString = HttpTools.getQueryString();
		this.paramMap = HttpUtils.getRequestParamMap();
		String curPage = HttpUtils.getCurServletRequest().getParameter("curPage");
		if(StringTools.isNotBlankNull(curPage)){
			this.pageIndex = DataUtil.toInt(curPage);
		}
	}
	


	public Integer getPageIndex() {
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		if(pageIndex>pageCount&&pageCount!=0){
			pageIndex=pageCount;
		}
		return pageIndex;
	}

	/**
	 * 设置当前页码
	 * @param pageIndex
	 */
	public void setPageIndex(Integer pageIndex) {
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		if(pageIndex>pageCount&&pageCount!=0){
			pageIndex=pageCount;
		}
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		if (pageSize < 1) {
			pageSize = 1;
		}
		
		this.pageSize = pageSize;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
		
	}

	public Integer getPageCount() {
		pageCount = (int) (totalCount / pageSize);
		if (totalCount % pageSize > 0) {
			pageCount++;
		}
		if(pageCount==0){
			pageCount=1;
		}
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public List getList() {
		return list;
	}


	public void setList(List list) {
		this.list = list;
	}


	public Map<String, String[]> getParamMap() {
		return paramMap;
	}


	public void setParamMap(Map<String, String[]> paramMap) {
		this.paramMap = paramMap;
	}
}
