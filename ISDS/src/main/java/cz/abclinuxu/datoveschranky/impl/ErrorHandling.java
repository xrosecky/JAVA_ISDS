package cz.abclinuxu.datoveschranky.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.impl.Status;
import cz.abclinuxu.datoveschranky.ws.db.TDbReqStatus;
import cz.abclinuxu.datoveschranky.ws.dm.TStatus;

/**
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class ErrorHandling {

	private final static String OK = "0000";

	static Logger logger = LoggerFactory.getLogger(ErrorHandling.class);

	private final static Status UNKNOWN_ERROR = new Status("9999",
			"Unknown error");

	public static void throwIfError(String message, TStatus tstatus) {
		if (!OK.equals(tstatus.getDmStatusCode())) {
			Status status = new Status(tstatus.getDmStatusCode(),
					tstatus.getDmStatusMessage());
			logger.warn(String.format(
					"DataBoxException throwed: message:%s, status:%s",
					status.getStatusMesssage(), status.getStatusCode()));
			throw new DataBoxException(message, status);
		}
	}

	public static void throwIfError(String message, TDbReqStatus reqStatus) {
		if (reqStatus == null) {
			logger.warn(String.format("DataBoxException - unknown error"));
			throw new DataBoxException(message, UNKNOWN_ERROR);
		}
		if (!(OK.equals(reqStatus.getDbStatusCode()))) {
			Status status = new Status(reqStatus.getDbStatusCode(),
					reqStatus.getDbStatusMessage());
			logger.warn(String.format(
					"DataBoxException throwed: message:%s, status:%s",
					status.getStatusMesssage(), status.getStatusCode()));
			throw new DataBoxException(message, status);
		}
	}
}
