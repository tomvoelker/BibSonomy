/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.ValidationUtils;

/**
 * Renders publications in Endnote's import format
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class EndnoteUtils {

	private static final Map<String, String> RIS_ENTRY_TYPE_MAP = new HashMap<String, String>();
	static {
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.ARTICLE, "Journal Article");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.BOOK, "Book");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.BOOKLET, "Book");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.INBOOK, "Book Section");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.INCOLLECTION, "Book Section");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.INPROCEEDINGS, "Conference Paper");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.MASTERS_THESIS, "Thesis");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.PHD_THESIS, "Thesis");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.PROCEEDINGS, "Conference Proceedings");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.TECH_REPORT, "Report");
		RIS_ENTRY_TYPE_MAP.put(BibTexUtils.UNPUBLISHED, "Unpublished Work");
	}

	/**
	 * mapping of BibTeX entry types to SWRC entry types
	 */
	private static final Map<String, String> SWRC_ENTRY_TYPE_MAP = new HashMap<String, String>();
	static {
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.ARTICLE, "Article");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.BOOK, "Book");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.BOOKLET, "Booklet");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.INBOOK, "InBook");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.INCOLLECTION, "InCollection");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.INPROCEEDINGS, "InProceedings");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.MANUAL, "Manual");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.MASTERS_THESIS, "MasterThesis");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.PHD_THESIS, "PhDThesis");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.PROCEEDINGS, "Proceedings");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.TECH_REPORT, "TechnicalReport");
		SWRC_ENTRY_TYPE_MAP.put(BibTexUtils.UNPUBLISHED, "Unpublished");
	}

	/**
	 * Maps BibTeX entry types to RIS entry types.
	 * 
	 * @param bibtexEntryType
	 * @return The RIS entry type
	 */
	public static String getRISEntryType(final String bibtexEntryType) {
		if (RIS_ENTRY_TYPE_MAP.containsKey(bibtexEntryType)) {
			return RIS_ENTRY_TYPE_MAP.get(bibtexEntryType);
		}
		return "Generic";
	}

	/**
	 * Maps BibTeX entry types to SWRC entry types.
	 * 
	 * @param bibtexEntryType
	 * @return the SWRC entry type
	 */
	public static String getSWRCEntryType(final String bibtexEntryType) {
		if (SWRC_ENTRY_TYPE_MAP.containsKey(bibtexEntryType)) {
			return SWRC_ENTRY_TYPE_MAP.get(bibtexEntryType);
		}
		return "Misc";
	}

	/**
	 * @param a
	 *            target {@link Appendable}
	 * @param post
	 *            {@link Post} to be rendered as endnote
	 * @throws IOException
	 */
	public static void append(Appendable a, Post<BibTex> post) throws IOException {
		final BibTex bib = post.getResource();
		
		a.append("%0 ").append(getRISEntryType(bib.getEntrytype())).append('\n');

		if (ValidationUtils.present(bib.getAuthor())) {
			for (PersonName person : bib.getAuthor()) {
				a.append("%A ").append(person.getLastName()).append(", ").append(person.getFirstName()).append('\n');
			}
		}

		if (ValidationUtils.present(bib.getBooktitle())) {
			a.append("%B ").append(bib.getBooktitle()).append('\n');
		}
		if (ValidationUtils.present(bib.getAddress())) {
			a.append("%C ").append(bib.getAddress()).append('\n');
		}
		if (ValidationUtils.present(bib.getYear())) {
			a.append("%D ").append(bib.getYear()).append('\n');
		}
		if (ValidationUtils.present(bib.getEditor())) {
			for (PersonName person : bib.getEditor()) {
				a.append("%E ").append(person.getLastName()).append(", ").append(person.getFirstName()).append('\n');
			}
		}
		if (ValidationUtils.present(bib.getPublisher())) {
			a.append("%I ").append(bib.getPublisher()).append('\n');
		}
		if (ValidationUtils.present(bib.getJournal())) {
			a.append("%J ").append(bib.getJournal()).append('\n');
		}
		a.append("%K ").append(TagUtils.toTagString(post.getTags(), " ")).append('\n');

		if (ValidationUtils.present(bib.getNumber())) {
			a.append("%N ").append(bib.getNumber()).append('\n');
		}
		if (ValidationUtils.present(bib.getPages())) {
			a.append("%P ").append(bib.getPages()).append('\n');
		}
		if (ValidationUtils.present(bib.getTitle())) {
			a.append("%T ").append(bib.getTitle()).append('\n');
		}
		if (ValidationUtils.present(bib.getUrl())) {
			a.append("%U ").append(bib.getUrl()).append('\n');
		}
		if (ValidationUtils.present(bib.getVolume())) {
			a.append("%V ").append(bib.getVolume()).append('\n');
		}
		if (ValidationUtils.present(bib.getAbstract())) {
			a.append("%X ").append(bib.getAbstract()).append('\n');
		}
		if (ValidationUtils.present(bib.getAnnote())) {
			a.append("%Z ").append(bib.getAnnote()).append('\n');
		}
		if (ValidationUtils.present(bib.getEdition())) {
			a.append("%7 ").append(bib.getEdition()).append('\n');
		}
		if (ValidationUtils.present(bib.getChapter())) {
			a.append("%& ").append(bib.getChapter()).append('\n');
		}
	}
}
