package cz.abclinuxu.datoveschranky.common.entities;

import java.util.List;

/**
 * 
 * @author xrosecky
 */
public class SearchResult {

	public enum Status {
		COMPLETE, EMPTY, SEARCH_LIMIT_REACHED, NO_UNIQUE_RESULT
	}

	private List<DataBoxWithDetails> result;
	private Status status;

	public List<DataBoxWithDetails> getResult() {
		return result;
	}

	public void setResult(List<DataBoxWithDetails> result) {
		this.result = result;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
