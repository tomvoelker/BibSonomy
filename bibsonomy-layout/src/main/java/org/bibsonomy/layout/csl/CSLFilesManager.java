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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.export.CSLUtils;
import org.bibsonomy.services.filesystem.CslFileLogic;
import org.bibsonomy.util.IOUtils;
import org.bibsonomy.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.json.JSONObject;

/**
 * class for managing all csl files
 * 
 * @author jp
 */
public class CSLFilesManager {
	private static final Log log = LogFactory.getLog(CSLFilesManager.class);
	private static LogicInterface logic;

	private static final String BASE_PATH = "classpath:/org/citationstyles/";
	private static final String BASE_PATH_STYLES = BASE_PATH + "styles/";
	private static final String BASE_PATH_LOCALES = BASE_PATH + "locales/";

	private CslConfig config;

	/**
	 * mapping from id to CSLStyle which contains the id itself, a display name
	 * and the content of the file
	 */
	private Map<String, CSLStyle> cslFiles = new HashMap<>();

	/** custom user layouts */
	private Map<String, List<CSLStyle>> cslCustomFiles = new HashMap<>();

	private Map<String, String> cslLocaleFiles = new HashMap<>();

	/**
	 * init this manager
	 * - reads all csl files from classpath: /org/citationstyles/styles/
	 */
	protected void init() {
		final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(CSLFilesManager.class.getClassLoader());

		try {
			final Resource aliasesResource = resolver.getResource(BASE_PATH_STYLES + "renamed-styles.json");
			final String jsonContent = IOUtils.readInputStreamToString(aliasesResource.getInputStream());
			final Map<String, Set<String>> aliases = new HashMap<>();
			// "inverting" hashmap
			final JSONObject aliasesObj = JSONObject.fromObject(jsonContent);
			for (final Object keyObj : aliasesObj.keySet()) {
				final String key = (String) keyObj;
				final String value = (String) aliasesObj.get(key);

				if (!aliases.containsKey(value)) {
					aliases.put(value, new HashSet<String>());
				}
				aliases.get(value).add(key);
			}

			final Resource[] resources = resolver.getResources(BASE_PATH_STYLES + "*.csl");
			for (final Resource resource : resources) {
				try {
					// get the content, the style name, and the title of the csl file
					final String cslStyleSource = IOUtils.readInputStreamToString(resource.getInputStream());
					final String fileName = resource.getFilename();
					final String layoutName = fileName.toLowerCase().replace(".csl", "");
					this.cslFiles.put(layoutName, new CSLStyle(fileName, CSLUtils.extractTitle(cslStyleSource), cslStyleSource));

					// also add the style to the configured aliases
					if (aliases.containsKey(layoutName)) {
						for (final String alias : aliases.get(layoutName)) {
							this.cslFiles.put(alias, new CSLStyle(fileName, CSLUtils.extractTitle(cslStyleSource), cslStyleSource, layoutName));
						}
					}
				} catch (final ParserConfigurationException | SAXException | IOException e) {
					log.error("error reading file " + resource.getFilename(), e);
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
				final String locale = FilenameUtils.getBaseName(resource.getFilename()).replaceAll("locales-", "");
				locales.put(locale, cslLocaleSource);
			}
		}
		return locales;
	}

	/**
	 * @param cslName
	 * @return the csl style
	 */
	public CSLStyle getStyleByName(final String cslName) {
		final boolean custom = cslName.toUpperCase().startsWith(CSLUtils.CUSTOM_PREFIX);

		if (custom) {
			return this.loadUserLayout(cslName);
		}

		final CSLStyle globalStyle = this.cslFiles.get(cslName);
		return globalStyle;
	}

	private CSLStyle loadUserLayout(final String cslName) {
		/*
		 * cslName is in format "CUSTOM USER_NAME STYLE"
		 */
		final String cut = FilenameUtils.getBaseName(cslName); // remove extension
		final String[] parts = cut.split(" ", 3);

		// extract username
		final String userName = parts[1].toLowerCase();

		// search layout owned by the parsed username
		for (final CSLStyle style : this.loadUserLayouts(userName)) {
			if (FilenameUtils.getBaseName(style.getName()).equalsIgnoreCase(cslName)) {
				return style;
			}
		}

		// not found return null
		return null;
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

	/**
	 * @param userName
	 * @return the layout for the given user. If no layout could be found,
	 *         <code>null</code> is returned instead of throwing an exception.
	 */
	public synchronized List<CSLStyle> loadUserLayouts(final String userName) {
		if (this.cslCustomFiles.containsKey(userName)) {
			return this.cslCustomFiles.get(userName);
		}

		final List<CSLStyle> cslLayoutFiles = new LinkedList<>();
		this.cslCustomFiles.put(userName, cslLayoutFiles);
		for (org.bibsonomy.model.Document document : getUploadedLayouts(userName)) {
			/*
			 * check if custom filter exists
			 */
			if (present(userName)) {
				/*
				 * custom filter of current user is not loaded yet -> check if a
				 * filter exists at all
				 */
				try {
					final CSLStyle layout = CslLayoutUtils.loadUserLayout(userName, document.getFileName(), this.config);

					/*
					 * we add the layout only to the map, if it is complete,

					 */
					if (layout != null) {
						/*
						 * add user layout to map
						 */
						log.debug("user layout exists - loading it");
						cslLayoutFiles.add(layout);
					}
				} catch (final Exception e) {
					log.info("Error loading custom filter for user " + userName, e);
				}
			}
		}

		return cslLayoutFiles;
	}

	/**
	 * Loads all uploaded csl layouts from database.
	 * 
	 * @param user
	 * @return list of all documents
	 */
	private List<org.bibsonomy.model.Document> getUploadedLayouts(final String user) {
		/*
		 * all styles from DB. So JabRef too
		 */
		final List<org.bibsonomy.model.Document> documents = logic.getDocuments(user);

		/*
		 * only csl files will be in this list
		 * filtering for correct file extension
		 */
		final List<org.bibsonomy.model.Document> cslLayouts = new LinkedList<>();
		for (org.bibsonomy.model.Document document : documents) {
			if (document.getFileName().endsWith(CslFileLogic.LAYOUT_FILE_EXTENSION)) {
				cslLayouts.add(document);
			}
		}
		return cslLayouts;
	}

	/**
	 * Unloads the custom layout of the user.
	 * should be tread-safe
	 *
	 * @param userName
	 * @param fileName
	 */
	public synchronized void unloadUserLayout(final String userName, final String fileName) {
		CSLStyle foundLayout = null;
		for (final CSLStyle style : cslCustomFiles.get(userName)) {
			if (style.getId().equals(CslLayoutUtils.userLayoutName(userName, fileName))) {
				foundLayout = style;
				break;
			}
		}
		cslCustomFiles.get(userName).remove(foundLayout);
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(CslConfig config) {
		this.config = config;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		CSLFilesManager.logic = logic;
	}

}
