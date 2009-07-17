/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.model.util.BibTexUtils;


/** Converts EndNote (RIS) into BibTeX.
 * 
 * @author rja
 *
 */
public class EndnoteToBibtexConverter {
	
	private static final Logger log = Logger.getLogger(EndnoteToBibtexConverter.class);
	
	private static Map<String,String> endnoteToBibtexEntryTypeMap = new HashMap<String,String>();
	private static Map<String,String> endnoteToBibtexFieldMap     = new HashMap<String,String>();
	
	private static final Pattern _eachLinePattern       = Pattern.compile("(?s)((%\\S)\\s+(([^%]|%\\s)*))");

	
	static {
		// fill both maps to provide the data
		buildEndnoteToBibtexFieldMap();
		buildEndnoteToBibtexEntryTypeMap();
	}
	
	/**
	 * example for a StackOverFlowException (thos occure only on 32bit Java versions)
	 * @param args
	 */
	public static void main (String[] args){
		String test1 = "%A Knox, John A.\n" +
			"%T Recent and Future Trends in U.S. Undergraduate Meteorology Enrollments, Degree Recipients, and Employment Opportunities\n" +
			"%0 Journal Article\n" +
			"%D 2008\n" +
			"%J Bulletin of the American Meteorological Society\n" +
			"%P 873-883\n" +
			"\n" +
			"%V 89\n" +
			"%N 6\n" +
			"%U http://dx.doi.org/10.1175%2F2008BAMS2375.1\n" +
			"%8 June 01, 2008\n" +
			"%X Using data derived from the American Meteorological Society&#8211;University Corporation for Atmospheric Research Curricula and U.S. Department of Education statistics, it is found that the number of meteorology bachelor&#039;s degree recipients in the United States has reached a level unprecedented in at least the past 40 years: from 600 to possibly 1,000 graduates per year. Furthermore, this number is increasing at a rate of approximately 8&#037;&#8211;11&#037; per year. The number of meteorology majors has also increased up to 10&#037; per year since the late 1990s. The number of meteorology bachelor&#039;s degree recipients is projected to increase at a rate of approximately 5&#037;&#8211;12&#037; per year through 2011. This simultaneous combination of record numbers and rapid recent increases is not mirrored in other related fields or in the American college population as a whole, suggesting a meteorology-specific cause for the increase in undergraduates. These graduation and enrollment trends are compared to data on the employment of meteorology bachelor&#039;s degree holders. The number of entry-level meteorology positions in the United States available each year appears to be no more than about half the number of new degreed meteorologists. According to data from the U.S. Bureau of Labor Statistics, growth in meteorology employment has averaged 1.2&#037; per year from 1994&#8211;2004 and is expected to be no more than 1.6&#037; per year through 2014. These numbers and trends portend an increasing oversupply of meteorology graduates versus meteorology employment opportunities if current enrollment and employment trends continue. Possible responses of the meteorology community are explored.";

		String test2 = "%D 2003\n" +
			"%0 ARTICLE\n" +
			"%T Genomic gene clustering analysis of pathways in eukaryotes .\n" + 
			"%J Genome Research\n" +
			"%V 13\n" +
			"%P 875 882\n" + 
			"%A Lee JM\n" +
			"%A Sonnhammer ELL\n" +
			"%M WBPaper00005873\n" +
			"%X Genomic clustering of genes in a pathway is commonly found in prokaryotes due to transcriptional operons , but these are not present in most eukaryotes . Yet , there might be clustering to a lesser extent of pathway members in eukaryotic genomes , that assist coregulation of a set of functionally cooperating genes . We analyzed five sequenced eukaryotic genomes for clustering of genes assigned to the same pathway in the KEGG database . Between 98% and 30% of the analyzed pathways in a genome were found to exhibit significantly higher clustering levels than expected by chance . in descending order by the level of clustering , the genomes studied were Saccharomyces cerevisiae , Homo sapiens , Caenorhabditis elegans , Arabidopsis thaliana , and Drosophila melanogaster . Surprisingly , there is not much agreement between genomes in terms of which pathways are most clustered . Only seven of 69 pathways found in all species were significantly clustered in all five of them . This species-specific pattern of pathway clustering may reflect adaptations or evolutionary events unique to a particular lineage . We note that although operons are common in C elegans , only 58% of the pathways showed significant clustering , which is less than in human . Virtually all pathways in S cerevisiae showed significant";

		EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
		System.out.println(converter.processEntry(test1));
		System.out.println(converter.processEntry(test2));

	}
	
