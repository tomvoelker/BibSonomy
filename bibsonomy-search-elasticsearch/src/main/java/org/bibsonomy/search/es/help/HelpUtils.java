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
	/** the file suffix */
	public static final String FILE_SUFFIX = ".md";

	private HelpUtils() {}

	/**
	 * build Map for variable replacement
	 * @param projectName
	 * @param projectTheme
	 * @param projectHome
	 * @return the map
	 */
	public static Map<String, String> buildReplacementMap(final String projectName, final String projectTheme, final String projectHome) {
		final Map<String, String> replacements = new HashMap<>();
		replacements.put("project.name", projectName);
		replacements.put("project.theme", projectTheme);
		replacements.put("project.home", projectHome);
		replacements.put("project.version", BasicUtils.VERSION);
		return replacements;
	}

}
