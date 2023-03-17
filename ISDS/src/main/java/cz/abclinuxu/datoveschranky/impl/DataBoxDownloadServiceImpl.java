package cz.abclinuxu.datoveschranky.impl;

import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.interfaces.AttachmentStorer;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxDownloadService;
import cz.abclinuxu.datoveschranky.ws.dm.BigMessageDownloadResponse.DmReturnedMessage;
import cz.abclinuxu.datoveschranky.ws.dm.DmOperationsPortType;
import cz.abclinuxu.datoveschranky.ws.dm.DmVoDZPortType;
import cz.abclinuxu.datoveschranky.ws.dm.TReturnedMessage;
import cz.abclinuxu.datoveschranky.ws.dm.TStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author xrosecky
 */
public class DataBoxDownloadServiceImpl implements DataBoxDownloadService {

    private DmOperationsPortType dmOp = null;
    private DmVoDZPortType dmBigOp = null;
    private MessageValidator validator = null;
    static Logger logger = LoggerFactory.getLogger(DataBoxDownloadServiceImpl.class);

    public DataBoxDownloadServiceImpl(DmOperationsPortType dmOpService, DmVoDZPortType dataBigMessageOperationsService, MessageValidator validate) {
        this.dmOp = dmOpService;
        this.dmBigOp = dataBigMessageOperationsService;
        this.validator = validate;
    }

    /*
     * Stahne prijatou zpravu. Pro odeslane zpravy se muzi pouzit downloadSignedMessage - omezeni na strane ISDS..
    */
    public Message downloadMessage(MessageEnvelope envelope, AttachmentStorer storer) {
    	logger.info(String.format("downloadMessage: id:%s", envelope.getMessageID()));
        if (envelope.getType() != MessageType.RECEIVED) {
            throw new DataBoxException("Mohu stahnout pouze prijatou zpravu.");
        }
        Holder<TStatus> status = new Holder<TStatus>();
        Holder<TReturnedMessage> hMessage = new Holder<TReturnedMessage>();
        dmOp.messageDownload(envelope.getMessageID(), hMessage, status);
        ErrorHandling.throwIfError("Nemohu stahnout prijatou zpravu.", status.value);
        logger.info(String.format("downloadMessage successfull"));
        TReturnedMessage message = hMessage.value;
        return validator.buildMessage(envelope, message, storer);
    }

    public Message downloadBigMessage(MessageEnvelope envelope,
			AttachmentStorer storer) {
    	logger.info(String.format("downloadMessage: id:%s", envelope.getMessageID()));
    	Holder<TStatus> status = new Holder<TStatus>();
    	Holder<DmReturnedMessage> hMessage = new Holder<DmReturnedMessage>();
    	dmBigOp.bigMessageDownload(envelope.getMessageID(), hMessage, status);
    	DmReturnedMessage message = hMessage.value;
    	return validator.buildBigMessage(envelope, message, storer);
	}

	public void downloadSignedMessage(MessageEnvelope env, OutputStream os) {
		logger.info(String.format("downloadSignedMessage: id:%s", env.getMessageID()));
        String id = env.getMessageID();
        Holder<byte[]> messageAsPKCS7 = new Holder<byte[]>();
        Holder<TStatus> status = new Holder<TStatus>();
        switch (env.getType()) {
            case RECEIVED:
                dmOp.signedMessageDownload(id, messageAsPKCS7, status);
                break;
            case SENT:
                dmOp.signedSentMessageDownload(id, messageAsPKCS7, status);
                break;
            default:
                throw new DataBoxException("Neodeslanou zpravu nelze stahnout");
        }
        ErrorHandling.throwIfError("Nemohu stahnout podepsanou zpravu.", status.value);
        try {
            os.write(messageAsPKCS7.value);
            os.flush();
	    logger.info(String.format("downloadSignedMessage successfull"));
        } catch (IOException ioe) {
            throw new DataBoxException("Chyba pri zapisu do vystupniho proudu.", ioe);
        }
    }

	public void downloadSignedBigMessage(MessageEnvelope env,
			OutputStream os) {
		logger.info(String.format("downloadSignedBigMessage: id:%s", env.getMessageID()));
        String id = env.getMessageID();
        Holder<DataHandler> dataHandler = new Holder<DataHandler>();
        Holder<TStatus> status = new Holder<TStatus>();
        switch (env.getType()) {
            case RECEIVED:
            	dmBigOp.signedBigMessageDownload(id, dataHandler, status);
                break;
            case SENT:
            	dmBigOp.signedSentBigMessageDownload(id, dataHandler, status);
                break;
            default:
                throw new DataBoxException("Neodeslanou zpravu nelze stahnout");
        }
        ErrorHandling.throwIfError("Nemohu stahnout podepsanou zpravu.", status.value);
        try {
            InputStream is = dataHandler.value.getDataSource().getInputStream();
            this.copy(is, os);
            os.flush();
	    logger.info(String.format("downloadSignedMessage successfull"));
        } catch (IOException ioe) {
            throw new DataBoxException("Chyba pri zapisu do vystupniho proudu.", ioe);
        }
	}

	private void copy(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[8096];
		int length = 0;
		while ((length = is.read(buffer)) != -1) {
		    os.write(buffer, 0, length);
		}
	}

}
