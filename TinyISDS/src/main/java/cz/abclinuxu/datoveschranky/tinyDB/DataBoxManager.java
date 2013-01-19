package cz.abclinuxu.datoveschranky.tinyDB;

import cz.abclinuxu.datoveschranky.common.entities.DeliveryInfo;
import cz.abclinuxu.datoveschranky.common.entities.MessageStateChange;
import cz.abclinuxu.datoveschranky.common.impl.Utils;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.interfaces.AttachmentStorer;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxAccessService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxDownloadService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxSearchService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.tinyDB.responseparsers.DownloadReceivedMessage;
import cz.abclinuxu.datoveschranky.tinyDB.responseparsers.GetListOfReceivedMessages;
import cz.abclinuxu.datoveschranky.tinyDB.responseparsers.DownloadSignedReceivedMessage;
import cz.abclinuxu.datoveschranky.tinyDB.responseparsers.VerifyMessage;
import cz.abclinuxu.datoveschranky.tinyDB.responseparsers.AbstractResponseParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.codec.binary.Base64;
import org.xml.sax.SAXException;

/**
 * Tato třída umožnuje přihlášení k datové schránce a základní operace s ní, tzn.
 * stažení přijatých zpráv a stažení přijaté zprávy včetně příloh.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 * 
 */
public class DataBoxManager implements DataBoxMessagesService, DataBoxDownloadService, DataBoxServices {
    
    private static final int MAX_REDIRECT_COUNT = 25;
    private static final List<Integer> redirectionCodes = Arrays.asList(301, 302);
    private static final List<Integer> OKCodes = Arrays.asList(200, 304);
    protected final Config config;
    protected String authCookie = null;
    protected SSLSocketFactory socketFactory = null;
    protected Logger logger = Logger.getLogger(this.getClass().getName());
    protected String authorization;

    private DataBoxManager(Config configuration) {
        this.config = configuration;
    }

    /**
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
    public static DataBoxManager login(Config config, String userName, String password) throws Exception {
        DataBoxManager manager = new DataBoxManager(config);
        manager.loginImpl(userName, password);
        return manager;
    }

    public DataBoxDownloadService getDataBoxDownloadService() {
        return this;
    }

    public DataBoxMessagesService getDataBoxMessagesService() {
        return this;
    }

    public DataBoxUploadService getDataBoxUploadService() {
        throw new UnsupportedOperationException("Sluzba DataBoxUploadService neni pristupna.");
    }

    // metody z DataBoxMessages
    public List<MessageEnvelope> getListOfReceivedMessages(Date from, Date to,
            EnumSet<MessageState> state, int offset, int limit) {
        // tohle hrani se stringy je neefektivní, ale pro nase 
        // demonstracni ucely to vyhovuje.
        String resource = "/GetListOfReceivedMessages.xml";
        String post = Utils.readResourceAsString(this.getClass(), resource);
        post = post.replace("${DATE_FROM}", XMLUtils.toXmlDate(from).toString());
        post = post.replace("${DATE_TO}", XMLUtils.toXmlDate(to).toString());
        post = post.replace("${OFFSET}", String.valueOf(offset));
        post = post.replace("${LIMIT}", String.valueOf(limit));
        GetListOfReceivedMessages result = new GetListOfReceivedMessages();
        this.postAndParseResponse(post, "dx", result);
        return result.getMessages();
    }

    public List<MessageEnvelope> getListOfSentMessages(Date from,
            Date to, EnumSet<MessageState> state,  int offset, int limit) {
        throw new UnsupportedOperationException();
    }

    public Hash verifyMessage(MessageEnvelope envelope) {
        String resource = "/VerifyMessage.xml";
        String post = Utils.readResourceAsString(this.getClass(), resource);
        post = post.replace("${ID}", envelope.getMessageID());
        VerifyMessage parser = new VerifyMessage();
        this.postAndParseResponse(post, "dx", parser);
        return parser.getResult();
    }

    // metody z DataBoxDownload
    public Message downloadMessage(MessageEnvelope envelope,
            AttachmentStorer storer) {
        if (envelope.getType() != MessageType.RECEIVED) {
            throw new UnsupportedOperationException("Stahnout lze pouze prijatou zpravu");
        }
        String resource = "/DownloadReceivedMessage.xml";
        String post = Utils.readResourceAsString(this.getClass(), resource);
        post = post.replace("${ID}", envelope.getMessageID());
        DownloadReceivedMessage parser = new DownloadReceivedMessage(envelope, storer);
        this.postAndParseResponse(post, "dz", parser);
        return new Message(envelope, null, null, parser.getResult());
    }

    public void downloadSignedMessage(MessageEnvelope envelope, OutputStream os) {
        if (envelope.getType() != MessageType.RECEIVED) {
            throw new UnsupportedOperationException("Stahnout lze pouze prijatou zpravu");
        }
        String resource = "/DownloadSignedReceivedMessage.xml";
        String post = Utils.readResourceAsString(this.getClass(), resource);
        post = post.replace("${ID}", envelope.getMessageID());
        DownloadSignedReceivedMessage parser = new DownloadSignedReceivedMessage(os);
        this.postAndParseResponse(post, "dz", parser);
    }

    public DeliveryInfo getDeliveryInfo(MessageEnvelope arg0) {
        throw new UnsupportedOperationException("Operace getDeliveryInfo neni touto " +
                "knihovnou podporovana.");
    }

    public List<MessageStateChange> GetMessageStateChanges(Date from, Date to) {
	throw new UnsupportedOperationException("Operace GetMessageStateChanges neni touto " +
                "knihovnou podporovana.");
    }

    public DataBoxSearchService getDataBoxSearchService() {
        throw new UnsupportedOperationException("Sluzba DataBoxSearchService neni pristupna.");
    }

    public void markMessageAsDownloaded(MessageEnvelope env) {
        throw new UnsupportedOperationException("Operace markMessageAsDownloaded neni " +
                "touto knihovnou podporovana.");
    }
    
    public DataBoxAccessService getDataBoxAccessService() {
        throw new UnsupportedOperationException("Operace getDataBoxAccessService neni " +
        "touto knihovnou podporovana.");
    }

    /**
     * Stáhne přijatou zprávu včetně SOAP obálky a příloh jako XML soubor. Vhodné pouze
     * pro debugovací účely, ne pro záholování.
     * 
     * @param envelope  obálka zprávy, která se má stáhnout
     * @param os        kam přijde uložit
     * @throws DataBoxException
     * 
     */
    public void storeMessageAsXML(MessageEnvelope envelope, OutputStream os) {
        if (envelope.getType() != MessageType.RECEIVED) {
            throw new UnsupportedOperationException("Stahnout lze pouze prijatou zpravu");
        }
        String resource = "/resources/DownloadReceivedMessage.xml";
        String post = Utils.readResourceAsString(this.getClass(), resource);
        post = post.replace("${ID}", envelope.getMessageID());
        this.storeRequest(post, "dz", os);
    }

