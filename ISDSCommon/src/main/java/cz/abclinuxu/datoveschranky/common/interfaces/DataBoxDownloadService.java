package cz.abclinuxu.datoveschranky.common.interfaces;

import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import java.io.OutputStream;

/**
 *
 * Stahování celých zpráv, tedy včetně příloh.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface DataBoxDownloadService {

    /*
     * Stáhne celou přijatou zprávu (i s přílohami) k dané obálce.
     * 
     * @param envelope  obálka zprávy, ke které má stáhnout přílohy
     * @param storer    určuje, jak se mají zprávy ukládat
     * @throws DataBoxException
     */
    public Message downloadMessage(MessageEnvelope envelope,
            AttachmentStorer storer);
    
     /*
     * Stáhne podepsanou zprávu ve formátu PKCS7 a uloží ji do výstupního
     * proudu. Tento formát je vhodný pro záholování. PKCS7 je binární formát,
     * žádné XML ani zfo soubor. Po odstranění podpisu je výsledek XML.
     * 
     * @param envelope  obálka zprávy, ke které má stáhnout přílohy
     * @param os        kam se má uložit
     * @throws DataBoxException
     */
    public void downloadSignedMessage(MessageEnvelope envelope, OutputStream os);
    
}
