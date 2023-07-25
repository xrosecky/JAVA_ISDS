package cz.abclinuxu.datoveschranky.ws;

import cz.abclinuxu.datoveschranky.ws.db.DataBoxManipulation;
import cz.abclinuxu.datoveschranky.ws.db.DataBoxSearch;
import cz.abclinuxu.datoveschranky.ws.db.DataBoxAccess;
import cz.abclinuxu.datoveschranky.ws.dm.DmInfoWebService;
import cz.abclinuxu.datoveschranky.ws.dm.DmOperationsWebService;
import cz.abclinuxu.datoveschranky.ws.dm.DmVoDZWebService;

import java.net.URL;

import javax.xml.namespace.QName;

/**
 * 
 * @author xrosecky
 */
public class ServiceBuilder {

	public static DmOperationsWebService createDmOperationsWebService() {
		QName qName = new QName("http://isds.czechpoint.cz/v20",
				"dmOperationsWebService");
		URL url = ServiceBuilder.class.getResource("/wsdl/dm_operations.wsdl");
		return new DmOperationsWebService(url, qName);
	}

	public static DmInfoWebService createDmInfoWebService() {
		QName qName = new QName("http://isds.czechpoint.cz/v20",
				"dmInfoWebService");
		URL url = ServiceBuilder.class.getResource("/wsdl/dm_info.wsdl");
		return new DmInfoWebService(url, qName);
	}

	public static DataBoxManipulation createDataBoxManipulation() {
		QName qName = new QName("http://isds.czechpoint.cz/v20",
				"DataBoxManipulation");
		URL url = ServiceBuilder.class
				.getResource("/wsdl/db_manipulations.wsdl");
		return new DataBoxManipulation(url, qName);
	}

	public static DataBoxSearch createDataBoxSearch() {
		QName qName = new QName("http://isds.czechpoint.cz/v20",
				"DataBoxSearch");
		URL url = ServiceBuilder.class.getResource("/wsdl/db_search.wsdl");
		return new DataBoxSearch(url, qName);
	}

	public static DataBoxAccess createDataBoxAccessService() {
		QName qName = new QName("http://isds.czechpoint.cz/v20",
				"DataBoxAccess");
		URL url = ServiceBuilder.class
				.getResource("/wsdl/db_access.wsdl");
		return new DataBoxAccess(url, qName);
	}

	public static DmVoDZWebService createDmVoDZWebService() {
		QName qName = new QName("http://isds.czechpoint.cz/v20",
				"dmVoDZWebService");
		URL url = ServiceBuilder.class.getResource("/wsdl/dm_VoDZ.wsdl");
		return new DmVoDZWebService(url, qName);
	}

}
