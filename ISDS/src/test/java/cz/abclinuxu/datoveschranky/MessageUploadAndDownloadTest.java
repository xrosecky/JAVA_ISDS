package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.BigMessage;
import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DeliveryInfo;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.entities.MessageStateChange;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.UploadedAttachment;
import cz.abclinuxu.datoveschranky.common.entities.content.ByteContent;
import cz.abclinuxu.datoveschranky.common.impl.ByteArrayAttachmentStorer;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.impl.MessageValidator;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumSet;
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
 * @author xrosecky
 */
public class MessageUploadAndDownloadTest {

	// private static DataBoxServices services = null;
	private static TestHelper helper = new TestHelper();
	private static GregorianCalendar begin = null;
	private static GregorianCalendar end = null;

	@BeforeClass
	public static void setUpClass() throws Exception {
		begin = new GregorianCalendar();
		begin.roll(Calendar.DAY_OF_YEAR, -10);
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
	public void testSendMessageAsOVM() throws Exception {
		DataBoxServices services = helper.connectAsOVM();
		String recipientID = helper.getProperties().getProperty("fo.id");
		testSendMessage(services, recipientID);
	}

	@Test
	public void testSendMessageAsFO() throws Exception {
		DataBoxServices services = helper.connectAsFO();
		String recipientID = helper.getProperties().getProperty("ovm.id");
		testSendMessage(services, recipientID);
	}

	//@Test
	public void testSendBigMessageAsFO() throws Exception {
		DataBoxServices services = helper.connectAsFO();
		String recipientID = helper.getProperties().getProperty("ovm.id");
		testSendBigMessage(services, recipientID);
	}

	@Test
	public void testGetListOfSentMessages() throws Exception {
		DataBoxServices services = helper.connectAsFO();
		testGetListOfSentMessages(services);
	}

	@Test
	public void testGetListOfReceivedtMessages() throws Exception {
		DataBoxServices services = helper.connectAsFO();
		testGetListOfReceivedMessages(services);
	}

	@Test
	public void testSignedDeliveryInfo() throws Exception {
		DataBoxServices services = helper.connectAsFO();
		List<MessageEnvelope> messages = services.getDataBoxMessagesService()
				.getListOfSentMessages(begin.getTime(), end.getTime(), null, 0,
						5);
		for (MessageEnvelope env : messages) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			services.getDataBoxMessagesService()
					.getSignedDeliveryInfo(env, bos);
			MessageValidator validator = new MessageValidator();
			DeliveryInfo delivery = validator.createDeliveryInfo(bos
					.toByteArray());
			assert (delivery.getHash() != null);
		}
	}

	@Test
	public void testIntegrityOfSentMessages() throws Exception {
		DataBoxServices services = helper.connectAsFO();
		testIntegrityOfSentMessages(services);
	}

	@Test
	public void testIntegrityOfReceivedMessages() throws Exception {
		DataBoxServices services = helper.connectAsFO();
		testIntegrityOfReceivedMessages(services);
	}

	private void testSendMessage(DataBoxServices services, String recipientID)
			throws Exception {
		DataBoxUploadService uploadService = services.getDataBoxUploadService();
		MessageEnvelope env = new MessageEnvelope();
		env.setRecipient(new DataBox(recipientID));
		env.setAnnotation("Óda_na_příliš_žluťoučkého_koně");
		List<Attachment> attachments = new ArrayList<Attachment>();
		// prvni priloha
		String prolog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		Attachment attach1 = new Attachment();
		attach1.setDescription("ahoj.xml");
		attach1.setMetaType("main");
		// attach1.setMimeType("text/xml");
		attach1.setContents(new ByteContent(
				(prolog + "<text>Vanoce jsou svatky klidu</text>")
						.getBytes("UTF-8")));
		attachments.add(attach1);
		// druha priloha
		Attachment attach2 = new Attachment();
		attach2.setDescription("Óda_na_příliš_žluťoučkého_koně.xml");
		attach2.setMetaType("enclosure");
		// attach2.setMimeType("text/xml");
		attach2.setContents(new ByteContent(
				(prolog + "<text>Příliš žluťoučký kůň úpěl ďábelské ódy.</text>")
						.getBytes("UTF-8")));
		attachments.add(attach2);
		// a ted ji poslem
		Message message = new Message(env, null, null, attachments);
		uploadService.sendMessage(message);
		while (true) {
			DeliveryInfo delivery = services.getDataBoxMessagesService()
					.getDeliveryInfo(env);
			if (delivery.getDelivered() != null) {
				System.out.println(delivery.getDelivered().getTime());
				break;
			}
			Thread.sleep(5000);
		}
		List<MessageStateChange> changes = services.getDataBoxMessagesService()
				.GetMessageStateChanges(null, null);
		for (MessageStateChange change : changes) {
			Assert.assertNotNull(change.getEventTime());
			Assert.assertNotNull(change.getMessageId());
			Assert.assertNotNull(change.getState());
		}
	}

