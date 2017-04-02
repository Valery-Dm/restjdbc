package dmv.spring.demo.model.exceptions;

/**
 * Describes the situation when update or retrieval or deletion
 * is not possible because specified entity does not exist in database
 * @author dmv
 */
public class EntityDoesNotExistException extends RuntimeException {

	/**
	 * Default serialization ID
	 */
	private static final long serialVersionUID = 1L;

	public EntityDoesNotExistException() {
		super();
	}

	public EntityDoesNotExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EntityDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityDoesNotExistException(String message) {
		super(message);
	}

	public EntityDoesNotExistException(Throwable cause) {
		super(cause);
	}

}
