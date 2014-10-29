/**
 *
 *  BibSonomy-Layout - Layout engine for the webapp.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package net.sf.jabref.export.layout.format;

import net.sf.jabref.AuthorList;
import net.sf.jabref.AuthorList.Author;

/**
 * @author Sebastian Böttger, sbo@cs.uni-kassel.de
 */
public class MittelalterEditorFirstLast extends MittelalterEditorNamesFormatter {

	@Override
	public String format(String fieldText) {
		return getEditorsString(fieldText);
	}
	
	@Override
	protected String getPersonName(Author a) {
		return a.getFirstLast(false);
	}
	
	@Override
	protected String getPersonNames(AuthorList list) {
		return list.getAuthorsFirstFirst(false, false);
	}
}
