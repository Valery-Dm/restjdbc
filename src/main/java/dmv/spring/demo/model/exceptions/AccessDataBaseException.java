package dmv.spring.demo.model.exceptions;

/**
 * Stands for Internal (or system) errors that could happen
 * when a repository service accesses the database
 * @author dmv
 */
public class AccessDataBaseException extends RuntimeException {
	
	private String laterMsg;

	/**
	 * Default serialization ID
	 */
	private static final long serialVersionUID = 1L;

	public AccessDataBaseException() {
		super();
	}
	
	/**
	 * If exception is prepared early and may not be thrown
	 * at all, this message may be set (and composed from other strings)
	 * later. Note, that if initial message exists the latter one
	 * will be ignored.
	 * @param msg the late message
	 */
	public void setMessage(String msg) {
		laterMsg = msg;
	}

	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}

	@Override
	public String getMessage() {
		String msg = super.getMessage();
		if (msg == null || msg.length() == 0)
			msg = laterMsg;
		return msg;
	}

	public AccessDataBaseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AccessDataBaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessDataBaseException(String message) {
		super(message);
	}

	public AccessDataBaseException(Throwable cause) {
		super(cause);
	}

}
