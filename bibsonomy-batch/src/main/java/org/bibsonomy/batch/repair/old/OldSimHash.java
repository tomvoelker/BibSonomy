package org.bibsonomy.batch.repair.old;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.SimHash;
import org.bibsonomy.util.StringUtils;

/**
 * @author rja
 * @version $Id$
 */
public class OldSimHash {


	/**
	 * 
	 * @param resource the object whose hash is to be calculated
	 * @param simHash the type of hash to be calculated
	 * @return the corresponding simhash for a resource.
	 * 
	 */
	public static String getSimHash(final Resource resource, final HashID simHash) {
		if (resource instanceof Bookmark) {
			return SimHash.getSimHash((Bookmark) resource, simHash);
		}

		if (resource instanceof OldBibTex) {
			return SimHash.getSimHash(resource, simHash);
		}

		throw new UnsupportedResourceTypeException();
	}

	/**
	 * @param bibtex the object whose hash is to be calculated
	 * @param simHash the type of hash to be calculated
	 * @return the corresponding simhash for a bookmark.
	 */
	public static String getSimHash(final OldBibTex bibtex, final HashID simHash) {
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

	// FIXME: HashID was meant for BibTexs only - we should create a new enum for Bookmarks
	/**
	 * @param bookmark the object whose hash is to be calculated
	 * @param simHash the type of hash to be calculated
	 * @return the corresponding simhash for a bookmark.
	 */
	public static String getSimHash(final Bookmark bookmark, final HashID simHash) {
		if (simHash.getId() == HashID.SIM_HASH0.getId()) {
			// XXX: do we want to return simHash1 for SIM_HASH0?
			return getSimHash1(bookmark);
		} else if (simHash.getId() == HashID.SIM_HASH1.getId()) {
			// XXX: do we want to return simHash2 for SIM_HASH1?
			return getSimHash2(bookmark);
		} else {
			throw new RuntimeException("SimHash " + simHash.getId() + " doesn't exist.");
		}
	}

	/**
	 * Calculates the simHash0 for a bookmark, which consideres: url of a bookmark
	 * Currently, all hashes for bookmark are equal.
	 * @param bookmark the object whose hash is to be calculated
	 * @return the calculated hash
	 */
	public static String getSimHash0(final Bookmark bookmark) {
		return StringUtils.getMD5Hash(bookmark.getUrl());
	}

	/**
	 * Calculates the simHash1 for a bookmark, which consideres: url of a bookmark
	 * Currently, all hashes for bookmark are equal.
	 * @param bookmark the object whose hash is to be calculated
	 * @return the calculated hash
	 */
	public static String getSimHash1(final Bookmark bookmark) {
		return StringUtils.getMD5Hash(bookmark.getUrl());
	}

	/**
	 * Calculates the simHash2 for a bookmark, which consideres: url of a bookmark
	 * Currently, all hashes for bookmark are equal.
	 * @param bookmark the object whose hash is to be calculated
	 * @return the calculated hash
	 */
	public static String getSimHash2(final Bookmark bookmark) {
		return StringUtils.getMD5Hash(bookmark.getUrl());
	}

	/**
	 * Calculates the simHash3 for a bookmark, which consideres: url of a bookmark
	 * Currently, all hashes for bookmark are equal.
	 * @param bookmark the object whose hash is to be calculated
	 * @return the calculated hash
	 */
	public static String getSimHash3(final Bookmark bookmark) {
		return StringUtils.getMD5Hash(bookmark.getUrl());
	}

	/**
	 * @param bibtex the object whose hash is to be calculated
	 * @return the calculated simHash0, which consideres: title, author, editor, year,
	 * entrytype, journal, booktitle.
	 */
	public static String getSimHash0(final OldBibTex bibtex) {
		return StringUtils.getMD5Hash(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getTitle()) + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getAuthor())    + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getEditor())    + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getYear())      + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getEntrytype()) + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getJournal())   + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getBooktitle()));
	}

	/**
	 * @param publication the object whose hash is to be calculated
	 * @return the calculated simHash1, which consideres: title, author/editor, year.
	 */
	public static String getSimHash1(final OldBibTex publication) {	
		if (!present(StringUtils.removeNonNumbersOrLetters(publication.getAuthor()))) {
			// no author set --> take editor
			return StringUtils.getMD5Hash(getNormalizedTitle(publication.getTitle()) + " " +
					getNormalizedEditor(publication.getEditor())            + " " +
					getNormalizedYear(publication.getYear()));				
		}
		// author set
		return StringUtils.getMD5Hash(getNormalizedTitle(publication.getTitle()) + " " + 
				getNormalizedAuthor(publication.getAuthor())            + " " + 
				getNormalizedYear(publication.getYear()));
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
	/**
	 * @param bibtex the object whose hash is to be calculated
	 * @return the calculated simHash0, which consideres: author, editor, year, entryType, journal, booktitle, volume, number.
	 */
	public static String getSimHash2(final OldBibTex bibtex) {
		return StringUtils.getMD5Hash(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getTitle())     + " " + 
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
	 * Reproduces the behaviour of the former BibTex-model, where simHash3 was
	 * always an empty string.
	 * @return an empty String
	 */
	public static String getSimHash3() {
		return "";
	}

	public static String getNormalizedAuthor(final String str) {
		if (str == null) return "";
		return StringUtils.getStringFromList(normalizePersonList(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(str.toLowerCase())));
	}

	private static String getNormalizedEditor(final String str) {
		if (str == null) return "";
		return StringUtils.getStringFromList(normalizePersonList(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(str.toLowerCase())));
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
	private static Set<String> normalizePersonList(final String s) {
		final Scanner t = new Scanner(s).useDelimiter(OldPersonNameUtils.PERSON_NAME_DELIMITER);
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
	private static String normalizePerson(final String s) {
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
		// only last name (=first) given
		if (first != null && last == null) {
			return first;
		}
		// first and last given
		if (first != null && last != null) {
			return (first.substring(0,1) + "." + last);
		}
		return first;
	}

}
