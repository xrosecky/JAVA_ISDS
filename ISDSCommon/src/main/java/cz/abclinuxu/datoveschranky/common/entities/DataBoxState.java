package cz.abclinuxu.datoveschranky.common.entities;

/**
 * 
 * Reprezentuje stav datové schránky, jen do schránke typu IS_ACCESSIBLE lze
 * zasílát zprávy. Pokud o zaslání zprávy do schránky jiného stavu selže chybou.
 * 
 * @author xrosecky
 */
public enum DataBoxState {

	UNKNOWN, ERROR, IS_ACCESSIBLE, TEMPORARY_INACCESSIBLE, INACTIVE, PERMANENTLY_INACCESSIBLE, DELETED;

	static public DataBoxState create(int state) {
		switch (state) {
		case 0:
			return ERROR;
		case 1:
			return IS_ACCESSIBLE;
		case 2:
			return TEMPORARY_INACCESSIBLE;
		case 3:
			return INACTIVE;
		case 4:
			return PERMANENTLY_INACCESSIBLE;
		case 5:
			return DELETED;
		default:
			return UNKNOWN;
		}
	}

}
