package cz.abclinuxu.datoveschranky.common.impl;

import java.io.Serializable;
import java.security.KeyStore;;

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
    private final KeyStore keyStore;
    
    public Config(String servURL) {
        this.url = servURL;
        this.keyStore = Utils.createTrustStore();
    }

    public Config(String url, KeyStore keys) {
        this.url = url;
        this.keyStore = keys;
    }
    
    
    public String getServiceURL() {
        return "https://www."+url+"/DS/";
    }
    
    public String getLoginScope() {
        return "login." + url;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    
    
}
