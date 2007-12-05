package org.bibsonomy.model.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.model.BibTex;

/**
 *
 * @version: $Id$
 * @author:  dbenz
 * $Author$
 *
 */
public class BibTexUtils {
	private static final Logger LOGGER = Logger.getLogger(BibTexUtils.class);
		
	/**
	 * Builds a string from a given bibtex object which can be used to build an OpenURL
	 * see http://www.exlibrisgroup.com/sfx_openurl.htm
	 *
	 * @param bib the bibtex object
	 * @return the DESCRIPTION part of the OpenURL of this BibTeX object
	 */

	public static String getOpenurl (BibTex bib) {
		
		// stores the completed URL (just the DESCRIPTION part)
		StringBuffer openurl = new StringBuffer();
		
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
			Matcher m = Pattern.compile("http://.+/(.+?/.+?$)").matcher(doi);
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
			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		return openurl.toString();	
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
	public static void parseMiscField (BibTex bib) {
		if (bib.getMisc() != null) {
			Matcher m = Pattern.compile("([a-zA-Z]+)\\s*=\\s*\\{(.+?)\\}").matcher(bib.getMisc());
			while (m.find()) {
				bib.addMiscField(m.group(1), m.group(2));
			}
		}
	}
	
	
	/**
	 * return a bibtex string representation of the given bibtex object
	 * 
	 * @param bib
	 * @return String bibtexString
	 * 
	 * TODO: check handling  of misc = {}... field
	 * 
	 */
	public String toBibtexString(BibTex bib) {
		try {
			final BeanInfo bi = Introspector.getBeanInfo(this.getClass());
			
			StringBuffer sb = new StringBuffer();
			sb.append("@");
			sb.append(bib.getEntrytype());
			sb.append("{");
			sb.append(bib.getBibtexKey());
			sb.append(",\n");
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
				final Method getter = d.getReadMethod();
				// loop over all String attributes
				if (d.getPropertyType().equals(String.class) 
						&& getter.invoke(this, (Object[]) null) != null) {
					sb.append(d.getName());
					sb.append(" = ");
					sb.append("{");
					sb.append( (String) getter.invoke(this, (Object[]) null) );
					sb.append("}, \n");					
				}
			}				
			sb.append("}");	
			return sb.toString();
		} catch (IntrospectionException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}		
		return null;
	}
	
	private static void appendOpenURL(StringBuffer s, String name, String value) throws UnsupportedEncodingException {
		if (value != null && !value.trim().equals("")) {
			s.append("&" + name + "=" + URLEncoder.encode(value.trim(), "UTF-8"));
		}
	}
	
}
