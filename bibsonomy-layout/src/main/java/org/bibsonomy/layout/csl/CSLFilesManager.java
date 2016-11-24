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
package org.bibsonomy.layout.csl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * class for managing all csl files
 * 
 * @author jp
 */
public class CSLFilesManager {
	private static final Log log = LogFactory.getLog(CSLFilesManager.class);
	
	private static final String BASE_PATH = "classpath:/org/citationstyles/";
	private static final String BASE_PATH_STYLES = BASE_PATH + "styles/";
	private static final String BASE_PATH_LOCALES = BASE_PATH + "locales/";

	// mapping from id to CSLStyle which contains the id itself, a display name and the content of the file
	private Map<String, CSLStyle> cslFiles = new HashMap<>();

	private Map<String, String> cslLocaleFiles = new HashMap<>();
	
	/**
	 * init this manager
	 * reads all csl files from classpath: /org/citationstyles/styles/
	 */
	public void init() {
		final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(CSLFilesManager.class.getClassLoader());

		try {
			final Resource aliasesResource = resolver.getResource(BASE_PATH_STYLES + "renamed-styles.json");
			final BufferedReader jsonReader = new BufferedReader(new InputStreamReader(aliasesResource.getInputStream(), StringUtils.DEFAULT_CHARSET));
			final StringBuilder jsonBuilder = new StringBuilder();
			while (jsonReader.ready()) {
				jsonBuilder.append(jsonReader.readLine());
			}
			final Map<String, Set<String>> aliases = new HashMap<>();
			// "inverting" hashmap
			final JSONObject aliasesObj = JSONObject.fromObject(jsonBuilder.toString());
			for (final Object keyObj : aliasesObj.keySet() ){
				final String key = (String) keyObj;
				final String value = (String) aliasesObj.get(key);
				
				if (!aliases.containsKey(value)) {
					aliases.put(value, new HashSet<String>());
				}
				aliases.get(value).add(key);
			}
			final Resource[] resources = resolver.getResources(BASE_PATH_STYLES + "*.csl");
			
			for (final Resource resource : resources) {
				try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
					final StringBuilder builder = new StringBuilder();
					while (reader.ready()) {
						builder.append(reader.readLine() + "\n");
					}
					
					try {
						final String cslStyleSource = builder.toString().trim();
						final String fileName = resource.getFilename();
						final String layoutName = fileName.toLowerCase().replace(".csl", "");
						this.cslFiles.put(layoutName, new CSLStyle(fileName, extractTitle(cslStyleSource), cslStyleSource));
						if (aliases.containsKey(layoutName)){
							for (final String alias : aliases.get(layoutName)){
								this.cslFiles.put(alias, new CSLStyle(fileName, extractTitle(cslStyleSource), cslStyleSource, layoutName));
							}
						}
					} catch (final ParserConfigurationException | SAXException | IOException e) {
						log.error("error reading file " + resource.getFilename(), e);
					}
				}
			}
			
			this.cslLocaleFiles = loadLanguageFiles(resolver);
		} catch (final IOException e) {
			log.error("error while loading csl files", e);
		}
	}

	/**
	 * @param resolver 
	 * @return
	 * @throws IOException 
	 */
	private static Map<String, String> loadLanguageFiles(PathMatchingResourcePatternResolver resolver) throws IOException {
		final Map<String, String> locales = new HashMap<>();
		final Resource[] resources = resolver.getResources(BASE_PATH_LOCALES + "locales-*.xml");
		for (final Resource resource : resources) {
			try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
				final StringBuilder builder = new StringBuilder();
				while (reader.ready()) {
					builder.append(reader.readLine() + "\n");
				}
				
				final String cslLocaleSource = builder.toString().trim();
				final String locale = resource.getFilename().replaceAll("locales-", "").replaceAll(".xml", "");
				locales.put(locale, cslLocaleSource);
			}
		}
		return locales;
	}

	/**
	 * @param cslStyleSource
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	static String extractTitle(final String cslStyleSource) throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		final Document document = documentBuilder.parse(new InputSource(new StringReader(cslStyleSource)));
		
		return document.getElementsByTagName("title").item(0).getTextContent().trim();
	}
	
	/**
	 * @param cslName
	 * @return the csl style
	 */
	public CSLStyle getStyleByName(final String cslName) {
		return this.cslFiles.get(cslName);
	}
	
	/**
	 * @param locale
	 * @return the locale file content
	 */
	public String getLocaleFile(final String locale) {
		return this.cslLocaleFiles.get(locale);
	}

	/**
	 * @return the cslFiles
	 */
	public Map<String, CSLStyle> getCslFiles() {
		return Collections.unmodifiableMap(this.cslFiles);
	}
	
	/** Unloads the custom layout of the user.
	 * 
	 * @param userName
	 */
	public void unloadUserLayout(final String userName) {
		cslFiles.remove(CslLayoutUtils.userLayoutName(userName));
	}
}
