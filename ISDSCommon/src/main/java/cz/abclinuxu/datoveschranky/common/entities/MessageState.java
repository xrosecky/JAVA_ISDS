package cz.abclinuxu.datoveschranky.common.entities;

import java.lang.String;
import java.util.EnumSet;

/**
 *
 * Reprezentuje stav zprávy.
 * 
 * @author xrosecky
 */
public enum MessageState {

    SUBMITTED  ("podána", 1, false),
    TIMESTAMPED ("opatřena časovým razítkem", 2, false),
    VIRUS_FOUND ("neprošla antivirovou kontrolou", 3, false),
    DELIVERED ("dodána do ISDS", 4, true),
    DELIVERED_BY_FICTION ("doručena fikcí", 5, true),
    DELIVERED_BY_LOGIN ("doručena přihlášením", 6, true),
    READ ("přečtena", 7, true),
    UNDELIVERABLE ("nedoručitelná, DS byla zpětně znepřístupněna", 8, false),
    DELETED ("smazána", 9, false),
    IN_DEPOSITORY ("v datovém trezoru", 10, true);
    
    private final String message;
    private final int value;
    private boolean download;

    MessageState(String mess, int val, boolean download) {
        this.message = mess;
        this.value = val;
	this.download = download;
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

    public boolean canBeDownloaded() {
	return download;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return message;
    }
    
    
}
