package cz.abclinuxu.datoveschranky.impl;

import java.util.GregorianCalendar;

import javax.xml.ws.Holder;

import cz.abclinuxu.datoveschranky.common.entities.DataBoxState;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxType;
import cz.abclinuxu.datoveschranky.common.entities.OwnerInfo;
import cz.abclinuxu.datoveschranky.common.entities.UserInfo;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxAccessService;
import cz.abclinuxu.datoveschranky.ws.db.DataBoxAccessPortType;
import cz.abclinuxu.datoveschranky.ws.db.TDbOwnerInfo;
import cz.abclinuxu.datoveschranky.ws.db.TDbReqStatus;

public class DataBoxAccessServiceImpl implements DataBoxAccessService {
	
	private DataBoxAccessPortType dmOp = null;
	
	public DataBoxAccessServiceImpl(DataBoxAccessPortType dmOp) {
		this.dmOp = dmOp;
	}

	public OwnerInfo GetOwnerInfoFromLogin() {
		Holder<TDbOwnerInfo> dbOwner = new Holder<TDbOwnerInfo>(); 
		Holder<TDbReqStatus> dbStatus = new Holder<TDbReqStatus>();
		dmOp.getOwnerInfoFromLogin("", dbOwner, dbStatus);
		TDbOwnerInfo owner = dbOwner.value;
		DataBoxType type = null;
		DataBoxState state = null;
		OwnerInfo ownerInfo = new OwnerInfo(owner.getDbID(), type, state,
				owner.isDbEffectiveOVM(), owner.isDbOpenAddressing());
		return ownerInfo;
	}

	public UserInfo GetUserInfoFromLogin() {
		// TODO Auto-generated method stub
		return null;
	}

	public GregorianCalendar GetPasswordInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
