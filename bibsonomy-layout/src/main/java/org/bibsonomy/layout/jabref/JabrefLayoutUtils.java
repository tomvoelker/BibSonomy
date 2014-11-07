/**
 *
 *  BibSonomy-Layout - Layout engine for the webapp.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.layout.jabref;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.jabref.export.layout.Layout;
import net.sf.jabref.export.layout.LayoutHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.services.filesystem.JabRefFileLogic;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;

/**
 * 
 * @author:  rja
 */
public class JabrefLayoutUtils {
	private static final Log log = LogFactory.getLog(JabrefLayoutUtils.class);
	
	/** copied from JabRefs Globals */
	private static final String GLOBALS_FORMATTER_PACKAGE = "net.sf.jabref.export.layout.format.";
	
	/**
	 * One layout my consist of several files - e.g., sublayouts. These are 
	 * the possible (typical) sublayouts. They're used as part of the file 
	 * name.
	 */
	private static final String[] SUB_LAYOUTS = new String[] {
		"", 											     /* the default layout - should always exist; renders one entry */
		"." + LayoutPart.BEGIN.name().toLowerCase(), 	     /* the beginning - is added to the beginning of the rendered entries */
		"." + LayoutPart.EMBEDDEDBEGIN.name().toLowerCase(), /* the beginning - for embedded layouts */
		"." + LayoutPart.END.name().toLowerCase(),			 /* the end - is added to the end of the rendered entries */
		"." + LayoutPart.EMBEDDEDEND.name().toLowerCase(),	 /* the end - for embedded layouts */
		".article",								             /* ****************************************************** */ 
		".inbook",								             /* the remaining sublayouts are for different entry types */
		".book",
		".booklet",
		".incollection",
		".conference",
		".inproceedings",
		".proceedings",
		".manual",
		".mastersthesis",
		".phdthesis",
		".techreport",
		".unpublished",
		".patent",
		".periodical",
		".presentation",
		".preamble",
		".standard",
		".electronic",
		".periodical",
		".misc",
		".other"
	};

	/** Builds the hash for the custom layout files of the user. Depending on the 
	 * layout part the hash differs.
	 * 
	 * @param user
	 * @param part
	 * @return
	 */
	public static String userLayoutHash (final String user, final LayoutPart part) {
		return StringUtils.getMD5Hash("user." + user.toLowerCase() + "." + part + "." + JabRefFileLogic.LAYOUT_FILE_EXTENSION).toLowerCase();
	}
	
	/** Builds the name of a custom user layout, for the map and elsewhere. Typically "custom_" + userName.
	 * 
	 * @param userName
	 * @return
	 */
	public static String userLayoutName (final String userName) {
		return "custom_" + userName;
	}

	/** Loads a resource using the classloader.
	 * 
	 * @param location
	 * @return
	 */
	public static InputStream getResourceAsStream (final String location) {
		final InputStream resourceAsStream = JabrefLayoutRenderer.class.getClassLoader().getResourceAsStream(location);
		if (resourceAsStream != null) 
			return resourceAsStream;
		return JabrefLayoutRenderer.class.getResourceAsStream(location);
	}

	/** Constructs the name of a layout file.
	 * 
	 * @param layout
	 * @param part
	 * @return
	 */
	protected static String getLayoutFileName(final String layout, final String part) {
		return layout + "." + part + "." + JabRefFileLogic.LAYOUT_FILE_EXTENSION;
	}

	protected static String getLayoutFileName(final String layout) {
		return layout + "." + JabRefFileLogic.LAYOUT_FILE_EXTENSION;
	}

	/**
	 * @param jabrefLayout
	 * @param config
	 * @return
	 * @throws IOException 
	 */
	public static Map<String, Layout> loadSubLayouts(JabrefLayout jabrefLayout, JabRefConfig config) throws IOException {
		final Map<String, Layout> subLayouts = new HashMap<String, Layout>();
		final String filePath = config.getDefaultLayoutFilePath() + "/" + getDirectory(jabrefLayout.getDirectory());
		/*
		 * iterate over all subLayouts and check if each exists
		 */
		for (final String subLayout : SUB_LAYOUTS) {
			final String fileName = filePath + jabrefLayout.getBaseFileName() + subLayout + "." + JabRefFileLogic.LAYOUT_FILE_EXTENSION;
			log.debug("trying to load sublayout " + fileName + "...");
			final Layout layout = loadLayout(fileName);
			if (layout != null) {
				log.debug("... success!");
				subLayouts.put(subLayout, layout);
			}
		}
		return subLayouts;
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
	private static Layout loadLayout(final String fileLocation) throws IOException {
		final InputStream resourceAsStream = JabrefLayoutUtils.getResourceAsStream(fileLocation);
		if (resourceAsStream != null) {
			/*
			 * give file to layout helper
			 */
			final LayoutHelper layoutHelper = new LayoutHelper(new BufferedReader(new InputStreamReader(resourceAsStream, StringUtils.CHARSET_UTF_8)));
			/*
			 * load layout
			 */
			try {
				return layoutHelper.getLayoutFromText(GLOBALS_FORMATTER_PACKAGE);
			} catch (Exception e) {
				log.error("Error while trying to load layout " + fileLocation + " : " + e.getMessage());
				throw new IOException(e);
			} finally {
				resourceAsStream.close();
			}
		}
		return null;
	}

	/**
	 * @param userName
	 * @param config
	 * @return 
	 * @throws Exception 
	 */
	public static JabrefLayout loadUserLayout(String userName, JabRefConfig config) throws Exception {
		/*
		 * initialize a new user layout
		 */
		final JabrefLayout jabrefLayout = new JabrefLayout(JabrefLayoutUtils.userLayoutName(userName));
		jabrefLayout.addDescription("en", "Custom layout of user " + userName);
		jabrefLayout.setDisplayName("custom");
		jabrefLayout.setMimeType("text/html"); // FIXME: this should be adaptable by the user ...
		jabrefLayout.setUserLayout(true);
		jabrefLayout.setPublicLayout(false);

		/*
		 * iterate over layout parts (.begin, .item, .end)
		 */
		for (final LayoutPart layoutPart : LayoutPart.layoutParts) {
			final String hashedName = JabrefLayoutUtils.userLayoutHash(userName, layoutPart);
			final File file = new File(FileUtil.getFileDirAsFile(config.getUserLayoutFilePath(), hashedName), hashedName);

			log.debug("trying to load custom user layout (part " + layoutPart + ") for user " + userName + " from file " + file);

			if (file.exists()) {
				log.debug("custom layout (part '" + layoutPart + "') found!");
				final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringUtils.CHARSET_UTF_8));
				final LayoutHelper layoutHelper = new LayoutHelper(reader);
				try {
					jabrefLayout.addSubLayout(layoutPart, layoutHelper.getLayoutFromText(GLOBALS_FORMATTER_PACKAGE));
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
		}
		
		return jabrefLayout;
	}
}

