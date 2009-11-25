package cz.abclinuxu.datoveschranky.tinyDB.responseparsers;

import cz.abclinuxu.datoveschranky.common.impl.Utils;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.tinyDB.holders.OutputHolder;
import cz.abclinuxu.datoveschranky.tinyDB.holders.OutputStreamHolder;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.xml.sax.Attributes;

/**
 *
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class VerifyMessage extends AbstractResponseParser {

    private ByteArrayOutputStream hash = new ByteArrayOutputStream();
    private String algorithm = null;

    public VerifyMessage() {
    }

    @Override
    public OutputHolder startElementImpl(String elName, Attributes attributes) {
        if (super.match("dmHash")) { // tohle nás zajímá
            algorithm = attributes.getValue("algorithm");
            Base64OutputStream bos = new Base64OutputStream(hash, false, 0, null);
            return new OutputStreamHolder(bos);
        }
        return null;
    }

    @Override
    public void endElementImpl(String elName, OutputHolder holder) {
        if (holder instanceof Closeable) {
            Utils.close((Closeable) holder);
        }
    }
    
    public Hash getResult() {
        return new Hash(algorithm, hash.toByteArray());
    }
    
}
