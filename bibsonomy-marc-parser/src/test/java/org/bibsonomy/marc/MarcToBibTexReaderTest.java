package org.bibsonomy.marc;

import java.util.Collection;

import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class MarcToBibTexReaderTest {
	@Test
	public void testSomething() {
		MarcToBibTexReader reader = new MarcToBibTexReader();
		Collection<BibTex> bibs = reader.read(getClass().getClassLoader().getResourceAsStream("marc_files/part29.dat"));
		for (BibTex b : bibs) {
			System.out.println(b);
		}
	}
}
