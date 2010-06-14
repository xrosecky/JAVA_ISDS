package cz.abclinuxu.datoveschranky.common.entities;

import java.util.EnumSet;

public enum DataBoxType {
	 OVM  ("OVM", 10),
	 PO  ("PO", 20),
	 PFO  ("PFO", 30),
	 FO  ("PFO", 40);
	 
    private final String name;
    private final int value;

    DataBoxType(String name, int value){
    	this.name = name;
    	this.value = value;
    }
    
    
    public static DataBoxType valueOf(int val) {
        for (DataBoxType ms : EnumSet.allOf(DataBoxType.class)) {
            if (ms.value == val) {
                return ms;
            }
        }
        throw new IllegalArgumentException(String.format("Číslo %d není povolený číselný typ datové schránky.", val));
    }

    public static DataBoxType valueOfByName(String name) {
        for (DataBoxType ms : EnumSet.allOf(DataBoxType.class)) {
            if (ms.name.equals(name)) {
                return ms;
            }
        }
        throw new IllegalArgumentException(String.format("Název %s není povolený název typ datové schránky.", name));
    }

    @Override
    public String toString() {
        return name;
    }
}
