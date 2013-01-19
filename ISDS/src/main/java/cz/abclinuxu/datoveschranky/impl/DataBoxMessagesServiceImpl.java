package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DeliveryEvent;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.DeliveryInfo;
import cz.abclinuxu.datoveschranky.common.entities.DocumentIdent;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.entities.MessageStateChange;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.ws.XMLUtils;
import cz.abclinuxu.datoveschranky.ws.dm.DmInfoPortType;
import cz.abclinuxu.datoveschranky.ws.dm.TDelivery;
import cz.abclinuxu.datoveschranky.ws.dm.TEvent;
import cz.abclinuxu.datoveschranky.ws.dm.THash;
import cz.abclinuxu.datoveschranky.ws.dm.TRecord;
import cz.abclinuxu.datoveschranky.ws.dm.TRecordsArray;
import cz.abclinuxu.datoveschranky.ws.dm.TStateChangesArray;
import cz.abclinuxu.datoveschranky.ws.dm.TStateChangesRecord;
import cz.abclinuxu.datoveschranky.ws.dm.TStatus;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import org.apache.log4j.Logger;

/**
 *
 * @author xrosecky
 */
public class DataBoxMessagesServiceImpl implements DataBoxMessagesService {

    protected DmInfoPortType dataMessageInfo;
    static Logger logger = Logger.getLogger(DataBoxMessagesServiceImpl.class);

    public DataBoxMessagesServiceImpl(DmInfoPortType dmInfo) {
        this.dataMessageInfo = dmInfo;
    }

    public List<MessageEnvelope> getListOfReceivedMessages(Date from,
            Date to, EnumSet<MessageState> filter, int offset, int limit) {
        logger.info(String.format("getListOfReceivedMessages: offset:%s limit:%s", offset, limit));
        Holder<TRecordsArray> records = new Holder<TRecordsArray>();
        Holder<TStatus> status = new Holder<TStatus>();
        XMLGregorianCalendar xmlFrom = XMLUtils.toXmlDate(from);
        XMLGregorianCalendar xmlTo = XMLUtils.toXmlDate(to);
        BigInteger bOffset = BigInteger.valueOf(offset);
        BigInteger bLimit = BigInteger.valueOf(limit);
        String value = String.valueOf(MessageState.toInt(filter));
        dataMessageInfo.getListOfReceivedMessages(xmlFrom, xmlTo, null, value, bOffset, bLimit, records, status);
        ErrorHandling.throwIfError("Nemohu stahnout seznam prijatych zprav", status.value);
        logger.info(String.format("getListOfReceivedMessages finished"));
        return createMessages(records.value, MessageType.RECEIVED);
    }

    public List<MessageEnvelope> getListOfSentMessages(Date from,
            Date to, EnumSet<MessageState> filter, int offset, int limit) {
        logger.info(String.format("getListOfSentMessages: offset:%s limit:%s", offset, limit));
        Holder<TRecordsArray> records = new Holder<TRecordsArray>();
        Holder<TStatus> status = new Holder<TStatus>();
        XMLGregorianCalendar xmlSince = XMLUtils.toXmlDate(from);
        XMLGregorianCalendar xmlTo = XMLUtils.toXmlDate(to);
        BigInteger bOffset = BigInteger.valueOf(offset);
        BigInteger bLimit = BigInteger.valueOf(limit);
        String value = String.valueOf(MessageState.toInt(filter));
        dataMessageInfo.getListOfSentMessages(xmlSince, xmlTo, null, value, bOffset, bLimit, records, status);
        ErrorHandling.throwIfError("Nemohu stahnout seznam odeslanych zprav", status.value);
        logger.info(String.format("getListOfSentMessages finished"));
        return createMessages(records.value, MessageType.SENT);
    }

    public List<MessageStateChange> GetMessageStateChanges(Date from, Date to) {
	logger.info(String.format("GetMessageStateChanges: from:%s to:%s", from, to));
        Holder<TStatus> status = new Holder<TStatus>();
	Holder<TStateChangesArray> changes = new Holder<TStateChangesArray>();
	XMLGregorianCalendar xmlSince = null;
	if (from != null) {
	    xmlSince = XMLUtils.toXmlDate(from);
	}
        XMLGregorianCalendar xmlTo = null;
	if (to != null) {
	    xmlTo = XMLUtils.toXmlDate(to);
	}
	dataMessageInfo.getMessageStateChanges(xmlSince, xmlTo, changes, status);
	ErrorHandling.throwIfError("GetMessageStateChanges failed", status.value);
	List<MessageStateChange> result = new ArrayList<MessageStateChange>();
	for (TStateChangesRecord record : changes.value.getDmRecord()) {
	    MessageStateChange stateChange = new MessageStateChange();
	    stateChange.setEventTime(record.getDmEventTime().toGregorianCalendar());
	    stateChange.setMessageId(record.getDmID());
	    stateChange.setState(MessageState.valueOf(record.getDmMessageStatus()));
	    result.add(stateChange);
	}
	logger.info(String.format("GetMessageStateChanges finished, result size is %s.", changes.value.getDmRecord().size()));
	return result;
    }

