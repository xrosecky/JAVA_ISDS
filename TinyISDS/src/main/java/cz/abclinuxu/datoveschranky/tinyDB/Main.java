package cz.abclinuxu.datoveschranky.tinyDB;

import cz.abclinuxu.datoveschranky.common.impl.Utils;
import cz.abclinuxu.datoveschranky.common.impl.FileAttachmentStorer;
import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxDownloadService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class Main {
    
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            String readMe = Utils.readResourceAsString(Main.class, "/resources/readme.txt");
            System.err.println(readMe);
            System.exit(1);
        }
        String type = args[0];
        String loginName = args[1];
        String password = args[2];
        File whereToPutFiles = new File(args[3]);
        String url = type.equals("production")?Config.PRODUCTION_URL:Config.TEST_URL;
        Config config = new Config(url);
        DataBoxServices services = DataBoxManager.login(config, loginName, password);
        DataBoxMessagesService messagesService = services.getDataBoxMessagesService();
        DataBoxDownloadService downloadService = services.getDataBoxDownloadService();
        // ISDSManager manager = ISDSManager.login("5s59sd", "Ab123456");
        GregorianCalendar begin = new GregorianCalendar();
        begin.roll(Calendar.DAY_OF_YEAR, -28);
        GregorianCalendar end = new GregorianCalendar();
        end.roll(Calendar.DAY_OF_YEAR, 1);
        List<MessageEnvelope> messages = messagesService.getListOfReceivedMessages(begin, end, 0, 15);
        FileAttachmentStorer storer = new FileAttachmentStorer(whereToPutFiles);
        for (MessageEnvelope envelope : messages) {
            // uložíme celou podepsanou zprávu
            FileOutputStream fos = new FileOutputStream(new File(whereToPutFiles,
                    envelope.getMessageID() + ".bin"));
            try {
                downloadService.downloadSignedMessage(envelope, fos);
            } finally {
                fos.close();
            }
            // stáhneme přílohy ke zprávě
            List<Attachment> attachments = downloadService.downloadMessage(envelope, storer).getAttachments();
            String sep = "=======================================";
            sep = sep + sep;
            System.out.println(sep);
            DataBox sender = envelope.getSender();
            System.out.println(String.format("Odesilatel: %s (%s)", sender.getIdentity(),
                    sender.getAddress()));
            System.out.println("Jednoznacne ID zpravy: " + envelope.getMessageID());
            System.out.println("Predmet zpravy: " + envelope.getAnnotation());
            System.out.println("Zprava byla prijata: " + envelope.getDeliveryTime().getTime());
            System.out.println("Zprava byla akceptovana: " + envelope.getAcceptanceTime().getTime());
            Hash hash = messagesService.verifyMessage(envelope);
            System.out.println("Hash zpravy je: " + hash);
            System.out.println("Seznam priloh zpravy:");
            for (Attachment attachment : attachments) {
                System.out.println("       "+ attachment.getDescription() + " -> " +
                        attachment.getContent().toString());
            }
            System.out.println(sep);
        }
    }

}
