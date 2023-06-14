package cz.abclinuxu.datoveschranky.common.impl;

import cz.abclinuxu.datoveschranky.common.interfaces.AttachmentStorer;
import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.content.FileContent;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Ukládá přílohy do souboru.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class FileAttachmentStorer implements AttachmentStorer {

	protected File directory = null;

	/**
	 * Soubory se budou ukládat do adresáře dir.
	 * 
	 * @param dir
	 *            adresář, do kterého se mají ukládat soubory
	 */
	public FileAttachmentStorer(File dir) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(String.format(
					"%s neni adresarem.", dir.getAbsolutePath()));
		}
		this.directory = dir;
	}

	public OutputStream store(MessageEnvelope envelope, Attachment attachment)
			throws IOException {
		String name = name(envelope, attachment);
		File output = new File(directory, name);
		attachment.setContents(new FileContent(output));
		return new FileOutputStream(output);
	}

	protected String name(MessageEnvelope envelope, Attachment attachment) {
		String prefix = envelope.getMessageID();
		String description = attachment.getDescription();
		return prefix + "_" + description;
	}

}
