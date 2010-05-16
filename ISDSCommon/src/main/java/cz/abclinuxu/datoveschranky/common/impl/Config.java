package cz.abclinuxu.datoveschranky.common.impl;

import java.io.Serializable;
import java.security.KeyStore;

/**
 *
 * Konfigurace připojení k ISDS.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class Config implements Serializable {

    private static final long serialVersionUID = 3L;
    /**
     *  URL testovacího provozu
     */
    public static final String TEST_URL = "ws1.czebox.cz";
    /**
     *  URL produkčního prostředí
     */
    public static final String PRODUCTION_URL = "ws1.mojedatovaschranka.cz";

    private final String url;
    private final KeyStore keyStore;

    /**
     * Vytvoří konfiguraci s daným URL a s KeyStore načteným z resources.
     * Konstruktor je určen pro testovací účely, pro realné nasazení použijte
     * vlastní keyStore.
     * 
     * @see Config#constructor((String, KeyStore) konstruktor Config.
     * 
     * @param servURL   URL služby (TEST_URL či PRODUCTION_URL)
     * 
     */ 
    public Config(String servURL) {
        this.url = servURL;
        this.keyStore = Utils.createTrustStore();
    }

    /**
     * Vytvoří konfiguraci s daným URL a příslušným klíči
     * 
     * @param servURL   URL služby (TEST_URL či PRODUCTION_URL)
     * @param keys      instance třídy KeyStore, která obsahuje certifikáty
     *    nutné pro přihlášení do ISDS, certifikáty, kterými je podepsána obálka
     *    zprávy a certifikáty časových razítek.
     * 
     */ 
    public Config(String url, KeyStore keys) {
        this.url = url;
        this.keyStore = keys;
    }

    public String getServiceURL() {
        return "https://" + url + "/DS/";
    }

    public String getLoginScope() {
        return "login." + url;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }
}
