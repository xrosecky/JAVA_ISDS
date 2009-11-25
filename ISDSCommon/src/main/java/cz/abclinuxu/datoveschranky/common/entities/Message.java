package cz.abclinuxu.datoveschranky.common.entities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Celá zpráva, tzn. obálka zprávy + přílohy
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com> 
 */
public class Message {

    protected MessageEnvelope envelope;
    protected TimeStamp timeStamp;
    protected Hash hash;
    protected List<Attachment> attachments;

    public Message() {
        envelope = new MessageEnvelope();
        attachments = new ArrayList<Attachment>();
    }
    
    public Message(MessageEnvelope envelope, TimeStamp ts, Hash hs, List<Attachment> attachments) {
        this.envelope = envelope;
        this.timeStamp = ts;
        this.hash = hs;
        this.attachments = attachments;
    }

    /*
     * Vrátí seznam příloh.
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    /*
     * Vrátí obálku zprávy.
     */
    public MessageEnvelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(MessageEnvelope envelope) {
        this.envelope = envelope;
    }

    public TimeStamp getTimestamp() {
        return timeStamp;
    }

    public void setTimestamp(TimeStamp timestamp) {
        this.timeStamp = timestamp;
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }
    
}
