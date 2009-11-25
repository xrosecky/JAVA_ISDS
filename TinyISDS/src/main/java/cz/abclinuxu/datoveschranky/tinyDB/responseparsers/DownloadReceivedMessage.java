package cz.abclinuxu.datoveschranky.tinyDB.responseparsers;

import cz.abclinuxu.datoveschranky.common.interfaces.AttachmentStorer;
import cz.abclinuxu.datoveschranky.common.impl.Utils;
import cz.abclinuxu.datoveschranky.tinyDB.holders.OutputHolder;
import cz.abclinuxu.datoveschranky.tinyDB.holders.OutputStreamHolder;
import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.xml.sax.Attributes;

/**
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 * 
 */
public class DownloadReceivedMessage extends AbstractResponseParser {

    private Attachment attachment = null; // právě zpracovávaná příloha
    private List<Attachment> attachments = new ArrayList<Attachment>();
    private AttachmentStorer storer = null;
    private MessageEnvelope envelope = null;
    
    public DownloadReceivedMessage(MessageEnvelope env, AttachmentStorer attachStorer) {
        this.envelope = env;
        this.storer = attachStorer;
    }

    @Override
    public OutputHolder startElementImpl(String elName, Attributes attributes) {
        if ("dmFile".equals(elName)) {
            attachment = new Attachment();
            attachment.setDescription(attributes.getValue("dmFileDescr"));
            attachment.setMetaType(attributes.getValue("dmFileMetaType"));
            attachment.setMimeType(attributes.getValue("dmMimeType"));
        }
        if ("dmEncodedContent".equals(elName)) {
            try {
                OutputStream os = storer.store(envelope, attachment);
                attachments.add(attachment);
                // FileOutputStream fos = new FileOutputStream(file);
                Base64OutputStream bos = new Base64OutputStream(os, false, 0, null);
                OutputHolder input = new OutputStreamHolder(bos);
                return input;
            } catch (IOException ioe) {
                throw new RuntimeException("Nemohu otevrit soubor", ioe);
            }

        }
        return null;
    }

    @Override
    public void endElementImpl(String elName, OutputHolder handle) {
        if (handle instanceof Closeable) {
            Utils.close((Closeable) handle);
        }
    }
    
    public List<Attachment> getResult() {
        return attachments;
    }
}
