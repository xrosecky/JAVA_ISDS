package cz.abclinuxu.datoveschranky.common.entities;

import java.util.EnumSet;

public enum DataBoxType {
	SYSTEM("SYSTEM", 0), // System datovych zprav - např. uvítací zpráva do
							// systému
	OVM("OVM", 10), // DS orgánu veřejné moci
	OVM_NOTAR("OVM_NOTAR", 11), // DS notáře
	OVM_EXEKUT("OVM_EXEKUT", 12), // DS soudního exekutora
	OVM_REQ("OVM_REQ", 13), // podřízená DS typu OVM vzniklá na žádost (§6 a 7)
	OVM_FO("OVM_FO", 14), OVM_PFO("OVM_PFO", 15), OVM_PO("OVM_PO", 16), PO(
			"PO", 20), // DS právnické osoby (z
						// obchodního rejstříku)
	PO_ZAK("PO_ZAK", 21), // DS PO zřízené zákonem
	PO_REQ("PO_REQ", 22), // DS PO, vzniklá na žádost dle §5 odst. 2
	PFO("PFO", 30), // DS podnikající fyzické osoby
	PFO_ADVOK("PFO_ADVOK", 31), // DS advokáta (od 1. 7. 2012)
	PFO_DANPOR("PFO_DANPOR", 32), // DS daňového poradce (od 1. 7. 2012)
	PFO_INSSPR("PFO_INSSPR", 33), // DS insolvenčního správce
	PFO_AUDITOR("PFO_AUDITOR", 34),
	PFO_ZNALEC("PFO_ZNALEC", 35),
	PFO_TLUMOCNIK("PFO_TLUMOCNIK", 36),
	FO("FO", 40), // DS fyzické osoby,
	PFO_REQ("PFO_REQ", 50), TEST("TEST", 60);

	private final String name;
	private final int value;

	DataBoxType(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public static DataBoxType valueOf(int val) {
		for (DataBoxType ms : EnumSet.allOf(DataBoxType.class)) {
			if (ms.value == val) {
				return ms;
			}
		}
		throw new IllegalArgumentException(String.format(
				"Číslo %d není povolený číselný typ datové schránky.", val));
	}

	public static DataBoxType valueOfByName(String name) {
		for (DataBoxType ms : EnumSet.allOf(DataBoxType.class)) {
			if (ms.name.equals(name)) {
				return ms;
			}
		}
		throw new IllegalArgumentException(String.format(
				"Název %s není povolený název typ datové schránky.", name));
	}

	public int value() {
		return value;
	}

	@Override
	public String toString() {
		return name;
	}
}
