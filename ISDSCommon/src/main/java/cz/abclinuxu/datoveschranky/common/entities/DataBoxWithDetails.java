/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.abclinuxu.datoveschranky.common.entities;

/**
 * 
 * @author xrosecky
 */
public class DataBoxWithDetails extends DataBox {

	private Address addressDetail;

	private String IC;

	private String firmName;

	private String pnFirstName;

	private String pnMiddleName;

	private String pnLastName;

	private boolean openAddressing;

	public DataBoxWithDetails() {
	}

	public DataBoxWithDetails(String id) {
		super(id);
	}

	public Address getAddressDetails() {
		return addressDetail;
	}

	public void setAddressDetails(Address address) {
		this.addressDetail = address;
	}

	public String getIC() {
		return IC;
	}

	public void setIC(String ic) {
		this.IC = ic;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getPnFirstName() {
		return pnFirstName;
	}

	public void setPnFirstName(String pnFirstName) {
		this.pnFirstName = pnFirstName;
	}

	public String getPnMiddleName() {
		return pnMiddleName;
	}

	public void setPnMiddleName(String pnMiddleName) {
		this.pnMiddleName = pnMiddleName;
	}

	public String getPnLastName() {
		return pnLastName;
	}

	public void setPnLastName(String pnLastName) {
		this.pnLastName = pnLastName;
	}

	public boolean isOpenAddressing() {
		return openAddressing;
	}

	public void setOpenAddressing(boolean openAddressing) {
		this.openAddressing = openAddressing;
	}

	@Override
	public String toString() {
		if (this.getIC() != null) {
			return "IC:" + this.getIC() + " " + super.toString();
		} else {
			return super.toString();
		}
	}

}
