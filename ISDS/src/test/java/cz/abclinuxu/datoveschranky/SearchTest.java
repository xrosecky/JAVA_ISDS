package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.entities.Address;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxType;
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

    private static TestHelper helper = new TestHelper();
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        services = helper.connectAsOVM();
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

    @Test
    public void searchExtended() {
	// podle jmena
	DataBoxWithDetails db1 = new DataBoxWithDetails();
	db1.setIdentity("min");
	dump(services.getDataBoxSearchService().find(DataBoxType.OVM, db1));
	// podle jmena a mesta
	DataBoxWithDetails db2 = new DataBoxWithDetails();
	db2.setAddressDetails(new Address());
	db2.getAddressDetails().setCity("Praha");
	db2.setIdentity("SMS exekutor");
	dump(services.getDataBoxSearchService().find(DataBoxType.OVM_EXEKUT, db2));
	// podle jmena a mesta
	DataBoxWithDetails db3 = new DataBoxWithDetails();
	db3.setAddressDetails(new Address());
	db3.getAddressDetails().setCity("Brno");
	db3.setIdentity("SMS exekutor");
	dump(services.getDataBoxSearchService().find(DataBoxType.OVM_EXEKUT, db3));
	// test vyhledani PO
	DataBoxWithDetails db4 = new DataBoxWithDetails();
	db4.setAddressDetails(new Address());
	db4.getAddressDetails().setCity("Ústí nad Labem");
	db4.setIdentity("FONTANA");
	dump(services.getDataBoxSearchService().find(DataBoxType.PO, db4));
	// test hledani podle IC
	DataBoxWithDetails db5 = new DataBoxWithDetails();
	db5.setIC("31342183 ");
	dump(services.getDataBoxSearchService().find(DataBoxType.OVM, db5));
    }

    private void dump(List<DataBoxWithDetails> dbs) {
	System.out.println("========================================");
	for (DataBoxWithDetails db : dbs) {
	    System.out.println(db);
	}
	System.out.println("========================================");
    }
}
