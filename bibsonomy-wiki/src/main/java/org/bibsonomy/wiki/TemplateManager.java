package org.bibsonomy.wiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemplateManager {

	static {
		templates = new HashMap<String, String>();
		loadTemplatesFromFile();
	}

	private static final Map<String, String> templates;

	/**
	 * Receives a template by name.
	 * @param name the name of the template
	 * @return a template, if it exists in the resource folder
	 */
	public static String getTemplate(String name) {
		return templates.get(name);
	}
	
	/**
	 * Returns all possible template names for later use.
	 * @return all possible template names for later use.
	 */
	public static Set<String> getTemplateNames() {
		return templates.keySet();
	}

	private static void loadTemplatesFromFile() {
		URL resources = TemplateManager.class.getClassLoader().getResource("org/bibsonomy/wiki");

		File folder = new File(resources.getFile());
		for (File file : folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".wikitemplate");
			}
		})) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String content = "";
				while (reader.ready()) {
					content += reader.readLine() + "\n";
				}
				templates.put(file.getName().split("\\.")[0], content);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
