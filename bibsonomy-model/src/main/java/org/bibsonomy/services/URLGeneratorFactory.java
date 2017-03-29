/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.services;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * a factory for {@link URLGenerator}
 * @author dzo
 */
public class URLGeneratorFactory {
	
	/**
	 * creates a new url generator for the specified systemUrl
	 * @param systemUrl
	 * @return the {@link URLGenerator} for the provided systemUrl
	 */
	public URLGenerator createURLGeneratorForSystem(final String systemUrl) {
		return new URLGenerator(systemUrl);
	}
	
	/**
	 * @param projectHome
	 * @param prefix
	 * @return the {@link URLGenerator} for this prefix
	 */
	public URLGenerator createURLGeneratorForPrefix(final String projectHome, final String prefix) {
		return new URLGenerator(buildProjectHome(projectHome, prefix));
	}

	/**
	 * @param projectHome
	 * @param prefix
	 * @return the projectHome including the prefix
	 */
	protected static String buildProjectHome(final String projectHome, final String prefix) {
		String rootPath = projectHome;
		if (!present(rootPath)) {
			rootPath = "";
		}
		
		if (!rootPath.endsWith("/")) {
			rootPath += "/";
		}
		
		if (present(prefix)) {
			rootPath += prefix;
		}
		
		if (!rootPath.endsWith("/")) {
			rootPath += "/";
		}
		
		return rootPath;
	}
}
