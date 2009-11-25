package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DeliveryEvent;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.DeliveryInfo;
import cz.abclinuxu.datoveschranky.common.impl.Utils;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.ws.dm.DmInfoPortType;
import cz.abclinuxu.datoveschranky.ws.dm.TDelivery;
import cz.abclinuxu.datoveschranky.ws.dm.TEvent;
import cz.abclinuxu.datoveschranky.ws.dm.THash;
import cz.abclinuxu.datoveschranky.ws.dm.TRecord;
import cz.abclinuxu.datoveschranky.ws.dm.TRecordsArray;
import cz.abclinuxu.datoveschranky.ws.dm.TStatus;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
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

    public List<MessageEnvelope> getListOfReceivedMessages(GregorianCalendar from,
            GregorianCalendar to, int offset, int limit) {
        Holder<TRecordsArray> records = new Holder<TRecordsArray>();
        Holder<TStatus> status = new Holder<TStatus>();
        XMLGregorianCalendar xmlFrom = Utils.toXmlDate(from);
        XMLGregorianCalendar xmlTo = Utils.toXmlDate(to);
        BigInteger bOffset = BigInteger.valueOf(offset);
        BigInteger bLimit = BigInteger.valueOf(limit);
        dataMessageInfo.getListOfReceivedMessages(xmlFrom, xmlTo, null, "-1", bOffset, bLimit, records, status);
        ErrorHandling.throwIfError("Nemohu stahnout seznam prijatych zprav", status.value);
        return createMessages(records.value, MessageType.RECEIVED);
    }

    public List<MessageEnvelope> getListOfSentMessages(GregorianCalendar from,
            GregorianCalendar to, int offset, int limit) {
        Holder<TRecordsArray> records = new Holder<TRecordsArray>();
        Holder<TStatus> status = new Holder<TStatus>();
        XMLGregorianCalendar xmlSince = Utils.toXmlDate(from);
        XMLGregorianCalendar xmlTo = Utils.toXmlDate(to);
        BigInteger bOffset = BigInteger.valueOf(offset);
        BigInteger bLimit = BigInteger.valueOf(limit);
        dataMessageInfo.getListOfSentMessages(xmlSince, xmlTo, null, "-1", bOffset, bLimit, records, status);
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

    protected List<MessageEnvelope> createMessages(TRecordsArray records, MessageType type) {
        List<MessageEnvelope> result = new ArrayList<MessageEnvelope>();
        for (TRecord record : records.getDmRecord()) {
            String senderID = record.getDbIDSender().getValue();
            String senderIdentity = record.getDmSender().getValue();
            String senderAddress = record.getDmSenderAddress().getValue();
            DataBox sender = new DataBox(senderID, senderIdentity, senderAddress);
            String recipientID = record.getDbIDRecipient().getValue();
            String recipientIdentity = record.getDmRecipient().getValue();
            String recipientAddress = record.getDmRecipientAddress().getValue();
            DataBox recipient = new DataBox(recipientID, recipientIdentity, recipientAddress);
            String annotation = record.getDmAnnotation().getValue();
            String messageID = record.getDmID();
            MessageEnvelope env = new MessageEnvelope(type, sender, recipient, messageID, annotation);
            if (record.getDmAcceptanceTime().getValue() != null) {
                env.setAcceptanceTime(record.getDmAcceptanceTime().getValue().toGregorianCalendar());
            }
            if (record.getDmDeliveryTime().getValue() != null) {
                env.setDeliveryTime(record.getDmDeliveryTime().getValue().toGregorianCalendar());
            }
            result.add(env);
        }
        return result;
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
    
}
