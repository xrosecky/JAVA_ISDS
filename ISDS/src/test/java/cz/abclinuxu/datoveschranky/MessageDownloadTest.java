package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.content.ByteContent;
import cz.abclinuxu.datoveschranky.common.impl.ByteArrayAttachmentStorer;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxDownloadService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.impl.DataBoxManager;
import cz.abclinuxu.datoveschranky.impl.MessageValidator;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author user
 */
public class BasicTest {

    private static final String loginName = "5s59sd";
    private static final String password = "Ab123456";
    private static Config config = null;
    private static DataBoxServices services = null;
    private static DataBoxMessagesService messagesService = null;
    private static DataBoxDownloadService downloader = null;
    private static GregorianCalendar begin = null;
    private static GregorianCalendar end = null;

    public BasicTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        config = new Config(Config.TEST_URL);
        services = DataBoxManager.login(config, loginName, password);
        messagesService = services.getDataBoxMessagesService();
        downloader = services.getDataBoxDownloadService();
        begin = new GregorianCalendar();
        begin.roll(Calendar.DAY_OF_YEAR, -28);
        end = new GregorianCalendar();
        end.roll(Calendar.DAY_OF_YEAR, 1);
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
    public void testSendMessage() throws Exception {
        DataBoxUploadService uploadService = services.getDataBoxUploadService();
        for (String recipientID : Arrays.asList("p36ab6k", "vqbab52", "hjyaavk")) {
            MessageEnvelope env = new MessageEnvelope();
            env.setRecipient(new DataBox(recipientID));
            env.setAnnotation("Óda_na_příliš_žluťoučkého_koně");
            List<Attachment> attachments = new ArrayList<Attachment>();
            // prvni priloha
            Attachment attach1 = new Attachment();
            attach1.setDescription("ahoj.txt");
            attach1.setMetaType("main");
            attach1.setMimeType("txt");
            attach1.setContents(new ByteContent("Vanoce jsou svatky klidu".getBytes("UTF-8")));
            attachments.add(attach1);
            // druha priloha
            Attachment attach2 = new Attachment();
            attach2.setDescription("Óda_na_příliš_žluťoučkého_koně.txt");
            attach2.setMetaType("enclosure");
            attach2.setMimeType("txt");
            attach2.setContents(new ByteContent("Příliš žluťoučký kůň úpěl ďábelské ódy.".getBytes("UTF-8")));
            attachments.add(attach2);
            // a ted ji poslem
            Message message = new Message(env, null, null, attachments);
            uploadService.sendMessage(message);
        }
    }

    @Test
    public void testGetListOfSentMessages() throws Exception {
        List<MessageEnvelope> messages = messagesService.getListOfSentMessages(begin, end, 0, 0);
        Assert.assertTrue(messages.size() == 0);
        messages = messagesService.getListOfSentMessages(begin, end, 0, 5);
        Assert.assertTrue(messages.size() == 5);
        for (MessageEnvelope mess : messages) {
            Assert.assertTrue(mess.getType().equals(MessageType.SENT));
        }
    }

    @Test
    public void testGetListOfReceivedMessages() throws Exception {
        List<MessageEnvelope> messages = messagesService.getListOfReceivedMessages(begin, end, 0, 0);
        Assert.assertTrue(messages.size() == 0);
        messages = messagesService.getListOfReceivedMessages(begin, end, 0, 5);
        Assert.assertTrue(messages.size() == 5);
        for (MessageEnvelope mess : messages) {
            Assert.assertTrue(mess.getType().equals(MessageType.RECEIVED));
        }
    }

    @Test
    public void testIntegrityOfSentMessages() throws Exception {
        List<MessageEnvelope> messages = messagesService.getListOfSentMessages(begin, end, 0, 15);
        for (MessageEnvelope mess : messages) {
            testIntegrity(mess);
        }
    }

    @Test
    public void testIntegrityOfReceivedMessages() throws Exception {
        List<MessageEnvelope> messages = messagesService.getListOfReceivedMessages(begin, end, 0, 15);
        for (MessageEnvelope mess : messages) {
            testIntegrity(mess);
        }
    }

    private void testIntegrity(MessageEnvelope envelope) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            downloader.downloadSignedMessage(envelope, os);
        } finally {
            os.close();
        }
        MessageValidator validator = new MessageValidator();
        Message mess = validator.validateAndBuildMessage(os.toByteArray(), new ByteArrayAttachmentStorer());
    }
}
