/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jabref.export.layout.format;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.export.layout.LayoutFormatter;

import org.apache.commons.lang.StringUtils;

/**
 * This is an ugly hack which is needed to only print "&" between authors and editors if both authors and editors are present.
 * XXX: replace with <code>\begin{author&&editor} & \end{author&&editor}</code> in later JabRef Versions.
 * 
 * @author Jens Illig
 */
public class AndSymbolIfBothPresent implements LayoutFormatter{
	
	private static final String SEPARATOR = "@SEPARATOR@";
	public static final String AUTHORS_AND_EDITORS = "authorsAndEditors";

	@Override
	public String format(String arg0) {
		final int i = StringUtils.indexOf(arg0, SEPARATOR);
		if ((i > 0) && (i < (StringUtils.length(arg0) - SEPARATOR.length()))) {
			return " & ";
		}
		return "";
	}

	public static void prepare(BibtexEntry entry) {
		entry.setField(AUTHORS_AND_EDITORS, "" + StringUtils.trimToEmpty(entry.getField("author")) + SEPARATOR + StringUtils.trimToEmpty(entry.getField("editor")));
	}

}
