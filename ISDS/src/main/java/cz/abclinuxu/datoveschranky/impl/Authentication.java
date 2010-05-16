package cz.abclinuxu.datoveschranky.impl;

import com.sun.xml.ws.developer.JAXWSProperties;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.impl.Utils;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

/**
 *
 * Autentizace
 * 
 */
public class Authentication {

    private final Config config;
    private SSLSocketFactory socketFactory = null;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    protected Map<String, List<String>> httpHeaders;
    protected String userName = null;
    protected String password = null;

    private Authentication(Config config, String userName, String password) {
        KeyStore keyStore = config.getKeyStore();
        this.socketFactory = Utils.createSSLSocketFactory(keyStore);
        this.config = config;
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
        Authentication auth = new Authentication(config, userName, password);
        return auth;
    }

    public <T> T createService(Service serviceBuilder, Class<T> serviceClass, String servicePostfix) {
        T service = serviceBuilder.getPort(serviceClass);
        configureService(((BindingProvider) service).getRequestContext(), servicePostfix);
        return service;
    }

    protected void configureService(Map<String, Object> requestContext, String servicePostfix) {
        requestContext.put(JAXWSProperties.SSL_SOCKET_FACTORY, socketFactory);
        requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.getServiceURL() + servicePostfix);
    }
}
