package cz.abclinuxu.datoveschranky.common.interfaces;

import cz.abclinuxu.datoveschranky.common.entities.DeliveryInfo;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.entities.MessageStateChange;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import java.io.OutputStream;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * Získání seznamu přijatých, odeslaných zpráv, ověření haše zprávy.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface DataBoxMessagesService {

    /**
     * Stáhne seznam přijatých zpráv ve formě obálky (identifikace odesílatele,
     * příjemce, předmět zprávy, bez příloh) v daném časovém rozmezí.
     * 
     * @param from    čas dodání do ISDS od
     * @param to      čas dodání do ISDS do
     * @param filter  filter na stav zpráv, null nebo prázdná množina vybere
     *     všechny zprávy, v opačném případě se vyberou zprávy se stavem,
     *     který je obsažen v množině filter. 
     * @param offset  číslo první požadavané obálky (indexuje se od jedne!!!)
     * @param limit   počet vrácených obálek
     * @throws DataBoxException při vracení seznamu nastala chyba
     * 
     */    
    public List<MessageEnvelope> getListOfReceivedMessages(Date from,
            Date to, EnumSet<MessageState> state, int offset, int limit);
    
    /**
     * Stáhne seznam odeslaných zpráv ve formě obálky (identifikace odesílatele,
     * příjemce, předmět zprávy, bez příloh) v daném časovém rozmezí.
     * 
     * @param from    čas dodání do ISDS od
     * @param to      čas dodání do ISDS do
     * @param filter  filter na stav zpráv, null nebo prázdná množina vybere
     *     všechny zprávy, v opačném případě se vyberou zprávy se stavem,
     *     který je obsažen v množině filter. 
     * @param offset  číslo první požadavané obálky (indexuje se od jedne!!!)
     * @param limit   počet vrácených obálek
     * @throws DataBoxException při vracení seznamu nastala chyba
     * 
     */    
    public List<MessageEnvelope> getListOfSentMessages(Date from,
            Date to, EnumSet<MessageState> filter, int offset, int limit);
    
    /**
     * Vrátí hodnotu haše dané zprávy, který je uložený v datové schránce. Tento
     * haš lze pak použít pro porovnání s hašem zprávy, která je uložená na
     * disku.
     * 
     * @param envelope  obálka zprávy u které chceme znát has
     * @throws DataBoxException
     */
    public Hash verifyMessage(MessageEnvelope envelope);
    
    /**
     * Vrátí doručenku pro danou obálku zprávy.
     * 
     */
    public DeliveryInfo getDeliveryInfo(MessageEnvelope env);
    
    /**
     * Označí danou zprávu jako přečtenou.
     * 
     */
    public void markMessageAsDownloaded(MessageEnvelope env);

    /**
     * Stáhne doručenku zprávy ve formátu PKCS7 a uloží ji do výstupního
     * proudu. Tento formát je vhodný pro zálohování. PKCS7 je binární formát,
     * žádné XML ani zfo soubor. Po odstranění podpisu je výsledek XML.
     *
     * @param envelope  obálka zprávy, ke které má stáhnout přílohy
     * @param os        kam se má uložit
     * @throws DataBoxException
     */
    public void getSignedDeliveryInfo(MessageEnvelope envelope, OutputStream os);

    /**
     * Stáhne změny stavu u odeslaných zpráv.
     *
     * @param from  od
     * @param to    do
     * @return seznam změn
     */
    public List<MessageStateChange> GetMessageStateChanges(Date from, Date to);
            
}
