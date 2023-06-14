package cz.abclinuxu.datoveschranky.common.interfaces;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Rozhraní, které určuje, kam se budou přílohy ukládat (do souboru, do paměti,
 * ...)
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface AttachmentStorer {

	/**
	 * Vráti výstupní proud, do kterého bude uložen obsah přílohy a u třídy
	 * attachment příslušně nastaví obsah zprávy metodou setContent. O uzavření
	 * výstupního proudu se postará volající třída.
	 * 
	 * @param envelope
	 *            obálka zprávy
	 * @param attachment
	 *            příloha zprávy, u které je nastaven popis zprávy, druh
	 *            písemnosti a MIME typ a povinností této třídy je nastavit
	 *            atribut content metodou setContent tak, aby volání metody
	 *            getContent vrátilo zapsaný obsah.
	 * 
	 */
	public OutputStream store(MessageEnvelope envelope, Attachment attachment)
			throws IOException;

}
