package cz.abclinuxu.datoveschranky.common.entities;

import java.io.Serializable;

/**
 * Reprezentuje datovou schránku.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class DataBox implements Serializable {

    private static final long serialVersionUID = 2L;
    private String dataBoxID = null;
    private String identity = null;
    private String address = null;
    private DataBoxType dataBoxType = null;

    public DataBox() {
    }

    public DataBox(String id) {
	this.dataBoxID = id;
    }

    public DataBox(String id, String name, String adress) {
	if (id == null) {
	    throw new NullPointerException("id");
	}
	this.dataBoxID = id;
	this.identity = name;
	this.address = adress;
    }

    public DataBox(String id, DataBoxType dataBoxType, String name, String adress) {
	if (id == null) {
	    throw new NullPointerException("id");
	}
	this.dataBoxID = id;
	this.identity = name;
	this.address = adress;
	this.dataBoxType = dataBoxType;
    }

    /**
     * Vrátí jednoznačné ID schránky.
     */
    public String getdataBoxID() {
	return dataBoxID;
    }

    public void setDataBoxID(String newID) {
	Validator.assertNotNull(newID);
	Validator.assertValidDataBoxID(newID);
	this.dataBoxID = newID;
    }

    /**
     * Jméno majitele datové schránky.
     */
    public String getIdentity() {
	return identity;
    }

    public void setIdentity(String identity) {
	this.identity = identity;
    }

    /**
     * Adresa majitele datové schránky
     */
    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    @Override
    public String toString() {
	return String.format("Uzivatel:%s, id schranky:%s adresa:%s", identity, dataBoxID, address);
    }

    public DataBoxType getDataBoxType() {
	return dataBoxType;
    }
}
