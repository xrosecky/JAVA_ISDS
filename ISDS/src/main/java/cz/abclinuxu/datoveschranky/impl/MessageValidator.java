package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DocumentIdent;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.TimeStamp;
import cz.abclinuxu.datoveschranky.common.entities.content.Content;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.impl.Utils;
import cz.abclinuxu.datoveschranky.common.interfaces.AttachmentStorer;
import cz.abclinuxu.datoveschranky.ws.dm.TFilesArray.DmFile;
import cz.abclinuxu.datoveschranky.ws.dm.TMessDownOutput;
import cz.abclinuxu.datoveschranky.ws.dm.TReturnedMessage;
import cz.abclinuxu.datoveschranky.ws.dm.TReturnedMessage.DmDm;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Třída pro práci s podepsanými zprávami, umožňuje ověření podpisu a časového
 * razítka, extrakci příloh z podepsané zprávy a výpočet haše zprávy.
 * 
 * 
 * TODO: Rozhrání a implementace této třídy přejde předělat, nyní slouží pouze za
 * účelem testování. Implementace je neefektivní.
 * 
 * @author xrosecky
 */
public class MessageValidator {

    private static final String encoding = "UTF-8";
    private static final String startTag = "<p:dmDm";
    private static final String endTag = "</p:dmDm>";
    
    private Logger logger= Logger.getLogger(MessageValidator.class.getCanonicalName());
    private Validator validator;

    public MessageValidator() {
        this.validator = new Validator();
    }

    public MessageValidator(Config config) {
        this.validator = new Validator(Utils.getX509Certificates(config.getKeyStore()), false);
    }

