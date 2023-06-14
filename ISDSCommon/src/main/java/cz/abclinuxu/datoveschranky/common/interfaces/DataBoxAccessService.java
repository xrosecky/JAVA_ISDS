package cz.abclinuxu.datoveschranky.common.interfaces;

import java.util.GregorianCalendar;

import cz.abclinuxu.datoveschranky.common.entities.OwnerInfo;
import cz.abclinuxu.datoveschranky.common.entities.UserInfo;

public interface DataBoxAccessService {

	OwnerInfo GetOwnerInfoFromLogin();

	UserInfo GetUserInfoFromLogin();

	/**
	 * Vraci datum expirace hesla
	 * 
	 * @return
	 */
	GregorianCalendar GetPasswordInfo();
}