	/**
	 * @param in
	 * @return A reader returning the BibTeX.
	 */
	public Reader EndnoteToBibtex(BufferedReader in){
		
		try {
		
			StringBuffer result = new StringBuffer();

			// convert the data to a string
			String _fileToString = readLines(in);
			
			// split the endnote entry by 2 blank lines
			String[] _endNoteParts = _fileToString.split("(?m)^\\n{1}(?=%\\w{1}\\s{1})");
			
			// process each endnote entry
			for (String part: _endNoteParts){
				result.append("\n\n" + processEntry(part));
			}
			
			// convert the String back to a Reader
			return new BufferedReader(new StringReader(result.toString()));
			
		} catch (Exception e){
			log.fatal("Could not import EndNote: " + e);
		}
		return null;
	}
	
	/**
	 * method to process the data
	 * @param entry
	 * @return The processed string.
	 */
	public String processEntry(String entry){
		
		/*
		 * bug fix with large abstract
		 */
		String abstractEntry = null;
		int startAbstract = entry.indexOf("%X");
		if(startAbstract != -1){
			
			String entryToAbstract = entry.substring(0, startAbstract);
			int endAbstract = entry.indexOf("\n", startAbstract)+1;
			abstractEntry = entry.substring(startAbstract+3, endAbstract-1);
			String entryAfterAbstract = entry.substring(endAbstract);

			// build new entry without abstract
			entry = entryToAbstract + entryAfterAbstract;
		}
		
		
		
	
		final Map<String,String> map = new HashMap<String,String>();
		
		try {
			// need to get every line (i.e. %T Foo %K bar)
			final Matcher _eachLineMatcher = _eachLinePattern.matcher(entry);
			
			//process each line
			while (_eachLineMatcher.find()){
				
				final String _tempData = _eachLineMatcher.group(3).trim();
				
				if (_tempData == null){
					continue;
				}

				final String _tempLine = _eachLineMatcher.group(0).trim();
				final String _endnoteType = _eachLineMatcher.group(2).trim();
				
				/*
				 * map the reference type
				 */
				if (endnoteToBibtexEntryTypeMap.containsKey(_tempLine)) {
					map.put("type", endnoteToBibtexEntryTypeMap.get(_tempLine));
				} else {
					/*
					 * handle standard fields
					 */
					if (endnoteToBibtexFieldMap.containsKey(_endnoteType)) {
						final String bibtexFieldName = endnoteToBibtexFieldMap.get(_endnoteType);
						if (map.containsKey(bibtexFieldName)) {
							/*
							 * field already contained: special handling!
							 */
							if ("author".equals(bibtexFieldName)) {
								final String newAuthor = map.get(bibtexFieldName) + " and " + _tempData;
								map.put(bibtexFieldName, newAuthor);
							}
							
							if ("editor".equals(bibtexFieldName)) {
								final String newEditor = map.get(bibtexFieldName) + " and " + _tempData;
								map.put(bibtexFieldName, newEditor);
							}
						} else {
							map.put(bibtexFieldName, _tempData);
						}
					}
				}
			}
						
			/*
			 * special handling for ISBN/ISSN
			 */
			if (map.containsKey("isbn")) {
				final String isbn = map.get("isbn");
				if (isbn.length() == 8 || isbn.length() == 9) {
					map.remove("isbn");
					map.put("issn", isbn);
				}
			}
			
			/*
			 * build the bibtex key
			 */
			String year = map.get("year");
			if (year != null && year.length() != 4) {
				// if year is not valid (sometimes it contains day, month and/or time), ignore it
				year = null;
			}
			map.put("key", BibTexUtils.generateBibtexKey(map.get("author"), map.get("editor"), year, map.get("title")));
			
			/*
			 * build abstract
			 */
			if(abstractEntry != null)
				map.put("abstract", abstractEntry);
			
		} catch (RuntimeException e){
			log.fatal("Could not process the data: " + e);
		} catch (Exception e){
			log.fatal("Could not process the data: " + e);
		}
		
		/*
		 * in the end its necessary to build the complete bibtex part 
		 * i.e. 
		 * 
		 * @article{foo{
		 * author={bar and foo},
		 * title={foobars big revenge},
		 * abstract={this is a shot example}}
		 * 
		 */
		return buildBibtex(map);
	}
	
