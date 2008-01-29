package scraper.url.kde.amazon;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

/**
 * Scraper for the amazon onlineshop
 * @author tst
 */
public class AmazonScraper implements Scraper {
	
	/**
	 * info
	 */
	private static final String INFO = "AmazonScraper: Extracts publications from the amazon onlineshop.";

	/**
	 * access key for the amazon web service (aws)
	 */
	private static final String AMAZON_ACCESS_KEY = "****";
	
	/**
	 * regex for searching the key of the current product
	 */
	private static final String PATTERN_PRODUKT_KEY = "/([^/]+)/ref=";
	
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
			
			//extract product key from url
			Pattern pattern = Pattern.compile(PATTERN_PRODUKT_KEY);
			Matcher matcher = pattern.matcher(sc.getUrl().toString());
			if(matcher.find()){
				String amazonProduktKey = matcher.group();
				amazonProduktKey = amazonProduktKey.substring(1, amazonProduktKey.length()-5);

				/*
				 * init aws client with same locale as the shop from url
				 */
				AmazonA2S service = null;
				if(sc.getUrl().getHost().contains("amazon.com"))
					service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.US);
				else if(sc.getUrl().getHost().contains("amazon.de"))
					service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.DE);
				else if(sc.getUrl().getHost().contains("amazon.ca"))
					service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.CA);
				else if(sc.getUrl().getHost().contains("amazon.fr"))
					service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.FR);
				else if(sc.getUrl().getHost().contains("amazon.co.jp") || sc.getUrl().getHost().contains("amazon.jp"))
					service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.JP);
				else if(sc.getUrl().getHost().contains("amazon.co.uk"))
					service = new AmazonA2SClient(AMAZON_ACCESS_KEY, "", AmazonA2SLocale.UK);
				else
					throw new ScrapingException("not supportet amazon shop");
				
				if(service != null){
					try {
						
						/*
						 * get data about product from aws
						 */
						ItemLookupRequest request = new ItemLookupRequest();
						request.getResponseGroup().add("ItemAttributes");
						request.getResponseGroup().add("TagsSummary");
						ItemLookupResponse response = service.itemLookup(request.withItemId(amazonProduktKey));
						
						/*
						 * build bibtex
						 */
						List<Items> itemsList = response.getItems();
						for(Items items: itemsList){
							List<Item> itemList = items.getItem();
							if(itemList != null && itemList.size() > 0){
								Item item = itemList.get(0);
								
								if(item != null){
									StringBuffer bibtex = new StringBuffer();
									
									/*
									 * default type = book
									 */
									bibtex.append("@book{" + amazonProduktKey + ",\n");
									
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
			                        if(date != null)
			                        	bibtex.append("year = {" + date +"},\n");
			                        else{
			                        	date = itemAttributes.getReleaseDate();
				                        if(date != null)
				                        	bibtex.append("year = {" + date +"},\n");
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
			                        bibtex.append("asin = {" + amazonProduktKey +"},\n");
		
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
		
			                        // return bibtex
			                        sc.setBibtexResult(bibtex.toString());
			                        sc.setScraper(this);
			                        return true;
								}
							}
						}
					} catch (AmazonA2SException ex) {
						throw new ScrapingException(ex);
					}
				}
			}
		}
		return false;
	}

}