    /**
     * Na vstup dostane podepsanou zprávu v binárním formátu PKCS#7 (žádné XML),
     * a vrátí zprávu včetně příloh při splnění následujících podmínek:
     * 
     * - zpráva je podepsaná platným certifikátem
     * - časové razítko je podepsané platným certifikátem
     * - haš časového razítka a haš zprávy (element dmHash) jsou totožné a
     *   souhlasí se spočítaným hašem ze zprávy způsobem definovaným
     *   v dokumentaci k ISDS.
     * 
     * Validace zpráv probíha proti certifikátům, které jsou předány při
     * volání konstruktoru této třídy, ne proti přiloženým certifikátům k
     * časovému razítku či podpisu zprávy.
     * 
     * Pokud zpráva nesplnuje výše uvedené podmínky, je vyhozena vyjímka
     * DataBoxException s detailním popisem chyby.
     * 
     * @param  asPKCS7  zpráva v obalu PKCS#7
     * @return  zpráva včetně příloh
     * @throws DataBoxException při neúspěšné validaci
     * 
     */
    public Message validateAndCreateMessage(Content content, AttachmentStorer storer) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Utils.copy(content.getInputStream(), bos);
        return this.validateAndCreateMessage(bos.toByteArray(), storer);
    }
    
    Message validateAndCreateMessage(byte[] asPCKS7, AttachmentStorer storer) throws DataBoxException {
        byte[] asXML = validator.readPKCS7(asPCKS7);
        MarshallerResult result = null;
        try {
            result = load(TMessDownOutput.class, asXML);
        } catch (Exception ex) {
            throw new DataBoxException("Nemohu demarsalovat zpravu", ex);
        }
        TMessDownOutput out = (TMessDownOutput) ((JAXBElement) result.value).getValue();
        TReturnedMessage tMessage = out.getDmReturnedMessage().getValue();
        MessageEnvelope envelope = null;
        if (result.rootUri.endsWith("/v20/SentMessage")) {
            envelope = this.buildMessageEnvelope(tMessage, MessageType.SENT);
        } else if (result.rootUri.endsWith("/v20/message")) {
            envelope = this.buildMessageEnvelope(tMessage, MessageType.RECEIVED);
        } else {
            logger.log(Level.SEVERE, String.format("Neplatny namespace '%s' u zpravy.",
                    result.rootUri));
            envelope = this.buildMessageEnvelope(tMessage, MessageType.CREATED);
        }
        Message message = buildMessage(envelope, tMessage, storer);
        Hash messageHash = new Hash(tMessage.getDmHash().getAlgorithm(), tMessage.getDmHash().getValue());
        Hash rightHash = computeMessageHash(asXML, message.getTimeStamp().getHash().getAlgorithm());
        if (!rightHash.equals(message.getTimeStamp().getHash())) {
            throw new DataBoxException("Poruseni integrity zpravy, spocitany has zpravy " +
                    "nen roven hasi uvedenemu v casovem razitku.");
        }
        if (!rightHash.equals(messageHash)) {
            throw new DataBoxException("Poruseni integrity zpravy, spocitany hash zpravy " +
                    "nen roven hasi uvedenemu ve zprave.");
        }
        return message;
    }

    /**
     * Spočítá haš zprávy jak je definován v ISDS u zprávy v XMLku, tzn.
     * od elementu <p:dmDm> až po </p:dmDm> včetně (od zobáčku po zobáček).
     * 
     * DOM ani SAX nezachovává fyzickou strukturu, např. mezery oddělujíci
     * atributy (třeba "<a href='bla bla'/>" vs "<a    href='bla bla'/>" ) a haš v
     * takovém případě by nevyšel, takže se na to musí jít takhle přímo, tzn. najít
     * v posloupnosti bytů počátek elementu <p:dmDm> a konec elementu </p:dmDm> a 
     * z této posloupnosti vypočítat haš zprávy.
     * 
     * TODO: místo pole bajtů to bude akceptovat InputStream a hledat začátek
     * a konec pomocí stavového automatu a obsah mezi nimi po částech pumpovat
     * do hašovací funkce, takže spočítání haše bude efektivní z hlediska času
     * a paměti, ne jako tohle, kde alokuji velký String v paměti...
     * 
     */
    static Hash computeMessageHash(byte[] messageInXML, String algorithm) throws DataBoxException {
        try {
            // keep it simple, stupid...
            String asString = new String(messageInXML, encoding);
            int startAt = asString.indexOf(startTag);
            int endAt = asString.indexOf(endTag);
            String substr = asString.substring(startAt, endAt + endTag.length());
            byte[] toHash = substr.getBytes(encoding);
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(toHash);
            return new Hash(algorithm, md.digest());
        } catch (NoSuchAlgorithmException nsae) {
            throw new DataBoxException(nsae.toString(), nsae);
        } catch (UnsupportedEncodingException uee) {
            throw new DataBoxException(uee.toString(), uee);
        }
    }

    MessageEnvelope buildMessageEnvelope(TReturnedMessage message, MessageType type) {
        MessageEnvelope result = new MessageEnvelope();
        result.setState(MessageState.valueOf(message.getDmMessageStatus().intValue()));
        XMLGregorianCalendar accepted = message.getDmAcceptanceTime();
        if (accepted != null) {
            result.setAcceptanceTime(accepted.toGregorianCalendar());
        }
        XMLGregorianCalendar delivered = message.getDmAcceptanceTime();
        if (delivered != null) {
            result.setDeliveryTime(delivered.toGregorianCalendar());
        }
        result.setType(type);
        DmDm mess = message.getDmDm();
        return buildMessage(mess, result);
    }
    
    MessageEnvelope buildMessage(DmDm mess, MessageEnvelope result) {
        // id zprávy a předmět
        result.setMessageID(mess.getDmID());
        result.setAnnotation(mess.getDmAnnotation());
        // odesílatel
        String senderID = mess.getDbIDSender();
        String senderIdentity = mess.getDmSender();
        String senderAddress = mess.getDmSenderAddress();
        DataBox sender = new DataBox(senderID, senderIdentity, senderAddress);
        result.setSender(sender);
        // příjemce
        String recipientID = mess.getDbIDRecipient();
        String recipientIdentity = mess.getDmRecipient();
        String recipientAddress = mess.getDmRecipientAddress();
        DataBox recipient = new DataBox(recipientID, recipientIdentity, recipientAddress);
        result.setRecipient(recipient);
        // identifikace zprávy odesílatelem
        String senderIdent = mess.getDmSenderIdent();
        String senderRefNumber = mess.getDmSenderRefNumber();
        result.setSenderIdent(new DocumentIdent(senderIdent, senderRefNumber));
        // identifikace zprávy příjemcem
        String recipientIdent = mess.getDmRecipientIdent();
        String recipientRefNumber = mess.getDmRecipientRefNumber();
        result.setSenderIdent(new DocumentIdent(recipientIdent, recipientRefNumber));
        // a máme hotovo :-)
        return result;
    }

    Message buildMessage(MessageEnvelope envelope, TReturnedMessage message, AttachmentStorer storer) {
        List<Attachment> attachments = new ArrayList<Attachment>();
        for (DmFile file : message.getDmDm().getDmFiles().getDmFile()) {
            Attachment attachment = new Attachment();
            attachment.setDescription(file.getDmFileDescr());
            attachment.setMetaType(file.getDmFileMetaType());
            attachment.setMimeType(file.getDmMimeType());
            OutputStream os = null;
            try {
                try {
                    os = storer.store(envelope, attachment);
                    os.write(file.getDmEncodedContent());
                } finally {
                    if (os != null) {
                        os.close();
                    }
                }
            } catch (IOException ioe) {
                throw new DataBoxException("Nelze zapisovat do vystupniho proudu", ioe);
            }
            attachments.add(attachment);
        }
        TimeStamp ts = validator.readTimeStamp(message.getDmQTimestamp());
        return new Message(envelope, ts, null, attachments);
    }

    private static class MarshallerResult {

        public Object value;
        public String rootUri;

        public MarshallerResult(Object val, String uri) {
            this.value = val;
            this.rootUri = uri;
        }
    }

    // viz dokumentace k ISDS, upraví u stažené nebo odeslané zprávy jmenný prostor,
    // aby to šlo úspěšně přes JAXB zpátky demaršalovat a validovat proti schématu.
    private static class DBMessageXMLFilter extends XMLFilterImpl {

        private static final String namespace = "http://isds.czechpoint.cz/v20";
        public String rootURI = null;

        public DBMessageXMLFilter(XMLReader arg0) {
            super(arg0);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (rootURI == null) {
                rootURI = uri;
            }
            super.startElement(namespace, localName, localName, attributes);
        }
    }

    private static <E> MarshallerResult load(Class<E> clazz, byte[] what) throws Exception {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        SAXParserFactory SAXfactory = SAXParserFactory.newInstance();
        XMLReader reader = SAXfactory.newSAXParser().getXMLReader();
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        DBMessageXMLFilter xmlFilter = new DBMessageXMLFilter(reader);
        reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
        SAXSource source = new SAXSource(xmlFilter, new InputSource(new ByteArrayInputStream(what)));
        return new MarshallerResult(unmarshaller.unmarshal(source), xmlFilter.rootURI);
    }
}
