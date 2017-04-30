package dmv.spring.demo.model.entity.sqlfields;

/**
 * Common type for Entity SQL Fields names
 * (they are also known as Column names).
 * To be used throughout the program.
 * @author dmv
 */
public interface EntityFields {

	/**
	 * Get field's name (name of a column in corresponding table)
	 * @return field's name
	 */
	String getName();
}
