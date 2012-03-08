/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Converter for OAI to BibTeX
 * @author tst
 * @version $Id$
 */
public class OAIConverter {

	private static final Pattern PATTERN_TITLE = Pattern.compile("<dc:title>([^<]*)<");
	private static final Pattern PATTERN_CREATOR = Pattern.compile("<dc:creator>([^<]*)<");
	private static final Pattern PATTERN_DESCRIPTION = Pattern.compile("<dc:description>([^<]*)<", Pattern.MULTILINE);
	private static final Pattern PATTERN_DATE = Pattern.compile("<dc:date>([^<]*)<");
	private static final Pattern PATTERN_IDENTIFIER = Pattern.compile("<dc:identifier>([^<]*)<");

	private static final Pattern PATTERN_YEAR = Pattern.compile(".*(\\d{4}).*");

	/**
	 * convert a oai2 refernce into bibtex
	 * @param reference 
	 * @return The resultign BibTeX string.
	 * @throws ScrapingException
	 */
	public static String convert(String reference) throws ScrapingException{

		/*
		 * title
		 */
		String title = null;
		final Matcher matcherTitle = PATTERN_TITLE.matcher(reference);
		if (matcherTitle.find()) {
			title = StringEscapeUtils.unescapeHtml(matcherTitle.group(1));
		}

		/*
		 * author 
		 */
		final List<PersonName> author = new LinkedList<PersonName>();
		final Matcher matcherCreator = PATTERN_CREATOR.matcher(reference);
		while (matcherCreator.find()) {
			author.addAll(PersonNameUtils.discoverPersonNamesIgnoreExceptions(StringEscapeUtils.unescapeHtml(matcherCreator.group(1))));
		}


		String year = null;
		final Matcher matcherDate = PATTERN_DATE.matcher(reference);
		if (matcherDate.find()) {
			final String date = matcherDate.group(1);
			final Matcher matcherYear = PATTERN_YEAR.matcher(date);
			if (matcherYear.find()) {
				year = matcherYear.group(1);
			}
		}
		
		String description = "";
		String note = "";
		final Matcher matcherDescription = PATTERN_DESCRIPTION.matcher(reference);
		while (matcherDescription.find()) {
			if (matcherDescription.group(1).startsWith("Comment:"))
				note = StringEscapeUtils.unescapeHtml(matcherDescription.group(1));
			else
				description = description + StringEscapeUtils.unescapeHtml(matcherDescription.group(1)) + " ";
		}

		String doi = null;
		String url = null;
		final Matcher matcherIdentifier = PATTERN_IDENTIFIER.matcher(reference);
		while (matcherIdentifier.find()) {
			final String identifier = matcherIdentifier.group(1);
			if (identifier.startsWith("doi:")) {
				doi = identifier.substring("doi:".length());
			} else if (identifier.startsWith("http")) {
				url = StringEscapeUtils.unescapeHtml(identifier);
			}
		}


		// start with BibTeX key
		final StringBuilder bibtexResult = new StringBuilder("@misc{" + BibTexUtils.generateBibtexKey(author, null, year, title) + ",\n");

		// title
		if (present(title))
			bibtexResult.append("  title = {" + title + "},\n");
		
		// author
		if (present(author))
			bibtexResult.append("  author = {" + PersonNameUtils.serializePersonNames(author) + "},\n");

		// year
		if (present(year)) 
			bibtexResult.append("  year = {" + year + "},\n");

		// abstract
		if (present(description))
			bibtexResult.append("  abstract = {" + description.trim() + "},\n");
		
		// URL
		if (present(url))
			bibtexResult.append("  url = {" + url + "},\n");

		// DOI
		if (present(doi))
			bibtexResult.append("  doi = {" + doi + "},\n");

		// note
		if (present(note))
			bibtexResult.append("  note = {" + note + "},\n");

		// finish BibTeX
		bibtexResult.append("}\n");

		return bibtexResult.toString();
	}

}
