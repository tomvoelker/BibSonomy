package org.bibsonomy.marc;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.ValidationUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.matchers.IsCollectionContaining;

import bibtex.parser.ParseException;

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
		Assert.assertEquals("Werkausgabe : Tractatus logico-philosophicus", bib.getTitle());
	}
	
	@Test
	public void testAdditionalTitleStuff() {
		BibTex bib = get("HEB325875243");
		Assert.assertEquals("Tabellen für die Rezeptur : Plausibilitätsprüfung in der Apotheke", bib.getTitle());
	}
	
	@Test
	public void testPhysicalInfo() {
		BibTex bib = get("HEB178705594");
		Assert.assertEquals("25", bib.getPages());
		Assert.assertEquals("10", bib.getDay());
		Assert.assertEquals("5", bib.getMonth());
		Assert.assertEquals("article", bib.getEntrytype());
		Assert.assertEquals("[Frankfurter Rundschau <Frankfurt, Main> / S-Ausgabe]: Frankfurter Rundschau", bib.getJournal());
	}
	
	@Test
	public void testTarantino() {
		BibTex bib = get("HEB221544364");
		Assert.assertTrue(bib.getAuthor().contains(new PersonName("Quentin", "Tarantino")));
		Assert.assertEquals("electronic", bib.getEntrytype());
	}
	
	@Test
	public void testMonsterCd() {
		BibTex bib = get("HEB320628140");
		Assert.assertFalse(ValidationUtils.present(bib.getEditor()));
		// TODO: ask martina whether we should really change this
		// Assert.assertEquals(Arrays.asList(new PersonName("noauthor", "HEB320628140")), bib.getAuthor());
		Assert.assertEquals("audio", bib.getEntrytype());
	}
	
	@Test
	public void testAnd() throws ParseException, IOException {
		final BibTex bib = get("HEB21356114X");
		Assert.assertEquals(Arrays.asList(new PersonName("", "{School of English Literature, Language and Linguistics}")), bib.getAuthor());
		final String bibtexString = BibTexUtils.toBibtexString(bib);
		final BibTex reparsedBib = new SimpleBibTeXParser().parseBibTeX(bibtexString);
		Assert.assertEquals(Arrays.asList(new PersonName("", "{School of English Literature, Language and Linguistics}")), reparsedBib.getAuthor());
	}
	
	@Test
	public void testEditorOnly() {
		BibTex bib = get("HEB321472683");
		Assert.assertEquals(Collections.emptyList(), bib.getAuthor());
		Assert.assertEquals(Arrays.asList(new PersonName("Adeline", "Ooi")), bib.getEditor());
		Assert.assertEquals("book", bib.getEntrytype());
	}
	
	@Test
	public void testEditorOnly2() {
		BibTex bib = get("HEB321193962");
		Assert.assertEquals(Collections.emptyList(), bib.getAuthor());
		Assert.assertEquals(Arrays.asList(new PersonName("Lili", "Gaesset")), bib.getEditor());
		Assert.assertEquals("book", bib.getEntrytype());
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
		Assert.assertEquals("Falar... ler... escrever... Português : um curso para estrangeiros", bib.getTitle());
		
	}
	
	@Test
	public void testPhdThesis() {
		BibTex bib = get("HEB231779038");
		Assert.assertEquals("Formal concept analysis and tag recommendations in collaborative tagging systems", bib.getTitle());
		Assert.assertEquals("phdthesis", bib.getEntrytype());
		Assert.assertEquals("2011", bib.getYear());
		Assert.assertEquals("Zugl.: Kassel, Univ., Diss. 2010", bib.getNote());
		Assert.assertEquals("9783898383325", bib.getMiscField("isbn"));
	}
	
	@Test
	public void testContributor() {
		BibTex bib = get("HEB226718743");
		Assert.assertEquals("Algorithmen - eine Einführung", bib.getTitle());
		Assert.assertThat(bib.getAuthor(), IsCollectionContaining.hasItem(new PersonName("Thomas H.", "Cormen")));
		Assert.assertEquals("3., überarb. und erw. Aufl.", bib.getEdition());
		Assert.assertEquals("München", bib.getAddress());
		Assert.assertEquals("Oldenbourg", bib.getPublisher());
		Assert.assertEquals("9783486590029", bib.getMiscField("isbn"));
		Assert.assertEquals("2010", bib.getYear());
		Assert.assertEquals("book", bib.getEntrytype());
	}
	
	@Test
	public void testContributor2() {
		BibTex bib = get("HEB207127891");
		Assert.assertThat(bib.getAuthor(), IsCollectionContaining.hasItem(new PersonName("Yannick", "Versley")));
		Assert.assertEquals("Tagging kausaler Relationen", bib.getTitle());
		Assert.assertEquals("In dieser Diplomarbeit geht es um kausale Beziehungen zwischen Ereignissen und Erklärungsbeziehungen zwischen Ereignissen, bei denen kausale Relationen eine wichtige Rolle spielen. Nachdem zeitliche Relationen einerseits ihrer einfacheren Formalisierbarkeit und andererseits ihrer gut sichtbaren Rolle in der Grammatik (Tempus und Aspekt, zeitliche Konjunktionen) wegen in jüngerer Zeit stärker im Mittelpunkt des Interesses standen, soll hier argumentiert werden, dass kausale Beziehungen und die Erklärungen, die sie ermöglichen, eine wichtigere Rolle im Kohärenzgefüge des Textes spielen. Im Gegensatz zu tiefenʺ Verfahren, die auf einer detaillierten semantischen Repräsentation des Textes aufsetzen und infolgedessen für unrestringierten Text m. E. nicht geeignet sind, wird hier untersucht, wie man dieses Ziel erreichen kann, ohne sich auf eine aufwändig konstruierte Wissensbasis verlassen zu müssen.", bib.getAbstract());
		Assert.assertEquals("Hamburg, Univ., Dipl.-Arbeit, 2004", bib.getNote());
		//Assert.assertEquals("masterthesis", bib.getEntrytype());
		Assert.assertEquals("phdthesis", bib.getEntrytype());
		
		
	}
}
