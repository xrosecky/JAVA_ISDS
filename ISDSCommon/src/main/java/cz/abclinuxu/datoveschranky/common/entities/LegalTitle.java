package cz.abclinuxu.datoveschranky.common.entities;

public class LegalTitle {
	private String law;
	private String year;
	private String sect;
	private String par;
	private String point;

	public LegalTitle(String law, String year, String sect, String par,
			String point) {
		super();
		this.law = law;
		this.year = year;
		this.sect = sect;
		this.par = par;
		this.point = point;
	}

	public String getLaw() {
		return law;
	}

	public String getYear() {
		return year;
	}

	public String getSect() {
		return sect;
	}

	public String getPar() {
		return par;
	}

	public String getPoint() {
		return point;
	}

}
