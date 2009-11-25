package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxDownloadService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxSearchService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.ws.db.DataBoxManipulation;
import cz.abclinuxu.datoveschranky.ws.db.DataBoxManipulationPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmInfoPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmInfoWebService;
import cz.abclinuxu.datoveschranky.ws.dm.DmOperationsPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmOperationsWebService;


/**
 *
 * @author xrosecky
 */
public class DataBoxManager implements DataBoxServices {

    protected Authentication auth = null;
    protected DataBoxMessagesService dataBoxMessagesService = null;
    protected DataBoxDownloadService dataBoxDownloadService = null;
    protected DataBoxUploadService dataBoxUploadService = null;
    protected DataBoxSearchServiceImpl dataBoxFindingService = null;

    private DataBoxManager(Authentication auth) {
        this.auth = auth;
    }

    public static DataBoxManager login(Config config, String userName, String password) throws Exception {
        Authentication auth = Authentication.login(config, userName, password);
        DataBoxManager manager = new DataBoxManager(auth);
        return manager;
    }

    public DataBoxDownloadService getDataBoxDownloadService() {
        if (dataBoxDownloadService == null) {
            DmOperationsPortType dataMessageOperationsService = auth.createService(new DmOperationsWebService(),
                    DmOperationsPortType.class, "dz");
            dataBoxDownloadService = new DataBoxDownloadServiceImpl(dataMessageOperationsService);
        }
        return dataBoxDownloadService;
    }

    public DataBoxMessagesService getDataBoxMessagesService() {
        if (dataBoxMessagesService == null) {
            DmInfoPortType dataMessageInfo = auth.createService(new DmInfoWebService(),
                    DmInfoPortType.class, "dx");
            dataBoxMessagesService = new DataBoxMessagesServiceImpl(dataMessageInfo);
        }
        return dataBoxMessagesService;
    }

    public DataBoxUploadService getDataBoxUploadService() {
        if (dataBoxUploadService == null) {
            DmOperationsPortType dataMessageOperationsService = auth.createService(new DmOperationsWebService(),
                    DmOperationsPortType.class, "dz");
            dataBoxUploadService = new DataBoxUploadServiceImpl(dataMessageOperationsService);
        }
        return dataBoxUploadService;
    }

    public DataBoxSearchService getDataBoxSearchService() {
        if (dataBoxFindingService == null) {
            DataBoxManipulationPortType service = auth.createService(new DataBoxManipulation(),
                    DataBoxManipulationPortType.class, "df");
            dataBoxFindingService = new DataBoxSearchServiceImpl(service);
        }
        return dataBoxFindingService;
    }
}
