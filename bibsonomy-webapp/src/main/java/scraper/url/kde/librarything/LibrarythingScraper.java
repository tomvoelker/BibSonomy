package scraper.url.kde.librarything;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

/**
 * Scraper for www.librarything.com
 * Its supports the following URL-prefixes:
 * http://www.librarything.com/work-info/
 * http://www.librarything.com/work/
 * @author tst
 */
public class LibrarythingScraper implements Scraper {
	
	private static final String INFO = "LibrarythingScraper: extracts publication from http://www.librarything.com/work-info and convert it to bibtex. If a http://www.librarything.com/work page is selectd, then the scraper trys to download the according work-info page.";
	
	
	private static final String URL_LIBRARYTHING_PAGE = "http://www.librarything.com";
	
	private static final String URL_LIBRARYTHING_PAGE_HOST = "librarything.com";

	/*
	 * supported URLs
	 */

	private static final String PATH_LIBRARYTHING_WORKINFO_PAGE = "/work-info";
	
	private static final String PATH_LIBRARYTHING_WORK_PAGE = "/work";
	

	/*
	 * URL elements which are neede to switch between book information and social information
	 */
	
	private static final String WORK = "work";
	
	private static final String WORK_INFO = "work-info";
	
	
	/*
	 * supported tabel headlines
	 */
	
	private static final String TABEL_HEADLINE_THIS_BOOK = "This book";
	
	private static final String TABEL_HEADLINE_ABOUT_THE_WORK = "About the work";
	
	
	/*
	 * used HTML elements & attributes (+values)
	 */
	
	private static final String HTML_ELEMENT_DIV = "div";
	
	private static final String HTML_ELEMENT_H2 = "h2";
	
	private static final String HTML_ELEMENT_TABEL = "table";
	
	private static final String HTML_ELEMENT_TR = "tr";
	
	private static final String HTML_ELEMENT_TD = "td";
	
	private static final String HTML_ELEMENT_B = "b";
	
	private static final String HTML_ATTRIBUTE_CLASS = "class";
	
	private static final String HTML_ATTRIBUTE_CLASS_VALUE = "middlecolumn";
	
	/*
	 * librarything publication elements
	 */
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_AUTHOR = "Author";
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_AUTHORS = "Authors";
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_OTHER_AUTHOR = "Outher author";
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_TITLE = "Title";
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_TAG = "Tags";
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_DATE = "Date";
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_PUBLICATION = "Publication";
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_ISBN = "ISBN";
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_ISBN_10 = "ISBN-10";
	
	private static final String LIBRARYTHING_PUBL_ELEMENT_ISBN_13 = "ISBN-13";
	
	/*
	 * check mode
	 */
	
	private boolean thisBook = false;

	private String author = null;
	private String title = null;
	private String year = null;
	private String misc = null;
	private String publisher = null;
	private String keywords = null;
	private String key = "librarything";
	
	/**
	 * This Scraper works only with the following URL-prefixes and no selected text.
	 * http://www.librarything.com/work-info/
	 * http://www.librarything.com/work/
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().contains("librarything") && sc.getSelectedText() == null){
			URL url = null;
			
			// build .com url			
			if(!sc.getUrl().getHost().contains("librarything.com")){
				String urlString = sc.getUrl().toString();
				
				// extract part bevor tld
				int indexLibrarything = urlString.indexOf("librarything.");
				String bevorTLD = urlString.substring(0, indexLibrarything + 13);
				
				//extract part after tld
				urlString = urlString.substring(indexLibrarything+12);
				int indexFirstSlash = urlString.indexOf("/");
				String afterTLD = urlString.substring(indexFirstSlash);
				
				// build new .com url
				try {
					url = new URL(bevorTLD + "com" + afterTLD);
				} catch (MalformedURLException e) {
					throw new ScrapingException(e);
				}
				
			// is already a .com url
			}else{
				url = sc.getUrl();
			}
			
			Document libraryThingDocument = null;
			
			/*
			 * check if www.librarything.com URL is supported
			 */
			if(url.getPath().startsWith(PATH_LIBRARYTHING_WORKINFO_PAGE)){
				try {
					libraryThingDocument = getDOMDocument(sc.getContentAsString(url));
				} catch (ScrapingException e) {
					throw e;
				} catch (IOException e) {
					throw new ScrapingException("LibrarythingScraper: failure during reading data from www.librarything.com");
				}
			}else if(url.getPath().startsWith(PATH_LIBRARYTHING_WORK_PAGE)){
				try {
					String urlWorkInfo = url.toString().replaceFirst(WORK, WORK_INFO);
					libraryThingDocument = getDOMDocument(sc.getContentAsString(new URL(urlWorkInfo)));
				} catch (MalformedURLException e) {
					throw new ScrapingException("LibrarythingScraper: failure during building new URL. " + e.getMessage());
				} catch (ScrapingException e) {
					throw e;
				} catch (IOException e) {
					throw new ScrapingException("LibrarythingScraper: failure during reading data from www.librarything.com");
				}
			}else{
				throw new ScrapingException("LibrarythingScraper: not supported librarything URL.");
			}
			
