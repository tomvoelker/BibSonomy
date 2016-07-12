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
package org.bibsonomy.layout.standard;

import java.io.InputStream;

/**
 * 
 * @author:  lsc
 * 
 */
public class LayoutUtils {

	/**
	 * The file extension of layout filter file names.
	 */
	public final static String layoutFileExtension = "layout";

	/** Loads a resource using the classloader.
	 * 
	 * @param location
	 * @return
	 */
	public static InputStream getResourceAsStream (final String location) {
		final InputStream resourceAsStream = StandardLayouts.class.getClassLoader().getResourceAsStream(location);
		if (resourceAsStream != null) 
			return resourceAsStream;
		return StandardLayouts.class.getResourceAsStream(location);
	}

	/** Constructs the name of a layout file.
	 * 
	 * @param layout
	 * @param part
	 * @return
	 */
	protected static String getLayoutFileName(final String layout, final String part) {
		return layout + "." + part + "." + layoutFileExtension;
	}

	protected static String getLayoutFileName(final String layout) {
		return layout + "." + layoutFileExtension;
	}
}

