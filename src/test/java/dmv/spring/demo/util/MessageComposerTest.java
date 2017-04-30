package dmv.spring.demo.util;

import static dmv.spring.demo.util.MessageComposer.compose;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageComposerTest {


	@Test
	public void testCorrectInput() {
		String act = compose("some {0} base string with {1}{2},    {3} arguments",
				                    "sample", 1, "  2", '3');
		String exp = "some sample base string with 1  2,    3 arguments";
		assertTrue(exp.equals(act));
	}

	@Test
	public void testInputWithNullArgs() {
		String act = compose("some {0} base string with {1}{2},    {3} arguments",
				                    "sample", 1, null, "");
		String exp = "some sample base string with 1null,     arguments";
		assertTrue(exp.equals(act));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testBaseNull() {
		compose(null, 1, 2);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testBaseEmpty() {
		compose("", "", "arg");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNoArgs() {
		compose("base");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testTooManyArgs() {
		compose("base", 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "overflow");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrongNumberOfMarkers() {
		compose("base some {0} base string with {2}", 1, 2);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrongNumberOfArgs() {
		compose("base some {0} base string with {1}", 1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrongNumberOfArgs2() {
		compose("base some {0} base string with {1}", 1, 2, 3);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrongMarker() {
		compose("base some {!} base string with {1}", 1, 2, 3);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrongMarker2() {
		compose("base some { base string with {1}", 1, 2, 3);
	}

}
