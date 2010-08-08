package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DeliveryEvent;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.DeliveryInfo;
import cz.abclinuxu.datoveschranky.common.entities.DocumentIdent;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.ws.XMLUtils;
import cz.abclinuxu.datoveschranky.ws.dm.DmInfoPortType;
import cz.abclinuxu.datoveschranky.ws.dm.TDelivery;
import cz.abclinuxu.datoveschranky.ws.dm.TEvent;
import cz.abclinuxu.datoveschranky.ws.dm.THash;
import cz.abclinuxu.datoveschranky.ws.dm.TRecord;
import cz.abclinuxu.datoveschranky.ws.dm.TRecordsArray;
import cz.abclinuxu.datoveschranky.ws.dm.TStatus;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

/**
 *
 * @author xrosecky
 */
public class DataBoxMessagesServiceImpl implements DataBoxMessagesService {

    protected DmInfoPortType dataMessageInfo;

    public DataBoxMessagesServiceImpl(DmInfoPortType dmInfo) {
        this.dataMessageInfo = dmInfo;
    }

    public List<MessageEnvelope> getListOfReceivedMessages(Date from,
            Date to, EnumSet<MessageState> filter, int offset, int limit) {
        Holder<TRecordsArray> records = new Holder<TRecordsArray>();
        Holder<TStatus> status = new Holder<TStatus>();
        XMLGregorianCalendar xmlFrom = XMLUtils.toXmlDate(from);
        XMLGregorianCalendar xmlTo = XMLUtils.toXmlDate(to);
        BigInteger bOffset = BigInteger.valueOf(offset);
        BigInteger bLimit = BigInteger.valueOf(limit);
        String value = String.valueOf(MessageState.toInt(filter));
        dataMessageInfo.getListOfReceivedMessages(xmlFrom, xmlTo, null, value, bOffset, bLimit, records, status);
        ErrorHandling.throwIfError("Nemohu stahnout seznam prijatych zprav", status.value);
        return createMessages(records.value, MessageType.RECEIVED);
    }

    public List<MessageEnvelope> getListOfSentMessages(Date from,
            Date to, EnumSet<MessageState> filter, int offset, int limit) {
        Holder<TRecordsArray> records = new Holder<TRecordsArray>();
        Holder<TStatus> status = new Holder<TStatus>();
        XMLGregorianCalendar xmlSince = XMLUtils.toXmlDate(from);
        XMLGregorianCalendar xmlTo = XMLUtils.toXmlDate(to);
        BigInteger bOffset = BigInteger.valueOf(offset);
        BigInteger bLimit = BigInteger.valueOf(limit);
        String value = String.valueOf(MessageState.toInt(filter));
        dataMessageInfo.getListOfSentMessages(xmlSince, xmlTo, null, value, bOffset, bLimit, records, status);
        ErrorHandling.throwIfError("Nemohu stahnout seznam odeslanych zprav", status.value);
        return createMessages(records.value, MessageType.SENT);
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
        DeliveryInfo result = new DeliveryInfo();
        XMLGregorianCalendar accepted = delivery.value.getDmAcceptanceTime();
        if (accepted != null) {
            result.setAccepted(accepted.toGregorianCalendar());
        }
        XMLGregorianCalendar delivered = delivery.value.getDmDeliveryTime();
        if (delivered != null) {
            result.setDelivered(delivered.toGregorianCalendar());
        }
        result.setHash(new Hash(delivery.value.getDmHash().getAlgorithm(), delivery.value.getDmHash().getValue()));
        result.setMessageEnvelope(env);
        List<DeliveryEvent> events = new ArrayList<DeliveryEvent>();
        for (TEvent tEvent : delivery.value.getDmEvents().getDmEvent()) {
            DeliveryEvent event = new DeliveryEvent(tEvent.getDmEventTime().toGregorianCalendar(), tEvent.getDmEventDescr());
            events.add(event);
        }
        result.setEvents(events);
        return result;
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
            env.setSenderIdent(new DocumentIdent(senderIdent, senderRefNumber));
            // identifikace zprávy příjemcem
            String recipientIdent = record.getDmRecipientIdent().getValue();
            String recipientRefNumber = record.getDmRecipientRefNumber().getValue();
            env.setSenderIdent(new DocumentIdent(recipientIdent, recipientRefNumber));
            // a máme hotovo :-)
            result.add(env);
        }
        return result;
    }
    
}
