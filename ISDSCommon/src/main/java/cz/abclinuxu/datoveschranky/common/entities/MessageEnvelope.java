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

    private static final long serialVersionUID = 4L;
    private DataBox sender = null;
    private DocumentIdent senderIdent;
    private DataBox recipient = null;
    private DocumentIdent recipientIdent;
    private MessageType type = null;
    private MessageState state = null;
    private String id = null;
    private String annotation = null;
    private GregorianCalendar deliveryTime = null;
    private GregorianCalendar acceptanceTime = null;
    private String toHands;
    private LegalTitle legalTitle;
    private boolean personalDelivery;
    private boolean allowSubstDelivery;
    private String dmType = null;

    public MessageEnvelope() {
        this.type = MessageType.CREATED;
    }

    public MessageEnvelope(MessageType type, DataBox sender, DataBox recipient, String id, String annotation) {
        Validator.assertNotNull(type);
        Validator.assertNotNull(sender);
        Validator.assertNotNull(recipient);
        Validator.assertNotNull(id);
        Validator.assertValidMessageID(id);
        // Validator.assertNotNull(annotation); // annotation is optional
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.id = id;
        this.annotation = annotation;
    }

    public MessageEnvelope(MessageType type, DataBox sender, DataBox recipient, String id, String annotation, LegalTitle legalTitle, String toHands, boolean personalDelivery, boolean allowSubstDelivery) {
        this(type, sender, recipient, id, annotation);
        this.legalTitle = legalTitle;
        this.toHands = toHands;
        this.allowSubstDelivery = allowSubstDelivery;
        this.personalDelivery = personalDelivery;
    }

    /**
     * Vrátí předmět zprávy. 
     */
    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * Vrátí příjemce zprávy. 
     */
    public DataBox getRecipient() {
        return recipient;
    }

    public void setRecipient(DataBox recipient) {
        Validator.assertNotNull(recipient);
        this.recipient = recipient;
    }

    /**
     * Vrátí odesílatele zprávy.
     */
    public DataBox getSender() {
        return sender;
    }

    public void setSender(DataBox sender) {
        Validator.assertNotNull(sender);
        this.sender = sender;
    }

    /**
     * Vrátí jednoznačný identifikátor této zprávy.
     */
    public String getMessageID() {
        return id;
    }

    public void setMessageID(String newID) {
        Validator.assertNotNull(newID);
        this.id = newID;
    }

    /**
     * Vrátí typ zprávy (zpráva přijatá, odeslaná, vytvořená za účelem odesláni)
     */
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        Validator.assertNotNull(type);
        this.type = type;
    }

    public MessageState getState() {
        return state;
    }

    public void setState(MessageState state) {
        this.state = state;
    }

    public DocumentIdent getRecipientIdent() {
        return recipientIdent;
    }

    public void setRecipientIdent(DocumentIdent recipientIdent) {
        this.recipientIdent = recipientIdent;
    }

    public DocumentIdent getSenderIdent() {
        return senderIdent;
    }

    public void setSenderIdent(DocumentIdent senderIdent) {
        this.senderIdent = senderIdent;
    }

    /**
     * Čas, kdy byla zpráva akceptována nebo null, nebyla-li ještě akceptována
     */
    public GregorianCalendar getAcceptanceTime() {
        return acceptanceTime;
    }

    public void setAcceptanceTime(GregorianCalendar acceptanceTime) {
        this.acceptanceTime = acceptanceTime;
    }

    /**
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

    public String getDmType() {
        return dmType;
    }

    public void setDmType(String dmType) {
        this.dmType = dmType;
    }

    public boolean isAllowSubstDelivery() {
        return allowSubstDelivery;
    }

    public void setAllowSubstDelivery(boolean allowSubstDelivery) {
        this.allowSubstDelivery = allowSubstDelivery;
    }

    public LegalTitle getLegalTitle() {
        return legalTitle;
    }

    public void setLegalTitle(LegalTitle legalTitle) {
        this.legalTitle = legalTitle;
    }

    public boolean getPersonalDelivery() {
        return personalDelivery;
    }

    public void setPersonalDelivery(boolean personalDelivery) {
        this.personalDelivery = personalDelivery;
    }

    public String getToHands() {
        return toHands;
    }

    public void setToHands(String toHands) {
        this.toHands = toHands;
    }

}
