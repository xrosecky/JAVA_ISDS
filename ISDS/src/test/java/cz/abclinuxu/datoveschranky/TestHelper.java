package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.impl.DataBoxManager;
import org.junit.Test;

/**
 *
 * @author xrosecky
 */
public class TestHelper {

    public static final String login = "5e7mvf";
    public static final String passwd = "Ab123456b";
    public static final Config config = new Config(Config.TEST_URL);
    
    public static DataBoxServices connect() throws Exception {
        return DataBoxManager.login(config, login, passwd);
    }
    
    @Test 
    public void testConnect() throws Exception {
        TestHelper.connect();
    }
}
