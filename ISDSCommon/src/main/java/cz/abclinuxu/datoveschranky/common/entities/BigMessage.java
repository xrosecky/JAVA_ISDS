package cz.abclinuxu.datoveschranky.common.entities;

import java.util.List;

public class BigMessage extends Message {

	private static final long serialVersionUID = 1L;

	protected List<UploadedAttachment> uploadedAttachments = null;

	public BigMessage(MessageEnvelope env, List<Attachment> attachs, List<UploadedAttachment> uploadedAttachments) {
		super(env, attachs);
		this.uploadedAttachments = uploadedAttachments;
	}

	public List<UploadedAttachment> getUploadedAttachments() {
		return uploadedAttachments;
	}

	public void setUploadedAttachments(List<UploadedAttachment> uploadedAttachments) {
		this.uploadedAttachments = uploadedAttachments;
	}

}
