package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.impl.Status;
import cz.abclinuxu.datoveschranky.ws.db.TDbReqStatus;
import cz.abclinuxu.datoveschranky.ws.dm.TStatus;
import org.apache.log4j.Logger;

/**
 *
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class ErrorHandling {

    private final static String OK = "0000";
    static Logger logger = Logger.getLogger(ErrorHandling.class);
    
    public static void throwIfError(String message, TStatus tstatus) {
        if (!OK.equals(tstatus.getDmStatusCode())) {
            Status status = new Status(tstatus.getDmStatusCode(), tstatus.getDmStatusMessage());
	    logger.warn(String.format("DataBoxException throwed: message:%s, status:%s", status.getStatusMesssage(), status.getStatusCode()));
            throw new DataBoxException(message, status);
        }
    }
    
    public static void throwIfError(String message, TDbReqStatus reqStatus) {
        if (!(OK.equals(reqStatus.getDbStatusCode()))) {
            Status status = new Status(reqStatus.getDbStatusCode(), reqStatus.getDbStatusMessage());
	    logger.warn(String.format("DataBoxException throwed: message:%s, status:%s", status.getStatusMesssage(), status.getStatusCode()));
            throw new DataBoxException(message, status);
        }
    }
}
