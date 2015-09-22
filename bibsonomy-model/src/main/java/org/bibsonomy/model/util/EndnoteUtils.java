/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.ValidationUtils;

/**
 * Renders publications in Endnote's import format
 * 
 * @author Jens Illig
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
	 * @param skipDummyValues whether to skip fields with dummy values such as 'noauthor'
	 * @throws IOException
	 */
	public static void append(final Appendable a, final Post<BibTex> post, final boolean skipDummyValues) throws IOException {
		if (!skipDummyValues) {
			appendInternal(a, post);
			return;
		}
		BibTexUtils.runWithRemovedOrReplacedDummyValues(post.getResource(), false, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				appendInternal(a, post);
				return null;
			}
		});
	}
	
	private static void appendInternal(Appendable a, Post<BibTex> post) throws IOException {
		final BibTex bib = post.getResource();
		
		a.append("%0 ").append(getRISEntryType(bib.getEntrytype())).append('\n');
		
		if (ValidationUtils.present(bib.getBibtexKey())) {
			a.append("%1 ").append(BibTexUtils.cleanBibTex(bib.getBibtexKey())).append('\n'); 
		}

		if (ValidationUtils.present(bib.getAuthor())) {
			for (PersonName person : bib.getAuthor()) {
				a.append("%A ").append(BibTexUtils.cleanBibTex(person.getLastName())).append(", ").append(BibTexUtils.cleanBibTex(person.getFirstName())).append('\n');
			}
		}

		if (ValidationUtils.present(bib.getBooktitle())) {
			a.append("%B ").append(BibTexUtils.cleanBibTex(bib.getBooktitle())).append('\n');
		} else if (ValidationUtils.present(bib.getSeries())) { // TODO: ask Martina whether this is correct
			a.append("%B ").append(BibTexUtils.cleanBibTex(bib.getSeries())).append('\n');
		}
		if (ValidationUtils.present(bib.getAddress())) {
			a.append("%C ").append(BibTexUtils.cleanBibTex(bib.getAddress())).append('\n');
		}
		if (ValidationUtils.present(bib.getYear())) {
			a.append("%D ").append(BibTexUtils.cleanBibTex(bib.getYear())).append('\n');
		}
		if (ValidationUtils.present(bib.getEditor())) {
			for (PersonName person : bib.getEditor()) {
				a.append("%E ").append(BibTexUtils.cleanBibTex(person.getLastName())).append(", ").append(BibTexUtils.cleanBibTex(person.getFirstName())).append('\n');
			}
		}
		if (ValidationUtils.present(bib.getPublisher())) {
			a.append("%I ").append(BibTexUtils.cleanBibTex(bib.getPublisher())).append('\n');
		}
		if (ValidationUtils.present(bib.getJournal())) {
			a.append("%J ").append(BibTexUtils.cleanBibTex(bib.getJournal())).append('\n');
		}
		a.append("%K ").append(TagUtils.toTagString(post.getTags(), " ")).append('\n');

		if (ValidationUtils.present(bib.getNumber())) {
			a.append("%N ").append(BibTexUtils.cleanBibTex(bib.getNumber())).append('\n');
		}
		if (ValidationUtils.present(bib.getPages())) {
			a.append("%P ").append(BibTexUtils.cleanBibTex(bib.getPages())).append('\n');
		}
		if (ValidationUtils.present(bib.getMiscField("doi"))) {
			a.append("%R ").append(BibTexUtils.cleanBibTex(bib.getMiscField("doi"))).append('\n');
		}
		if (ValidationUtils.present(bib.getTitle())) {
			a.append("%T ").append(BibTexUtils.cleanBibTex(bib.getTitle())).append('\n');
		}
		if (ValidationUtils.present(bib.getUrl())) {
			a.append("%U ").append(BibTexUtils.cleanBibTex(bib.getUrl())).append('\n');
		}
		if (ValidationUtils.present(bib.getVolume())) {
			a.append("%V ").append(BibTexUtils.cleanBibTex(bib.getVolume())).append('\n');
		}
		if (ValidationUtils.present(bib.getAbstract())) {
			a.append("%X ").append(BibTexUtils.cleanBibTex(bib.getAbstract())).append('\n');
		}
		if (ValidationUtils.present(bib.getAnnote())) {
			a.append("%Z ").append(BibTexUtils.cleanBibTex(bib.getAnnote())).append('\n');
		}
		if (ValidationUtils.present(bib.getEdition())) {
			a.append("%7 ").append(BibTexUtils.cleanBibTex(bib.getEdition())).append('\n');
		}
		if (ValidationUtils.present(bib.getChapter())) {
			a.append("%& ").append(BibTexUtils.cleanBibTex(bib.getChapter())).append('\n');
		}
		if (ValidationUtils.present(bib.getMiscField("isbn"))) {
			a.append("%@ ").append(BibTexUtils.cleanBibTex(bib.getMiscField("isbn"))).append('\n');
		}
	}
	
	/**
	 * Renders Endnote
	 * 
	 * @param post the post to be rendered
	 * @param skipDummyValues whether to skip fields with dummyvalues like 'noauthor'
	 * @return The Endnote-serialized publication
	 */
	public static String toEndnoteString(final Post<BibTex> post, final boolean skipDummyValues) {
		StringWriter sw = new StringWriter();
		try {
			EndnoteUtils.append(sw, post, skipDummyValues);
		} catch (IOException ex) {
			// never happens
		}
		return sw.toString();
	}
}
