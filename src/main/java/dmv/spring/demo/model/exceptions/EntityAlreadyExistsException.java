package dmv.spring.demo.model.exceptions;

/**
 * Describes situations when new entity creation is not possible
 * because of existence of another one with the same unique fields
 * @author dmv
 */
public class EntityAlreadyExistsException extends RuntimeException {

	/**
	 * Default serialization ID
	 */
	private static final long serialVersionUID = 1L;

	public EntityAlreadyExistsException() {
		super();
	}

	public EntityAlreadyExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EntityAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityAlreadyExistsException(String message) {
		super(message);
	}

	public EntityAlreadyExistsException(Throwable cause) {
		super(cause);
	}

}
