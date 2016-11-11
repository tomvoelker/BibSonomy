package org.bibsonomy.layout.csl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.services.filesystem.CslFileLogic;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.xml.sax.SAXException;

import net.sf.jabref.export.layout.LayoutHelper;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class CslLayoutUtils {
	private static final Log log = LogFactory.getLog(CslLayoutUtils.class);
	
	/** Builds the hash for the custom layout files of the user.
	 * 
	 * @param user
	 * @return
	 */
	public static String userLayoutHash (final String user) {
		return StringUtils.getMD5Hash("user." + user.toLowerCase() + "." + CslFileLogic.LAYOUT_FILE_EXTENSION).toLowerCase();
	}
	
	/**
	 * Builds the name of a custom user layout, for the map and elsewhere. Typically "custom_" + userName.
	 * 
	 * @param userName
	 * @return the name of a custom layout
	 */
	public static String userLayoutName (final String userName) {
		return LayoutRenderer.CUSTOM_LAYOUT + "_" + userName;
	}
	
	/** Loads a resource using the classloader.
	 * 
	 * @param location
	 * @return
	 */
	public static InputStream getResourceAsStream (final String location) {
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
			final LayoutHelper layoutHelper = new LayoutHelper(reader);
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
