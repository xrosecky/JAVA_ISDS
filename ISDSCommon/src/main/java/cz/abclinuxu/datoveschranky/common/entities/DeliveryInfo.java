package cz.abclinuxu.datoveschranky.common.entities;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class DeliveryInfo implements Serializable  {

    protected MessageEnvelope envelope = null;
    protected GregorianCalendar accepted = null;
    protected GregorianCalendar delivered = null;
    protected Hash hash = null;
    protected byte[] timestamp = null;
    protected List<DeliveryEvent> events = null;

    public DeliveryInfo() {
    }

    public GregorianCalendar getAccepted() {
        return accepted;
    }

    public void setAccepted(GregorianCalendar accepted) {
        this.accepted = accepted;
    }

    public GregorianCalendar getDelivered() {
        return delivered;
    }

    public void setDelivered(GregorianCalendar delivered) {
        this.delivered = delivered;
    }

    public Hash getHash() {
        return hash;
    }

    public void setHash(Hash hash) {
        this.hash = hash;
    }

    public byte[] getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(byte[] timestamp) {
        this.timestamp = timestamp;
    }

    public MessageEnvelope getMessageEnvelope() {
        return envelope;
    }

    public void setMessageEnvelope(MessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public List<DeliveryEvent> getEvents() {
        return events;
    }

    public void setEvents(List<DeliveryEvent> events) {
        this.events = events;
    }



}
