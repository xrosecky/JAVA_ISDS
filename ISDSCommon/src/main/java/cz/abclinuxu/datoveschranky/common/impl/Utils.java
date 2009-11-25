package cz.abclinuxu.datoveschranky.common.impl;

import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.GregorianCalendar;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class Utils {

    public static String readResourceAsString(Class<?> clazz, String resourceFile) {
        try {
            InputStream is = clazz.getResourceAsStream(resourceFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } catch (IOException ioe) {
            String message = String.format("IO chyba pri cteni zdroje %s.", resourceFile);
            throw new DataBoxException(message);
        }
    }

    public static KeyStore createTrustStore() throws Exception {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream instream = Utils.class.getResourceAsStream("/keys/server.keystore");
        try {
            keystore.load(instream, "aaaaaa".toCharArray());
        } finally {
            instream.close();
        }
        return keystore;
    }

    public static SSLSocketFactory createSSLSocketFactory() throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(createTrustStore());
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        return context.getSocketFactory();
    }

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

    public static void close(Closeable... closeUs) {
        IOException lastException = null;
        for (Closeable closeMe : closeUs) {
            try {
                closeMe.close();
            } catch (IOException ioe) {
                lastException = ioe;
            }
        }
        if (lastException != null) {
            throw new RuntimeException("Chyba pri zavirani.", lastException);
        }
    }

    public static void copy(InputStream source, OutputStream dest) throws IOException {
        byte[] b = new byte[4*4096];
        int read;
        while ((read = source.read(b)) != -1) {
            dest.write(b, 0, read);
        }
    }
}
