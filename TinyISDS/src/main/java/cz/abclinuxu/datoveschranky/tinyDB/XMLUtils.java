package cz.abclinuxu.datoveschranky.tinyDB;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * These methods were put here from ISDSCommon. 
 * ISDS cannot contain them, since some platforms (Android) do not support XMLGregorianCalendar 
 * @author b00lean
 *
 */
public class XMLUtils {
    public static XMLGregorianCalendar toXmlDate(GregorianCalendar date) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
        } catch (DatatypeConfigurationException dtce) {
            throw new UnsupportedOperationException("Nemohu prevest " +
                    "GregorianCalendar na XMLGregorianCalendar", dtce);
        }
    }

    public static GregorianCalendar toGregorianCalendar(String date) {
        try {
            XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
            return xmlDate.toGregorianCalendar();
        } catch (DatatypeConfigurationException dtce) {
            throw new UnsupportedOperationException("Nemohu prevest " +
                    "GregorianCalendar na XMLGregorianCalendar", dtce);
        }
    }

}
