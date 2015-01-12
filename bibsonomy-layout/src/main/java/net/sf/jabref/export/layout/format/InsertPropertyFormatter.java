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

import java.util.Properties;

import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;

import net.sf.jabref.export.layout.LayoutFormatter;

/**
 * This Formatter inserts values of Properties managed by Spring
 *
 * @author MarcelM
 */
public class InsertPropertyFormatter implements LayoutFormatter {

	/* (non-Javadoc)
	 * @see net.sf.jabref.export.layout.LayoutFormatter#format(java.lang.String)
	 */
	@Override
	public String format(String arg0) {
		//Property,default
		String[] parts = arg0.split(",", 2);
		String property = null;
		String defValue = null;
		
		if (parts != null && parts.length == 2) {
			property = parts[0];
			defValue = parts[1];
		} else {
			property = arg0;
		}
		//Get Spring-Managed Properties
		Properties properties = JabrefLayoutRenderer.getProperties();
		if (properties != null) {
			return properties.getProperty(property);
		}
		return defValue;
	}

}
