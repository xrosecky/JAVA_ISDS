package cz.abclinuxu.datoveschranky.common.entities;

public class UploadedAttachment {

	protected String attachmentId;

	protected String metaType;

	protected Hash hash1;

	protected Hash hash2;

	public UploadedAttachment(String attachmentId, String metaType, Hash hash1, Hash hash2) {
		super();
		this.attachmentId = attachmentId;
		this.metaType = metaType;
		this.hash1 = hash1;
		this.hash2 = hash2;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getMetaType() {
		return metaType;
	}

	public void setMetaType(String metaType) {
		this.metaType = metaType;
	}

	public Hash getHash1() {
		return hash1;
	}

	public void setHash1(Hash hash1) {
		this.hash1 = hash1;
	}

	public Hash getHash2() {
		return hash2;
	}

	public void setHash2(Hash hash2) {
		this.hash2 = hash2;
	}

}
