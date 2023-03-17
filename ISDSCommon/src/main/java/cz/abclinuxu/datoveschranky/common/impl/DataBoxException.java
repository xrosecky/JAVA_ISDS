package cz.abclinuxu.datoveschranky.common.impl;

/**
 *
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public class DataBoxException extends RuntimeException {

    private static final long serialVersionUID = 3L;
    
    private Status status = null;
    
    public DataBoxException(String mess) {
        super(mess);
    }

    public DataBoxException(String mess, Exception cause) {
        super(mess, cause);
    }
    
    public DataBoxException(String mess, Status st) {
        super(mess);
        this.status = st;
    }

    public Status getStatus() {
		return status;
	}

	@Override
    public String toString() {
        if (status == null) {
            return super.toString();
        } else {
            return super.toString() + " " + status.getStatusCode() + ":" + status.getStatusMesssage();
        }
    }
}
