package cz.abclinuxu.datoveschranky.common.entities;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 
 * Doručenka datové zprávy, viz dokumentace k ISDS.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class DeliveryInfo implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = 827309450119226032L;
	protected MessageEnvelope envelope = null;
	protected GregorianCalendar accepted = null;
	protected GregorianCalendar delivered = null;
	protected Hash hash = null;
	protected byte[] timestamp = null;
	protected List<DeliveryEvent> events = null;

	public DeliveryInfo() {
	}

	/**
	 * Čas doručení, je-li zpráva doručena.
	 * 
	 * @return čas doručení nebo null, není-li zpráva doručena.
	 * 
	 */
	public GregorianCalendar getAccepted() {
		return accepted;
	}

	public void setAccepted(GregorianCalendar accepted) {
		this.accepted = accepted;
	}

	/**
	 * Čas dodání
	 * 
	 * @return čas dodání
	 */
	public GregorianCalendar getDelivered() {
		return delivered;
	}

	public void setDelivered(GregorianCalendar delivered) {
		this.delivered = delivered;
	}

	public Hash getHash() {
		return hash;
	}

	/**
	 * Haš této zprávy.
	 */
	public void setHash(Hash hash) {
		this.hash = hash;
	}

	/**
	 * Časové razítko
	 */
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

	/**
	 * Vrátí seznam událostí
	 */
	public List<DeliveryEvent> getEvents() {
		return events;
	}

	public void setEvents(List<DeliveryEvent> events) {
		this.events = events;
	}

}
