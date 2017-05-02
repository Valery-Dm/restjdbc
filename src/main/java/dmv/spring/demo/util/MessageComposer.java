package dmv.spring.demo.util;

/**
 * Provides an ability to lazily compose messages from the base message with
 * replacement markers <tt>{index}</tt> and corresonding arguments
 * <pre>
 * 	Example:
 * 	base messgage - "Base message where {0} markers {1} with given {2}"
 * 	args - [several, replaced, arguments]
 * 	Result:
 * 	"Base message where several markers replaced with given arguments"
 * </pre>
 * @author dmv
 */
public class MessageComposer {

	/* Not for instantiation */
	private MessageComposer() {}

	/**
	 * Simple message composer, supports up to 10 markers (0 through 9),
	 * represented as numbers in curly braces, like <code>{0} or {1}</code>
	 * <p>
	 * See example at the Type level
	 * <p>
	 * Currently there are no support for escape character '\',
	 * so just don't use curly braces elsewhere except for markers.
	 * @param baseMsg the base message with <tt>{index}</tt> markers
	 * @param args Corresponding arguments. Method toString will be invoked
	 *             on them. If argument is null the word 'null' is printed
	 * @return composed message
	 * @throws IllegalArgumentException if incorrect number of arguments or no arguments
	 *                                  provided, or unrecognized markers used
	 */
	public static String compose(String baseMsg, Object... args) {
		if (baseMsg == null || baseMsg.length() == 0 || args.length == 0)
			throw new IllegalArgumentException("MessageComposer: nothing to compose - there are no base or no arguments");
		if (args.length > 10)
			throw new IllegalArgumentException("MessageComposer: too many arguments: more than 10");

		StringBuilder builder = new StringBuilder();
		int i = 0;
		boolean awaitN = false;
		for (char ch : baseMsg.toCharArray()) {
			if (awaitN) {
				if (ch == '}')
					awaitN = false;
				else if (ch - '0' == i) {
					if (i == args.length)
						throw new IllegalArgumentException("MessageComposer: incorrect number of arguments " + i);
					Object object = args[i++];
					builder.append(object != null ? object.toString() : "null");
				} else
					throw new IllegalArgumentException("MessageComposer: unrecognized position " + ch);
			} else {
				if (ch == '{')
					awaitN = true;
				else
					builder.append(ch);
			}
		}

		if (i != args.length)
			throw new IllegalArgumentException("MessageComposer: too many arguments: more than markers");
		return builder.toString();
	}
}
