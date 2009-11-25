package cz.abclinuxu.datoveschranky.impl;

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
    private final static String SEARCH_LIMIT_REACHED = "0003";
    
    public static void throwIfError(String message, TStatus tstatus) {
        if (!OK.equals(tstatus.getDmStatusCode())) {
            Status status = new Status(tstatus.getDmStatusCode(), tstatus.getDmStatusMessage());
            throw new DataBoxException(message, status);
        }
    }
    
    public static void throwIfError(String message, TDbReqStatus reqStatus) {
        if (!(OK.equals(reqStatus.getDbStatusCode()) ||
                SEARCH_LIMIT_REACHED.equals(reqStatus.getDbStatusCode()))) {
            Status status = new Status(reqStatus.getDbStatusCode(), reqStatus.getDbStatusMessage());
            throw new DataBoxException(message, status);
        }
    }
}
