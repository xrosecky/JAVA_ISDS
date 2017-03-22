package cz.abclinuxu.datoveschranky.common.entities;

import java.io.Serializable;

public class DataBoxQuery implements Serializable {

	private static final long serialVersionUID = 1L;

	private DataBoxQueryType queryType = DataBoxQueryType.GENERAL;

	private DataBoxType scope = null;

	private String query;

	private int page = 0;

	private int pageSize = 20;

	public DataBoxQueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(DataBoxQueryType queryType) {
		this.queryType = queryType;
	}

	public DataBoxType getScope() {
		return scope;
	}

	public void setScope(DataBoxType scope) {
		this.scope = scope;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
