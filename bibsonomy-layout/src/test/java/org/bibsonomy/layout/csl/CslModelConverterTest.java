/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.layout.csl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.junit.Test;

/**
 * @author sbo
 */
public class CslModelConverterTest {
	
	private static List<PersonName> generateAuthors() {
		final List<PersonName> authors = new ArrayList<PersonName>();
		authors.add(new PersonName("Folke", "Mitzlaff"));
		authors.add(new PersonName("Martin", "Atzmueller"));
		authors.add(new PersonName("Gerd", "Stumme"));
		authors.add(new PersonName("Andreas", "Hotho"));
		return authors;
	}
	
	private static Post<BibTex> generateInproceedings() {
		final Post<BibTex> inproceedingsPost = new Post<BibTex>();
		final BibTex inproceedings = new BibTex();
		inproceedings.setAuthor(generateAuthors());
		
		inproceedings.setAddress("Bamberg, Germany");
		inproceedings.setBooktitle("Proc. LWA 2013 (KDML Special Track)");
		inproceedings.setEntrytype(BibTexUtils.INPROCEEDINGS);
		inproceedings.setInterHash("73088600a500f7d06768615d6e1c2b3d");
		inproceedings.setIntraHash("820ffb2166b330bf60bb30b16e426553");
		inproceedings.setKey("MASH:13b");
		inproceedings.setPublisher("University of Bamberg");
		inproceedings.setSeries("Lecture Notes in Computer Science");
		inproceedings.setTitle("{On the Semantics of User Interaction in Social Media (Extended Abstract, Resubmission)}");
		inproceedings.setYear("2011");
		
		inproceedingsPost.setResource(inproceedings);
		inproceedingsPost.setUser(new User("test"));
		
		return inproceedingsPost;
	}

	private static Post<BibTex> generateInCollection() {
		final Post<BibTex> inproceedingsPost = new Post<BibTex>();
		final BibTex incollection = new BibTex();
		incollection.setAuthor(generateAuthors());
		incollection.setEditor(generateAuthors());
		incollection.setAddress("Cambridge, MA");
		incollection.setBooktitle("Mind: {I}ntroduction to Cognitive Science");
		incollection.setEntrytype(BibTexUtils.INCOLLECTION);
		incollection.setInterHash("73088600a500f7d06768615d6e1c2b3d");
		incollection.setIntraHash("820ffb2166b330bf60bb30b16e426553");
		incollection.setKey("MASH:13b");
		incollection.setPublisher("University of Bamberg");
		incollection.setSeries("Lecture Notes in Computer Science");
		incollection.setTitle("{Connections}");
		incollection.setChapter("2");
		incollection.setYear("2011");
		
		inproceedingsPost.setResource(incollection);
		inproceedingsPost.setUser(new User("test"));
		
		return inproceedingsPost;
	}
	
	private static Post<BibTex> generateBook() {
		final Post<BibTex> bookPost = new Post<BibTex>();
		final BibTex book = new BibTex();
		book.setAddress("Berlin [u.a.]");
		book.setAuthor(generateAuthors());
		book.setEntrytype(BibTexUtils.BOOK);
		book.setMisc("ISBN = {3642380557}");
		book.setPublisher("Springer-Vieweg");
		book.setSeries("Xpert.press");
		
		book.setTitle("Informationelle Selbstbestimmung im Web 2.0 : Chancen und Risiken sozialer Verschlagwortungssysteme");
		book.setUrl("http://deposit.d-nb.de/cgi-bin/dokserv?id=4327654&prov=M&dok_var=1&dok_ext=htm");
		book.setYear("2013");
		
		bookPost.setResource(book);
		bookPost.setUser(new User("test"));
		return bookPost;
	}
	
	private static Post<BibTex> generateArticle() {
		final Post<BibTex> articlePost = new Post<BibTex>();
		final BibTex article = new BibTex();
		
		final List<PersonName> authors = new ArrayList<PersonName>();
		authors.add(new PersonName("Jeffrey", "Dean"));
		authors.add(new PersonName("Ghemawat", "Sanjay"));
		
		article.setAddress("New York, NY, USA");
		article.setAuthor(authors);
		article.setMisc("doi = {10.1145/1327452.1327492}");
		article.setEntrytype(BibTexUtils.ARTICLE);
		article.setInterHash("b8a00982bf087c8543855897b7362a04");
		article.setIntraHash("bff539224836d703c2d21141985fa1a3");
		article.setMisc("issn = {0001-0782}");
		article.setJournal("Communications of the ACM");
		article.setMonth("jan");
		article.setNumber("1");
		article.setPages("107--113");
		article.setPublisher("ACM");
		article.setTitle("MapReduce: simplified data processing on large clusters");
		article.setUrl("http://doi.acm.org/10.1145/1327452.1327492");
		article.setVolume("51");
		article.setYear("2008");
		
		articlePost.setResource(article);
		articlePost.setUser(new User("test"));
		
		return articlePost;
	}
	
