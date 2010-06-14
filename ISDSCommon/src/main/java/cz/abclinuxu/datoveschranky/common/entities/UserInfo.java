package cz.abclinuxu.datoveschranky.common.entities;

public class UserInfo {
	private String userId;
	private String userType;
	private String userPrivils;
	
	
	
	private String personNameFirstName = null;
	private String personNameMiddleName = null;
	private String personNameLastName = null;
	private String personNameLastNameAtBirth = null;

	private String birthDate = null;

	private String addressCity = null;
	private String addressStreet = null;
	private String addressNumberInStreet = null;
	private String addressNumberInMunicipality = null;
	private String addressZipCode = null;
	private String addressState = null;

	
	private String firmName = null;
	private String IC = null;

	private String contactAdressStreet = null;
	private String contactAdressCity = null;
	private String contactAdressZipCode = null;
	
	
	public UserInfo(String userId, String userType, String userPrivils) {
		super();
		this.userId = userId;
		this.userType = userType;
		this.userPrivils = userPrivils;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getUserType() {
		return userType;
	}


	public void setUserType(String userType) {
		this.userType = userType;
	}


	public String getUserPrivils() {
		return userPrivils;
	}


	public void setUserPrivils(String userPrivils) {
		this.userPrivils = userPrivils;
	}


	public String getPersonNameFirstName() {
		return personNameFirstName;
	}


	public void setPersonNameFirstName(String personNameFirstName) {
		this.personNameFirstName = personNameFirstName;
	}


	public String getPersonNameMiddleName() {
		return personNameMiddleName;
	}


	public void setPersonNameMiddleName(String personNameMiddleName) {
		this.personNameMiddleName = personNameMiddleName;
	}


	public String getPersonNameLastName() {
		return personNameLastName;
	}


	public void setPersonNameLastName(String personNameLastName) {
		this.personNameLastName = personNameLastName;
	}


	public String getPersonNameLastNameAtBirth() {
		return personNameLastNameAtBirth;
	}


	public void setPersonNameLastNameAtBirth(String personNameLastNameAtBirth) {
		this.personNameLastNameAtBirth = personNameLastNameAtBirth;
	}


	public String getBirthDate() {
		return birthDate;
	}


	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}


	public String getAddressCity() {
		return addressCity;
	}


	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}


	public String getAddressStreet() {
		return addressStreet;
	}


	public void setAddressStreet(String addressStreet) {
		this.addressStreet = addressStreet;
	}


	public String getAddressNumberInStreet() {
		return addressNumberInStreet;
	}


	public void setAddressNumberInStreet(String addressNumberInStreet) {
		this.addressNumberInStreet = addressNumberInStreet;
	}


	public String getAddressNumberInMunicipality() {
		return addressNumberInMunicipality;
	}


	public void setAddressNumberInMunicipality(String addressNumberInMunicipality) {
		this.addressNumberInMunicipality = addressNumberInMunicipality;
	}


	public String getAddressZipCode() {
		return addressZipCode;
	}


	public void setAddressZipCode(String addressZipCode) {
		this.addressZipCode = addressZipCode;
	}


	public String getAddressState() {
		return addressState;
	}


	public void setAddressState(String addressState) {
		this.addressState = addressState;
	}


	public String getFirmName() {
		return firmName;
	}


	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}


	public String getIC() {
		return IC;
	}


	public void setIC(String ic) {
		IC = ic;
	}


	public String getContactAdressStreet() {
		return contactAdressStreet;
	}


	public void setContactAdressStreet(String contactAdressStreet) {
		this.contactAdressStreet = contactAdressStreet;
	}


	public String getContactAdressCity() {
		return contactAdressCity;
	}


	public void setContactAdressCity(String contactAdressCity) {
		this.contactAdressCity = contactAdressCity;
	}


	public String getContactAdressZipCode() {
		return contactAdressZipCode;
	}


	public void setContactAdressZipCode(String contactAdressZipCode) {
		this.contactAdressZipCode = contactAdressZipCode;
	}

	
	
}
