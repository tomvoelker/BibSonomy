package org.bibsonomy.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.bibsonomy.ibatis.util.ResourceUtils;

public class SimHash {
	
	public static String getSimHash0 (Bibtex b) {
		// calculate an appropriate hash
		return ResourceUtils.hash(removeNonNumbersOrLettersOrDotsOrSpace(b.getTitle())     + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getAuthor())    + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getEditor())    + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getYear())      + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getEntrytype()) + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getJournal())   + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getBooktitle()));
	}
	
	public static String getSimHash1 (Bibtex b) {	
		if (removeNonNumbersOrLetters(b.getAuthor()).equals("")) {
			// no author set --> take editor
			return ResourceUtils.hash(getNormalizedTitle(b.getTitle()) + " " +
					getNormalizedEditor(b.getEditor())            + " " +
					getNormalizedYear(b.getYear()));				
		} else {
			// author set
			return ResourceUtils.hash(getNormalizedTitle(b.getTitle()) + " " + 
					getNormalizedAuthor(b.getAuthor())            + " " + 
					getNormalizedYear(b.getYear()));
		}
	}
	
	/*
	notwendige Anpassungen:

	 - entsprechende zusätzliche Spalten (Volume/Number) im ResourceHandler mit herausgeben
	   (in getBibtexSelect() columns[] anpassen)
	 - Bibtex.java anpassen: getSimHash(), setHashesToNull(), später getHash()
	 - writing of hashes in insertBibIntoDB() in BibtexHandler is already done for 0 to 4
	 - update queries in ResourceHandler to use correct hashes for query
	 
	 changing in Bibtex.getHash() from 0 to 2 should imply changing column in resource handler and 
	 pre-0 to pre-2 in JSPs?!
	 
	 have a look at SIM_HASH and INTRA_HASH - where are they used and should they be changed?

	*/
	public static String getSimHash2 (Bibtex b) {
		// calculate an appropriate hash
		return ResourceUtils.hash(removeNonNumbersOrLettersOrDotsOrSpace(b.getTitle())     + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getAuthor())    + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getEditor())    + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getYear())      + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getEntrytype()) + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getJournal())   + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getBooktitle()) + " " +
				removeNonNumbersOrLetters(b.getVolume())                 + " " +
				removeNonNumbersOrLetters(b.getNumber())
		);
	}
	
	private static String getNormalizedAuthor (String a) {
		if (a != null) {
			return getStringFromList(normalizePersonList(removeNonNumbersOrLettersOrDotsOrSpace(a))).toLowerCase();
		}
		return "";
	}	
	private static String getNormalizedEditor (String e) {
		if (e != null) {
			return getStringFromList(normalizePersonList(removeNonNumbersOrLettersOrDotsOrSpace(e))).toLowerCase();
		}
		return "";
	}
	private static String getNormalizedYear (String y) {
		if (y != null) {
			return removeNonNumbers(y);
		}
		return "";
	}
	private static String getNormalizedTitle (String t) {
		if (t != null) {
			return removeNonNumbersOrLetters(t).toLowerCase();	
		}
		return "";
	}
	
	
	
	
	
	
	
	/* Input: a String of persons, divided by " and "
	 * Output: a Set of normalized persons, divided by ", " and enclosed in brackets "[ ]"
	 * 
	 */
	protected static Set<String> normalizePersonList (String s) {
		Scanner t = new Scanner (s).useDelimiter(" and ");
		SortedSet<String> persons = new TreeSet<String>(); 
		while (t.hasNext()) {
			persons.add(normalizePerson(t.next()));
		}
		return persons;
	}
	
	/* Input: a String of a person name 
	 * Output: normalized String of person name
	 * Example: 
	 * Donald E. Knuth --> d.knuth
	 * D.E.      Knuth --> d.knuth
	 * Donald    Knuth --> d.knuth
	 *           Knuth --> knuth
	 */
	protected static String normalizePerson (String s) {
		StringTokenizer t = new StringTokenizer(s);
		String          first = null;
		String          last  = null;
		// get first and last name
		while (t.hasMoreTokens()) {
			if (first == null) {
				first = t.nextToken().trim();
			} else {
				last = t.nextToken().trim();
			}
		}
		// if both are equal --> only last name given
		if (first != null && first.equals(last)) {
			return first;
		}
		if (first != null && last != null) {
			return (first.substring(0,1) + "." + last);
		}
		return first;
	}
	
	protected static String getStringFromList (Collection<String> c) {
		if (c.isEmpty()) {
			return "[]";
		} else {
			// returns [item1,item2,item3,...]
			StringBuffer s = new StringBuffer("[");
			Iterator it = c.iterator();
			while (it.hasNext()) {
				s.append(it.next() + ",");
			}
			s.replace(s.length()-1,s.length(),"]");
			return s.toString();
		}
	}
	
	/*
	 * static methods for doing some String manipulation
	 */
	
	// removes everything which is not a Number
	protected static String removeNonNumbers (String s) {
		if (s != null) {
			return s.replaceAll("[^0-9]+","");
		}
		return "";
	}
	
	// removes everything which is neither a Number nor a Letter 
	protected static String removeNonNumbersOrLetters (String s) {
		if (s != null) {
			return s.replaceAll("[^0-9\\p{L}]+", "");
		}
		return "";
	}
	
	// removes everything which is neither a Number nor a Letter nor a Dot (.) nor Space
	protected static String removeNonNumbersOrLettersOrDotsOrSpace (String s) {
		if (s != null) {
			return normalizeWhitespace(s).replaceAll("[^0-9\\p{L}\\. ]+", "");
		}
		return "";
	}
	
	// removes all whitespace
	protected static String removeWhitespace (String s) {
		if (s != null) {
			return s.replaceAll("\\s+","");
		}
		return "";
	}
	
	// substitutes all whitespace with " "
	protected static String normalizeWhitespace (String s) {
		if (s != null) {
			return s.replaceAll("\\s+"," ");
		}
		return "";
	}

}