	//method to build a String with the complete Bibtex part
	private String buildBibtex(Map<String,String> data){
		
		//the StringBuffer to store the complete String
		StringBuffer result = new StringBuffer();
		
		try {
			/*
			 * if the data map is empty return null because it cant be an
			 * endnote file.
			 */
			if (data.isEmpty()){
				return null;
			}
			
			/*
			 * if no type is available the we use the following rules to specify the reference type
			 * 
			 * 1. %J and %V 					-> article (Journal Article)
			 * 2. %B 							-> incollection (Book Section)
			 * 3. %R but not %T 				-> misc (Report)
			 * 4. %I without %B, %J, or %R 		-> book (Book)
			 * 5. Neither %B, %J, %R, nor %I 	-> article (Journal Article)
			 */
			if (!data.containsKey("type")){
				if (data.containsKey("journal") && data.containsKey("volume")) {
					data.put("type", "article");
				} else if (data.containsKey("booktitle")) {
					data.put("type", "incollection");
				} else if (data.containsKey("address") && !(data.containsKey("booktitle") || data.containsKey("journal"))) {
					data.put("type", "book"); 
				} else if (!data.containsKey("booktitle") && !data.containsKey("journal") && !data.containsKey("%address")) {
					data.put("type", "article");
				}
			}
			
			// test if some necessary items were available, if not need to fix that with dummys
			if (!data.containsKey("type")) {
				data.put("type", "misc");
			}
			
			//now the bibtex part will be build completely starting with the type
			//clean up key (in InformaworldScraper a "," occurs in key, which results in a broken bibtex entry
			result.append("@" + data.get("type") + "{" + data.get("key").replace(",", "") + ",\n");
			data.remove("type");
			data.remove("key");
			
			//add every item of the data map to the stringbuffer
			for (String key : data.keySet()){
				result.append(key + " = {" + data.get(key) + "},\n");
			}

			// remove "," before last "\n"
			result.deleteCharAt(result.length() - 2 );
			
			// and at the end close the bibtexpart
			result.append("}");
			
		} catch (Exception e){
			log.fatal("Could not build the bibtex part :" + e);
		}
		
		//return the final String
		return result.toString();
	}
	
