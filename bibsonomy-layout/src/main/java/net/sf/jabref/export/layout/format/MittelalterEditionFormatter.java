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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * 
 * @author Sebastian Böttger, sbo@cs.uni-kassel.de
 */
public class MittelalterEditionFormatter implements LayoutFormatter {
	
	private static final Pattern EDITION_NUMBER_PATTERN = Pattern.compile(".*(\\d).*"); 
	
	@Override
	public String format(String arg0) {
		Matcher m = EDITION_NUMBER_PATTERN.matcher(arg0);
		while (m.find()) {
			int edition = Integer.parseInt(m.group(1));
			if(edition > 1) {
				arg0 = arg0.replace(m.group(), "<sup>" + edition + "</sup>");
			}
		}
		return arg0;
	}
}
