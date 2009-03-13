/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.comparators.BibTexPostComparator;
import org.bibsonomy.model.comparators.BibTexPostInterhashComparator;

/**
 * Some BibTex utility functions
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class BibTexUtils {

	/*
	 * patterns used for matching
	 */
	private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");
	private static final Pattern DOI_PATTERN = Pattern.compile("http://.+/(.+?/.+?$)");
	private static final Pattern MISC_FIELD_PATTERN = Pattern.compile("([a-zA-Z0-9]+)\\s*=\\s*\\{(.*?)\\}");

	private static final List<String> EXCLUDE_FIELDS = Arrays.asList(new String[] { "bibtexAbstract", "misc", "simHash0", "simHash1", "simHash2", "simHash3", "entrytype", "bibtexKey" });

	
	/**
	 * Builds a string from a given bibtex object which can be used to build an OpenURL
	 * see http://www.exlibrisgroup.com/sfx_openurl.htm
	 *
	 * @param bib the bibtex object
	 * @return the DESCRIPTION part of the OpenURL of this BibTeX object
	 */
	public static String getOpenurl(final BibTex bib) {
		// stores the completed URL (just the DESCRIPTION part)
		final StringBuffer openurl = new StringBuffer();

		/*
		 * extract first authors parts of the name
		 */
		// get first author (if author not present, use editor)
		String author = bib.getAuthor();
		if (author == null) {
			author = bib.getEditor();
		}
		// TODO: this is only neccessary because of broken (DBLP) entries which have neither author nor editor!
		if (author == null) {
			author = "";
		}
		author = author.replaceFirst(" and .*", "").trim();
		// get first authors last name
		String aulast = author.replaceFirst(".* ", "");
		// get first authors first name
		String aufirst = author.replaceFirst("[\\s\\.].*", "");
		// check, if first name is just an initial
		String auinit1 = null;
		if (aufirst.length() == 1) {
			auinit1 = aufirst;
			aufirst = null;
		}
		
		// parse misc fields
		parseMiscField(bib);
		// extract DOI
		String doi = bib.getMiscField("doi");
		if (doi != null) {
			// TODO: urls rausfiltern testen
			final Matcher m = DOI_PATTERN.matcher(doi);
			if (m.find()) {
				doi = m.group(1);
			}
		}

		try {
			// append year (always given!)
			openurl.append("date=" + bib.getYear().trim());
			// append doi
			if (doi != null) {
				appendOpenURL(openurl,"id", "doi:" + doi.trim());
			}
			// append isbn + issn
			appendOpenURL(openurl,"isbn", bib.getMiscField("isbn"));
			appendOpenURL(openurl,"issn", bib.getMiscField("issn"));
			// append name information for first author
			appendOpenURL(openurl, "aulast", aulast);
			appendOpenURL(openurl, "aufirst", aufirst);
			appendOpenURL(openurl, "auinit1", auinit1);
			// genres == entrytypes
			if (bib.getEntrytype().toLowerCase().equals("journal")) {
				appendOpenURL(openurl, "genre", "journal");
				appendOpenURL(openurl, "title", bib.getTitle());
			} else if (bib.getEntrytype().toLowerCase().equals("book")) {
				appendOpenURL(openurl, "genre", "book");
				appendOpenURL(openurl, "title", bib.getTitle());
			} else if (bib.getEntrytype().toLowerCase().equals("article")) {
				appendOpenURL(openurl, "genre", "article");
				appendOpenURL(openurl, "title", bib.getJournal());
				appendOpenURL(openurl, "atitle", bib.getTitle());
			} else if (bib.getEntrytype().toLowerCase().equals("inbook")) {
				appendOpenURL(openurl, "genre", "bookitem");
				appendOpenURL(openurl, "title", bib.getBooktitle());
				appendOpenURL(openurl, "atitle", bib.getTitle());
			} else if (bib.getEntrytype().toLowerCase().equals("proceedings")) {
				appendOpenURL(openurl, "genre", "proceeding");
				appendOpenURL(openurl, "title", bib.getBooktitle());
				appendOpenURL(openurl, "atitle", bib.getTitle());
			} else {
				appendOpenURL(openurl, "title", bib.getBooktitle());
				appendOpenURL(openurl, "atitle", bib.getTitle());
			}
			appendOpenURL(openurl, "volume", bib.getVolume());
			appendOpenURL(openurl, "issue", bib.getNumber());
		} catch (final UnsupportedEncodingException ex) {
			// TODO please improve me ASAP...
			ex.printStackTrace();
		}

		return openurl.toString();
	}

	private static void appendOpenURL(final StringBuffer buffer, final String name, final String value) throws UnsupportedEncodingException {
		if (value != null && !value.trim().equals("")) {
			buffer.append("&" + name + "=" + URLEncoder.encode(value.trim(), "UTF-8"));
		}
	}

	/**
	 * This is a helper method to parse the misc-field of a bibtex object
	 * and store the   
	 * 
	 *   key = {value}
	 *   
	 * pairs in its miscFields map.
	 * 
	 * @param bib the bibtex object
	 */
	public static void parseMiscField(final BibTex bib) {
		if (bib.getMisc() != null) {
			final Matcher m = MISC_FIELD_PATTERN.matcher(bib.getMisc());
			while (m.find()) {
				bib.addMiscField(m.group(1), m.group(2));
			}
		}
	}
	
	/**
	 * This is a helper method to convert the key = value pairs contained in the 
	 * miscFields map of a bibtex object into a serialized representation in the 
	 * misc-Field. It appends 
	 * 
	 *  key1 = {value1}, key2 = {value2}, ...
	 *  
	 * for all defined miscFields to the misc field of the given entry.
	 * 
	 * @param bib the bibtex object
	 */
	public static void serializeMiscFields(final BibTex bib) {
		HashMap<String,String> miscFields = bib.getMiscFields();
		StringBuffer miscFieldsSerialized = new StringBuffer();
		// loop over misc fields, if any
		if (miscFields != null && miscFields.values().size() > 0) {
			for (String key : miscFields.keySet()) {
				miscFieldsSerialized.append(key + " = {" + miscFields.get(key) + "}, ");
			}
			// remove last comma
			miscFieldsSerialized.delete(miscFieldsSerialized.lastIndexOf(","), miscFieldsSerialized.length());
		}
		// write serialized misc fields into misc field
		bib.setMisc(miscFieldsSerialized.toString());
	}

	/**
	 * helper method to parse misc field of a bibtex entry
	 * 
	 * @param misc String value of misc field
	 * @return the parsed misc fields as a hashmap
	 */
	public static HashMap<String, String> parseMiscField(final String misc) {
		final BibTex bib = new BibTex();
		bib.setMisc(misc);
		parseMiscField(bib);
		return bib.getMiscFields();
	}

	/**
	 * return a bibtex string representation of the given bibtex object
	 * 
	 * @param bib
	 * @return String bibtexString
	 * 
	 */
	public static String toBibtexString(BibTex bib) {
		try {
			final BeanInfo bi = Introspector.getBeanInfo(bib.getClass());
			
						
			final StringBuffer buffer = new StringBuffer();
			buffer.append("@");
			buffer.append(bib.getEntrytype());
			buffer.append("{");
			buffer.append(bib.getBibtexKey());
			buffer.append(",\n");
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
				final Method getter = d.getReadMethod();
				// loop over all String attributes
				if (d.getPropertyType().equals(String.class) 
						&& getter.invoke(bib, (Object[]) null) != null 
					    && ! EXCLUDE_FIELDS.contains(d.getName()) ) {
					buffer.append(d.getName());
					buffer.append(" = ");
					buffer.append("{");
					buffer.append( (String) getter.invoke(bib, (Object[]) null) );
					buffer.append("},\n");					
				}
			}		
			if (bib.getMiscFields() != null && bib.getMiscFields().size() > 0) {
				// parse the misc field
				BibTexUtils.serializeMiscFields(bib);
				buffer.append(bib.getMisc() + "\n");
			}
			else {
				buffer.delete(buffer.length()-2, buffer.length()-1); // remove last comma
			}
			buffer.append("}");	
			return buffer.toString();
		} catch (IntrospectionException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}		
		return null;
	}

	/**
	 * Generates a bibtex key of the form "first persons lastname from authors
	 * or editors" or "noauthororeditor" concatenated with year.
	 * 
	 * @param authors
	 *            some string representation of the list of authors with their
	 *            first- and lastnames
	 * @param editors
	 *            some string representation of the list of editors with their
	 *            first- and lastnames
	 * @param year
	 * @param title
	 * @return a bibtex key for a bibtex with the fieldvalues given by arguments
	 */
	public static String generateBibtexKey(final String authors, final String editors, final String year, final String title) {
		/*
		 * TODO: pick either author or editor. DON'T use getAuthorlist (it sorts alphabetically!). CHECK for null values.
		 * What to do with Chinese authors and other broken names?
		 * How to extract the first RELEVANT word of the title?
		 * remove Sonderzeichen, LaTeX markup!
		 */
		final StringBuffer buffer = new StringBuffer();

		/* get author */
		String first = getFirstPersonsLastName(authors);
		if (first == null) {
			first = getFirstPersonsLastName(editors);
			if (first == null) {
				first = "noauthororeditor";
			}
		}
		buffer.append(first);

		/* the year */ 
		if (year != null) {
			buffer.append(year.trim());
		}

		/* first relevant word of the title */
		if (title != null) {
			/* best guess: pick first word with more than 4 characters, longest first word */
			// FIXME: what do we want to do inside this if statement?
		}

		return buffer.toString().toLowerCase();
	}

	/**
	 * Tries to extract the last name of the first person.
	 * 
	 * @param person some string representation of a list of persons with their first- and lastnames  
	 * @return the last name of the first person
	 */
	public static String getFirstPersonsLastName(final String person) {
		if (person != null) {
			final String firstauthor;
			/*
			 * check, if there is more than one author
			 */
			final int firstand = person.indexOf(" and ");
			if (firstand < 0) {
				firstauthor = person;
			} else {
				firstauthor = person.substring(0, firstand);				
			}
			/*
			 * first author extracted, get its last name
			 */
			final int lastspace = firstauthor.lastIndexOf(' ');
			final String lastname;
			if (lastspace < 0) {
				lastname = firstauthor;
			} else {
				lastname = firstauthor.substring(lastspace + 1, firstauthor.length());
			}
			return lastname;
		}
		return null;
	}	

	/**
	 * Cleans up a string containing LaTeX markup and converts special chars to HTML special chars.
	 * 
	 * @param bibtex a bibtex string
	 * @return the cleaned bibtex string
	 */
	public static String cleanBibTex(String bibtex) {
		
		// replace markup
		bibtex = bibtex.replaceAll("\\\\[a-z]+\\{([^\\}]+)\\}", "$1");  // \\markup{marked_up_text}		
		
		// replace special character sequences for umlauts
		// NOTE: this is just a small subset - could / should be extended to french, ...
		bibtex = bibtex.replaceAll("\\{|\\}|\\\\", ""). // remove '\','{' and '\'
	       replaceAll("\\s+"," ").
	       replaceAll("\\\"o", "ö").
	       replaceAll("\\\"u", "ü").
	       replaceAll("\\\"a", "ä").
	       replaceAll("\\\"O", "Ö").
	       replaceAll("\\\"U", "Ü").
	       replaceAll("\\\"A", "Ä").
	       replaceAll("\\\"s", "ß").
	       trim();
		
		final StringBuffer buffer = new StringBuffer(bibtex.length());
		char c;		
		for (int i = 0; i < bibtex.length(); i++) {
			c = bibtex.charAt(i);
			
			// HTML Special Chars
			if (c == '"')
				buffer.append("&quot;");
			else if (c == '&')
				buffer.append("&amp;");
			else if (c == '<')
				buffer.append("&lt;");
			else if (c == '>')
				buffer.append("&gt;");
			else {
				int ci = 0xffff & c;
				if (ci < 160 )
					// nothing special only 7 Bit
					buffer.append(c);
				else {
					// Not 7 Bit use the unicode system
					buffer.append("&#");
					buffer.append(new Integer(ci).toString());
					buffer.append(';');
				}
			}
		}
		return buffer.toString();
	} 

	/**
	 * Tries to find a year (four connected digits) in a string and returns them as int.
	 * If it fails, returns Integer.MAX_VALUE.
	 * 
	 * @param year
	 * @return an integer representation of the year, or Integer.MAX_VALUE if it fails
	 */
	public static int getYear(final String year) {
		try {
			return Integer.parseInt(year);
		} catch (final NumberFormatException ignore) {
			/*
			 * try to get four digits ...
			 */
			final Matcher m = YEAR_PATTERN.matcher(year);
			if (m.find()) {
				return Integer.parseInt(m.group());
			}
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * Sort a list of bibtex posts (and eventually remove duplicates).
	 * 
	 * @param bibtexList
	 * @param sortKeys
	 * @param sortOrders
	 */
	public static void sortBibTexList(final List<Post<BibTex>> bibtexList, final List<SortKey> sortKeys, final List<SortOrder> sortOrders) {
		Collections.sort(bibtexList, new BibTexPostComparator(sortKeys, sortOrders));
	}

	/**
	 * Sorts a list of bibtex posts and removes duplicates.
	 * 
	 * @param bibtexList
	 */
	public static void removeDuplicates(final List<Post<BibTex>> bibtexList) {
		final TreeSet<Post<BibTex>> temp = new TreeSet<Post<BibTex>>(new BibTexPostInterhashComparator());
		temp.addAll(bibtexList);
		// FIXME: a bit cumbersome at this point - but we need to work on the bibtexList
		bibtexList.clear();
		bibtexList.addAll(temp);
	}
}