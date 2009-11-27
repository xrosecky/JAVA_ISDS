package cz.abclinuxu.datoveschranky.common.entities;

import java.util.EnumSet;

/**
 *
 * Reprezentuje stav zprávy.
 * 
 * @author xrosecky
 */
public enum MessageState {

    SUBMITTED  ("podána", 1),
    TIMESTAMPED ("opatřena časovým razítkem", 2),
    VIRUS_FOUND ("neprošla antivirovou kontrolou", 3),
    DELIVERED ("dodána do ISDS", 4),
    DELIVERED_BY_FICTION ("doručena fikcí", 5),
    DELIVERED_BY_LOGIN ("doručena přihlášením", 6),
    READ ("přečtena", 7),
    UNDELIVERABLE ("nedoručitelná, DS byla zpětně znepřístupněna", 8),
    DELETED ("smazána", 9),
    IN_DEPOSITORY ("v datovém trezoru", 10);
    
    private final String message;
    private final int value;
    
    MessageState(String mess, int val) {
        this.message = mess;
        this.value = val;
    }
    
    public static MessageState valueOf(int val) {
        for (MessageState ms : EnumSet.allOf(MessageState.class)) {
            if (ms.value == val) {
                return ms;
            }
        }
        throw new IllegalArgumentException(String.format("Číslo %d není povolený "+
                "číselný stav zprávy.", val));
    }
    
    public static int toInt(EnumSet<MessageState> states) {
        if (states == null || states.size() == 0) {
            return -1;
        }
        int result = 0;
        for (MessageState state : states) {
            result |= (1 << state.value);
        }
        return result;
    }
    
    @Override
    public String toString() {
        return message;
    }
    
    
}
