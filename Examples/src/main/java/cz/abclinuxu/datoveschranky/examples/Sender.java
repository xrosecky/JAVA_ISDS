package cz.abclinuxu.datoveschranky.examples;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.impl.ByteArrayAttachmentStorer;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxEnvironment;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.impl.Authentication;
import cz.abclinuxu.datoveschranky.impl.BasicAuthentication;
import cz.abclinuxu.datoveschranky.impl.DataBoxManager;
import cz.abclinuxu.datoveschranky.impl.MessageValidator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author xrosecky
 */
public class Sender {

    protected final DataBoxManager manager;
    protected final String receiverId;
    protected final String directory;
    protected final MessageValidator validator;
    private static final int LIMIT = 100;

    public Sender(DataBoxManager manager, String receiverId, String directory) {
	this.receiverId = receiverId;
	this.manager = manager;
	this.directory = directory;
	this.validator = new MessageValidator();
    }

    public void send() throws Exception {
	File dir = new File(directory);
	String[] children = dir.list();
	if (children != null) {
	    int count = 0;
	    for (String child : children) {
		count++;
		if (count > LIMIT) {
		    break;
		}
		String fileName = directory + File.separator + child;
		File input = new File(fileName);
		byte[] content = getBytesFromFile(input);
		System.out.format("sending: %s\n", input.getCanonicalPath());
		try {
		    Message message = validator.createMessage(content, new ByteArrayAttachmentStorer());
		    message.getEnvelope().setRecipient(new DataBox(receiverId));
		    manager.getDataBoxUploadService().sendMessage(message);
		} catch (Exception e) {
		    System.out.format("skipping: %s because of: %s\n", input.getCanonicalPath(), e.getMessage());
		}
	    }
	} else {
	    throw new IllegalArgumentException(String.format("%s is not a directory", directory));
	}
    }

    public static void main(String[] args) throws Exception {
	String directory = args[1];
	String propertiesFile = args[0];
	Properties props = new Properties();
	FileInputStream fis = new FileInputStream(propertiesFile);
	props.load(fis);
	fis.close();
	String login = props.getProperty("sender.login");
	String passwd = props.getProperty("sender.password");
	String receiverId = props.getProperty("receiver.id");
	Config config = new Config(DataBoxEnvironment.TEST);
	Authentication auth = new BasicAuthentication(config, login, passwd);
	DataBoxManager manager = new DataBoxManager(config, auth);
	Sender sender = new Sender(manager, receiverId, directory);
	sender.send();
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
	InputStream is = new FileInputStream(file);
	long length = file.length();
	byte[] bytes = new byte[(int) length];
	int offset = 0;
	int numRead = 0;
	while (offset < bytes.length
		&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
	    offset += numRead;
	}
	if (offset < bytes.length) {
	    throw new IOException("Could not completely read file " + file.getName());
	}
	is.close();
	return bytes;
    }
}
