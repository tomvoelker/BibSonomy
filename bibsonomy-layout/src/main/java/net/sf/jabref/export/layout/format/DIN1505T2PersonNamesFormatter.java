/**
 *
 *  BibSonomy-Layout - Layout engine for the webapp.
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

package net.sf.jabref.export.layout.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.layout.LayoutFormatter;

public class DIN1505T2PersonNamesFormatter implements LayoutFormatter {
	
	public static final Pattern PERSON_NAMES_PATTERN = Pattern.compile("(\\A|;\\s)(.+?)(,|\\z)");

	@Override
	public String format(String arg0) {
		arg0 = arg0.trim().replaceAll("[\\{\\}]", ""); // we need to remove curly brackets, as they break the regex later
		Matcher m = PERSON_NAMES_PATTERN.matcher(arg0);
		while (m.find()) {
			arg0 = arg0.replaceFirst(Matcher.quoteReplacement(m.group()), m.group(1) + "<span style=\"font-variant: small-caps\">" + m.group(2) + "</span>" + m.group(3));
		}
		return arg0;
	}

}
