package cz.abclinuxu.datoveschranky.common.entities;

import java.security.cert.X509Certificate;
import java.util.Date;

/**
 *
 * Časové razítko zprávy
 * 
 * @author xrosecky
 */
public class TimeStamp {
    private final Hash hash;
    private final X509Certificate certificate;
    private final Date generatedTime;

    public TimeStamp(Hash hash, X509Certificate certificate, Date generatedTime) {
        this.hash = hash;
        this.certificate = certificate;
        this.generatedTime = generatedTime;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public Date getGeneratedTime() {
        return generatedTime;
    }

    public Hash getHash() {
        return hash;
    }
    
}
