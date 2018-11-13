/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.help;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.util.BasicUtils;

/**
 * utils for help
 *
 * @author dzo
 */
public final class HelpUtils {
	/** the key for project home */
	public static final String PROJECT_HOME = "project.home";
	/** the file suffix */
	public static final String FILE_SUFFIX = ".md";
	/** the name of the markdown file of the sidebar */
	public static String HELP_SIDEBAR_NAME = "Sidebar";

	private HelpUtils() {}

	/**
	 * build Map for variable replacement
	 * @param projectName
	 * @param projectTheme
	 * @param projectHome
	 * @param projectEmail
	 * @param projectNoSpamEmail
	 * @param projectAPIEmail 
	 * @return the map
	 */
	public static Map<String, String> buildReplacementMap(final String projectName, final String projectTheme, final String projectHome, final String projectEmail, final String projectNoSpamEmail, final String projectAPIEmail) {
		final Map<String, String> replacements = new HashMap<>();
		replacements.put("project.name", projectName);
		replacements.put("project.theme", projectTheme);
		replacements.put(PROJECT_HOME, projectHome);
		replacements.put("project.version", BasicUtils.VERSION);
		replacements.put("project.email", projectEmail);
		replacements.put("project.nospamemail", projectNoSpamEmail);
		replacements.put("project.apiemail", projectAPIEmail);
		return replacements;
	}


}
