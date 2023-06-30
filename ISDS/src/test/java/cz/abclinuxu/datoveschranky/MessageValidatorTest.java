package cz.abclinuxu.datoveschranky;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.content.ByteContent;
import cz.abclinuxu.datoveschranky.common.impl.ByteArrayAttachmentStorer;
import cz.abclinuxu.datoveschranky.impl.MessageValidator;

public class MessageValidatorTest {

	@Test
	public void testZfoWithXmlAttachment() throws Exception {
		MessageValidator validator = new MessageValidator();
		InputStream is = this.getClass().getResourceAsStream(
				"/examples/ODZ_with_xml_attachment.zfo");
		byte[] bytes = getBytesFromInputStream(is);
		Message mess = validator.createMessage(bytes,
				new ByteArrayAttachmentStorer());
		ByteContent content = (ByteContent) mess.getAttachments().get(0)
				.getContent();
		System.out.println(new String(content.getBytes(), "UTF-8"));
	}

	public static byte[] getBytesFromInputStream(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[0xFFFF];
			for (int len; (len = is.read(buffer)) != -1;) {
				os.write(buffer, 0, len);
			}
			os.flush();
			return os.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

}
