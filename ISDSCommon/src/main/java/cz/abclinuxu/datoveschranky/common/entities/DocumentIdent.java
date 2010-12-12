package cz.abclinuxu.datoveschranky.common.entities;

import java.io.Serializable;

/**
 *
 * Nepovinná identifikace zprávy, číslo jednací + spisová značka.
 * 
 * @author xrosecky
 */
public class DocumentIdent implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String refNumber;
    private String ident;

    public DocumentIdent(String refNumber) {
        this.refNumber = refNumber;
    }

    public DocumentIdent(String refNumber, String ident) {
        this.refNumber = refNumber;
        this.ident = ident;
    }

    /**
     * Spisová značka
     */
    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    /**
     * Číslo jednací
     */
    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }
    
    
    
}
