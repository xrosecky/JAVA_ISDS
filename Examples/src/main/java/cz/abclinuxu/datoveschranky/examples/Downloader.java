package cz.abclinuxu.datoveschranky.examples;

import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.impl.Config;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxEnvironment;
import cz.abclinuxu.datoveschranky.impl.Authentication;
import cz.abclinuxu.datoveschranky.impl.BasicAuthentication;
import cz.abclinuxu.datoveschranky.impl.DataBoxManager;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author xrosecky
 */
public class Downloader {

    private DataBoxManager manager;
    private int threads;

    private static class DownloadThread extends Thread {

        private DataBoxManager manager;
        private List<MessageEnvelope> envelopes;

        public DownloadThread(DataBoxManager manager, List<MessageEnvelope> envelopes) {
            this.manager = manager;
            this.envelopes = envelopes;
        }

        public void run() {
            while (!envelopes.isEmpty()) {
                MessageEnvelope envelope = envelopes.remove(0);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                manager.getDataBoxDownloadService().downloadSignedMessage(envelope, os);
            }
        }
    }

    public Downloader(DataBoxManager manager, int threads) {
        this.manager = manager;
        this.threads = threads;
    }

    public void download() throws Exception {
        GregorianCalendar begin = new GregorianCalendar();
	begin.roll(Calendar.DAY_OF_YEAR, -10);
	GregorianCalendar end = new GregorianCalendar();
	end.roll(Calendar.DAY_OF_YEAR, 1);
        List<MessageEnvelope> result = Collections.synchronizedList(
                new LinkedList<MessageEnvelope>(
                manager.getDataBoxMessagesService().getListOfReceivedMessages(begin.getTime(), end.getTime(), null, 0, 500)));
        List<DownloadThread> threadsToWait = new LinkedList<DownloadThread>();
        for (int i = 0; i != threads; i++) {
            DownloadThread thread = new DownloadThread(manager, result);
            threadsToWait.add(thread);
            thread.start();
        }
        for (DownloadThread thread : threadsToWait) {
            thread.join();
        }
    }

    public static void main(String[] args) throws Exception {
        int threads = Integer.parseInt(args[1]);
        String propertiesFile = args[0];
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(propertiesFile);
        props.load(fis);
        fis.close();
        String login = props.getProperty("downloader.login");
        String passwd = props.getProperty("downloader.password");
        Config config = new Config(DataBoxEnvironment.TEST);
        Authentication auth = new BasicAuthentication(config, login, passwd);
        DataBoxManager manager = new DataBoxManager(config, auth);
        Downloader downloader = new Downloader(manager, threads);
        downloader.download();
    }
}
