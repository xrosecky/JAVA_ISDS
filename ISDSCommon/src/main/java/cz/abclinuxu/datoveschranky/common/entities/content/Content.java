package cz.abclinuxu.datoveschranky.common.entities.content;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * Samotný obsah přílohy, příloha může být např. uložena v souboru či v paměti.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface Content {
    
    /*
     * Vráti vstupní proud, ze kterého lze číst obsah zprávy. Za uzavření tohoto
     * proudu je zodpovědná volající třída. 
     * 
     */
    public InputStream getInputStream() throws IOException;
    
}
