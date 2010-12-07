package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.entities.DataBoxWithDetails;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author xrosecky
 */
public class SearchTest {

    private static DataBoxServices services = null;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        services = TestHelper.connect();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test 
    public void search() {
        List<DataBoxWithDetails> boxes1 = services.getDataBoxSearchService().findOVMsByName("min");
        Assert.assertTrue(boxes1.size() > 1);
        for (DataBoxWithDetails db : boxes1) {
            Assert.assertNotNull(db.getIC());
            Assert.assertNotNull(db.getAddressDetails().getCity());
            Assert.assertNotNull(db.getAddressDetails().getNumberInStreet());
            Assert.assertNotNull(db.getAddressDetails().getNumberInMunicipality());
            Assert.assertNotNull(db.getAddressDetails().getStreet());
            Assert.assertNotNull(db.getAddressDetails().getZipCode());
            Assert.assertNotNull(db.getAddressDetails().getState());
        }
        List<DataBoxWithDetails> boxes2 = services.getDataBoxSearchService().findOVMsByName("Ministerstvo nepravdy a lasky");
        Assert.assertTrue(boxes2.isEmpty());
    }
}
