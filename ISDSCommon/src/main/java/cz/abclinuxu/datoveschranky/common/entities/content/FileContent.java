package cz.abclinuxu.datoveschranky.common.entities.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Obsah přílohy uložený v souboru.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class FileContent implements Content {

	private File file;

	public FileContent(File file) {
		this.file = file;
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public long estimatedSize() {
		return file.length();
	}

	@Override
	public String toString() {
		return file.getAbsolutePath();
	}

}
