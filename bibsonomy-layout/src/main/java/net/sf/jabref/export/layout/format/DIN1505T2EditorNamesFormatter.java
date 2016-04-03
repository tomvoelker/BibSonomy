/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * This formatter prints "(Bearb.)" behind each name. Input must already be
 * formatted by the DIN1505T2PersonNamesFormatter.
 * 
 * @author hagen
 *
 */
public class DIN1505T2EditorNamesFormatter implements LayoutFormatter {
	
	public static final Pattern EDITOR_NAMES_PATTERN = Pattern.compile("((\\A|\\s)<span style=\"font-variant: small-caps\">.+?</span>.*?)(\\s;|\\z)"); 
	
	@Override
	public String format(String arg0) {
		Matcher m = EDITOR_NAMES_PATTERN.matcher(arg0);
		while (m.find()) {
			arg0 = arg0.replace(m.group(), m.group(1) + " (Bearb.)" + m.group(3));
		}
		return arg0;
	}

}
