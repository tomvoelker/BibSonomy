/**
 * BibSonomy-Layout - Layout engine for the webapp.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * TODO: document difference to {@link MittelalterEditorNamesFormatter}
 * TODO: add documentation to this class
 *
 * @author sbo
 */
public class MittelalterPersonNamesFormatter implements LayoutFormatter {
	
	// TODO: check if this pattern is sufficient for every strange author list
	private static final Pattern PERSON_NAMES_PATTERN = Pattern.compile("(\\A|and\\s)(.+?)(,|\\z)");

	@Override
	public String format(String fieldString) {
		fieldString = fieldString.trim().replaceAll("[\\{\\}]", ""); // we need to remove curly brackets, as they break the regex later
		Matcher m = PERSON_NAMES_PATTERN.matcher(fieldString);
		while (m.find()) {
			fieldString = fieldString.replaceFirst(Pattern.quote(m.group()), Matcher.quoteReplacement(m.group(1) + "<span style=\"font-variant: small-caps\">" + m.group(2) + "</span>" + m.group(3)));
		}
		return fieldString;
	}

}
