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
public class MessageDownloadTest {

    private static DataBoxServices services = null;
    private static DataBoxMessagesService messagesService = null;
    private static DataBoxDownloadService downloader = null;
    private static GregorianCalendar begin = null;
    private static GregorianCalendar end = null;

    public MessageDownloadTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        services = TestHelper.connect();
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
        List<MessageEnvelope> envelopes = messagesService.getListOfReceivedMessages(begin, end, 0, 15);
        for (MessageEnvelope env : envelopes) {
            Message mess1 = testIntegrity(env);
            Message mess2 = downloader.downloadMessage(env, new ByteArrayAttachmentStorer());
            List<Attachment> list1 = mess1.getAttachments();
            List<Attachment> list2 = mess2.getAttachments();
            Assert.assertEquals(list1.size(), list2.size());
            for (int i = 0; i!=list1.size(); i++) {
                byte[] bytes1 = ((ByteContent)list1.get(i).getContent()).getBytes();
                byte[] bytes2 = ((ByteContent)list2.get(i).getContent()).getBytes();
                Assert.assertTrue(Arrays.equals(bytes1, bytes2));
            }
        }
    }

    private Message testIntegrity(MessageEnvelope envelope) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            downloader.downloadSignedMessage(envelope, os);
        } finally {
            os.close();
        }
        MessageValidator validator = new MessageValidator();
        ByteContent content = new ByteContent(os.toByteArray());
        Message mess = validator.validateAndCreateMessage(content, new ByteArrayAttachmentStorer());
        return mess;
    }
}
