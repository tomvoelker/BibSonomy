package org.bibsonomy.layout.csl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.junit.Before;
import org.junit.Test;

import static org.bibsonomy.model.util.BibTexUtils.cleanBibTex;

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
	public void convertPostTest() {
		
		/************************
		 *** INPROCEEDINGS    ***
		 ************************/
		
		Record inproceedingsRec = CslModelConverter.convertPost(inproceedingsPost);
		System.out.println(inproceedingsRec.getContainer_title());
		System.out.println(inproceedings.getBooktitle());
		System.out.println(inproceedingsRec.getType());
		System.out.println("-----------------------------------------------------------------");
		//test series => collection title
		Assert.assertTrue(inproceedingsRec.getCollection_title().equals(cleanBibTex(inproceedings.getSeries())));
		//test booktitle => container title
		Assert.assertTrue(inproceedingsRec.getContainer_title().equals(cleanBibTex(inproceedings.getBooktitle())));
		//test inproceedings => paper-conference
		Assert.assertEquals(cleanBibTex(inproceedingsRec.getType()), "paper-conference");
		
		
		/************************
		 *** BOOK             ***
		 ************************/
		
		Record bookRec = CslModelConverter.convertPost(bookPost);
		System.out.println(bookRec.getTitle());
		System.out.println(book.getTitle());
		
		System.out.println(bookRec.getIssued().getDate_parts().get(0).get(0));
		
		System.out.println("-----------------------------------------------------------------");
		
		Assert.assertEquals(cleanBibTex(book.getTitle()), bookRec.getTitle());
		Assert.assertEquals(cleanBibTex(book.getYear()), bookRec.getIssued().getDate_parts().get(0).get(0));
		
		
		/************************
		 *** ARTICLE          ***
		 ************************/
		
		//the journal entry of articles must be mapped to the container title
		
		Record articleRec = CslModelConverter.convertPost(articlePost);
		System.out.println(articleRec.getTitle());
		System.out.println(article.getTitle());
		System.out.println(articleRec.getContainer_title());  //Journal?
		System.out.println(article.getJournal());
		
		Assert.assertEquals(articleRec.getContainer_title(), article.getJournal());
		
	}

}
