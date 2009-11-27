package cz.abclinuxu.datoveschranky.impl;

import com.sun.xml.ws.developer.JAXWSProperties;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.impl.Utils;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

/**
 *
 * Autentizace
 * 
 * Část kódu je založena na prototypu Filipa Jirsáka, oproti tomutu prototypu bylo
 * upraveno hledání autorizační cookie a přidaná podpora pro apache http client
 * ve verzi 4.0.
 * 
 */
public class Authentication {

    private class MyRedirectHandler implements RedirectHandler {

        @Override
        public URI getLocationURI(HttpResponse response, HttpContext context) {
            Header header = response.getFirstHeader("Location");
            if (header != null) {
                try {
                    return new URI(header.getValue());
                } catch (URISyntaxException ex) {
                    throw new DataBoxException(String.format("URL '%s' je syntakticky spatne.",
                            header.getValue()), ex);
                }
            } else {
                throw new DataBoxException("V odpovedi chybi hlavicka location s "+
                        "urcenim, kam se presmerovat.");
            }
        }

        @Override
        public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
            int responseCode = response.getStatusLine().getStatusCode();
            return (responseCode >= 300 && responseCode < 400); // pro nase ucely to staci
        }
    }
    private static final List<Integer> OKCodes = Arrays.asList(200, 304);
    private final Config config;
    private SSLSocketFactory socketFactory = null;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    protected Map<String, List<String>> httpHeaders;

    private Authentication(Config config) {
        this.config = config;
    }

    /*
     * Realizuje přihlášení do datové schránky pod daným uživatelským jménem
     * a heslem a při úspěšném přihlášení vrátí příslušnou instanci ISDSManageru
     * poskytující služby k této schránce.
     * 
     * @param userName   jméno uživatele
     * @param password   heslo uživatele
     * @throws DataBoxException   při přihlašování do DS došlo k chybě. Důvodem může
     * být špatné heslo či uživatelské jméno, zacyklení při přesměrování či absence
     * autorizační cookie.
     * 
     */
    public static Authentication login(Config config, String userName, String password) throws Exception {
        Authentication auth = new Authentication(config);
        auth.loginImpl(userName, password);
        return auth;
    }

    protected void loginImpl(String userName, String password) {
        try {
            KeyStore keyStore = config.getKeyStore();
            this.socketFactory = Utils.createSSLSocketFactory(keyStore);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(config.getServiceURL() + "df");
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", new org.apache.http.conn.ssl.SSLSocketFactory(config.getKeyStore()), 443));
            httpClient.getCredentialsProvider().setCredentials(new AuthScope(config.getLoginScope(), 443), new UsernamePasswordCredentials(userName, password));
            httpClient.setRedirectHandler(new MyRedirectHandler());
            HttpResponse result = httpClient.execute(post);
            StatusLine status = result.getStatusLine();
            if (OKCodes.contains(status.getStatusCode())) {
                processCookies(httpClient);
            } else {
                String error = status.getStatusCode() + " " + status.getReasonPhrase();
                throw new DataBoxException(String.format("Prihlaseni do DS selhalo, HTTP stavovy kod je %s. ", error));
            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new DataBoxException("Prihlaseni do DS se nezdarilo.", ex);
            }
        }
    }

    protected void processCookies(DefaultHttpClient httpClient) {
        String cookieHeader = null;
        for (Cookie cookie : httpClient.getCookieStore().getCookies()) {
            if (cookie.getName().startsWith("IPC")) {
                List<Header> headers = httpClient.getCookieSpecs().getCookieSpec("rfc2965").formatCookies(Collections.singletonList(cookie));
                assert headers.size() == 1;
                cookieHeader = headers.get(0).getValue();
            }
        }
        if (cookieHeader != null) {
            logger.log(Level.INFO, "Autorizacni cookie byla nalezena.");
            httpHeaders = Collections.singletonMap("Cookie", Collections.singletonList(cookieHeader));
        } else {
            throw new DataBoxException("Nebyla nalezena autentizacni cookie.");
        }
    }

    public <T> T createService(Service serviceBuilder, Class<T> serviceClass, String servicePostfix) {
        T service = serviceBuilder.getPort(serviceClass);
        configureService(((BindingProvider) service).getRequestContext(), servicePostfix);
        return service;
    }

    protected void configureService(Map<String, Object> requestContext, String servicePostfix) {
        assert this.httpHeaders != null;
        HashMap<String, List<String>> map = new HashMap<String, List<String>>(httpHeaders);
        requestContext.put(JAXWSProperties.SSL_SOCKET_FACTORY, socketFactory);
        requestContext.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.getServiceURL() + servicePostfix);
        requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, map);
    }
}
