package cz.abclinuxu.datoveschranky.common.impl;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * Konfigurace připojení k ISDS.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class Config implements Serializable {

    private static final long serialVersionUID = 3L;
    
    // testovací provoz
    public static final String TEST_URL = "czebox.cz";
    // produkční provoz
    public static final String PRODUCTION_URL = "mojedatovaschranka.cz";
    // defaultní konfigurace
    public static final String DEFAULT = TEST_URL;
    
    private final String url;
    private final Collection<X509Certificate> certificates;
    
    public Config(String servURL) {
        this.url = servURL;
        this.certificates = new ArrayList<X509Certificate>();
    }

    public Config(String url, Collection<X509Certificate> certificates) {
        this.url = url;
        this.certificates = certificates;
    }
    
    
    public String getServiceURL() {
        return "https://www."+url+"/DS/";
        // return serviceURL;
    }
    
    public String getLoginScope() {
        return "login." + url;
    }

    public Collection<X509Certificate> getCertificates() {
        return certificates;
    }
    
}
