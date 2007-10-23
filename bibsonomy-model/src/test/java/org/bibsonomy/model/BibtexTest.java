package org.bibsonomy.model;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public class BibtexTest {

	private final static Logger log = Logger.getLogger(BibtexTest.class);
	
	/**
	 * check toBibTex String method
	 */
	@Test
	public void testToBibtexString() {
		BibTex b = new BibTex();
		b.setBibtexKey("myBibtexKey");
		b.setEntrytype("inproceedings");
		b.setTitle("My Title");
		b.setAuthor("Hans Dampf and Peter Silie");
		b.setYear("2006");
		log.debug(b.toBibtexString());
	}
}