package cz.abclinuxu.datoveschranky.common.interfaces;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxState;
import java.util.List;

/**
 *
 * @author xrosecky
 */
public interface DataBoxSearchService {

    DataBoxState checkDataBox(DataBox db);

    DataBox findDataBoxByID(String id);

    List<DataBox> findOVMsByName(String prefix);

}
