/**
 * 
 * BibSonomy-Layout - Layout engine for the webapp.
 * 
 * Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * http://www.kde.cs.uni-kassel.de/
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package net.sf.jabref.export.layout.format;

import net.sf.jabref.AuthorList;
import net.sf.jabref.AuthorList.Author;
import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * @author Sebastian Böttger sbo@cs.uni-kassel.de
 */
public abstract class MittelalterEditorNamesFormatter implements LayoutFormatter {

	/**
	 * returns
	 * 
	 * @param fieldText
	 * @return formatted string
	 */
	public String getEditorsString(final String fieldText) {
		final AuthorList authorlist = AuthorList.getAuthorList(fieldText);
		final StringBuilder ret = new StringBuilder();
		/*
		 * If the publication has more than three editors only the first is
		 * displayed with (Hgg.) to hint at further editors.
		 */
		if (authorlist.size() > 3) {
			ret.append(this.getPersonName(authorlist.getAuthor(0))).append(" (Hgg.)");
		} else {
			ret.append(this.getAllPersonsString(authorlist));
		}

		return ret.toString();
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	protected String getAllPersonsString(final AuthorList list) {
		final StringBuilder ret = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			String author = this.getPersonName(list.getAuthor(i));

			// (Über.) for translator
			if (!author.toLowerCase().contains("(Über.)".toLowerCase())) {
				ret.append(author).append(" (Hrsg.)");
			} else {
				// replace (Über.)
				author = author.replaceFirst("\\s\\(Über\\.\\)", "");

				// append (Über.) at the end
				ret.append(author).append(" (Über.)");
			}

			if (i == (list.size() - 2)) {
				ret.append(" und ");
			} else if (i < (list.size() - 2)) {
				ret.append(", ");
			}
		}
		return ret.toString();
	}

	/**
	 * returns name of
	 * 
	 * @param a
	 * @return
	 */
	protected abstract String getPersonName(final Author a);
	
	/**
	 * @param list
	 * @return
	 */
	protected abstract String getPersonNames(final AuthorList list);

}
