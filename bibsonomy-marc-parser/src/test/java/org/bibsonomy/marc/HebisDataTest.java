package org.bibsonomy.marc;

import java.util.Arrays;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class HebisDataTest extends AbstractDataDownloadingTestCase {
	@Test
	public void testHEB291478336() {
		BibTex bib = get("HEB291478336");
		Assert.assertEquals(Arrays.asList(new PersonName("Gene", "Smith")), bib.getAuthor());
	}
	
	@Test
	public void testDependentPartTitle() {
		BibTex bib = get("HEB105811688");
		Assert.assertEquals("Werkausgabe: Tractatus logico-philosophicus", bib.getTitle());
	}
	
	@Test
	public void testIndependentPartTitle() {
		BibTex bib = get("HEB02269773X");
		Assert.assertEquals("Seminumerical algorithms", bib.getTitle());
		Assert.assertEquals(Arrays.asList(new PersonName("Donald Ervin", "Knuth")), bib.getAuthor());
		Assert.assertEquals("book", bib.getEntrytype());
	}
	
	@Test
	public void testMultivolume() {
		BibTex bib = get("HEB009840354");
		Assert.assertEquals("The art of computer programming", bib.getTitle());
		Assert.assertEquals(Arrays.asList(new PersonName("Donald Ervin", "Knuth")), bib.getAuthor());
		Assert.assertEquals("mvbook", bib.getEntrytype());
	}
	
	@Test
	public void testSpecialChars() {
		BibTex bib = get("HEB107697521");
		Assert.assertEquals("Falar... ler... escrever... Português: um curso para estrangeiros", bib.getTitle());
		
	}
}
