/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.StringUtils;

/**
 * Similarity hashes for publications are calculated here.
 * @version $Id$
 */
public class SimHash {
	
	private static final Pattern SINGLE_LETTER = Pattern.compile("(\\p{L})");
	
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
		
		if (resource instanceof BibTex) {
			return SimHash.getSimHash((BibTex) resource, simHash);
		}
		
		throw new UnsupportedResourceTypeException();
	}
	
	/**
	 * @param bibtex the object whose hash is to be calculated
	 * @param simHash the type of hash to be calculated
	 * @return the corresponding simhash for a bookmark.
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
	public static String getSimHash0(final BibTex bibtex) {
		return StringUtils.getMD5Hash(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getTitle()) + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(PersonNameUtils.serializePersonNames(bibtex.getAuthor(), false))    + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(PersonNameUtils.serializePersonNames(bibtex.getEditor(), false))    + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getYear())      + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getEntrytype()) + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getJournal())   + " " +
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getBooktitle()));
	}

	/**
	 * @param publication the object whose hash is to be calculated
	 * @return the calculated simHash1, which consideres: title, author/editor, year.
	 */
	public static String getSimHash1(final BibTex publication) {	
		if (!present(StringUtils.removeNonNumbersOrLetters(PersonNameUtils.serializePersonNames(publication.getAuthor())))) {
			// no author set --> take editor
			return StringUtils.getMD5Hash(getNormalizedTitle(publication.getTitle()) + " " +
					getNormalizedPersons(publication.getEditor())            + " " +
					getNormalizedYear(publication.getYear()));				
		}
		// author set
		return StringUtils.getMD5Hash(getNormalizedTitle(publication.getTitle()) + " " + 
				getNormalizedPersons(publication.getAuthor())            + " " + 
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
	public static String getSimHash2(final BibTex bibtex) {
		return StringUtils.getMD5Hash(StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(bibtex.getTitle())     + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(PersonNameUtils.serializePersonNames(bibtex.getAuthor(), false))    + " " + 
				StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(PersonNameUtils.serializePersonNames(bibtex.getEditor(), false))    + " " + 
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

	/**
	 * July 2010: added "orComma" since we now support the "Last, First" name format 
	 * where we need the comma in {@link #normalizePerson(String)} to extract the
	 * first and the last name.
	 * 
	 * @param str
	 * @return
	 */
	public static String getNormalizedPersons(final List<PersonName> persons) {
		if (!present(persons)) return "";
		return StringUtils.getStringFromList(normalizePersonList(persons));
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
	private static Set<String> normalizePersonList(final List<PersonName> persons) {
		final Set<String> normalized = new TreeSet<String>();
		for (final PersonName personName : persons) {
			normalized.add(normalizePerson(personName));
		}
		return normalized;
	}

	/**
	 * Used for "sloppy" hashes, i.e., the inter hash
	 * 
	 * Input: a String of a person name<br/>
	 *  
	 * Output: normalized String of person name<br/>
	 * 
	 * Example:<br/>
	 * Donald E. Knuth --> d.knuth<br/>
	 * D.E.      Knuth --> d.knuth<br/>
	 * Donald    Knuth --> d.knuth<br/>
	 *           Knuth --> knuth<br/>
	 * Knuth, Donald   --> d.knuth<br/>
	 * Knuth, Donald E.--> d.knuth<br/>
	 * Maarten de Rijke--> m.rijke<br/>
	 * Balby Marinho, Leandro--> l.marinho<br/>
	 */
	private static String normalizePerson(final PersonName p) {
		final String first = p.getFirstName();
		final String last  = p.getLastName();
		if (present(first) && !present(last)) {
			/*
			 * Only the first name is given. This should practically never happen,
			 * since we put such names into the last name field.
			 * 
			 */
			return StringUtils.removeNonNumbersOrLettersOrDotsOrCommaOrSpace(first).toLowerCase();
		}
		if (present(first) && present(last)) {
			/*
			 * First and last given - default.
			 * Take the first letter of the first name and append the last part
			 * of the last name.
			 */
			return getFirst(first) + "." + getLast(last);
		}
		if (present(last)) {
			/*
			 * Only last name available - could be a "regular" name enclosed
			 * in brackets.
			 */
			return getLast(trimBrackets(last));
		}
		return "";
	}
	
	/**
	 * A name enclosed in brackets {Like this One} is detected as a single 
	 * last name. We here re-parse such names to extract the "real" name.
	 * 
	 * @param last
	 * @return
	 */
	private static String trimBrackets(final String last) {
		final String trimmedLast = last.trim();
		if (trimmedLast.startsWith("{") && trimmedLast.endsWith("}")) {
			return normalizePerson(PersonNameUtils.discoverPersonName(trimmedLast.substring(1, trimmedLast.length() - 1)));
		}
		return last;
	}
	
	/**
	 * Returns the first letter of the first name, or an empty string, if no
	 * such letter exists.
	 * 
	 * @param first
	 * @return
	 */
	private static String getFirst(final String first) {
		final Matcher matcher = SINGLE_LETTER.matcher(first);
		if (matcher.find()) {
			return matcher.group(1).toLowerCase();
		}
		return "";
	}
	
	
	/**
	 * Extracts from the last name the last part and cleans it. I.e., from 
	 * "van de Gruyter" we get "gruyter"
	 * 
	 * @param last
	 * @return
	 */
	private static String getLast(final String last) {
		final int pos = last.lastIndexOf(' ');
		if (pos > 0) {
			return StringUtils.removeNonNumbersOrLettersOrDotsOrCommaOrSpace(last.substring(pos + 1)).toLowerCase();
		}
		return StringUtils.removeNonNumbersOrLettersOrDotsOrCommaOrSpace(last).toLowerCase();
	}

}