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

import static org.bibsonomy.model.util.BibTexUtils.cleanBibTex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.junit.Before;
import org.junit.Test;


public class CslModelConverterTest {
	final private Post<BibTex> inproceedingsPost = new Post<BibTex>();
	final private BibTex inproceedings = new BibTex();
	
	final private Post<BibTex> bookPost = new Post<BibTex>();
	final private BibTex book = new BibTex();
	
	final private Post<BibTex> articlePost = new Post<BibTex>();
	final private BibTex article = new BibTex();
	
	
	private List<PersonName> setAuthors() {
		List<PersonName> authors = new ArrayList<PersonName>();
		authors.add(new PersonName("Folke", "Mitzlaff"));
		authors.add(new PersonName("Martin", "Atzmueller"));
		authors.add(new PersonName("Gerd", "Stumme"));
		authors.add(new PersonName("Andreas", "Hotho"));
		return authors;
	}
	

	@Before
	public void setUpInproceedings() {
		
		inproceedings.setAuthor(setAuthors());
		
		inproceedings.setAddress("Bamberg, Germany");
		inproceedings.setBooktitle("Proc. LWA 2013 (KDML Special Track)");
		inproceedings.setEntrytype("inproceedings");
		inproceedings.setInterHash("73088600a500f7d06768615d6e1c2b3d");
		inproceedings.setIntraHash("820ffb2166b330bf60bb30b16e426553");
		inproceedings.setKey("MASH:13b");
		inproceedings.setPublisher("University of Bamberg");
		inproceedings.setSeries("Lecture Notes in Computer Science");
		inproceedings.setTitle("{On the Semantics of User Interaction in Social Media (Extended Abstract, Resubmission)}");
		inproceedings.setYear("2011");
		
		inproceedingsPost.setResource(inproceedings);
		inproceedingsPost.setUser(new User("test"));
	}

	@Before
	public void setUpBook() {
		
		book.setAddress("Berlin [u.a.]");
		book.setAuthor(setAuthors());
		book.setEntrytype("book");
		book.setMisc("ISBN = {3642380557}");
		book.setPublisher("Springer-Vieweg");
		book.setSeries("Xpert.press");
		
		book.setTitle("Informationelle Selbstbestimmung im Web 2.0 : Chancen und Risiken sozialer Verschlagwortungssysteme");
		book.setUrl("http://deposit.d-nb.de/cgi-bin/dokserv?id=4327654&prov=M&dok_var=1&dok_ext=htm");
		book.setYear("2013");
		
		bookPost.setResource(book);
		bookPost.setUser(new User("test"));
		
	}
	
	@Before
	public void setUpArticle() {
		
		List<PersonName> authors = new ArrayList<PersonName>();
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
		
	}
	
	@Test
	public void testConvertPostInproceedings() {
		final Record inproceedingsRec = CslModelConverter.convertPost(inproceedingsPost);
		//test series => collection title
		assertTrue(inproceedingsRec.getCollection_title().equals(cleanBibTex(inproceedings.getSeries())));
		//test booktitle => container title
		assertTrue(inproceedingsRec.getContainer_title().equals(cleanBibTex(inproceedings.getBooktitle())));
		//test inproceedings => paper-conference
		assertEquals(cleanBibTex(inproceedingsRec.getType()), "paper-conference");
	}
	
	@Test
	public void testConvertPostBook() {
		final Record bookRec = CslModelConverter.convertPost(bookPost);
		
		assertEquals(cleanBibTex(book.getTitle()), bookRec.getTitle());
		// TODO: @sbo: after fix for ordering in php-citeproc&co re-enable assertEquals(cleanBibTex(book.getYear()), bookRec.getIssued().getDate_parts().get(0).get(0));
	}
	
	@Test
	public void testConvertPostArticle() {
		//the journal entry of articles must be mapped to the container title
		
		Record articleRec = CslModelConverter.convertPost(articlePost);
		System.out.println(articleRec.getTitle());
		System.out.println(article.getTitle());
		System.out.println(articleRec.getContainer_title());  //Journal?
		System.out.println(article.getJournal());
		
		assertEquals(articleRec.getContainer_title(), article.getJournal());
	}

}
