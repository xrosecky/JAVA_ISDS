package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.content.ByteContent;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.impl.DataBoxManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author xrosecky
 */
public class MessageUploadTest {

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
}

