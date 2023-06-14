package cz.abclinuxu.datoveschranky.common.entities;

import java.util.GregorianCalendar;

/**
 * 
 * @author xrosecky
 */
public class MessageStateChange {

	private GregorianCalendar eventTime = null;
	private String messageId = null;
	private MessageState state = null;

	public GregorianCalendar getEventTime() {
		return eventTime;
	}

	public void setEventTime(GregorianCalendar eventTime) {
		this.eventTime = eventTime;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public MessageState getState() {
		return state;
	}

	public void setState(MessageState state) {
		this.state = state;
	}

}