    public void getSignedDeliveryInfo(MessageEnvelope envelope, OutputStream os) {
        throw new UnsupportedOperationException("Not supported.");
    }

    private void loginImpl(String username, String password) throws Exception {
        String userPassword = username + ":" + password;
        Base64 base64 = new Base64(0, null, false);
        // základní HTTP autorizace
        authorization = "Basic " + new String(base64.encode(userPassword.getBytes()), "UTF-8");
        this.socketFactory = Utils.createSSLSocketFactory(config.getKeyStore());
    }

    private void postAndParseResponse(String post, String prefix,
            AbstractResponseParser rp) {
        HttpsURLConnection con = null;
        try {
            // udelame post
            URL url = new URL(config.getServiceURL() + prefix);
            con = (HttpsURLConnection) url.openConnection();
            this.configure(con);
            con.getOutputStream().write(post.getBytes("UTF-8"));
            checkHttpResponseCode(con);
            // zparsujeme výsledek SAX parserem
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            parser.parse(con.getInputStream(), new SimpleSAXParser(rp));
            // ověříme vrácený stav pri volani webove služby
            if (!rp.getStatus().ok()) {
                String message = String.format("Pozadavek selhal chybou %s:%s",
                        rp.getStatus().getStatusCode(), rp.getStatus().getStatusMesssage());
                logger.log(Level.SEVERE, message);
                throw new DataBoxException(message);
            }
        } catch (SAXException sax) {
            throw new DataBoxException("Chyba pri parsovani odpovedi.", sax);
        } catch (ParserConfigurationException pce) {
            throw new DataBoxException("Chyba pri konfiguraci SAX parseru.", pce);
        } catch (IOException ioe) {
            throw new DataBoxException("IO chyba pri cteni odpovedi.", ioe);
        } finally {
            close(con);
        }
    }

    private void storeRequest(String request, String prefix, OutputStream os) {
        HttpsURLConnection con = null;
        try {
            URL url = new URL(config.getServiceURL() + prefix);
            con = (HttpsURLConnection) url.openConnection();
            this.configure(con);
            con.getOutputStream().write(request.getBytes("UTF-8"));
            this.checkHttpResponseCode(con);
            InputStream is = con.getInputStream();
            Utils.copy(is, os);
        } catch (IOException ioe) {
            throw new DataBoxException("Nemohu ulozit zpravu", ioe);
        } finally {
            close(con);
        }
    }

    private void checkHttpResponseCode(HttpsURLConnection con) throws IOException {
        if (!OKCodes.contains(con.getResponseCode())) {
            String message = String.format("Pozadavek selhal se stavovym kodem %d %s.",
                    con.getResponseCode(), con.getResponseMessage());
            logger.log(Level.SEVERE, message);
            throw new DataBoxException(message);
        }
    }

    private void configure(HttpsURLConnection connect) throws ProtocolException {
        connect.setSSLSocketFactory(socketFactory);
        // connect.setRequestProperty("Authorization", authorization);
        connect.setRequestProperty("Authorization", authorization);
        // connect.setRequestProperty("Cookie", authCookie);
        connect.setRequestMethod("POST");
        connect.setDoOutput(true);
        connect.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connect.setRequestProperty("Soapaction", "");
    }

    private void close(HttpsURLConnection con) {
        if (con != null) {
            con.disconnect();
        }
    }
    
    
}
