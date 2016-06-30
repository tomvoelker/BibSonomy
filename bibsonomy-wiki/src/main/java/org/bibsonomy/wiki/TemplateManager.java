/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
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
package org.bibsonomy.wiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 
 * manager that hold all cv wiki templates
 * 
 * @author tni
 */
public class TemplateManager {

	static {
		templates = new HashMap<String, String>();
		loadTemplatesFromFile();
	}

	private static final Map<String, String> templates;

	/**
	 * Receives a template by name.
	 * 
	 * @param name
	 *            the name of the template
	 * @return a template, if it exists in the resource folder
	 */
	public static String getTemplate(String name) {
		return templates.get(name);
	}

	/**
	 * Returns all possible template names for later use.
	 * 
	 * @return all possible template names for later use.
	 */
	public static Set<String> getTemplateNames() {
		return templates.keySet();
	}

	private static void loadTemplatesFromFile() {
		final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(TemplateManager.class.getClassLoader());
		try {
			final Resource[] resources = resolver.getResources("classpath:/org/bibsonomy/wiki/*.wikitemplate");
			for (Resource r : resources) {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(r.getInputStream()));
					String content = "";
					while (reader.ready()) {
						content += reader.readLine() + "\n";
					}
					templates.put(r.getFilename().split("\\.")[0], content);
					reader.close();
				} catch (IOException e) {
				}
				
			}
		} catch (IOException e) {
		}
	}
}
