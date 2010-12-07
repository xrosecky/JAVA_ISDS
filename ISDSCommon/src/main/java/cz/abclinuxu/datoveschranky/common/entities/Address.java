/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.abclinuxu.datoveschranky.common.entities;

/**
 *
 * @author xrosecky
 */
public class Address {

    private String city;
    private String street;
    private String numberInStreet;
    private String numberInMunicipality;
    private String zipCode;
    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumberInMunicipality() {
        return numberInMunicipality;
    }

    public void setNumberInMunicipality(String numberInMunicipality) {
        this.numberInMunicipality = numberInMunicipality;
    }

    public String getNumberInStreet() {
        return numberInStreet;
    }

    public void setNumberInStreet(String numberInStreet) {
        this.numberInStreet = numberInStreet;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

}