	private static Post<BibTex> generateAdditionalFieldsPost() {
		
		final Post<BibTex> additionalFieldsPost = new Post<BibTex>();
		final List<PersonName> authors = new ArrayList<PersonName>();
		authors.add(new PersonName("Jeffrey", "Dean"));
		authors.add(new PersonName("Ghemawat", "Sanjay"));
		
		final BibTex article = new BibTex();
		article.setAddress("New York, NY, USA");
		article.setAuthor(authors);
		article.setMisc("doi = {10.1145/1327452.1327492}," + System.getProperty("line.separator")
				+ "pdf = {http://research.google.com/archive/mapreduce-osdi04.pdf}," + System.getProperty("line.separator")
				+ "slides = {http://research.google.com/archive/mapreduce.html}," + System.getProperty("line.separator")
				+ "urn = {urn:nbn:de:0290-opus-16749}," + System.getProperty("line.separator")
				+ "issn = {0001-0782}");
		//article.parseMiscField();
		article.setEntrytype(BibTexUtils.ARTICLE);
		article.setInterHash("b8a00982bf087c8543855897b7362a04");
		article.setIntraHash("bff539224836d703c2d21141985fa1a3");
		article.setJournal("Communications of the ACM");
		article.setMonth("jan");
		article.setNumber("1");
		article.setPages("107--113");
		article.setPublisher("ACM");
		article.setTitle("MapReduce: simplified data processing on large clusters");
		article.setUrl("http://doi.acm.org/10.1145/1327452.1327492");
		article.setVolume("51");
		article.setYear("2008");
		
		/*
		article.addMiscField("pdf", "http://research.google.com/archive/mapreduce-osdi04.pdf");
		article.addMiscField("slides", "http://research.google.com/archive/mapreduce.html");
		article.addMiscField("urn", "urn:nbn:de:0290-opus-16749");
		*/
		
		additionalFieldsPost.setResource(article);
		additionalFieldsPost.setUser(new User("test"));
		
		return additionalFieldsPost;
	}
	
	@Test
	public void testConvertPostInproceedings() {
		final Post<BibTex> inproceedingPost = generateInproceedings();
		final BibTex inproceedings = inproceedingPost.getResource();
		final Record inproceedingsRec = CslModelConverter.convertPost(inproceedingPost);
		// test series => collection title
		assertEquals(BibTexUtils.cleanBibTex(inproceedings.getSeries()), inproceedingsRec.getCollection_title());
		// test booktitle => container title
		assertEquals(BibTexUtils.cleanBibTex(inproceedings.getBooktitle()), inproceedingsRec.getContainer_title());
		// test inproceedings => paper-conference
		assertEquals(BibTexUtils.cleanBibTex(inproceedingsRec.getType()), "paper-conference");
	}
	
	@Test
	public void testConvertPostBook() {
		final Post<BibTex> bookPost = generateBook();
		final BibTex book = bookPost.getResource();
		final Record bookRec = CslModelConverter.convertPost(bookPost);
		
		assertEquals(BibTexUtils.cleanBibTex(book.getTitle()), bookRec.getTitle());
		// TODO: @sbo: after fix for ordering in php-citeproc&co re-enable assertEquals(BibTexUtils.cleanBibTex(book.getYear()), bookRec.getIssued().getDate_parts().get(0).get(0));
	}
	
	@Test
	public void testConvertPostArticle() {
		// the journal entry of articles must be mapped to the container title
		final Post<BibTex> articlePost = generateArticle();
		final BibTex article = articlePost.getResource();
		final Record articleRec = CslModelConverter.convertPost(articlePost);
		assertEquals(article.getTitle(), articleRec.getTitle());
		assertEquals(article.getJournal(), articleRec.getContainer_title()); //Journal?
	}
	
	/*@Test
	public void testConvertPostIncollection() {
		final Post<BibTex> incollectionPost = generateInCollection();
		final Record record = CslModelConverter.convertPost(incollectionPost);
		
		final BibTex incollection = incollectionPost.getResource();
		assertEquals(BibTexUtils.cleanBibTex(incollection.getTitle()), record.getTitle());
		assertEquals(incollection.getChapter(), record.getChapter_number());
	}
	
	@Test
	public void testAdditionalFields() {
		final Post<BibTex> post = generateAdditionalFieldsPost();
		final Record record = CslModelConverter.convertPost(post);
		final BibTex m = post.getResource();
		assertEquals(BibTexUtils.cleanBibTex(m.getMiscField("pdf")), record.getPdf());
		assertEquals(BibTexUtils.cleanBibTex(m.getMiscField("slides")), record.getSlides());
		assertEquals(BibTexUtils.cleanBibTex(m.getMiscField("urn")), record.getUrn());
	}
*/
}
