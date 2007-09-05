package scraper.InformationExtraction;

import ie.ie.BibExtraction;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

public class IEScraper implements Scraper {

	/**
	 * Extract a valid Bibtex entry from a given publication snippet by using information extraction.
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		String selectedText = sc.getSelectedText();
		/*
		 * don't scrape, if there is nothing selected
		 */
		if (selectedText == null || selectedText.trim().equals("")) return false;
		
		try {
			HashMap<String, String> map = new BibExtraction().extraction(selectedText);

			if (map != null) {

				/*
				 * build Bibtex String from map
				 */
				sc.setBibtexResult(getBibtexString(map));

				/*
				 * save the text the user selected (and the scraper used) into map 
				 */
				map.put("ie_selectedText", selectedText);

				/*
				 * save map data as XML in scraping context 
				 */
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				XMLEncoder encoder = new XMLEncoder(bout);
				encoder.writeObject(map);
				encoder.close();
				sc.setMetaResult(bout.toString("UTF-8"));

				/*
				 * returns itself to know, which scraper scraped this
				 */
				sc.setScraper(this);

				return true;
			}
		
		} catch (IOException e) {
			throw new ScrapingException(e);
		} catch (ClassNotFoundException e) {
			throw new ScrapingException(e);
		} catch (NamingException e) {
			throw new ScrapingException(e);
		}
		return false;
	}

	/** Builds a bibtex string from a given hashmap
	 * @param map
	 * @return
	 */
	private String getBibtexString(HashMap<String, String> map) {
		/*
		 * start with a stringbuffer which contains start of bibtex entry
		 */
		StringBuffer bib = new StringBuffer("@misc{ieKey,");

		/*
		 * iterate over fields of hashmap
		 */
		for (String key:map.keySet()) {
			/*
			 * extract value
			 */
			String value = map.get(key);
			if (value != null) {
				/*
				 *  replace curly brackets
				 */
				value = value.replace('{','(').replace('}',')');
				/*
				 * clean person lists
				 */
				if ("author".equals(key) || "editor".equals(key)) {
					value = cleanPerson(value);
				}
				/*
				 * extract year from date
				 */
				if ("date".equals(key)) {
					/*
					 * look for YYYY, extract and append it
					 */
					Pattern p = Pattern.compile("\\d{4}");
					Matcher m = p.matcher(value);
					if (m.find()) {
						bib.append("year = {" + m.group() + "},");
					}
				}
				bib.append(key + " = {" + value + "},");
			}
			
		}

		/*
		 * replace last "," with a closing curly bracket "}"
		 */
		bib.replace(bib.length()-1, bib.length(), "}");
		
		return bib.toString();
	}

	/** Returns a self description of this scraper.
	 * 
	 */
	public String getInfo() {
		return "IEScraper: Extraction of bibliographic references by information extraction. Author: Thomas Steuber";
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper)this);
	}
	
	/** Cleans a String containing person names.
	 * @param person
	 * @return
	 */
	private String cleanPerson(String person) {
		// not modify references with " and " 
		if (person.contains(" and "))
			return person;
		// in references with ";" and no " and " replace ";" with " and "
		if (person.contains(";"))
			return person.replace(";", " and ");
		// in references with "," and no " and " or ";" replace "," with " and "
		if (person.contains(","))
			return person.replace(",", " and ");
		
		return person;
	}
	
}
