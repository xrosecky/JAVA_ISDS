package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.BigMessage;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.LegalTitle;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.UploadedAttachment;
import cz.abclinuxu.datoveschranky.common.entities.Validator;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.impl.Utils;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import cz.abclinuxu.datoveschranky.ws.dm.DmOperationsPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmVoDZPortType;
import cz.abclinuxu.datoveschranky.ws.dm.TBigMessEnvelope;
import cz.abclinuxu.datoveschranky.ws.dm.TBigMessageInput.DmFiles;
import cz.abclinuxu.datoveschranky.ws.dm.TBigMessageInput.DmFiles.DmExtFile;
import cz.abclinuxu.datoveschranky.ws.dm.TFilesArray;
import cz.abclinuxu.datoveschranky.ws.dm.TFilesArray.DmFile;
import cz.abclinuxu.datoveschranky.ws.dm.TStatus;
import cz.abclinuxu.datoveschranky.ws.dm.TMessageCreateInput.DmEnvelope;
import cz.abclinuxu.datoveschranky.ws.dm.UploadAttachmentResponse.DmAttHash1;
import cz.abclinuxu.datoveschranky.ws.dm.UploadAttachmentResponse.DmAttHash2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.activation.DataHandler;
import javax.xml.ws.Holder;

/**
 * 
 * @author xrosecky
 */
public class DataBoxUploadServiceImpl implements DataBoxUploadService {

	protected DmOperationsPortType dmOp;

	protected DmVoDZPortType dmBigOp = null;
	
	public DataBoxUploadServiceImpl(DmOperationsPortType dmOp, DmVoDZPortType dmBigOp) {
		this.dmOp = dmOp;
		this.dmBigOp = dmBigOp;
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
		envelope.setDmType(message.getEnvelope().getDmType());
		dmOp.createMessage(envelope, files, messageID, status);
		ErrorHandling.throwIfError("Poslani zpravy se nezdarilo", status.value);
		message.getEnvelope().setType(MessageType.SENT);
		message.getEnvelope().setMessageID(messageID.value);
	}
	
	public UploadedAttachment uploadAttachment(Attachment attachment) {
		cz.abclinuxu.datoveschranky.ws.dm.UploadAttachment.DmFile dmFile = new cz.abclinuxu.datoveschranky.ws.dm.UploadAttachment.DmFile();
		dmFile.setDmFileDescr(attachment.getDescription());
		dmFile.setDmMimeType(attachment.getMimeType());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Utils.copy(attachment.getContent().getInputStream(), out);
		} catch (IOException ioe) {
			throw new DataBoxException("IO chyba pri cteni prilohy.", ioe);
		}
		DataHandler dataHandler = new DataHandler(out.toByteArray(), "application/octet-stream");
		dmFile.setDmEncodedContent(dataHandler);
		Holder<String> dmAttID = new Holder<String>();
		Holder<DmAttHash1> dmAttHash1 = new Holder<DmAttHash1>();
		Holder<DmAttHash2> dmAttHash2 = new Holder<DmAttHash2>();
		Holder<TStatus> status = new Holder<TStatus>();
		dmBigOp.uploadAttachment(dmFile, dmAttID, dmAttHash1, dmAttHash2, status);
		ErrorHandling.throwIfError("Nahrani prilohy se nepodarilo", status.value);
		Hash hash1 = new Hash(dmAttHash1.value.getAttHashAlg(), dmAttHash1.value.getValue().getBytes(StandardCharsets.UTF_8));
		Hash hash2 = new Hash(dmAttHash2.value.getAttHashAlg(), dmAttHash2.value.getValue().getBytes(StandardCharsets.UTF_8));
		return new UploadedAttachment(dmAttID.value, attachment.getMetaType(), hash1, hash2);
	}

	public void sendBigMessage(BigMessage message) {
		TBigMessEnvelope envelope = new TBigMessEnvelope();
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
		envelope.setDmType(message.getEnvelope().getDmType());

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
		if (message.getAttachments() != null) {
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
		}

		// big attachments
		DmFiles dmFiles = new DmFiles();
		if (message.getUploadedAttachments() != null) {
			for (UploadedAttachment attachment : message.getUploadedAttachments()) {
				DmExtFile extFile = new DmExtFile();
				extFile.setDmAttHash1(new String(attachment.getHash1().getHash(), StandardCharsets.UTF_8));
				extFile.setDmAttHash1Alg(attachment.getHash1().getAlgorithm());
				extFile.setDmAttHash2(new String(attachment.getHash2().getHash(), StandardCharsets.UTF_8));
				extFile.setDmAttHash2Alg(attachment.getHash2().getAlgorithm());
				extFile.setDmAttID(attachment.getAttachmentId());
				extFile.setDmFileMetaType(attachment.getMetaType());
				dmFiles.getDmExtFile().add(extFile);
			}
		}

		Holder<String> messageID = new Holder<String>();
		Holder<TStatus> status = new Holder<TStatus>();
		dmBigOp.createBigMessage(envelope, dmFiles, messageID, status);
		ErrorHandling.throwIfError("Poslani zpravy se nezdarilo", status.value);
		message.getEnvelope().setType(MessageType.SENT);
		message.getEnvelope().setMessageID(messageID.value);
	}

}
