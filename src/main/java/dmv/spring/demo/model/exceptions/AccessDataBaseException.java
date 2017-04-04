package dmv.spring.demo.model.exceptions;

/**
 * Stands for Internal (or system) errors that could happen
 * when a repository service accesses the database
 * @author dmv
 */
public class AccessDataBaseException extends RuntimeException {

	/**
	 * Default serialization ID
	 */
	private static final long serialVersionUID = 1L;

	public AccessDataBaseException() {
		super();
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
