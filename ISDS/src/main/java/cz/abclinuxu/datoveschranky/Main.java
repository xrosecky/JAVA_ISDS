package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxState;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxWithDetails;
import cz.abclinuxu.datoveschranky.common.entities.DeliveryEvent;
import cz.abclinuxu.datoveschranky.common.entities.DeliveryInfo;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.TimeStamp;
import cz.abclinuxu.datoveschranky.common.entities.content.ByteContent;
import cz.abclinuxu.datoveschranky.common.entities.content.Content;
import cz.abclinuxu.datoveschranky.common.impl.ByteArrayAttachmentStorer;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxDownloadService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.impl.DataBoxManager;
import cz.abclinuxu.datoveschranky.impl.MessageValidator;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String loginName = "5e7mvf";
        String password = "Ab123456a";
        Config config = new Config(Config.TEST_URL);
        DataBoxManager services = DataBoxManager.login(config, loginName, password);
        List<DataBoxWithDetails> found = services.getDataBoxSearchService().findOVMsByName("min");
        for (DataBox entity : found) {
            DataBoxState dbs = services.getDataBoxSearchService().checkDataBox(entity);
            System.out.println(entity + " " + dbs);
        }
        
        DataBoxMessagesService messagesService = services.getDataBoxMessagesService();
        DataBoxDownloadService downloadService = services.getDataBoxDownloadService();
        DataBoxUploadService uploadService = services.getDataBoxUploadService();
        
        MessageEnvelope env = new MessageEnvelope();
        env.setRecipient(new DataBox("vqbab52"));
        env.setAnnotation("predmet zpravy");
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment attach1 = new Attachment();
        attach1.setDescription("StandardText.txt");
        attach1.setMetaType("main");
        attach1.setMimeType("text/plain");
        attach1.setContents(new ByteContent(new byte[]{1, 2}));
        attachments.add(attach1);
        Message message = new Message(env, null, null, attachments);
        uploadService.sendMessage(message);
        System.out.println("The messageID is "+message.getEnvelope().getMessageID());
        GregorianCalendar begin = new GregorianCalendar();
        begin.roll(Calendar.DAY_OF_YEAR, -28);
        GregorianCalendar end = new GregorianCalendar();
        end.roll(Calendar.DAY_OF_YEAR, 1);
        // List<MessageEnvelope> messages = messagesService.getListOfReceivedMessages(begin, end, null, 0, 5);
        List<MessageEnvelope> messages = messagesService.getListOfSentMessages(begin.getTime(), end.getTime(), null, 0, 5);
        MessageValidator helper = new MessageValidator(config);
        for (MessageEnvelope envelope : messages) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            downloadService.downloadSignedMessage(envelope, out);
            Content content = new ByteContent(out.toByteArray());
            Message mess = helper.validateAndCreateMessage(content, new ByteArrayAttachmentStorer());
            out.close();
            TimeStamp stamp = mess.getTimeStamp();
            System.err.println("Message id:" + envelope.getMessageID());
            System.err.println("        time:" + stamp.getGeneratedTime());
            System.err.println("        hash:" + stamp.getHash());
            System.err.println("        signed by:" + stamp.getCertificate().getIssuerDN().getName());
            System.err.println("        cert id:" + stamp.getCertificate().getSerialNumber());
            System.err.println("        status:"+mess.getEnvelope().getState().toString());
        }
    }

    public static void dumpDeliveryInfo(DeliveryInfo delivery) {
        System.out.println("Accepted: " + delivery.getAccepted().getTime());
        System.out.println("Delivered: " + delivery.getDelivered().getTime());
        System.out.println("List of events:");
        for (DeliveryEvent event : delivery.getEvents()) {
            System.out.println(event.getTime().getTime() + ":" + event.getDescription());
        }
    }
}
