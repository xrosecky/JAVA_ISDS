package cz.abclinuxu.datoveschranky.common.interfaces;

import cz.abclinuxu.datoveschranky.common.entities.Message;

/**
 * Služba zodpovědná za odesílání zpráv.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface DataBoxUploadService {

    /*
     * Odešle zprávu.
     * 
     * @param mess   zpráva k odeslání
     */ 
    public void sendMessage(Message mess);
    
}
