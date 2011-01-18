/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.abclinuxu.datoveschranky.impl;

import com.sun.xml.ws.developer.JAXWSProperties;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author xrosecky
 */
public class ClientCertAuthentication extends Authentication {

    protected File certFile;
    protected String certPassword;

    public ClientCertAuthentication(Config config, File certFile, String certPassword) {
        super(config);
        KeyStore keyStore = config.getKeyStore();
        this.certFile = certFile;
        this.certPassword = certPassword;
    }

    @Override
    protected void configureServiceOverride(Map<String, Object> requestContext, String servicePostfix) {
    }

    @Override
    protected void configureService(Map<String, Object> requestContext, String servicePostfix) {
        requestContext.put(JAXWSProperties.SSL_SOCKET_FACTORY, this.createSSLSocketFactory());
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.getServiceURLClientCert() + servicePostfix);
        this.configureServiceOverride(requestContext, servicePostfix);
    }

    @Override
    protected SSLSocketFactory createSSLSocketFactory() throws DataBoxException {
        try {
            // System.setProperty("https.protocols", "SSLv3");
            // System.setProperty("javax.net.debug", "all");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // KeyStore keyStore = Utils.createTrustStore();
            InputStream keyInput = new FileInputStream(certFile);
            keyStore.load(keyInput, certPassword.toCharArray());
            keyInput.close();
            keyManagerFactory.init(keyStore, certPassword.toCharArray());
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
            return context.getSocketFactory();
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new DataBoxException("Can't create SSLSocketFactory.", ex);
            }
        }
    }
}
