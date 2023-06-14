package cz.abclinuxu.datoveschranky.common.entities;

public enum DataBoxQueryType {

	GENERAL("GENERAL"), ADDRESS("ADDRESS"), ICO("ICO"), DBID("DBID");

	private final String type;

	private DataBoxQueryType(String type) {
		this.type = type;
	}

	public String type() {
		return this.type;
	}

}