    public Hash verifyMessage(MessageEnvelope envelope) {
        Holder<TStatus> status = new Holder<TStatus>();
        Holder<THash> hash = new Holder<THash>();
        dataMessageInfo.verifyMessage(envelope.getMessageID(), hash, status);
        ErrorHandling.throwIfError("Nemohu overit hash zpravy.", status.value);
        return new Hash(hash.value.getAlgorithm(), hash.value.getValue());
    }

    public void markMessageAsDownloaded(MessageEnvelope env) {
        TStatus status = dataMessageInfo.markMessageAsDownloaded(env.getMessageID());
        ErrorHandling.throwIfError("Nemohu oznacit zpravu jako prectenou.", status);
    }

    public DeliveryInfo getDeliveryInfo(MessageEnvelope env) {
        Holder<TStatus> status = new Holder<TStatus>();
        Holder<TDelivery> delivery = new Holder<TDelivery>();
        dataMessageInfo.getDeliveryInfo(env.getMessageID(), delivery, status);
        ErrorHandling.throwIfError("Nemohu stahnout informace o doruceni.", status.value);
        return MessageValidator.buildDeliveryInfo(env, delivery.value);
    }

    public void getSignedDeliveryInfo(MessageEnvelope envelope, OutputStream os) {
        Holder<TStatus> status = new Holder<TStatus>();
        Holder<byte[]> signedDeliveryInfo = new Holder<byte[]>();
        dataMessageInfo.getSignedDeliveryInfo(envelope.getMessageID(), signedDeliveryInfo, status);
        ErrorHandling.throwIfError(String.format("Nemohu stahnout podepsanou dorucenku pro zpravu s id=%s.",
                envelope.getMessageID()), status.value);
        try {
            os.write(signedDeliveryInfo.value);
            os.flush();
            logger.info(String.format("getSignedDeliveryInfo successfull"));
        } catch (IOException ioe) {
            throw new DataBoxException("Chyba pri zapisu do vystupniho proudu.", ioe);
        }
    }

    protected List<MessageEnvelope> createMessages(TRecordsArray records, MessageType type) {
        List<MessageEnvelope> result = new ArrayList<MessageEnvelope>();
        for (TRecord record : records.getDmRecord()) {
            // odesílatel
            String senderID = record.getDbIDSender().getValue();
            String senderIdentity = record.getDmSender().getValue();
            String senderAddress = record.getDmSenderAddress().getValue();
            DataBox sender = new DataBox(senderID, senderIdentity, senderAddress);
            // příjemce
            String recipientID = record.getDbIDRecipient().getValue();
            String recipientIdentity = record.getDmRecipient().getValue();
            String recipientAddress = record.getDmRecipientAddress().getValue();
            DataBox recipient = new DataBox(recipientID, recipientIdentity, recipientAddress);
            // anotace
            String annotation = record.getDmAnnotation().getValue();
            if (annotation == null) { // může se stát, že anotace je null...
                annotation = "";
            }
            String messageID = record.getDmID();
            MessageEnvelope env = new MessageEnvelope(type, sender, recipient, messageID, annotation);
            if (record.getDmAcceptanceTime().getValue() != null) {
                env.setAcceptanceTime(record.getDmAcceptanceTime().getValue().toGregorianCalendar());
            }
            if (record.getDmDeliveryTime().getValue() != null) {
                env.setDeliveryTime(record.getDmDeliveryTime().getValue().toGregorianCalendar());
            }
            env.setState(MessageState.valueOf(record.getDmMessageStatus().intValue()));
            // identifikace zprávy odesílatelem
            String senderIdent = record.getDmSenderIdent().getValue();
            String senderRefNumber = record.getDmSenderRefNumber().getValue();
            env.setSenderIdent(new DocumentIdent(senderRefNumber, senderIdent));
            // identifikace zprávy příjemcem
            String recipientIdent = record.getDmRecipientIdent().getValue();
            String recipientRefNumber = record.getDmRecipientRefNumber().getValue();
            env.setRecipientIdent(new DocumentIdent(recipientRefNumber, recipientIdent));
            env.setToHands(record.getDmToHands().getValue());
            env.setAllowSubstDelivery(record.getDmAllowSubstDelivery().getValue());
            env.setPersonalDelivery(record.getDmPersonalDelivery().getValue());
            // a máme hotovo :-)
            result.add(env);
        }
        return result;
    }
}
