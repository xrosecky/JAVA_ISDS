package cz.abclinuxu.datoveschranky.common.interfaces;

/**
 *
 * Zpřístupňuje služby pro datovou schránku
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface DataBoxServices {

    /*
     * Vrátí službu zodpovědnou za stahování celých zpráv, tzn. včetně příloh.
     */
    public DataBoxDownloadService getDataBoxDownloadService();
    
    /*
     * Vrátí službu zodpovědnou za stahování zpráv.
     */
    public DataBoxUploadService getDataBoxUploadService();
    
    /*
     * Vrátí službu zodpovědnou za práci se zpráv mimo stahování, např.
     * získání seznamu přijatých zpráv.
     */
    public DataBoxMessagesService getDataBoxMessagesService();
    
    public DataBoxSearchService getDataBoxSearchService();
    
}
