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
	 * URL testovacího provozu
	 */
	@Deprecated
	public static final String TEST_URL = "ws2.czebox.cz"; // was ws1.czebox.cz
	/**
	 * URL produkčního prostředí
	 */
	@Deprecated
	public static final String PRODUCTION_URL = "ws2.mojedatovaschranka.cz"; // was
																				// ws1.mojedatovaschranka.cz
	private final DataBoxEnvironment dataBoxEnvironment;
	// private final String url;
	private final KeyStore keyStore;

	/**
	 * Vytvoří konfiguraci s daným URL a s KeyStore načteným z resources.
	 * Konstruktor je určen pro testovací účely, pro realné nasazení použijte
	 * vlastní keyStore.
	 * 
	 * @see Config#constructor((String, KeyStore) konstruktor Config.
	 * 
	 * @param servURL
	 *            URL služby (TEST_URL či PRODUCTION_URL)
	 * 
	 */
	@Deprecated
	public Config(String servURL) {
		if (servURL.equals(TEST_URL)) {
			this.dataBoxEnvironment = DataBoxEnvironment.TEST;
		} else if (servURL.equals(PRODUCTION_URL)) {
			this.dataBoxEnvironment = DataBoxEnvironment.PRODUCTION;
		} else {
			throw new IllegalArgumentException("servURL");
		}
		this.keyStore = Utils.createTrustStore();
	}

	public Config(DataBoxEnvironment dbe) {
		this.dataBoxEnvironment = dbe;
		this.keyStore = Utils.createTrustStore();
	}

	/**
	 * Vytvoří konfiguraci s daným URL a příslušným klíči
	 * 
	 * @param servURL
	 *            URL služby (TEST_URL či PRODUCTION_URL)
	 * @param keys
	 *            instance třídy KeyStore, která obsahuje certifikáty nutné pro
	 *            přihlášení do ISDS, certifikáty, kterými je podepsána obálka
	 *            zprávy a certifikáty časových razítek.
	 * 
	 */
	@Deprecated
	public Config(String servURL, KeyStore keys) {
		if (servURL.equals(TEST_URL)) {
			this.dataBoxEnvironment = DataBoxEnvironment.TEST;
		} else if (servURL.equals(PRODUCTION_URL)) {
			this.dataBoxEnvironment = DataBoxEnvironment.PRODUCTION;
		} else {
			throw new IllegalArgumentException("servURL");
		}
		this.keyStore = keys;
	}

	public String getServiceURL() {
		return "https://" + dataBoxEnvironment.basicURL() + "/DS/";
	}

	public String getServiceURL(boolean isVODZ) {
		return "https://" + dataBoxEnvironment.basicURL(isVODZ) + "/DS/";
	}

	public String getServiceURLClientCert(boolean isVODZ) {
		return "https://" + dataBoxEnvironment.clientCertURL(isVODZ)
				+ "/cert/DS/";
	}

	public String getServiceURLClientCert() {
		return "https://" + dataBoxEnvironment.clientCertURL() + "/cert/DS/";
	}

	@Deprecated
	public String getLoginScope() {
		return "login." + dataBoxEnvironment.basicURL();
	}

	public KeyStore getKeyStore() {
		return keyStore;
	}
}
