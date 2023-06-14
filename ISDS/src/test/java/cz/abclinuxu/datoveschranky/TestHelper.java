package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.impl.Authentication;
import cz.abclinuxu.datoveschranky.impl.BasicAuthentication;
import cz.abclinuxu.datoveschranky.impl.ClientCertAuthentication;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxEnvironment;
import cz.abclinuxu.datoveschranky.impl.DataBoxManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import org.junit.Test;

/**
 * 
 * @author xrosecky
 */
public class TestHelper {

	/*
	 * public static final String login = "5e7mvf"; public static final String
	 * passwd = "Ab123456b"; public static final File certFile = new
	 * File("/path/to/your/certificate.p12"); public static final String
	 * certPassword = "your_client_cert_password"; public static final Config
	 * config = new Config(DataBoxEnvironment.TEST);
	 */
	private final Properties properties;
	private final Config config = new Config(DataBoxEnvironment.TEST);

	// public static final Properties properties = new
	// Properties(TestHelper.class.getClassLoader().getSystemResourceAsStream("configuration.properties"));
	public TestHelper() {
		InputStream is = this.getClass().getResourceAsStream(
				"/configuration.properties");
		properties = new Properties();
		try {
			properties.load(is);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public DataBoxServices connectAsOVM() throws Exception {
		return connectBasicAuthAsOVM();
	}

	public DataBoxServices connectAsFO() throws Exception {
		return connectBasicAuthAsFO();
	}

	private DataBoxServices connectClientCertAsOVM() throws Exception {
		File certFile = null;
		String certPassword = null;
		Config config = new Config(DataBoxEnvironment.TEST);
		Authentication auth = new ClientCertAuthentication(config, certFile,
				certPassword);
		return new DataBoxManager(config, auth);
	}

	private DataBoxServices connectBasicAuthAsOVM() throws Exception {
		Config config = new Config(DataBoxEnvironment.TEST);
		String login = properties.getProperty("ovm.login");
		String passwd = properties.getProperty("ovm.password");
		Authentication auth = new BasicAuthentication(config, login, passwd);
		return new DataBoxManager(config, auth);
	}

	private DataBoxServices connectBasicAuthAsFO() throws Exception {
		Config config = new Config(DataBoxEnvironment.TEST);
		String login = properties.getProperty("fo.login");
		String passwd = properties.getProperty("fo.password");
		Authentication auth = new BasicAuthentication(config, login, passwd);
		return new DataBoxManager(config, auth);
	}

	public Properties getProperties() {
		return properties;
	}

	@Test
	public void testConnect() throws Exception {
		TestHelper helper = new TestHelper();
		helper.connectAsOVM();
	}

	public Config getConfig() {
		return config;
	}

}
