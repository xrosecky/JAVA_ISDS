package cz.abclinuxu.datoveschranky.common.interfaces;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.BigMessage;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.UploadedAttachment;

/**
 * Služba zodpovědná za odesílání zpráv.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface DataBoxUploadService {

	/**
	 * Odešle zprávu. V případě jakékoliv chyby vyhodí vyjímku.
	 * 
	 * @param mess
	 *            zpráva k odeslání
	 */
	public void sendMessage(Message mess);

	public void sendBigMessage(BigMessage message);

	public UploadedAttachment uploadAttachment(Attachment attachment);

}
