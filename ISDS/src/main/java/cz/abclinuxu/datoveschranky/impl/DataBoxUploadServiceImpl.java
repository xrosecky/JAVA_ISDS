package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.Validator;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.impl.Utils;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.ws.dm.DmOperationsPortType;
import cz.abclinuxu.datoveschranky.ws.dm.TFilesArray;
import cz.abclinuxu.datoveschranky.ws.dm.TFilesArray.DmFile;
import cz.abclinuxu.datoveschranky.ws.dm.TMessageEnvelopeSub;
import cz.abclinuxu.datoveschranky.ws.dm.TStatus;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.ws.Holder;

/**
 *
 * @author xrosecky
 */
public class DataBoxUploadServiceImpl implements DataBoxUploadService {

    protected DmOperationsPortType dmOp;

    public DataBoxUploadServiceImpl(DmOperationsPortType dmOp) {
        this.dmOp = dmOp;
    }

    public void sendMessage(Message message) {
        Validator.assertValidMessageForSending(message);
        TMessageEnvelopeSub envelope = new TMessageEnvelopeSub();
        envelope.setDbIDRecipient(message.getEnvelope().getRecipient().getdataBoxID());
        envelope.setDmAnnotation(message.getEnvelope().getAnnotation());
        TFilesArray files = new TFilesArray();
        for (Attachment attachment : message.getAttachments()) {
            DmFile file = new DmFile();
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Utils.copy(attachment.getContent().getInputStream(), out);
                file.setDmEncodedContent(out.toByteArray());
            } catch (IOException ioe) {
                throw new DataBoxException("IO chyba pri cteni prilohy.", ioe);
            }
            file.setDmMimeType(attachment.getMimeType());
            file.setDmFileMetaType(attachment.getMetaType());
            file.setDmFileDescr(attachment.getDescription());
            files.getDmFile().add(file);
        }
        Holder<String> messageID = new Holder<String>();
        Holder<TStatus> status = new Holder<TStatus>();
        dmOp.createMessage(envelope, files, messageID, status);
        ErrorHandling.throwIfError("Poslani zpravy se nezdarilo", status.value);
        message.getEnvelope().setType(MessageType.SENT);
        message.getEnvelope().setMessageID(messageID.value);
    }
}
