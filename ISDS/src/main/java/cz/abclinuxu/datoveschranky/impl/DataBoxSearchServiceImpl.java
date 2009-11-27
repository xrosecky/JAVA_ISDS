package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxState;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxSearchService;
import cz.abclinuxu.datoveschranky.ws.db.DataBoxManipulationPortType;
import cz.abclinuxu.datoveschranky.ws.db.TDbOwnerInfo;
import cz.abclinuxu.datoveschranky.ws.db.TDbOwnersArray;
import cz.abclinuxu.datoveschranky.ws.db.TDbReqStatus;
import cz.abclinuxu.datoveschranky.ws.db.TDbType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.ws.Holder;

/**
 *
 * @author xrosecky
 */
public class DataBoxSearchServiceImpl implements DataBoxSearchService {

    protected static final int MIN_PREFIX_LENGHT = 3;
    protected final static String OK = "0000";
    protected final static String SEARCH_LIMIT_REACHED = "0003";
    protected final static String NOTHING_FOUND = "0002";
    List<String> searchOKCodes = Arrays.asList(OK, SEARCH_LIMIT_REACHED, NOTHING_FOUND);
    
    protected DataBoxManipulationPortType service;

    public DataBoxSearchServiceImpl(DataBoxManipulationPortType serv) {
        this.service = serv;
    }
    
    public DataBoxState checkDataBox(DataBox db) {
        String id = db.getdataBoxID();
        Holder<Integer> dbState = new Holder<Integer>();
        Holder<TDbReqStatus> status = new Holder<TDbReqStatus>();
        service.checkDataBox(id, true, "", dbState, status);
        ErrorHandling.throwIfError(String.format("Chyba pri zjistovani stavu schranky " +
                "s id=%s.", db.getdataBoxID()), status.value);
        return DataBoxState.create(dbState.value);
    }
    
    public List<DataBox> findOVMsByName(String prefix) {
        if (prefix.length() < MIN_PREFIX_LENGHT) {
            throw new IllegalArgumentException(String.format("Prefix musi obsahovat " +
                    "alespon %d znaky.", MIN_PREFIX_LENGHT));
        }
        TDbOwnerInfo ownerInfo = new TDbOwnerInfo();
        ownerInfo.setFirmName(prefix);
        ownerInfo.setDbType(TDbType.OVM);
        Holder<TDbOwnersArray> owners = new Holder<TDbOwnersArray>();
        Holder<TDbReqStatus> status = new Holder<TDbReqStatus>();
        service.findDataBox(ownerInfo, owners, status);
        if (!searchOKCodes.contains(status.value.getDbStatusCode())) {
            ErrorHandling.throwIfError("Nemohu najit OVM.", status.value);
        }
        List<DataBox> result = new ArrayList<DataBox>();
        for (TDbOwnerInfo owner : owners.value.getDbOwnerInfo()) {
            result.add(create(owner));
        }
        return result;
    }

    public DataBox findDataBoxByID(String id) {
        if (id == null) {
            throw new NullPointerException(id);
        }
        Holder<TDbOwnersArray> owners = new Holder<TDbOwnersArray>();
        Holder<TDbReqStatus> status = new Holder<TDbReqStatus>();
        TDbOwnerInfo ownerInfo = new TDbOwnerInfo();
        ownerInfo.setDbID(id);
        service.findDataBox(ownerInfo, owners, status);
        ErrorHandling.throwIfError(String.format("Chyba při hledaní datové "+
                "schránky s id=%s.", id), status.value);
        List<TDbOwnerInfo> found = owners.value.getDbOwnerInfo();
        if (found.size() > 1) {
            throw new AssertionError(String.format("Metoda findDataBoxByID pri hledani datove " +
                    "schranky s id=%s vratila vice nez jednu schranku.", id));
        }
        if (found.size() == 1) {
            return create(found.get(0));
        } else {
            return null;
        }
    }
    
    static DataBox create(TDbOwnerInfo owner) {
        DataBox result = new DataBox(owner.getDbID());
        result.setIdentity(owner.getFirmName());
        String street = null;
        if (owner.getAdNumberInMunicipality().trim().equals("")) {
            street = String.format("%s %s", owner.getAdStreet(), owner.getAdNumberInStreet());
        } else {
            street = String.format("%s %s/%s", owner.getAdStreet(),
                    owner.getAdNumberInMunicipality(), owner.getAdNumberInStreet());
        }
        String address = String.format("%s, %s %s, %s", street,
                owner.getAdZipCode(), owner.getAdCity(), owner.getAdState());
        result.setAddress(address);
        return result;
    }
}
