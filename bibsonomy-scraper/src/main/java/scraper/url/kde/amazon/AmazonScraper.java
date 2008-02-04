package scraper.url.kde.amazon;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

import com.amazonaws.a2s.AmazonA2S;
import com.amazonaws.a2s.AmazonA2SClient;
import com.amazonaws.a2s.AmazonA2SException;
import com.amazonaws.a2s.AmazonA2SLocale;
import com.amazonaws.a2s.model.Item;
import com.amazonaws.a2s.model.ItemAttributes;
import com.amazonaws.a2s.model.ItemLookupRequest;
import com.amazonaws.a2s.model.ItemLookupResponse;
import com.amazonaws.a2s.model.Items;
import com.amazonaws.a2s.model.Tag;

/**
 * Scraper for the amazon onlineshop
 * @author tst
 */
public class AmazonScraper implements Scraper {
	
	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(AmazonScraper.class);
	/**
	 * info
	 */
	private static final String INFO = "Extracts publications from the Amazon onlineshop.";

	/** 
	 * access key for the amazon web service (aws)
	 */
	private static String AMAZON_ACCESS_KEY = null;
	/**
	 * get access key from environment
	 */
	static {
		try {
			AMAZON_ACCESS_KEY = ((String) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("AmazonAccessKey"));
		} catch (NamingException e) {
			log.fatal("Could not read Amazon access key from environemnt: " + e);
		}
	}
	
	/**
	 * regex for searching the key of the current product
	 */
	private static final String PATTERN_PRODUKT_KEY = "/([^/]+)/ref=";
	private final Pattern pattern = Pattern.compile(PATTERN_PRODUKT_KEY);
	
	/**
	 * INFO field of this scraper
	 */
	public String getInfo() {
		return INFO;
	}

