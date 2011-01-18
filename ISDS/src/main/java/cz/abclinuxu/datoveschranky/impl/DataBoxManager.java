package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxAccessService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxDownloadService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxSearchService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.ws.ServiceBuilder;
import cz.abclinuxu.datoveschranky.ws.db.DataBoxManipulationPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmInfoPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmOperationsPortType;
import java.io.File;

/**
 *
 * @author xrosecky
 */
public class DataBoxManager implements DataBoxServices {

    protected Authentication auth = null;
    protected Config config = null;
    protected DataBoxMessagesService dataBoxMessagesService = null;
    protected DataBoxDownloadService dataBoxDownloadService = null;
    protected DataBoxUploadService dataBoxUploadService = null;
    protected DataBoxSearchServiceImpl dataBoxFindingService = null;
    protected MessageValidator messageValidator = null;

    public DataBoxManager(Config conf, Authentication auth) {
        this.auth = auth;
        this.config = conf;
        this.messageValidator = new MessageValidator(config);
    }

    public static DataBoxManager login(Config config, String userName, String password) throws Exception {
        Authentication auth = BasicAuthentication.login(config, userName, password);
        DataBoxManager manager = new DataBoxManager(config, auth);
        return manager;
    }

    public static DataBoxManager login(Config config, File clientCert, String password) throws Exception {
        return null;
    }

    public DataBoxDownloadService getDataBoxDownloadService() {
        if (dataBoxDownloadService == null) {
            DmOperationsPortType dataMessageOperationsService = auth.createService(
                    ServiceBuilder.createDmOperationsWebService(),
                    DmOperationsPortType.class, "dz");
            dataBoxDownloadService = new DataBoxDownloadServiceImpl(dataMessageOperationsService, messageValidator);
        }
        return dataBoxDownloadService;
    }

    public DataBoxMessagesService getDataBoxMessagesService() {
        if (dataBoxMessagesService == null) {
            DmInfoPortType dataMessageInfo = auth.createService(
                    ServiceBuilder.createDmInfoWebService(),
                    DmInfoPortType.class, "dx");
            dataBoxMessagesService = new DataBoxMessagesServiceImpl(dataMessageInfo);
        }
        return dataBoxMessagesService;
    }

    public DataBoxUploadService getDataBoxUploadService() {
        if (dataBoxUploadService == null) {
            DmOperationsPortType dataMessageOperationsService = auth.createService(
                    ServiceBuilder.createDmOperationsWebService(),
                    DmOperationsPortType.class, "dz");
            dataBoxUploadService = new DataBoxUploadServiceImpl(dataMessageOperationsService);
        }
        return dataBoxUploadService;
    }

    public DataBoxSearchService getDataBoxSearchService() {
        if (dataBoxFindingService == null) {
            DataBoxManipulationPortType service = auth.createService(
                    ServiceBuilder.createDataBoxManipulation(),
                    DataBoxManipulationPortType.class, "df");
            dataBoxFindingService = new DataBoxSearchServiceImpl(service);
        }
        return dataBoxFindingService;
    }
    
    public DataBoxAccessService getDataBoxAccessService() {
        throw new UnsupportedOperationException("Operace getDataBoxAccessService neni " +
        "touto knihovnou podporovana.");
    }

}
