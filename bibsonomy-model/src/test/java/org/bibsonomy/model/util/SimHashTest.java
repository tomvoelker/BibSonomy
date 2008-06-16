package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * Testcase for the SimHash class
 * 
 * @author Dominik Benz
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class SimHashTest {

	/**
	 * tests getSimHash
	 */
	@Test
	public void getSimHash() {
		final BibTex bibTex = new BibTex();		
		assertEquals("a127fd1f86e4ab650f2216f09992afa4", SimHash.getSimHash(bibTex, HashID.getSimHash(0)));
		assertEquals("23b58def11b45727d3351702515f86af", SimHash.getSimHash(bibTex, HashID.getSimHash(1)));
		assertEquals("7bb0edd98f22430a03b67f853e83c2ca", SimHash.getSimHash(bibTex, HashID.getSimHash(2)));
		assertEquals("", SimHash.getSimHash(bibTex, HashID.getSimHash(3)));
	}
	
	/**
	 * some tests to check author normalization
	 * 
	 * it only makes sense to test this with simHash1 (interhash), because normalization
	 * is not applied for intra-hash computation
	 */
	@Test
	public void testAuthorNormalization() {
		BibTex bib = new BibTex();
		bib.setAuthor("b and A");
		final String interHash = SimHash.getSimHash(bib, HashID.getSimHash(1));
		bib.setAuthor("B and A");
		assertEquals(interHash, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		bib.setAuthor("a and b");
		assertEquals(interHash, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		bib.setAuthor("a and a and b and b and a and B and A and B and a");
		assertEquals(interHash, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		
		bib.setAuthor("John Paul and Bridget Jones");
		final String interHash2 = SimHash.getSimHash(bib, HashID.getSimHash(1));
		bib.setAuthor("JoHN pAUl and brIDgeT JOneS");
		assertEquals(interHash2, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		bib.setAuthor("J PAUL and b jones");
		assertEquals(interHash2, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		
		bib.setAuthor("John and Paul John");
		final String interHash3 = SimHash.getSimHash(bib, HashID.getSimHash(1));
		bib.setAuthor("JoHN and PAUL jOhN");
		assertEquals(interHash3, SimHash.getSimHash(bib, HashID.getSimHash(1)));
	}
}