	/**
	 * this scraper
	 */
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	/**
	 * Scrapes a product from amazon
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().contains("amazon")){
			// extract product key from url
			final Matcher matcher = pattern.matcher(sc.getUrl().toString());
			if(matcher.find()){
				String amazonProduktKey = matcher.group();
				amazonProduktKey = amazonProduktKey.substring(1, amazonProduktKey.length()-5);

				/*
				 * init aws client with same locale as the shop from url
				 */
				final AmazonA2S service = getService(sc.getUrl());
				if(service != null){
					try {
						/*
						 * get data about product from aws
						 */
						final ItemLookupRequest request = new ItemLookupRequest();
						request.getResponseGroup().add("ItemAttributes");
						request.getResponseGroup().add("TagsSummary");
						final ItemLookupResponse response = service.itemLookup(request.withItemId(amazonProduktKey));
						
						/*
						 * get first item
						 */
						final List<Items> itemsList = response.getItems();
						for(final Items items: itemsList){
							final List<Item> itemList = items.getItem();
							if(itemList != null && itemList.size() > 0){
								final Item item = itemList.get(0);
								
								if(item != null){
									/*
									 * get BibTeX
									 */
									final StringBuffer bibtex = getBibTeX(item);
			                        /*
			                         * return result
			                         */
			                        sc.setBibtexResult(bibtex.toString());
			                        sc.setScraper(this);
			                        return true;
								}
							}
						}
					} catch (final AmazonA2SException ex) {
						throw new ScrapingException(ex);
					}
				}
			}
		}
		return false;
	}

	/** Generates the BibTeX string from an Amazon item object.
	 * 
	 * @param item - an item representing a book from Amazon.
	 * 
	 * @return The BibTeX string representing the item.
	 */
	private StringBuffer getBibTeX(final Item item) {
		final StringBuffer bibtex = new StringBuffer();
		
		/*
		 * default type = book
		 */
		bibtex.append("@book{" + item.getASIN() + ",\n");
		
		/*
		 * list with detailed data about the product
		 */
		ItemAttributes itemAttributes = item.getItemAttributes();
		
		// get tags from 
		List<Tag> tags = null; 
		if(item.getTags() != null)
			tags = item.getTags().getTag();
		
		// build authors
		List<String> authors = itemAttributes.getAuthor();//list
		String authorString = "";
		for(String author: authors){
			if(authorString.equals(""))
				authorString = author;
			else
				authorString = authorString + " and " + author;
		}
		if(!authorString.equals(""))
			bibtex.append("author = {" + authorString +"},\n");
		
		// build title
		String title = itemAttributes.getTitle();
		if(title != null)
			bibtex.append("title = {" + title +"},\n");
		 
		// build publisher
		String publisher = itemAttributes.getPublisher();
		if(publisher != null)
			bibtex.append("publisher = {" + publisher +"},\n");
		else{
		    publisher = itemAttributes.getDepartment();
		    if(publisher != null)
		    	bibtex.append("publisher = {" + publisher +"},\n");
		}

		// build edition
		String edition = itemAttributes.getEdition();
		if(edition != null)
			bibtex.append("edition = {" + edition +"},\n");
		
		// build publication date
		String date = itemAttributes.getPublicationDate();
		if(date == null) {
			date = itemAttributes.getReleaseDate();
		};
		if (date != null) {
			// try to find the year only if not just copy all info.
		    Pattern datePattern = Pattern.compile(".*([0-9]{4}).*");
		    Matcher dateMatcher = datePattern.matcher(date);
		    if (dateMatcher.find())
		    {
		    	bibtex.append("year = {" + dateMatcher.group(1) +"},\n");
		    } else {
				bibtex.append("year = {" + date +"},\n");
		    }
		}

		
		// add URL to product page
		String url = item.getDetailPageURL();
		if(url != null)
			bibtex.append("url = {" + url +"},\n");
		
		// build address
		String address = itemAttributes.getCountry();
		if(address != null)
			bibtex.append("address = {" + address +"},\n");
		
		// build dewey
		String dewey = itemAttributes.getDeweyDecimalNumber();
		if(dewey != null)
			bibtex.append("dewey = {" + dewey +"},\n");
		
		// build ean
		String ean = itemAttributes.getEAN();
		if(ean != null)
			bibtex.append("ean = {" + ean +"},\n");
		
		// build isbn
		String isbn = itemAttributes.getISBN();
		if(isbn != null)
			bibtex.append("isbn = {" + isbn +"},\n");
		
		// build asin
		
		bibtex.append("asin = {" + item.getASIN() +"},\n");

		/*
		 * Auswahl möglicher weiterer misc Values 
		itemAttributes.getLanguageName();
		itemAttributes.getLegalDisclaimer();
		itemAttributes.getManufacturer();
		itemAttributes.getNumberOfPages();
		itemAttributes.getProductTypeName();
		itemAttributes.getProductTypeSubcategory();
		itemAttributes.getGenre();
		itemAttributes.getLabel();
		*/
		
		// build keywords
		if(tags != null){
		    String tagString = "";
		    for(Tag tag:tags){
		    	tagString += tag.getName() + " ";
		    }
		    if(!tagString.equals(""))
		    	bibtex.append("keywords = {" + tagString +"},\n");
		}
		
		// remove last added ","
		bibtex.deleteCharAt(bibtex.length()-2);
		
		// finish
		bibtex.append("}");
		return bibtex;
	}

	/** Returns an Amazon service object for the given URL.
	 * 
	 * @param url - the requested URL. Depending on the TLD of the URL the corrrect
	 * instance of the Amazon service is returned.
	 * @return An instance of an Amazon service.
	 * 
	 * @throws ScrapingException - if the Amazon service is not available for that URL.
	 */
	private AmazonA2S getService(final URL url) throws ScrapingException {
		final String host = url.getHost();
		AmazonA2S service = null;
		if(host.contains("amazon.com"))
			service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.US);
		else if(host.contains("amazon.de"))
			service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.DE);
		else if(host.contains("amazon.ca"))
			service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.CA);
		else if(host.contains("amazon.fr"))
			service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.FR);
		else if(host.contains("amazon.co.jp") || host.contains("amazon.jp"))
			service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.JP);
		else if(host.contains("amazon.co.uk"))
			service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.UK);
		else {
			throw new ScrapingException("The Amazon service for " + host + " is currently not supported.");
		}
		return service;
	}

}
