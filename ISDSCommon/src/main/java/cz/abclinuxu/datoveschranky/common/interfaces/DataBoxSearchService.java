package cz.abclinuxu.datoveschranky.common.interfaces;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxState;
import java.util.List;

/**
 * Třída pro vyhledávání datových schránek a ověřování jejich stavu.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface DataBoxSearchService {

    /*
     * Vráti stav schránky.
     * 
     * @param db  datová schránka
     */
    public DataBoxState checkDataBox(DataBox db);

    /*
     * Vrátí datovou schránku s daným id pokud existuje, v opačném případě 
     * vrátí null
     * 
     * @param id  ID datové schránky
     */
    public DataBox findDataBoxByID(String id);

    /*
     * Vrátí seznam datových schránek OVM, jejichž jméno začína na prefix.
     * 
     * @param prefix  prefix jména OVM
     */
    public List<DataBox> findOVMsByName(String prefix);

}
