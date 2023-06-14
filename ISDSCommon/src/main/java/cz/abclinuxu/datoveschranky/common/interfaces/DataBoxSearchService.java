package cz.abclinuxu.datoveschranky.common.interfaces;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxQuery;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxSearchResult;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxState;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxType;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxWithDetails;
import cz.abclinuxu.datoveschranky.common.entities.SearchResult;

import java.util.List;

/**
 * Třída pro vyhledávání datových schránek a ověřování jejich stavu.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface DataBoxSearchService {

	/**
	 * Vráti stav schránky.
	 * 
	 * @param db
	 *            datová schránka
	 */
	public DataBoxState checkDataBox(DataBox db);

	/**
	 * Vrátí datovou schránku s daným id pokud existuje, v opačném případě vrátí
	 * null
	 * 
	 * @param id
	 *            ID datové schránky
	 */
	public DataBoxWithDetails findDataBoxByID(String id);

	/**
	 * Vrátí seznam datových schránek OVM, jejichž jméno začína na prefix.
	 * 
	 * @param prefix
	 *            prefix jména OVM
	 */
	public List<DataBoxWithDetails> findOVMsByName(String prefix);

	/**
	 * Vrátí seznam datových schránek OVM, jež vyhovují dotazu.
	 * 
	 * @param type
	 *            typ schránky (povinný atribut)
	 * @param what
	 *            co se má hledat (hledá se podle atributů, co nejsou null, více
	 *            dokumentace k ISDS)
	 */
	public List<DataBoxWithDetails> find(DataBoxType type,
			DataBoxWithDetails what);

	public DataBoxSearchResult find(DataBoxQuery query);

	/**
	 * Vrátí seznam datových schránek OVM, jež vyhovují dotazu.
	 * 
	 * @param what
	 *            co se má hledat (hledá se podle atributů, co nejsou null, více
	 *            dokumentace k ISDS)
	 */
	public SearchResult find(DataBoxWithDetails what);

}
