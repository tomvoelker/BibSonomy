package org.bibsonomy.wiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			// DO NOTHING
		}
	}
}