	//method to read all line out of the file and present them as String
	private String readLines(BufferedReader in) throws IOException{
		String line = null;
		StringBuffer _temp = new StringBuffer();
		
		try {
			while ((line = in.readLine()) != null){
				_temp.append(line+"\n");
			}
		} catch (Exception e){
			log.fatal("Could not read the lines out of the file :" + e);
		}
		
		return _temp.toString();
	}
	
	
	private static void buildEndnoteToBibtexFieldMap () {
		endnoteToBibtexFieldMap.put("%A",                     "author"); // Author
		endnoteToBibtexFieldMap.put("%B",                  "booktitle"); // Secondary Title
		endnoteToBibtexFieldMap.put("%C",               "howpublished"); // Place Published
		endnoteToBibtexFieldMap.put("%D",                       "year"); // Year
		endnoteToBibtexFieldMap.put("%E",                     "editor"); // Editor
//		endnoteToBibtexFieldMap.put("%F",                      "label"); // Label
//		endnoteToBibtexFieldMap.put("%G",                   "language"); // Language
//		endnoteToBibtexFieldMap.put("%H",          "translated_author"); // Translated Author
		endnoteToBibtexFieldMap.put("%I",                    "address"); // Publisher
		endnoteToBibtexFieldMap.put("%J",                    "journal"); // Journal Name
		endnoteToBibtexFieldMap.put("%K",                   "keywords"); // Keywords
//		endnoteToBibtexFieldMap.put("%L",                "call_number"); // Call Number
//		endnoteToBibtexFieldMap.put("%M",           "accession_number"); // Accession Number
		endnoteToBibtexFieldMap.put("%N",                     "series"); // Number
		endnoteToBibtexFieldMap.put("%P",                      "pages"); // Pages
//		endnoteToBibtexFieldMap.put("%Q",           "translated_title"); // Translated Title
//		endnoteToBibtexFieldMap.put("%R", "electronic_resource_number"); // Electronic Resource Number
//		endnoteToBibtexFieldMap.put("%S",             "tertiary title"); // Tertiary Title
		endnoteToBibtexFieldMap.put("%T",                      "title"); // Title
		endnoteToBibtexFieldMap.put("%U",                        "url"); // URL
		endnoteToBibtexFieldMap.put("%V",                     "volume"); // Volume
		// %W Database Provider
		endnoteToBibtexFieldMap.put("%X",                   "abstract"); // Abstract
		// %Y Tertiary Author
		endnoteToBibtexFieldMap.put("%Z",                     "annote"); // Notes
		// TODO: fehlende Felder (u.U. auskommentiert) einsetzen
		endnoteToBibtexFieldMap.put("%7",                    "edition"); // Edition
		endnoteToBibtexFieldMap.put("%&",                    "chapter"); // Section
		endnoteToBibtexFieldMap.put("%@",                       "isbn"); // ISBN/ISSN
		
	}
	
	private static void buildEndnoteToBibtexEntryTypeMap () {
		/*
		 * -> book
		 */
		endnoteToBibtexEntryTypeMap.put("%0 Book",            "book");
		endnoteToBibtexEntryTypeMap.put("%0 Edited Book",     "book");
		endnoteToBibtexEntryTypeMap.put("%0 Electronic Book", "book");
		
		/*
		 * -> article 
		 */
		endnoteToBibtexEntryTypeMap.put("%0 Journal Article",    "article"); 
		endnoteToBibtexEntryTypeMap.put("%0 Magazine Article",   "article");
		endnoteToBibtexEntryTypeMap.put("%0 Newspaper Article",  "article");
		endnoteToBibtexEntryTypeMap.put("%0 Electronic Article", "article");

		endnoteToBibtexEntryTypeMap.put("%0 Thesis",                 "mastersthesis");
		endnoteToBibtexEntryTypeMap.put("%0 Unpublished Work",       "unpublished");
		endnoteToBibtexEntryTypeMap.put("%0 Conference Paper",       "inproceedings");
		endnoteToBibtexEntryTypeMap.put("%0 Conference Proceedings", "proceedings");
		endnoteToBibtexEntryTypeMap.put("%0 Book Section",           "incollection");
		endnoteToBibtexEntryTypeMap.put("%0 Unpublished Work",       "unpublished");
			
		/*
		 * -> misc
		 */
		endnoteToBibtexEntryTypeMap.put("%0 Generic",               "misc"); 
		endnoteToBibtexEntryTypeMap.put("%0 Artwork",               "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Audiovisual Material",  "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Bill",                  "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Case",                  "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Chart or Table",        "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Classical Work",        "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Electronic Source",     "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Equation",              "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Figure",                "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Film or Broadcast",     "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Government Document",   "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Hearing",               "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Legal Rule/Regulation", "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Manuscript",            "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Map",                   "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Online Database",       "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Online Multimedia",     "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Patent",                "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Personal Communication","misc");
		endnoteToBibtexEntryTypeMap.put("%0 Report",                "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Statute",               "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Unused 1",              "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Unused 2",              "misc");
		endnoteToBibtexEntryTypeMap.put("%0 Unused 2",              "misc");

		
	}

	
}