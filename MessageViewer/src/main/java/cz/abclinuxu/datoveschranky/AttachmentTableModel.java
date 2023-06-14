package cz.abclinuxu.datoveschranky;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class AttachmentTableModel extends AbstractTableModel {

	private final List<Attachment> attachments;

	public AttachmentTableModel() {
		this.attachments = Collections.emptyList();
	}

	public AttachmentTableModel(List<Attachment> atts) {
		this.attachments = atts;
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return attachments.size();
	}

	@Override
	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "MIME typ";
		case 1:
			return "jméno";
		default:
			throw new AssertionError("table has only two columns");
		}
	}

	public Object getValueAt(int row, int col) {
		Attachment attachment = attachments.get(row);
		switch (col) {
		case 0:
			return attachment.getMimeType();
		case 1:
			return attachment.getDescription();
		default:
			throw new AssertionError("table has only two columns");
		}
	}

	public List<Attachment> getData() {
		return attachments;
	}
}
