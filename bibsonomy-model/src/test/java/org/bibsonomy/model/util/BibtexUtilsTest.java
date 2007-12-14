package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.bibsonomy.model.BibTex;
import org.junit.Test;

import sun.security.action.GetLongAction;

/**
 * Testcase for the Bibtex Utils class
 * 
 * @version $Id$
 * @author dbenz
 */
public class BibtexUtilsTest {

	private static final Logger LOGGER = Logger.getLogger(BibtexUtilsTest.class);
	
	/**
	 * tests generation of bibtex string
	 */
	@Test
	public void toBibtexString() {
		BibTex bib = new BibTex();
		bib.setEntrytype("inproceedings");
		bib.setBibtexKey("KIE");
		bib.setTitle("The most wonderfult title on earth");
		bib.setAuthor("Hans Dampf and Peter Silie");
		bib.setJournal("Journal of the most wonderful articles on earth");
		bib.setYear("2525");
		bib.setVolume("3");
		
		String expectedBibtex = 
			"@inproceedings{KIE,\n" +
			"author = {Hans Dampf and Peter Silie},\n" +
			"journal = {Journal of the most wonderful articles on earth},\n" +
			"title = {The most wonderfult title on earth},\n" +
			"volume = {3},\n" +
			"year = {2525}\n}";

		assertEquals(expectedBibtex, BibTexUtils.toBibtexString(bib));
	}
}