	private void testSendBigMessage(DataBoxServices services, String recipientID)
			throws Exception {
		DataBoxUploadService uploadService = services.getDataBoxUploadService();
		MessageEnvelope env = new MessageEnvelope();
		env.setRecipient(new DataBox(recipientID));
		env.setAnnotation("Óda_na_příliš_žluťoučkého_koně");
		List<UploadedAttachment> uploadedAttachments = new ArrayList<UploadedAttachment>();
		Attachment attachment = new Attachment();
		attachment.setDescription("ahoj.xml");
		attachment.setMetaType("main");
		attachment.setMimeType("text/plain");
		attachment.setContents(new ByteContent("TEST".getBytes("UTF-8")));
		UploadedAttachment uploadedAttachment = uploadService.uploadAttachment(attachment);
		uploadedAttachments.add(uploadedAttachment);
		// a ted ji poslem
		BigMessage message = new BigMessage(env, null, uploadedAttachments);
		uploadService.sendBigMessage(message);
	}
	
	private void testGetListOfSentMessages(DataBoxServices services)
			throws Exception {
		List<MessageEnvelope> messages = services.getDataBoxMessagesService()
				.getListOfSentMessages(begin.getTime(), end.getTime(), null, 0,
						0);
		Assert.assertTrue(messages.isEmpty());
		messages = services.getDataBoxMessagesService().getListOfSentMessages(
				begin.getTime(), end.getTime(), null, 0, 5);
		Assert.assertTrue(messages.size() > 0);
		for (MessageEnvelope mess : messages) {
			Assert.assertTrue(mess.getType().equals(MessageType.SENT));
		}
		messages = services.getDataBoxMessagesService().getListOfSentMessages(
				begin.getTime(), end.getTime(),
				EnumSet.of(MessageState.VIRUS_FOUND), 0, 5);
		Assert.assertEquals(0, messages.size());
	}

	private void testGetListOfReceivedMessages(DataBoxServices services)
			throws Exception {
		List<MessageEnvelope> messages = services.getDataBoxMessagesService()
				.getListOfReceivedMessages(begin.getTime(), end.getTime(),
						null, 0, 0);
		Assert.assertEquals(0, messages.size());
		messages = services.getDataBoxMessagesService()
				.getListOfReceivedMessages(begin.getTime(), end.getTime(),
						null, 0, 5);
		Assert.assertTrue(messages.size() > 0);
		for (MessageEnvelope mess : messages) {
			Assert.assertTrue(mess.getType().equals(MessageType.RECEIVED));
		}
	}

	private void testIntegrityOfSentMessages(DataBoxServices services)
			throws Exception {
		List<MessageEnvelope> messages = services.getDataBoxMessagesService()
				.getListOfSentMessages(begin.getTime(), end.getTime(), null, 0,
						15);
		for (MessageEnvelope mess : messages) {
			testIntegrity(services, mess);
		}
	}

	private void testIntegrityOfReceivedMessages(DataBoxServices services)
			throws Exception {
		List<MessageEnvelope> envelopes = services.getDataBoxMessagesService()
				.getListOfReceivedMessages(begin.getTime(), end.getTime(),
						null, 0, 15);
		for (MessageEnvelope env : envelopes) {
			Message mess1 = testIntegrity(services, env);
			Message mess2 = services.getDataBoxDownloadService()
					.downloadMessage(env, new ByteArrayAttachmentStorer());
			List<Attachment> list1 = mess1.getAttachments();
			List<Attachment> list2 = mess2.getAttachments();
			Assert.assertEquals(list1.size(), list2.size());
			for (int i = 0; i != list1.size(); i++) {
				byte[] bytes1 = ((ByteContent) list1.get(i).getContent())
						.getBytes();
				byte[] bytes2 = ((ByteContent) list2.get(i).getContent())
						.getBytes();
				Assert.assertTrue(Arrays.equals(bytes1, bytes2));
			}
		}
	}

	private Message testIntegrity(DataBoxServices services,
			MessageEnvelope envelope) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			services.getDataBoxDownloadService().downloadSignedMessage(
					envelope, os);
		} finally {
			os.close();
		}
		MessageValidator validator = new MessageValidator(helper.getConfig());
		ByteContent content = new ByteContent(os.toByteArray());
		Message mess = validator.validateAndCreateMessage(content,
				new ByteArrayAttachmentStorer());
		return mess;
	}
}
