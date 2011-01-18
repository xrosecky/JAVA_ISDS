package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import java.security.KeyStore;
import java.util.Map;
import javax.xml.ws.BindingProvider;

/**
 *
 * Autentizace
 *
 */
public class BasicAuthentication extends Authentication {

    protected String userName = null;
    protected String password = null;

    public BasicAuthentication(Config config, String userName, String password) {
        super(config);
        KeyStore keyStore = config.getKeyStore();
        this.userName = userName;
        this.password = password;
    }

    /**
     * Realizuje přihlášení do datové schránky pod daným uživatelským jménem
     * a heslem a při úspěšném přihlášení vrátí příslušnou instanci ISDSManageru
     * poskytující služby k této schránce.
     *
     * @param userName   jméno uživatele
     * @param password   heslo uživatele
     * @throws DataBoxException   při přihlašování do DS došlo k chybě. Důvodem může
     * být špatné heslo či uživatelské jméno.
     *
     */
    public static Authentication login(Config config, String userName, String password) throws Exception {
        Authentication auth = new BasicAuthentication(config, userName, password);
        return auth;
    }

    protected void configureServiceOverride(Map<String, Object> requestContext, String servicePostfix) {
        requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
    }

}
