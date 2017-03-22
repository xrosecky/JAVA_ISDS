package cz.abclinuxu.datoveschranky.common.entities;

import java.util.List;

public class DataBoxSearchResult {

	private List<DataBoxWithDetails> result;

	private int totalCount;

	private int currentCount;

	private int position;

	private boolean lastPage;

	public List<DataBoxWithDetails> getResult() {
		return result;
	}

	public void setResult(List<DataBoxWithDetails> result) {
		this.result = result;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isLastPage() {
		return lastPage;
	}

	public void setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
	}

}
