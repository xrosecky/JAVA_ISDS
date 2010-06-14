package cz.abclinuxu.datoveschranky.common.entities;

public class OwnerInfo {
	private String dataBoxID = null;
	private DataBoxType dataBoxType = null;
	private DataBoxState dbState = null;
	
	
	private String personNameFirstName = null;
	private String personNameMiddleName = null;
	private String personNameLastName = null;
	private String personNameLastNameAtBirth = null;

	private String birthDate = null;
	private String birthCity = null;
	private String birthCounty = null;
	private String birthState = null;

	
	private String firmName = null;
	private String IC = null;

	
	private String addressCity = null;
	private String addressStreet = null;
	private String addressNumberInStreet = null;
	private String addressNumberInMunicipality = null;
	private String addressZipCode = null;
	private String addressState = null;

	private String nationality = null;
	private String identifier = null;
	private String registryCode = null;
	
	private boolean dbEffectiveOVM = false;
	private boolean dbOpenAddressing = false;

	public OwnerInfo(String dataBoxID, DataBoxType dataBoxType, DataBoxState dbState, boolean dbEffectiveOVM, boolean dbOpenAddressing) {
		super();
		this.dataBoxID = dataBoxID;
		this.dataBoxType = dataBoxType;
		this.dbState = dbState;
		this.dbEffectiveOVM = dbEffectiveOVM;
		this.dbOpenAddressing = dbOpenAddressing;
	}

	public String getDataBoxID() {
		return dataBoxID;
	}

	public void setDataBoxID(String dataBoxID) {
		this.dataBoxID = dataBoxID;
	}

	public DataBoxType getDataBoxType() {
		return dataBoxType;
	}

	public void setDataBoxType(DataBoxType dataBoxType) {
		this.dataBoxType = dataBoxType;
	}

	public DataBoxState getDbState() {
		return dbState;
	}

	public void setDbState(DataBoxState dbState) {
		this.dbState = dbState;
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

	public String getBirthCity() {
		return birthCity;
	}

	public void setBirthCity(String birthCity) {
		this.birthCity = birthCity;
	}

	public String getBirthCounty() {
		return birthCounty;
	}

	public void setBirthCounty(String birthCounty) {
		this.birthCounty = birthCounty;
	}

	public String getBirthState() {
		return birthState;
	}

	public void setBirthState(String birthState) {
		this.birthState = birthState;
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

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getRegistryCode() {
		return registryCode;
	}

	public void setRegistryCode(String registryCode) {
		this.registryCode = registryCode;
	}

	public boolean isDbEffectiveOVM() {
		return dbEffectiveOVM;
	}

	public void setDbEffectiveOVM(boolean dbEffectiveOVM) {
		this.dbEffectiveOVM = dbEffectiveOVM;
	}

	public boolean isDbOpenAddressing() {
		return dbOpenAddressing;
	}

	public void setDbOpenAddressing(boolean dbOpenAddressing) {
		this.dbOpenAddressing = dbOpenAddressing;
	}
	
	
	
}
