package cz.abclinuxu.datoveschranky.common.interfaces;

/**
 * 
 * Zpřístupňuje služby pro datovou schránku
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface DataBoxServices {

	/**
	 * Vrátí službu zodpovědnou za stahování celých zpráv včetně příloh.
	 */
	public DataBoxDownloadService getDataBoxDownloadService();

	/**
	 * Vrátí službu zodpovědnou za odesílání zpráv.
	 */
	public DataBoxUploadService getDataBoxUploadService();

	/**
	 * Vrátí službu zodpovědnou za práci se zprávami mimo stahování zpráv včetně
	 * příloh a jejich odesílání.
	 * 
	 */
	public DataBoxMessagesService getDataBoxMessagesService();

	/**
	 * Vrátí služby pro hledávání v datových schránkách.
	 */
	public DataBoxSearchService getDataBoxSearchService();

	public DataBoxAccessService getDataBoxAccessService();

}