			try{
				/*
				 * extract publication from DOM
				 */
				Element tabel = getTabelWithPublication(libraryThingDocument);
				
				NodeList trs = tabel.getElementsByTagName(HTML_ELEMENT_TR);
				for(int i=0; i<trs.getLength(); i++){
					Element tr = (Element) trs.item(i);
					extractPublicationElement(tr);
				}

				StringBuffer resultBibtex = new StringBuffer();
				resultBibtex.append("@book{" + key + ",\n");			
			
				if(author != null)
					resultBibtex.append("\tauthor = {" + author + "},\n");
				if(title != null)
					resultBibtex.append("\ttitle = {" + title + "},\n");
				if(year != null)
					resultBibtex.append("\tyear = {" + year + "},\n");
				if(misc != null)
					resultBibtex.append("\t" + misc + ",\n");
				if(url != null)
					resultBibtex.append("\turl = {" + url + "},\n");
				if(publisher != null)
					resultBibtex.append("\tpublisher = {" + publisher + "},\n");
				if(keywords != null)
					resultBibtex.append("\tkeywords = {" + keywords + "},\n");
				else
					resultBibtex.append("\tkeywords = {},\n");
				
				String bibResult = resultBibtex.toString();
				bibResult = bibResult.substring(0, bibResult.length()-2) + "\n}";

				/*
				 * generate bibtex result string
				 */

				sc.setBibtexResult(bibResult);
				sc.setScraper(this);
				return true;
			}catch(Exception e){
				throw new ScrapingException("LibrarythingScraper: failure during parsing publication. " + e.getMessage());
			}
			
		}
		return false;
	}
	
	/**
	 * Parse a given HTML string to a DOM document
	 * @param stringDocument HTML document as string
	 * @return DOM document
	 * @throws IOException
	 */
	private Document getDOMDocument(String stringDocument) throws IOException{
		ByteArrayInputStream is = new ByteArrayInputStream(stringDocument.getBytes());
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		Document dom = tidy.parseDOM(is, null);
		is.close();
		return dom;
	}
	
	/**
	 * Extract the tabel element which has a supported headline.
	 * Supported headlines are:
	 * -"This book"
	 * -"About the work"
	 * @param dom DOM document of a supported librarything.com page 
	 * @return Tabel element which contains the publication
	 */
	private Element getTabelWithPublication(Document dom){
		Element result = null;
		
		NodeList divs = dom.getElementsByTagName(HTML_ELEMENT_DIV);
		
		//possible publication tabels
		Element this_book = null;
		Element about_the_work = null;
		
		for(int i=0; i<divs.getLength(); i++){
			// check all h2 elements which are childs from a div element.
			Element div = (Element)divs.item(i);
			
			// work only with div which has the class attribute value "middlecolumn"
			Attr classAttr = div.getAttributeNode(HTML_ATTRIBUTE_CLASS);
			if(classAttr != null && classAttr.getValue().equals(HTML_ATTRIBUTE_CLASS_VALUE)){
			
				NodeList h2s = div.getElementsByTagName(HTML_ELEMENT_H2);
				
				/*
				 * search h2 element which match to one of the supported headlines
				 */
				for(int j=0; j<h2s.getLength(); j++){
					Element h2 = (Element) h2s.item(j);		
					
					// Headline "This books" occurs befor "About the work". If "This book" exist use that tabel else...
					if(h2.getChildNodes().item(0).getNodeValue().equals(TABEL_HEADLINE_THIS_BOOK)){
						this_book = (Element) h2.getNextSibling();
					}
					// ... use the tabel with headline "About the work".
					else if(h2.getChildNodes().item(0).getNodeValue().equals(TABEL_HEADLINE_ABOUT_THE_WORK)){
						about_the_work = (Element) h2.getNextSibling();
					}
				}
				
			}
		}
		
		/*
		 * Select as first result the "This book" element, if it exist and it is a tabel element.
		 * Select as second result the  "About the work" element, if it exist and it is a tabel element.
		 * If no one match, the result is null.
		 */
		if(this_book != null && this_book.getTagName().equals(HTML_ELEMENT_TABEL)){
			result = this_book;
			thisBook = true;
		}else if(about_the_work != null && about_the_work.getTagName().equals(HTML_ELEMENT_TABEL)){
			result = about_the_work;
		}
		
		return result;
	}
	
	/**
	 * Extract a publication field from a tr element
	 * @param tr tr Element which contains the field
	 * @param result Bibtex resource which contains the result of the extraction
	 */
	private void extractPublicationElement(Element tr){
		try{
			/*
			 * get field of publication and its value
			 */
			String field = null;
			String fieldValue = null;
			
			// field name is the value of the first td element
			field = tr.getFirstChild().getFirstChild().getNodeValue();
			
			// the path to the field value depends on the name of the field
			if(field.equals(LIBRARYTHING_PUBL_ELEMENT_AUTHOR) || field.equals(LIBRARYTHING_PUBL_ELEMENT_TITLE))
				fieldValue = tr.getFirstChild().getNextSibling().getFirstChild().getFirstChild().getNodeValue();
			else
				fieldValue = tr.getFirstChild().getNextSibling().getFirstChild().getNodeValue();
			
			/*
			 * store only supported fields
			 */


			if(field.equals(LIBRARYTHING_PUBL_ELEMENT_AUTHOR)){
				// this is the first author element
				author = fieldValue;
			}else if(field.equals(LIBRARYTHING_PUBL_ELEMENT_AUTHORS)){
				// additional Author element
				author = author + " and " + fieldValue;
			}else if(field.equals(LIBRARYTHING_PUBL_ELEMENT_OTHER_AUTHOR)){
				// additional Author element
				author = author + " and " + fieldValue;
			}else if(field.equals(LIBRARYTHING_PUBL_ELEMENT_TITLE)){
				title = fieldValue;
			}else if(field.equals(LIBRARYTHING_PUBL_ELEMENT_TAG)){
				keywords = fieldValue;
			}else if(field.equals(LIBRARYTHING_PUBL_ELEMENT_DATE)){
				// get year from date
				Pattern pattern = Pattern.compile("\\d{4}");
				Matcher matcher = pattern.matcher(fieldValue);
				if(matcher.find()){
					year = matcher.group();
					// add year to bibtexkey
					key = key + year;
				}
			}else if(field.equals(LIBRARYTHING_PUBL_ELEMENT_PUBLICATION)){
				publisher = fieldValue;		
			}else if(field.equals(LIBRARYTHING_PUBL_ELEMENT_ISBN)){
				// number with 13 digits is isbn13
				Pattern pattern13 = Pattern.compile("\\d{13}");
				Matcher matcher13 = pattern13.matcher(fieldValue);
				String pattern13Result = null;
				if(matcher13.find()){
					pattern13Result = matcher13.group(); 
					if(misc == null || misc.trim().equals(""))
						misc = "isbn13 = {" + pattern13Result + "}";
					else
						misc = misc + ", isbn13 = {" + pattern13Result + "}";
				}
				
				// number with 10 digits and which is not a part of isbn13 is normal isbn
				Pattern pattern10 = Pattern.compile("\\d{10}");
				Matcher matcher10 = pattern10.matcher(fieldValue);
				while(matcher10.find()){
					String pattern10result = matcher10.group();
					if(pattern13Result != null && !pattern13Result.contains(pattern10result)){
						if(misc == null || misc.trim().equals(""))
							misc = "isbn = {" + pattern10result + "}";
						else
							misc = misc + ", isbn = {" + pattern10result + "}";
					}
				}
				
			}else if(field.equals(LIBRARYTHING_PUBL_ELEMENT_ISBN_10) && !thisBook){
				if(misc == null || misc.trim().equals(""))
					misc = "isbn = {" + fieldValue + "}";
				else
					misc = misc + ", isbn = {" + fieldValue + "}";
			}else if(field.equals(LIBRARYTHING_PUBL_ELEMENT_ISBN_13) && !thisBook){
				if(misc == null || misc.trim().equals(""))
					misc = "isbn13 = {" + fieldValue + "}";
				else
					misc = misc + ", isbn13 = {" + fieldValue + "}";
			}
		}catch(Exception e){
			// do nothing, no value for this field
		}
	}
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

}
