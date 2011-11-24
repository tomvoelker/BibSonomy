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

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.StringUtils;

/**
 * Similarity hashes for publications are calculated here.
 * @version $Id$
 */
public class SimHash {
	
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
					PersonNameUtils.getNormalizedPersons(publication.getEditor())            + " " +
					getNormalizedYear(publication.getYear()));				
		}
		// author set
		return StringUtils.getMD5Hash(getNormalizedTitle(publication.getTitle()) + " " + 
				PersonNameUtils.getNormalizedPersons(publication.getAuthor())            + " " + 
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

	private static String getNormalizedYear(final String str) {
		if (str == null) return "";
		return StringUtils.removeNonNumbers(str);
	}

	private static String getNormalizedTitle(final String str) {
		if (str == null) return "";
		return StringUtils.removeNonNumbersOrLetters(str).toLowerCase();
	}
	


}