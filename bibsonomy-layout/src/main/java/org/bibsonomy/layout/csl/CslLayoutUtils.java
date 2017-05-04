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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.filesystem.CslFileLogic;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.xml.sax.SAXException;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class CslLayoutUtils {
	private static final Log log = LogFactory.getLog(CslLayoutUtils.class);
	private static LogicInterface logic;
	
	/** Builds the hash for the custom layout files of the user.
	 * 
	 * @param user
	 * @return hash for custom file of user
	 */
	public static String userLayoutHash(final String user) {
		return StringUtils.getMD5Hash("user." + user.toLowerCase() + "." + CslFileLogic.LAYOUT_FILE_EXTENSION).toLowerCase();
	}
	
	/** Loads all uploaded csl layouts from DB.
	 * 
	 * @param user
	 * @param logic // undo! how to access?? static!
	 * @return list of all documents
	 */
	public static List<Document> getUploadedLayouts(final String user, LogicInterface logic) {
		//how to acces logic??
		final List<Document> documents = logic.getDocuments(user);
		List<Document> cslLayouts = new ArrayList<Document>();
		if(documents == null || documents.isEmpty()){
			return cslLayouts;
		}
		
		for (Document document : documents){
			if(document.getFileName().endsWith(CslFileLogic.LAYOUT_FILE_EXTENSION)){
				cslLayouts.add(document);
			}
		}
		return cslLayouts;
	}
	
	/**
	 * Builds the name of a custom user layout, for the map and elsewhere. Typically "custom_" + userName.
	 * 
	 * @param userName
	 * @return the name of a custom layout
	 */
	public static String userLayoutName(final String userName) {
		return LayoutRenderer.CUSTOM_LAYOUT + "_" + userName;
	}
	
	/** Loads a resource using the classloader.
	 * 
	 * @param location
	 * @return
	 */
	public static InputStream getResourceAsStream(final String location) {
		final InputStream resourceAsStream = CSLFilesManager.class.getClassLoader().getResourceAsStream(location);
		if (resourceAsStream != null) 
			return resourceAsStream;
		return CSLFilesManager.class.getResourceAsStream(location);
	}
	
	/** Constructs the name of a layout file.
	 * 
	 * @param layout
	 * @param part
	 * @return 
	 */
	protected static String getLayoutFileName(final String layout) {
		return layout + "." + CslFileLogic.LAYOUT_FILE_EXTENSION;
	}
	
	/** Create string for directories. If no given, the string is empty.
	 * @param directory
	 * @return
	 */
	private static String getDirectory(final String directory) {
		if (directory == null) return "";
		return directory + "/";
	}
	
	/**
	 * Loads a layout from the given location.
	 * 
	 * @param fileLocation - the location of the file, such that it can be found by the used class loader.
	 * @return The loaded layout, or <code>null</code> if it could not be found.
	 * @throws IOException
	 */
	private static CSLStyle loadLayout(final String fileLocation) throws IOException {
		final InputStream resourceAsStream = CslLayoutUtils.getResourceAsStream(fileLocation);
		CSLStyle cslLayout = null;
		if (resourceAsStream != null) {
			Scanner sc = new Scanner(resourceAsStream);
			StringBuilder sb = new StringBuilder();
			if (sc.hasNext()) {
				sb.append(sc.next());
			}
			sc.close();
			String fileContent = sb.toString();
			String displayName = "";
			String id;
			//TODO quite a bit hackyy
			try {
				displayName = CSLFilesManager.extractTitle(fileContent).trim();
			} catch (SAXException | ParserConfigurationException e1) {
				log.error("Failed to extract a display name in a user custom layout. XML-tag 'title' is missing or in the wrong place. File causing problems: " + fileLocation, e1);
				displayName = "User uploaded custom layout";
			}
			if(displayName == null || displayName.trim().isEmpty()){
				displayName = "User uploaded custom layout";
			}
			id = displayName.replaceAll(" ", "");
			cslLayout = new CSLStyle(id, displayName, fileContent);
		}
		return cslLayout;
	}
	
	/**
	 * @param userName
	 * @param config
	 * @return 
	 * @throws Exception 
	 */
	public static CSLStyle loadUserLayout(String userName, CslConfig config) throws Exception {
		/*
		 * initialize a new user layout
		 */
		final CSLStyle cslLayout = new CSLStyle(CslLayoutUtils.userLayoutName(userName));
		cslLayout.addDescription("en", "Custom layout of user " + userName);
		cslLayout.setDisplayName("custom");
		cslLayout.setMimeType("text/html"); // FIXME: this should be adaptable by the user ...
		cslLayout.setUserLayout(true);
		cslLayout.setPublicLayout(false);

		final String hashedName = CslLayoutUtils.userLayoutHash(userName);
		final File file = new File(FileUtil.getFileDirAsFile(config.getUserLayoutFilePath(), hashedName), hashedName);

		log.debug("trying to load custom user layout for user " + userName + " from file " + file);
		if (file.exists()) {
			log.debug("custom layout found!");
			
			final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringUtils.CHARSET_UTF_8));
			try {
				//TODO
				// cslLayout.addSubLayout(layoutHelper.getLayoutFromText(GLOBALS_FORMATTER_PACKAGE));
				} catch (final Exception e) {
					/*
					 * unfortunately, layoutHelper.getLayoutFromText throws a generic Exception, 
					 * so we catch it here
					 */
					throw new IOException ("Could not load layout: ", e);
				} finally {
					reader.close();
				}
			}		
		return cslLayout;
	}
}
