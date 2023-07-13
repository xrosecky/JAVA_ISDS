package cz.abclinuxu.datoveschranky.common.entities;

public enum StatusCode {

	OK("0000", "Provedeno úspěšně", false),
	REQUEST_WAS_REGISTERED("0001", "Úspěšně zaznamenáno", false),
	EMPTY_SEARCH_RESULTS("0002", "Podmínkám neodpovídá žádná datová schránka", false),
	TOO_GENERIC_SEARCH_QUERY("0003", "Výběr zadán příliš obecně, výsledků je příliš mnoho", false),
	TIMESTAMP_COULD_NOT_BE_OBTAINED("0005", "Nepodařilo se získat časové razítko pro elektronickou pečeť MV (pečetění datové zprávy při jejím stažení)", true),
	PDZ_NOT_ALLOWED("0009", "Do této schránky není možno zaslat Poštovní datovou zprávu", false),
	SENDING_PDZ_NOT_ALLOWED("1232", "Odesilatel ani příjemce datové zprávy není OVM a příjemce nedovolil příjem Poštovních datových zpráv", false),
	SENDING_INITIATORY_PDZ_WITHOUT_CONTRACT_NOT_ALLOWED("1242", "Není možné zasílat iniciační datovou zprávu bez smlouvy s provozovatelem systému", false),
	REPLY_PDZ_EXPIRED("1234", "Nemáte možnost odesílat odpovědní zprávu do zvolené schránky s identifikátorem uvedeným v poli pro Vaše číslo jednací", false),
	ATTACHMENTS_FOR_BIG_MESSAGE_UNDER_MINIMAL_SIZE("1297", "Přílohy datové zprávy nedosahují minimální délky pro VODZ", false),
	ATTACHMENTS_SIZE_EXCEEDED("1296", "Přílohy datové zprávy přesahují povolenu délku", false),
	MESSAGE_TOO_BIG("1200", "Překročen limit na velikost datové zprávy", false),
	INFORMATION_STATES("0000", "INFORMATION_STATES", false),
	// generic errors
	VIOLATION_OF_APP_LOGIC_BY_A_CLIENT_REQ("1000-1999", " Narušení aplikační logiky klientským požadavkem", false),
	WS_OR_XML_ERRORS("2000-2999", "Chyby ve struktuře webové komunikace a v zadaných XML datech", false),
	SPECIAL_ERRORS("3000-3999", "Speciální chyby", true),
	FUNCTIONAL_FAILURE("9000-9999", "Funkční selhání některé komponenty ISDS nebo externího zdroje", true),
	UNKNOWN_ERROR("XXXX", "Neznámá chyba", false);

	private final String code;

	private final String message;

	private final boolean retry;

	private final boolean generic;

	private StatusCode(String code, String message, boolean retry) {
		this.code = code;
		this.message = message;
		this.retry = retry;
		this.generic = code.contains("-");
	}

	public String code() {
		return code;
	}

	public String message() {
		return message;
	}

	public boolean retry() {
		return retry;
	}

	public boolean isGeneric() {
		return generic;
	}

	public static StatusCode byCode(String code) {
		StatusCode result = UNKNOWN_ERROR;
		for (StatusCode errorCode : StatusCode.values()) {
			if (!errorCode.isGeneric() && errorCode.code.equals(code)) {
				result = errorCode;
				break;
			} else if (errorCode.isGeneric()) {
				String[] codes = errorCode.code().split("-");
				String min = codes[0];
				String max = codes[1];
				if (code.compareTo(min) >= 0 && code.compareTo(max) <= 0) {
					result = errorCode;
				}
			}
		}
		return result;
	}

}
