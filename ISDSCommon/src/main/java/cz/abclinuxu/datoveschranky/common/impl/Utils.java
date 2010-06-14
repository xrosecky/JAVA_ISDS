package cz.abclinuxu.datoveschranky.common.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class Utils {

    private static final int BUFFER_SIZE = 4 * 4096;

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

    public static KeyStore createTrustStore() {
        return createTrustStore(readX509Certificates());
    }

    public static Collection<X509Certificate> readX509Certificates() {
        InputStream instream = Utils.class.getResourceAsStream("/keys/certificates");
        return readX509Certificates(instream);
    }

    public static Collection<X509Certificate> getX509Certificates(KeyStore store) {
        List<X509Certificate> certs = new ArrayList<X509Certificate>();
        try {
            for (Enumeration<String> e = store.aliases(); e.hasMoreElements();) {
                String alias = e.nextElement();
                Certificate cert = store.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    certs.add((X509Certificate) cert);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new DataBoxException("Nemohu ziskat seznam X.509 certifikatu z KeyStore.", ex);
        }
        return certs;
    }

    public static Collection<X509Certificate> readX509Certificates(InputStream is) {
        List<X509Certificate> certs = new ArrayList<X509Certificate>();
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                certs.add((X509Certificate) cf.generateCertificate(bis));
            }
        } catch (CertificateException ce) {
            throw new DataBoxException("Nemohu precist X.509 certifikat.", ce);
        } catch (IOException ioe) {
            throw new DataBoxException("IO chyba pri cteni X.509 certifikatu.", ioe);
        }
        return certs;
    }

    public static SSLSocketFactory createSSLSocketFactory(KeyStore keys) {
        try {
            String algo = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algo);
            trustManagerFactory.init(keys);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return context.getSocketFactory();
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new DataBoxException("Nemohu vytvorit SSLSocketFactory.", ex);
        }
    }

    public static KeyStore createTrustStore(Collection<X509Certificate> certs) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, "".toCharArray());
            int counter = 0;
            for (X509Certificate cert : certs) {
                keyStore.setCertificateEntry(String.valueOf(counter), cert);
                counter++;
            }
            return keyStore;
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new DataBoxException("Chyba pri importu X.509 certifikatu do keyStore.", ex);
        }
    }

    /*TODO: Not implemented on android
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
    */

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
        byte[] bytes = new byte[BUFFER_SIZE];
        int read;
        while ((read = source.read(bytes)) != -1) {
            dest.write(bytes, 0, read);
        }
    }
}
