package org.bibsonomy.model.util;

import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.StringUtils;

/**
 * Similarity hashes for publications are calculated here.
 */
public class SimHash {

	/**
	 * Returns the corresponding simhash.
	 */
	public static String getSimHash(final BibTex bibtex, final HashID simHash) {
		if (simHash.getId() == HashID.SIM_HASH0.getId()) {
			return getSimHash0(bibtex);
		} else if (simHash.getId() == HashID.SIM_HASH1.getId()) {
			return getSimHash1(bibtex);
		} else if (simHash.getId() == HashID.SIM_HASH2.getId()) {
			return getSimHash2(bibtex);
		} else if (simHash.getId() == HashID.SIM_HASH3.getId()) {
			return getSimHash3();
		} else {
			throw new RuntimeException("SimHash " + simHash.getId() + " doesn't exist.");
		}
	}

	/**
	 * Calculates the simHash0, which consideres: title, author, editor, year,
	 * entrytype, journal, booktitle.
	 */
	public static String getSimHash0(final BibTex bibtex) {
		return ResourceUtils.hash(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getTitle()) + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getAuthor())    + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getEditor())    + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getYear())      + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getEntrytype()) + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getJournal())   + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getBooktitle()));
	}

	public static String getSimHash1(final BibTex bibtex) {	
		if (StringUtils.removeNonNumbersOrLetters(bibtex.getAuthor()).equals("")) {
			// no author set --> take editor
			return ResourceUtils.hash(getNormalizedTitle(bibtex.getTitle()) + " " +
					getNormalizedEditor(bibtex.getEditor())            + " " +
					getNormalizedYear(bibtex.getYear()));				
		} else {
			// author set
			return ResourceUtils.hash(getNormalizedTitle(bibtex.getTitle()) + " " + 
					getNormalizedAuthor(bibtex.getAuthor())            + " " + 
					getNormalizedYear(bibtex.getYear()));
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
	public static String getSimHash2(final BibTex bibtex) {
		// calculate an appropriate hash
		return ResourceUtils.hash(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getTitle())     + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getAuthor())    + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getEditor())    + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getYear())      + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getEntrytype()) + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getJournal())   + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getBooktitle()) + " " +
				StringUtils.removeNonNumbersOrLetters(bibtex.getVolume())                 + " " +
				StringUtils.removeNonNumbersOrLetters(bibtex.getNumber())
		);
	}

	/**
	 * Reproduces the behaviour of the former BibTex-model, where simhash3 was
	 * always an empty string.
	 */
	public static String getSimHash3() {
		return "";
	}

	private static String getNormalizedAuthor(final String str) {
		if (str == null) return "";
		return StringUtils.getStringFromList(normalizePersonList(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(str))).toLowerCase();
	}

	private static String getNormalizedEditor(final String str) {
		if (str == null) return "";
		return StringUtils.getStringFromList(normalizePersonList(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(str))).toLowerCase();
	}

	private static String getNormalizedYear(final String str) {
		if (str == null) return "";
		return StringUtils.removeNonNumbers(str);
	}

	private static String getNormalizedTitle(final String str) {
		if (str == null) return "";
		return StringUtils.removeNonNumbersOrLetters(str).toLowerCase();
	}

	/**
	 * Input: a String of persons, separated by " and "<br/>
	 * 
	 * Output: a Set of normalized persons, divided by ", " and enclosed in
	 * brackets "[ ]"
	 */
	private static Set<String> normalizePersonList (final String s) {
		final Scanner t = new Scanner(s).useDelimiter(" and ");
		final SortedSet<String> persons = new TreeSet<String>(); 
		while (t.hasNext()) {
			persons.add(normalizePerson(t.next()));
		}
		return persons;
	}

	/**
	 * Input: a String of a person name<br/>
	 *  
	 * Output: normalized String of person name<br/>
	 * 
	 * Example:<br/>
	 * Donald E. Knuth --> d.knuth<br/>
	 * D.E.      Knuth --> d.knuth<br/>
	 * Donald    Knuth --> d.knuth<br/>
	 *           Knuth --> knuth
	 */
	private static String normalizePerson (final String s) {
		final StringTokenizer t = new StringTokenizer(s);
		String first = null;
		String last  = null;
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
}