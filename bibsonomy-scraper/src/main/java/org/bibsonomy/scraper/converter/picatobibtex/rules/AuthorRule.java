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

package org.bibsonomy.scraper.converter.picatobibtex.rules;


import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;
import org.bibsonomy.scraper.converter.picatobibtex.Row;
import org.bibsonomy.util.StringUtils;

/**
 * @author daill
 * @version $Id$
 */
public class AuthorRule extends Rules {
	private static final String SECOND_SUB_CATEGORY = "$8";
	private static final String[] AUTHOR_CATEGORIES = new String[]{"028A", "028B", "028C", "028D"};

	/**
	 * @param pica
	 */
	public AuthorRule(final PicaRecord pica){
		super(pica, null);
	}

	@Override
	public String getContent() {
		final List<String> authors = new LinkedList<String>();

		/*
		 * FIXME: use PersonNames to build author list and then serialize using
		 * PersonNameUtils.serializePersonNames().
		 */
		for (final String authorCategory : AUTHOR_CATEGORIES){
			// get the main category
			if (this.pica.isExisting(authorCategory)) {
				final Row row = this.pica.getRow(authorCategory);

				final String author = getAuthor(authorCategory, row); 
				if (present(author)) {
					authors.add(author);
				}
			} 

			final List<String> subAuthors = getSubAuthors(authorCategory);
			if (present(subAuthors)) {
				authors.addAll(subAuthors);
			}

		}

		return PicaUtils.cleanString(StringUtils.implodeStringCollection(authors, " and "));
	}

	private String getAuthor(final String authorCategory, final Row row) {
		if (row.isExisting(DEFAULT_SUB_CATEGORY)) {
			return PicaUtils.getData(this.pica, authorCategory, DEFAULT_SUB_CATEGORY) + ", " + PicaUtils.getData(this.pica, authorCategory, "$d").trim();
		} else if (row.isExisting(SECOND_SUB_CATEGORY)) {
			return PicaUtils.getData(this.pica, authorCategory, SECOND_SUB_CATEGORY).replaceAll("\\*.*\\*", "").trim();
		}
		return null;
	}

	@Override
	public boolean isAvailable() {
		for (final String authorCategory : AUTHOR_CATEGORIES) {
			if (this.pica.isExisting(authorCategory)){
				return true;
			}
		}

		return false;
	}

	private List<String> getSubAuthors(final String cat) {
		final List<String> authors = new LinkedList<String>();


		// get all other author by specific category
		int ctr = 1;

		Row row;
		
		String authorCategory = cat + getString(ctr);
		while ((row = this.pica.getRow(authorCategory)) != null) {
			
			final String author = getAuthor(authorCategory, row); 
			if (present(author)) {
				authors.add(author);
			}

			authorCategory = cat + getString(++ctr);
		}

		return authors;
	}
	
	private static String getString(final Integer i) {
		if (i < 10) {
			return "/0" + i;
		}
		return "/" + i;
	}

}
