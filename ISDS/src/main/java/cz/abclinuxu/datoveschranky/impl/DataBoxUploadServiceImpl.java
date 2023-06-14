package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.LegalTitle;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.Validator;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.impl.Utils;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.ws.dm.DmOperationsPortType;
import cz.abclinuxu.datoveschranky.ws.dm.TFilesArray;
import cz.abclinuxu.datoveschranky.ws.dm.TFilesArray.DmFile;
import cz.abclinuxu.datoveschranky.ws.dm.TStatus;
import cz.abclinuxu.datoveschranky.ws.dm.TMessageCreateInput.DmEnvelope;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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
		DmEnvelope envelope = new DmEnvelope();
		envelope.setDbIDRecipient(message.getEnvelope().getRecipient()
				.getdataBoxID());
		envelope.setDmAnnotation(message.getEnvelope().getAnnotation());

		// Process legal title
		LegalTitle legalTitle = message.getEnvelope().getLegalTitle();
		if (legalTitle != null) {
			if (legalTitle.getLaw() != null)
				envelope.setDmLegalTitleLaw(BigInteger.valueOf(Long
						.parseLong(legalTitle.getLaw())));
			envelope.setDmLegalTitlePar(legalTitle.getPar());
			envelope.setDmLegalTitlePoint(legalTitle.getPoint());
			envelope.setDmLegalTitleSect(legalTitle.getSect());
			if (legalTitle.getYear() != null)
				envelope.setDmLegalTitleYear(BigInteger.valueOf(Long
						.parseLong(legalTitle.getYear())));
		}

		// To hands
		envelope.setDmToHands(message.getEnvelope().getToHands());
		envelope.setDmPersonalDelivery(message.getEnvelope()
				.getPersonalDelivery());

		if (message.getEnvelope().getRecipientIdent() != null) {
			envelope.setDmRecipientIdent(message.getEnvelope()
					.getRecipientIdent().getIdent());
			envelope.setDmRecipientRefNumber(message.getEnvelope()
					.getRecipientIdent().getRefNumber());
		}
		if (message.getEnvelope().getSenderIdent() != null) {
			envelope.setDmSenderIdent(message.getEnvelope().getSenderIdent()
					.getIdent());
			envelope.setDmSenderRefNumber(message.getEnvelope()
					.getSenderIdent().getRefNumber());
		}
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
		if (message.getEnvelope().getDmType() != null) {
			envelope.setDmType(message.getEnvelope().getDmType());
		}
		dmOp.createMessage(envelope, files, messageID, status);
		ErrorHandling.throwIfError("Poslani zpravy se nezdarilo", status.value);
		message.getEnvelope().setType(MessageType.SENT);
		message.getEnvelope().setMessageID(messageID.value);
	}
}
