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
import cz.abclinuxu.datoveschranky.ws.db.DataBoxSearchPortType;
import cz.abclinuxu.datoveschranky.ws.db.DataBoxAccessPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmInfoPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmOperationsPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmVoDZPortType;

/**
 * 
 * Thread safe implementation of DataBoxServices.
 * 
 * Methods of this object are thread safe, returned instances are not.
 * 
 * @author xrosecky
 */
public class ThreadSafeDataBoxManager implements DataBoxServices {

	protected Authentication auth = null;
	protected Config config = null;

	public ThreadSafeDataBoxManager(Config conf, Authentication auth) {
		this.auth = auth;
		this.config = conf;
	}

	public DataBoxDownloadService getDataBoxDownloadService() {
		MessageValidator messageValidator = new MessageValidator(config);
		DmOperationsPortType dataMessageOperationsService = auth.createService(
				ServiceBuilder.createDmOperationsWebService(),
				DmOperationsPortType.class, "dz");
		DmVoDZPortType dataBigMessageOperationsService = auth.createService(
				ServiceBuilder.createDmVoDZWebService(), DmVoDZPortType.class,
				"vodz");
		return new DataBoxDownloadServiceImpl(dataMessageOperationsService,
				dataBigMessageOperationsService, messageValidator);
	}

	public DataBoxMessagesService getDataBoxMessagesService() {
		DmInfoPortType dataMessageInfo = auth.createService(
				ServiceBuilder.createDmInfoWebService(), DmInfoPortType.class,
				"dx");
		return new DataBoxMessagesServiceImpl(dataMessageInfo);
	}

	public DataBoxUploadService getDataBoxUploadService() {
		DmOperationsPortType dataMessageOperationsService = auth.createService(
				ServiceBuilder.createDmOperationsWebService(),
				DmOperationsPortType.class, "dz");
		return new DataBoxUploadServiceImpl(dataMessageOperationsService);
	}

	public DataBoxSearchService getDataBoxSearchService() {
		DataBoxManipulationPortType manipulationservice = auth.createService(
				ServiceBuilder.createDataBoxManipulation(),
				DataBoxManipulationPortType.class, "df");
		DataBoxSearchPortType searchService = auth.createService(
				ServiceBuilder.createDataBoxSearch(),
				DataBoxSearchPortType.class, "df");
		return new DataBoxSearchServiceImpl(manipulationservice, searchService);
	}

	public DataBoxAccessService getDataBoxAccessService() {
		DataBoxAccessPortType service = auth.createService(
				ServiceBuilder.createDataBoxAccessService(),
				DataBoxAccessPortType.class, "DsManage");
		return new DataBoxAccessServiceImpl(service);
	}

}
