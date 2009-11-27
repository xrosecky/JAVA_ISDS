package cz.abclinuxu.datoveschranky.common.entities;

import java.util.Arrays;
import java.util.List;

/**
 * Pomocná třída pro validaci
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class Validator {
    
    private static List<String> enabledMetaTypes = Arrays.asList("main", "enclosure", "signature", "meta");
    
    private Validator() {
    }
    
    static public void assertNotNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
    
    static public void assertValidMessageID(String id) {
    }
    
    static public void assertValidDataBoxID(String id) {
    }
    
    static public void assertValidMessageAnnotation(String annotation) {
        if (annotation.length() > 255) {
            throw new IllegalArgumentException("Předmět zprávy smí obsahovat nejvýše 255 znaků.");
        }
    }
    
    /**
     * Zvaliduje odesílanou zprávu proti pravidlům definovaným v dokumentaci k
     * ISDS. Jelikož přijaté zprávy mohou tato pravidla porušovat, o validaci
     * se stará tento kód při odesílání zprávy a ne jednotlivé gettery a 
     * settery u příslušných tříd. 
     * 
     * Tato metoda sice může odmítnout zprávy, které by rozhrání ISDS přijalo bez
     * problémů, ale držíme se striktně dokumentace k ISDS. Příkladem je např.
     * druh písemnosti (meta type), který může nabývat jakékoliv hodnoty a ISDS
     * jej přijme, ačkoliv v dokumentaci jsou povoleny pouze typy main, enclosure,
     * signature a meta. 
     * 
     * @param message   zpráva k validaci
     * @throws IllegalArgumentException   pokud zpráva tato pravidla nerespektuje
     * 
     */
    static public void assertValidMessageForSending(Message message) {
        MessageEnvelope env = message.getEnvelope();
        if (env.getRecipient() == null) {
            throw new IllegalArgumentException("Zprava nema prijemce.");
        }
        if (env.getRecipient().getdataBoxID() == null) {
            throw new IllegalArgumentException("ID prijemce zpravy je null.");
        }
        List<Attachment> attachments = message.getAttachments();
        if (attachments == null || attachments.size() == 0) {
            throw new IllegalArgumentException("Zprava musi obsahovat alespon jednu prilohu.");
        }
        Attachment first = attachments.get(0);
        if (!first.getMetaType().equals("main")) {
            throw new IllegalArgumentException(String.format("Druh (metatype) prvni pisemnosti "
                    +" v prilohach musí být main, tady je %s.", first.getMetaType()));
        }
        for (Attachment attach : attachments) {
            if (!enabledMetaTypes.contains(attach.getMetaType())) {
                throw new IllegalArgumentException(String.format("%s není povoleny " +
                        "druh pisemnosti (metatype). Povolen jsou: %s.", attach.getMetaType(),
                        enabledMetaTypes.toString()));
            }
            if (attach.getDescription() == null) {
                throw new IllegalArgumentException("Popis přílohy je null");
            }
            if (attach.getMimeType() == null) {
                throw new IllegalArgumentException("Příloha nemá vyplněný MIME type.");
            }
        }
    }
    
}
