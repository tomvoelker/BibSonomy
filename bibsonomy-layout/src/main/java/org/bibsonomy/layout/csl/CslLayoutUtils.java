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
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.services.export.CSLUtils;
import org.bibsonomy.services.filesystem.CslFileLogic;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.util.IOUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.xml.sax.SAXException;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class CslLayoutUtils {
	private static final Log log = LogFactory.getLog(CslLayoutUtils.class);

	/**
	 * Builds the hash for the custom layout files of the user.
	 * 
	 * @param user
	 * @param fileName
	 * @return hash for custom file of user
	 */
	public static String userLayoutHash(final String user, final String fileName) {
		return StringUtils.getMD5Hash("user." + user.toLowerCase() + "." + fileName + "." + CslFileLogic.LAYOUT_FILE_EXTENSION).toLowerCase();
	}

	/**
	 * Builds the name of a custom user layout, for the map and elsewhere.
	 * Typically "custom " + userName + " " + fileName.
	 * 
	 * @param userName
	 * @param fileName
	 * @return the name of a custom layout
	 */
	public static String userLayoutName(final String userName, final String fileName) {
		return LayoutRenderer.CUSTOM_LAYOUT + " " + userName + " " + fileName.replace(" ", "_");
	}

	/**
	 * @param userName
	 * @param config
	 * @return The loaded layout, or <code>null</code> if it could not be found.
	 * @throws Exception
	 */
	public static CSLStyle loadUserLayout(final String userName, final String fileName, CslConfig config)
			throws Exception {
		/*
		 * initialize a new user layout
		 */
		final CSLStyle cslLayout = new CSLStyle(CslLayoutUtils.userLayoutName(userName, fileName));
		cslLayout.addDescription("en", "Custom layout of user " + userName);
		cslLayout.setDisplayName(fileName.replace("." + CslFileLogic.LAYOUT_FILE_EXTENSION, ""));
		cslLayout.setMimeType("text/html"); // FIXME: this should be adaptable
											// by the user ...
		cslLayout.setUserLayout(true);
		cslLayout.setPublicLayout(false);

		final String hashedName = CslLayoutUtils.userLayoutHash(userName, cslLayout.getDisplayName() + ".csl");
		final File file = new File(FileUtil.getFileDirAsFile(config.getUserLayoutFilePath(), hashedName), hashedName);

		cslLayout.setFileHash(hashedName);

		log.debug("trying to load custom user layout for user " + userName + " from file " + file);

		if (file.exists()) {
			log.debug("custom layout found!");

			final String content = IOUtils.readInputStreamToString(new FileInputStream(file));
			cslLayout.setContent(content);

			// parse / set displayName
			try {
				final String displayName = CSLUtils.extractTitle(cslLayout.getContent()).trim();
				cslLayout.setDisplayName(displayName);
			} catch (SAXException | ParserConfigurationException e1) {
				log.error(
						"Failed to extract a display name in a user custom layout. XML-tag 'title' is missing or in the wrong place. File causing problems: "
								+ cslLayout.getName(), e1);
			}

			if (!present(cslLayout.getDisplayName())) {
				cslLayout.setDisplayName(CSLUtils.normStyle(fileName));
			}
		}

		return cslLayout;
	}
}
