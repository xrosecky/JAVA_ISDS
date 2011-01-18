package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.impl.Authentication;
import cz.abclinuxu.datoveschranky.impl.BasicAuthentication;
import cz.abclinuxu.datoveschranky.impl.ClientCertAuthentication;
import cz.abclinuxu.datoveschranky.impl.DataBoxEnvironment;
import cz.abclinuxu.datoveschranky.impl.DataBoxManager;
import java.io.File;
import org.junit.Test;

/**
 *
 * @author xrosecky
 */
public class TestHelper {

    public static final String login = "5e7mvf";
    public static final String passwd = "Ab123456b";
    public static final File certFile = new File("/path/to/your/certificate.p12");
    public static final String certPassword = "your_client_cert_password";
    public static final Config config = new Config(DataBoxEnvironment.TEST);

    public static DataBoxServices connect() throws Exception {
        return connectBasicAuth();
    }

    public static DataBoxServices connectClientCert() throws Exception {
        Authentication auth = new ClientCertAuthentication(config, certFile, certPassword);
        return new DataBoxManager(config, auth);
    }
    
    public static DataBoxServices connectBasicAuth() throws Exception {
        Authentication auth = new BasicAuthentication(config, login, passwd);
        return new DataBoxManager(config, auth);
    }

    @Test 
    public void testConnect() throws Exception {
        TestHelper.connect();
    }
}
