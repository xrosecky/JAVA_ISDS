package cz.abclinuxu.datoveschranky.common.entities;

public enum PDZType {

	NORMAL("Normal"),
	INIT("Init");

	private final String type;

	private PDZType(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}

}
