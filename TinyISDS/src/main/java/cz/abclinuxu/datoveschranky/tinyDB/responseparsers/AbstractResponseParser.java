package cz.abclinuxu.datoveschranky.tinyDB.responseparsers;

import cz.abclinuxu.datoveschranky.common.impl.Status;
import cz.abclinuxu.datoveschranky.tinyDB.holders.OutputHolder;
import cz.abclinuxu.datoveschranky.tinyDB.holders.StringHolder;
import java.util.HashMap;
import org.xml.sax.Attributes;

/**
 * Tato třída slouží k parsování odpovědi webové služby, je to nástavba nad
 * SAX parserem, která umožnuje jednoduchou navigaci (viz metoda match) a podstatně
 * zjednodušuje čtení obsahu elementu.
 * 
 * TODO: rozepsat dokumentaci k metodám.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public abstract class AbstractResponseParser implements ResponseParser {

    private static final boolean debug = false;
    private static final int MAX_DEPTH = 128;
    private static final String[] wanting = {"dmStatusCode", "dmStatusMessage"};
    
    private String[] path = new String[MAX_DEPTH];
    private int pathIndex = 0;
    private HashMap<String, StringHolder> map = new HashMap<String, StringHolder>();
    private Status status = null;

    public AbstractResponseParser() {
        for (String key : wanting) {
            map.put(key, new StringHolder());
        }
    }

    public Status getStatus() {
        return status;
    }

    public void done() {
        String dmStatusCode = map.get("dmStatusCode").toString();
        String dmStatusMessage = map.get("dmStatusMessage").toString();
        status = new Status(dmStatusCode, dmStatusMessage);

    }

    /**
     * Začátek elementu, vrátí OutputHandler, do kterého se načte obsah elementu
     */
    public OutputHolder startElement(String elName, Attributes attributes) {
        path[pathIndex] = elName.intern(); // rychlejší porovnání
        pathIndex++;
        if (debug) {
            System.err.println(dumpState());
        }
        OutputHolder handle = map.get(elName);
        if (handle == null) {
            return startElementImpl(elName, attributes);
        } else {
            return handle;
        }
    }
    
    /**
     * Konec elementu, handle obsahuje OutputHandler s obsaheme elementu.
     */ 
    public void endElement(String elName, OutputHolder handle) {
        if (debug) {
            System.err.println(dumpState());
        }
        if (!map.containsKey(elName)) {
            endElementImpl(elName, handle);
        }
        path[pathIndex] = null;
        pathIndex--;
    }
    
    /**
     *  Navigace x XML dokumentu, ověří shodu args proti vrcholu zásobníků.
     *  TODO rozepsat.
     */ 
    public boolean match(String... args) {
        if (pathIndex >= args.length) {
            int start = pathIndex - args.length;
            for (int i = 0; i!= args.length; i++) {
                String a = path[start + i];
                String b = args[i];
                if (!"*".equals(b) && !a.equals(b)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    public String dumpState() {
        StringBuffer result = new StringBuffer("/");
        for (int i = 0; i!= pathIndex; i++) {
            result.append(path[i]);
            result.append("/");
        }
        return result.toString();
    }

    public abstract OutputHolder startElementImpl(String elName, Attributes attributes);

    public abstract void endElementImpl(String elName, OutputHolder handle);
}
