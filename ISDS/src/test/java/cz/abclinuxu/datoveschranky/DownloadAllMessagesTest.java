package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author xrosecky
 */
public class DownloadAllMessagesTest {

    private static TestHelper helper = new TestHelper();
    private static int MAX = 10000;

    @Test
    public void downloadAllMessages() throws Exception {
	int limits[] = new int[]{1, 2, 3, 5, MAX};
	for (int limit : limits) {
	    downloadAllMessages(limit);
	}
    }

    public void downloadAllMessages(int limit) throws Exception {
	Set<String> seen = new HashSet<String>();
	GregorianCalendar begin = new GregorianCalendar();
	begin.roll(Calendar.DAY_OF_YEAR, -10);
	GregorianCalendar end = new GregorianCalendar();
	end.roll(Calendar.DAY_OF_YEAR, 1);
	DataBoxMessagesService messageService = helper.connectAsFO().getDataBoxMessagesService();
	int offset = 1;
	while (true) {
	    List<MessageEnvelope> messages = messageService.getListOfReceivedMessages(begin.getTime(), end.getTime(), null, offset, limit);
	    if (messages.isEmpty()) {
		break;
	    }
	    offset += messages.size();
	    for (MessageEnvelope envelope : messages) {
		String id = envelope.getMessageID();
		Assert.assertFalse(seen.contains(id));
		seen.add(id);
	    }
	}
	List<MessageEnvelope> messages = messageService.getListOfReceivedMessages(begin.getTime(), end.getTime(), null, 0, MAX);
	Assert.assertEquals(seen.size(), messages.size());
    }
}
