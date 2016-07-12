/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.BasicUtils;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.HelpPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.markdown.Parser;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * The controller for the help pages.
 *
 * @author Johannes Blum
 */
public class HelpPageController implements MinimalisticController<HelpPageCommand>, RequestAware {
	/** The name of the markdown file of the sidebar */
	private static String HELP_SIDEBAR_NAME = "Sidebar";
	
	/** The help home page */
	private static String HELP_HOME = "Main";
	
	/** Directory of the images */
	private static String HELP_IMG_DIR = "img";
	
	/** Name of the default project theme */
	private static String DEFAULT_PROJECT_THEME = "default";
	
	
	private String helpPath;
	
	/** The project name */
	private String projectName;
	
	/** The project theme */
	private String projectTheme;
	
	/** the url of the project */
	private String projectHome;
	
	/** The request logic */
	private RequestLogic requestLogic;
	
	/** The URL generator */
	private URLGenerator urlGenerator;

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public HelpPageCommand instantiateCommand() {
		return new HelpPageCommand();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final HelpPageCommand command) {
		final String requestLanguage = this.requestLogic.getLocale().getLanguage();
		final String language = command.getLanguage();
		
		// Override project theme if parameter set
		String theme = command.getTheme();
		if (!present(theme)) {
			theme = this.projectTheme;
		}
		
		/* Image request */
		final String image = command.getFilename();
		if (present(image)) {
			// Project specific image
			String filename = this.helpPath + language + "/" + HELP_IMG_DIR + "/" + theme + "/" + image;
			
			File imageFile = new File(filename);
			if (!imageFile.exists()) {
				filename = this.helpPath + language + "/" + HELP_IMG_DIR + "/" + DEFAULT_PROJECT_THEME + "/" + image;
				imageFile = new File(filename);
			}
			
			if (!imageFile.exists()) {
				throw new ObjectNotFoundException(image);
			}
			
			command.setPathToFile(filename);
			command.setContentType(FileUtil.getContentType(image));
			
			return Views.DOWNLOAD_FILE;
		}
		
		/* help page request */
		
		
		String helpPage = command.getHelpPage();
		if (!present(helpPage)) {
			return new ExtendedRedirectView(this.urlGenerator.getHelpPage(HELP_HOME, language), true);
		}
		
		// if pageName does not already have the correct language, redirect
		if (present(language) && !language.equals(requestLanguage)) {
			final String localizedPageName = getLocalizedHelpPageName(helpPage, language, requestLanguage);
			return new ExtendedRedirectView(this.urlGenerator.getHelpPage(localizedPageName, requestLanguage));
		}
		
		// Build HashMap for variable replacement
		final Map<String, String> replacements = new HashMap<>();
		replacements.put("project.name", this.projectName);
		replacements.put("project.theme", theme);
		replacements.put("project.home", this.projectHome);
		replacements.put("project.version", BasicUtils.VERSION);
		
		// Instantiate a new Parser
		final Parser parser = new Parser(replacements);
		
		// parse content
		try {
			command.setContent(parser.parseFile(this.getMarkdownLocation(requestLanguage, helpPage)));
		} catch (final IOException e) {
			command.setPageNotFound(true);
		}
		
		// parse sidebar
		try {
			command.setSidebar(parser.parseFile(this.getMarkdownLocation(requestLanguage, HELP_SIDEBAR_NAME)));
		} catch (final IOException e) {
			command.setSidebar("Error: sidebar for language " + requestLanguage + " not found.");
		}
		
		return Views.HELP;
	}
	
	/**
	 * Returns the location of the markdown source of the localized help page 
	 * @param language
	 * @param pageName The name of the help page
	 * @return the location of the source file
	 */
	private String getMarkdownLocation(String language, String pageName) {
		return this.helpPath + language + File.separator + pageName + ".md";
	}
	
	/**
	 * Tries to determine the name of the given help page in the given language
	 * @param pageName the name of the help page
	 * @param language the requested language
	 * @param requestLanguage 
	 * @return the name of the help page in the given language, if it could be
	 * found, <code>null</code> otherwise
	 */
	private String getLocalizedHelpPageName(String pageName, String language, String requestLanguage) {
		final String filename = this.getMarkdownLocation(language, pageName);
		
		try (final InputStream stream = new FileInputStream(new File(filename))) {
			final BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
			
			/*
			 * Try to find a line of form "<!-- language: localized page name -->"
			 * in the orignal markdown source and return "localized page name"
			 */
			String line = null;
			Pattern p = Pattern.compile("<!--\\s" + requestLanguage + ":\\s(.*)\\s*-->");
			Matcher m;
			while ((line = buf.readLine()) != null) {
				m = p.matcher(line);
				if (m.find()) {
					buf.close();
					return m.group(1).trim();
				}
			}
			buf.close();
		} catch (Exception e) {
			return null;
		}
		
		// Nothing found
		return HELP_HOME;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @param projectTheme the projectTheme to set
	 */
	public void setProjectTheme(String projectTheme) {
		this.projectTheme = projectTheme;
	}
	
	/**
	 * @param requestLogic the requestLogic to set
	 */
	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	/**
	 * @param helpPath the helpPath to set
	 */
	public void setHelpPath(String helpPath) {
		this.helpPath = helpPath;
	}

	/**
	 * @param projectHome the projectHome to set
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}

}
