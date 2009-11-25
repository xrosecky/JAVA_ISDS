package cz.abclinuxu.datoveschranky.common.entities;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 *
 * Obálka zprávy, neobsahuje přílohy (viz třída Attachment).
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class MessageEnvelope implements Serializable {

    private static final long serialVersionUID = 3L;
    private DataBox sender;
    private DataBox recipient;
    private MessageType type;
    private String id;
    private String annotation;
    private GregorianCalendar deliveryTime = null;
    private GregorianCalendar acceptanceTime = null;

    public MessageEnvelope() {
        this.type = MessageType.CREATED;
    }
    
    public MessageEnvelope(MessageType type, DataBox sender, DataBox recipient, String id, String annotation) {
        Validator.assertNotNull(type);
        Validator.assertNotNull(sender);
        Validator.assertNotNull(recipient);
        Validator.assertNotNull(id);
        Validator.assertValidMessageID(id);
        Validator.assertNotNull(annotation);
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.id = id;
        this.annotation = annotation;
    }

    /*
     * Vrátí předmět zprávy. 
     */
    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        Validator.assertNotNull(annotation);
        this.annotation = annotation;
    }
    
    /*
     * Vrátí příjemce zprávy. 
     */
    public DataBox getRecipient() {
        return recipient;
    }
    
    public void setRecipient(DataBox recipient) {
        Validator.assertNotNull(recipient);
        this.recipient = recipient;
    }

    /*
     * Vrátí odesílatele zprávy.
     */
    public DataBox getSender() {
        return sender;
    }
    
    public void setSender(DataBox sender) {
        Validator.assertNotNull(sender);
        this.sender = sender;
    }

    /*
     * Vrátí jednoznačný identifikátor této zprávy.
     */
    public String getMessageID() {
        return id;
    }

    public void setMessageID(String newID) {
        Validator.assertNotNull(newID);
        this.id = newID;
    }    

    /*
     * Vrátí typ zprávy (zpráva přijatá, odeslaná, vytvořená za účelem odesláni)
     */
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        Validator.assertNotNull(type);
        this.type = type;
    }

    /*
     * Čas, kdy byla zpráva akceptována nebo null, nebyla-li ještě akceptována
     */
    public GregorianCalendar getAcceptanceTime() {
        return acceptanceTime;
    }

    public void setAcceptanceTime(GregorianCalendar acceptanceTime) {
        this.acceptanceTime = acceptanceTime;
    }

    /*
     * Čas, kdy byla zpráva přijata nebo null, nebyla-li ještě přijata.
     */
    public GregorianCalendar getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(GregorianCalendar deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    @Override
    public String toString() {
        return String.format("odesilatel:%s prijemce:%s id zpravy:%s predmet zpravy:%s",
                sender.getIdentity(), recipient.getIdentity(), id, annotation);
    }
}
