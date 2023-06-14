package cz.abclinuxu.datoveschranky.tinyDB.holders;

import cz.abclinuxu.datoveschranky.common.impl.Utils;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Zapíše obsah elementu do OutputStreamu.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class OutputStreamHolder implements OutputHolder<OutputStream>,
		Closeable {

	private final OutputStream os;
	private final BufferedWriter bw;

	public OutputStreamHolder(OutputStream os) {
		this.os = os;
		bw = new BufferedWriter(new OutputStreamWriter(os));
	}

	public void write(char[] array, int start, int length) {
		try {
			bw.write(array, start, length);
			bw.flush();
		} catch (IOException ioe) {
			throw new RuntimeException("Nemohu zapisovat do bufferu", ioe);
		}
	}

	public OutputStream getResult() {
		return os;
	}

	public void close() {
		Utils.close(bw, os);
	}

}
