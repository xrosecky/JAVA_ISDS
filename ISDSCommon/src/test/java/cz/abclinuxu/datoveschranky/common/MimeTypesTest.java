package cz.abclinuxu.datoveschranky.common;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.impl.MimeTypes;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * 
 * @author xrosecky
 */
public class MimeTypesTest extends TestCase {

	public void testfileNameToMimeType() {
		Assert.assertEquals(MimeTypes.fileNameToMimeType("sample.pdf"),
				"application/pdf");
		Assert.assertEquals(MimeTypes.fileNameToMimeType("a.b.c.mpeg2"),
				"video/mpeg");
		// bad input
		Assert.assertEquals(MimeTypes.fileNameToMimeType("bez pripony"), null);
		Assert.assertEquals(MimeTypes.fileNameToMimeType("."), null);
		Assert.assertEquals(MimeTypes.fileNameToMimeType("e.e"), null);
		Assert.assertEquals(MimeTypes.fileNameToMimeType("e."), null);
		Assert.assertEquals(MimeTypes.fileNameToMimeType(".e"), null);
	}

	public void testAtachment() {
		Attachment attachment = new Attachment("priklad.pdf", null);
		Assert.assertEquals(attachment.getMimeType(), "application/pdf");
		attachment.setMimeType("foo/foo");
		Assert.assertEquals(attachment.getMimeType(), "foo/foo");
		attachment.setDescription("obrazek.jpg");
		Assert.assertEquals(attachment.getMimeType(), "image/jpeg");
	}

}
