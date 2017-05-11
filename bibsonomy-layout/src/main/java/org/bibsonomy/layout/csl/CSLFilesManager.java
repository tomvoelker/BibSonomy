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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.filesystem.CslFileLogic;
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

	/**mapping from id to CSLStyle which contains the id itself, a display name and the content of the file */
	private Map<String, CSLStyle> cslFiles = new HashMap<>();
	
	/**custom user layouts */
	private Map<String, List<CSLStyle>> cslCustomFiles = new HashMap<>();
	
	private Map<String, String> cslLocaleFiles = new HashMap<>();
		
	/**
	 * init this manager
	 * reads all csl files from classpath: /org/citationstyles/styles/
	 */
	private void init() {
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
				try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StringUtils.DEFAULT_CHARSET))) {
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
		CSLStyle tmp = this.cslFiles.get(cslName);
		if (tmp == null && cslName.toLowerCase().startsWith("custom") && cslName.toLowerCase().endsWith(".csl")){
			//username extrahieren
			//layoutname laden.
			String cut = cslName.substring(0, cslName.length()-4);
			String userName = cut.substring(cut.indexOf('_') + 1);
			String layoutName = userName.substring(userName.indexOf('_') + 1);
			userName = userName.substring(0, userName.indexOf('_'));
			
			//because correct upper and lowercase is lost when written to DB.
			if(!cslCustomFiles.containsKey(userName)){
				if(cslCustomFiles.containsKey(userName.toLowerCase())){
					//a shot in the dark
					userName = userName.toLowerCase();
				} else {
					//no? => bruteforce :(
					for(String key: cslCustomFiles.keySet()){
						if(key.equalsIgnoreCase(userName)){
							userName = key;
						}
					}
				}
			}
			for(CSLStyle style : cslCustomFiles.get(userName)){
				if (style.getName().equalsIgnoreCase(cslName)){
					tmp = style;
					break;
				}
			}
		}
		return tmp;
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
	 * @return the layout for the given user. If no layout could be found, <code>null</code>
	 * is returned instead of throwing an exception. This allows for missing parts (i.e., 
	 * no begin.layout).
	 */
	public List<CSLStyle> getUserLayouts(final String userName) {
		cslCustomFiles.put(userName, new ArrayList<CSLStyle>());
		for(org.bibsonomy.model.Document document : getUploadedLayouts(userName)){
		/*
		 * check if custom filter exists
		 */
		if (present(userName)) {
			/*
			 * custom filter of current user is not loaded yet -> check if a filter exists at all
			 */
			try {
				CSLStyle layout = CslLayoutUtils.loadUserLayout(userName, document.getFileName(), this.config);
				
				/*
				 * we add the layout only to the map, if it is complete, i.e., it contains an item layout
				 */
				if (layout != null) {
					/*
					 * add user layout to map
					 */
					log.debug("user layout exists - loading it");
					cslCustomFiles.get(userName).add(layout);
				}
			} catch (final Exception e) {
				log.info("Error loading custom filter for user " + userName, e);
			}
		}
		}
		return cslCustomFiles.get(userName);
	}
	
	/** Loads all uploaded csl layouts from DB.
	 * 
	 * @param user
	 * @return list of all documents
	 */
	public List<org.bibsonomy.model.Document> getUploadedLayouts(final String user) {
		//loading from DB
		
		/**
		 * all styles from DB. So JabRef too
		 */
		final List<org.bibsonomy.model.Document> documents = logic.getDocuments(user);
		
		/**
		 * only csl files will be in this list
		 */
		List<org.bibsonomy.model.Document> cslLayouts = new ArrayList<org.bibsonomy.model.Document>();
		
		if(documents == null || documents.isEmpty()){
			return cslLayouts;
		}
		
		//filtering for correct file extension
		for (org.bibsonomy.model.Document document : documents){
			if(document.getFileName().endsWith(CslFileLogic.LAYOUT_FILE_EXTENSION)){
				cslLayouts.add(document);
			}
		}
		return cslLayouts;
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

	/** Unloads the custom layout of the user.
	 * 
	 * @param userName
	 */
	public void unloadUserLayout(final String userName, final String fileName) {
		for(CSLStyle style : cslCustomFiles.get(userName)){
			if(style.getId() == CslLayoutUtils.userLayoutName(userName, fileName)){
				cslCustomFiles.get(userName).remove(style);
			}
		}
	}
	
}
