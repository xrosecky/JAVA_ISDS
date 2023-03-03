package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.TimeStamp;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Principal;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Pomocná třída pro validaci časového razítka a podpisu zprávy. Jen prototyp!
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class Validator {

    private static Map<String, String> OIDToAlgorithmName = new HashMap<String, String>();
    

    static {
        OIDToAlgorithmName.put("1.3.14.3.2.26", "SHA-1");
        OIDToAlgorithmName.put("2.16.840.1.101.3.4.2.1", "SHA-256");
        OIDToAlgorithmName.put("2.16.840.1.101.3.4.2.2", "SHA-384");
        OIDToAlgorithmName.put("2.16.840.1.101.3.4.2.3", "SHA-512");
        Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }
    private Collection<X509Certificate> certs = null;
    private boolean isValidating = false;
    private Logger logger = LoggerFactory.getLogger(Validator.class.getCanonicalName());

    public Validator(Collection<X509Certificate> certs, boolean validating) {
        this.certs = certs;
        isValidating = validating; 
    }

    public Validator() {
        this.certs = new ArrayList<X509Certificate>();
        isValidating = false;
    }

    public TimeStamp readTimeStamp(byte[] timeStamp) {
        try {
            CMSSignedData data = new CMSSignedData(timeStamp);
            TimeStampToken tst = new TimeStampToken(data);
            TimeStampTokenInfo tsti = tst.getTimeStampInfo();
            X509Certificate cert = this.findCertificate(tst.getSID());
            if (isValidating) {
                try {
                    tst.validate(cert, "BC");
                } catch (Exception ex) {
                    if (ex instanceof RuntimeException) {
                        throw (RuntimeException) ex;
                    } else {
                        throw new DataBoxException(ex.toString(), ex);
                    }
                }
            }
            String algo = OIDToAlgorithmName.get(tsti.getMessageImprintAlgOID());
            byte[] hash = tsti.getMessageImprintDigest();
            return new TimeStamp(new Hash(algo, hash), cert, tsti.getGenTime());
        } catch (CMSException ex) {
            throw new DataBoxException("Chyba pri cteni casoveho razitka.", ex);
        } catch (TSPException ioe) {
            throw new DataBoxException("Chyba pri cteni casoveho razitka.", ioe);
        } catch (IOException ioe) {
            throw new DataBoxException("IO chyba pri cteni casoveho razitka.", ioe);
        }
    }

    /**
     * Vrátí obsah po odstranění PKCS7 obálky.
     */
    public byte[] readPKCS7(byte[] signedBytes) throws DataBoxException {
        try {
            CMSSignedData data = new CMSSignedData(signedBytes);
            verifySignature(data);
            CMSProcessable signedContent = data.getSignedContent();
            return (byte[]) signedContent.getContent();
        } catch (Exception ex) {
            throw new DataBoxException("Nemohu otevrit PKCS#7 obalku.", ex);
        }
    }

    /**
     * Vrátí obsah po odstranění PKCS7 obálky.
     */
    public InputStream readPKCS7(InputStream is) throws DataBoxException {
        try {
            CMSSignedData data = new CMSSignedData(is);
            CMSProcessable signedContent = data.getSignedContent();
            verifySignature(data);
            return (InputStream) signedContent.getContent();
        } catch (Exception ex) {
            throw new DataBoxException("Nemohu otevrit PKCS#7 obalku.", ex);
        }
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
    
    private void verifySignature(CMSSignedData data) throws Exception {
        if (isValidating) {
            SignerInformationStore signerStore = data.getSignerInfos();
            Collection<SignerInformation> signers = signerStore.getSigners();
            for (SignerInformation signer : signers) {
                X509Certificate cert = this.findCertificate(signer.getSID());
                if (cert == null) {
                    throw new DataBoxException("Nemohu najit certifikat.");
                }
                if (!signer.verify(cert, "BC")) {
                    throw new DataBoxException("Nemohu overit oproti certifikatu stazenou zpravu.");
                }
            }
        }
    }

    private X509Certificate findCertificate(SignerId signer) {
        return this.findCertificate(signer.getIssuer(), signer.getSerialNumber());
    }

    private X509Certificate findCertificate(Principal issuer, BigInteger serNumber) {
        for (X509Certificate cert : certs) {
            if (cert.getIssuerX500Principal().getName().equals(issuer.getName()) && cert.getSerialNumber().equals(serNumber)) {
                return cert;
            }
        }
        logger.info(String.format("Nemohu najit certifikat, vydavatel je %s "
                +", seriove cislo je %d.", issuer.getName(), serNumber));
        return null;
    }
}
