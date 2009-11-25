package cz.abclinuxu.datoveschranky.common.impl;

import java.io.Serializable;

/**
 * Reprezentuje návratový kód při volání webové služby.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 * 
 */
public class Status implements Serializable {

    private static final long serialVersionUID = 2L;
    
    protected final String statusCode;
    protected final String statusMesssage;
    
    public Status(String statusCode, String statusMesssage) {
        this.statusCode = statusCode;
        this.statusMesssage = statusMesssage;
    }

    /*
     * Kód chyby, viz oficiální dokumentace k DS.
     * 
     */
    public String getStatusCode() {
        return statusCode;
    }

    /*
     * Stavová hláška, viz oficiální dokumentace k DS.
     * 
     */
    public String getStatusMesssage() {
        return statusMesssage;
    }
   
    public boolean ok() {
        return "0000".equals(statusCode);
    }
    
}
