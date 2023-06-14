package cz.abclinuxu.datoveschranky.common.impl;

import cz.abclinuxu.datoveschranky.common.interfaces.AttachmentStorer;
import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.content.ByteContent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Ukládá přílohy do pole bytů v paměti.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class ByteArrayAttachmentStorer implements AttachmentStorer {

	class MyOutputStream extends OutputStream {

		private final ByteArrayOutputStream output = new ByteArrayOutputStream();
		private final Attachment attachment;

		public MyOutputStream(Attachment attach) {
			this.attachment = attach;
		}

		@Override
		public void write(int byt) throws IOException {
			output.write(byt);
		}

		@Override
		public void close() throws IOException {
			super.close();
			output.close();
			attachment.setContents(new ByteContent(output.toByteArray()));
		}

		@Override
		public void write(byte[] bytes) throws IOException {
			output.write(bytes);
		}

		@Override
		public void write(byte[] bytes, int offset, int length)
				throws IOException {
			super.write(bytes, offset, length);
		}
	}

	/**
	 * Bezparametrický konstruktor
	 */
	public ByteArrayAttachmentStorer() {

	}

	public OutputStream store(MessageEnvelope envelope, Attachment attachment)
			throws IOException {
		return new MyOutputStream(attachment);
	}
}
