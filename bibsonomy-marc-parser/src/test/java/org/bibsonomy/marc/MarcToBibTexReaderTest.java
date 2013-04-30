package org.bibsonomy.marc;

import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class MarcToBibTexReaderTest {
	@Test
	public void testSomething() {
		MarcToBibTexReader reader = new MarcToBibTexReader();
		reader.read(getClass().getClassLoader().getResourceAsStream("marc_files/part29.dat"));
	}
}
