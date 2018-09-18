/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.marc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.ValidationUtils;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;

import bibtex.parser.ParseException;

/**
 * @author jensi
 */
public class HebisDataTest extends AbstractDataDownloadingTestCase {
	
	@Test
	public void testHEB291478336() {
		BibTex bib = get("HEB291478336");
		assertEquals(Arrays.asList(new PersonName("Gene", "Smith")), bib.getAuthor());
	}
	
	@Test
	public void testDependentPartTitle() {
		BibTex bib = get("HEB105811688");
		assertEquals("Werkausgabe [in 8 Bd.] : 1. Tractatus logico-philosophicus", bib.getTitle());
	}
	
	@Test
	public void testAdditionalTitleStuff() {
		BibTex bib = get("HEB325875243");
		assertEquals("Tabellen für die Rezeptur : Plausibilitätsprüfung in der Apotheke", bib.getTitle());
	}
	
	@Test
	public void testPhysicalInfo() {
		BibTex bib = get("HEB178705594");
		assertEquals("25", bib.getPages());
		assertEquals("10", bib.getDay());
		assertEquals("5", bib.getMonth());
		assertEquals("article", bib.getEntrytype());
		assertEquals("[Frankfurter Rundschau <Frankfurt, Main> / S-Ausgabe]: Frankfurter Rundschau", bib.getJournal());
	}
	
	@Test
	public void testTarantino() {
		BibTex bib = get("HEB221544364");
		assertTrue(bib.getAuthor().contains(new PersonName("Quentin", "Tarantino")));
		assertEquals("electronic", bib.getEntrytype());
	}
	
	@Test
	public void testMonsterCd() {
		BibTex bib = get("HEB320628140");
		assertFalse(ValidationUtils.present(bib.getEditor()));
		// TODO: ask martina whether we should really change this
		// assertEquals(Arrays.asList(new PersonName("noauthor", "HEB320628140")), bib.getAuthor());
		assertEquals("audio", bib.getEntrytype());
	}
	
	@Test
	public void testAnd() throws ParseException, IOException {
		final BibTex bib = get("HEB21356114X");
		assertEquals(Arrays.asList(new PersonName("", "{School of English Literature, Language and Linguistics}")), bib.getAuthor());
		final String bibtexString = BibTexUtils.toBibtexString(bib);
		final BibTex reparsedBib = new SimpleBibTeXParser().parseBibTeX(bibtexString);
		assertEquals(Arrays.asList(new PersonName("", "{School of English Literature, Language and Linguistics}")), reparsedBib.getAuthor());
	}
	
	@Test
	public void testEditorOnly() {
		BibTex bib = get("HEB321472683");
		assertEquals(Collections.emptyList(), bib.getAuthor());
		assertEquals(Arrays.asList(new PersonName("Adeline", "Ooi")), bib.getEditor());
		assertEquals("book", bib.getEntrytype());
	}
	
	@Test
	public void testEditorOnly2() {
		BibTex bib = get("HEB321193962");
		assertEquals(Collections.emptyList(), bib.getAuthor());
		assertEquals(Arrays.asList(new PersonName("Lili", "Gaesset")), bib.getEditor());
		assertEquals("book", bib.getEntrytype());
	}
	
	@Test
	public void testIndependentPartTitle() {
		BibTex bib = get("HEB02269773X");
		assertEquals("Seminumerical algorithms", bib.getTitle());
		assertEquals(Arrays.asList(new PersonName("Donald Ervin", "Knuth")), bib.getAuthor());
		assertEquals("book", bib.getEntrytype());
	}
	
	@Test
	public void testMultivolume() {
		BibTex bib = get("HEB009840354");
		assertEquals("The art of computer programming", bib.getTitle());
		assertEquals(Arrays.asList(new PersonName("Donald Ervin", "Knuth")), bib.getAuthor());
		assertEquals("mvbook", bib.getEntrytype());
	}
	
	@Test
	public void testSpecialChars() {
		BibTex bib = get("HEB107697521");
		assertEquals("Falar... ler... escrever... Português : um curso para estrangeiros", bib.getTitle());
		
	}
	
	@Test
	public void testPhdThesis() {
		BibTex bib = get("HEB231779038");
		assertEquals("Formal concept analysis and tag recommendations in collaborative tagging systems", bib.getTitle());
		assertEquals("phdthesis", bib.getEntrytype());
		assertEquals("2011", bib.getYear());
		assertEquals("Zugl.: Kassel, Univ., Diss. 2010", bib.getNote());
		assertEquals("9783898383325", bib.getMiscField("isbn"));
	}
	
	@Test
	public void testContributor() {
		BibTex bib = get("HEB226718743");
		assertEquals("Algorithmen - eine Einführung", bib.getTitle());
		assertThat(bib.getAuthor(), IsCollectionContaining.hasItem(new PersonName("Thomas H.", "Cormen")));
		assertEquals("3., überarb. und erw. Aufl.", bib.getEdition());
		assertEquals("München", bib.getAddress());
		assertEquals("Oldenbourg", bib.getPublisher());
		assertEquals("9783486590029", bib.getMiscField("isbn"));
		assertEquals("2010", bib.getYear());
		assertEquals("book", bib.getEntrytype());
	}
	
	@Test
	public void testContributor2() {
		BibTex bib = get("HEB207127891");
		assertThat(bib.getAuthor(), IsCollectionContaining.hasItem(new PersonName("Yannick", "Versley")));
		assertEquals("Tagging kausaler Relationen", bib.getTitle());
		assertEquals("In dieser Diplomarbeit geht es um kausale Beziehungen zwischen Ereignissen und Erklärungsbeziehungen zwischen Ereignissen, bei denen kausale Relationen eine wichtige Rolle spielen. Nachdem zeitliche Relationen einerseits ihrer einfacheren Formalisierbarkeit und andererseits ihrer gut sichtbaren Rolle in der Grammatik (Tempus und Aspekt, zeitliche Konjunktionen) wegen in jüngerer Zeit stärker im Mittelpunkt des Interesses standen, soll hier argumentiert werden, dass kausale Beziehungen und die Erklärungen, die sie ermöglichen, eine wichtigere Rolle im Kohärenzgefüge des Textes spielen. Im Gegensatz zu tiefenʺ Verfahren, die auf einer detaillierten semantischen Repräsentation des Textes aufsetzen und infolgedessen für unrestringierten Text m. E. nicht geeignet sind, wird hier untersucht, wie man dieses Ziel erreichen kann, ohne sich auf eine aufwändig konstruierte Wissensbasis verlassen zu müssen.", bib.getAbstract());
		assertEquals("Hamburg, Univ., Dipl.-Arbeit, 2004", bib.getNote());
		//assertEquals("masterthesis", bib.getEntrytype());
		assertEquals("phdthesis", bib.getEntrytype());
	}
	
	@Test
	public void testCallimachus() {
		BibTex bib = get("HEB30399794X");
		assertEquals( Arrays.asList(new PersonName("Annette", "Harder")), bib.getEditor());
		assertEquals( Arrays.asList(new PersonName("", "Callimachus")), bib.getAuthor());
		assertEquals( "Callimachus, Aetia introduction, text, translation, and commentary : 2. Commentary", bib.getTitle());
	}
}
