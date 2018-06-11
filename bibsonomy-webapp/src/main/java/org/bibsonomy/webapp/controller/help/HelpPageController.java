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

package org.bibsonomy.webapp.controller.help;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.search.InvalidSearchRequestException;
import org.bibsonomy.search.es.help.HelpUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.help.HelpSearch;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.help.HelpPageCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ResponseAware;
import org.bibsonomy.webapp.util.ResponseLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.markdown.Parser;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * The controller for the help pages.
 *
 * @author Johannes Blum
 */
public class HelpPageController implements MinimalisticController<HelpPageCommand>, RequestAware, ResponseAware, ErrorAware {
	/** the help home page */
	private static final String HELP_HOME = "Main";
	
	/** directory of the images */
	private static final String HELP_IMG_DIR = "img";
	
	/** name of the default project theme */
	private static final String DEFAULT_PROJECT_THEME = "default";
	
	private static final Pattern REDIRECT_PATTERN = Pattern.compile("<!--\\s*redirect\\s*:(.*)\\s*-->");

	private Errors errors;

	private HelpSearch search;
	
	private String helpPath;
	
	/** the project name */
	private String projectName;
	
	/** the project theme */
	private String projectTheme;
	
	/** the url of the project */
	private String projectHome;
	
	/** the mail of the project */
	private String projectEmail;
	
	/** the mail of the project */
	private String projectNoSpamEmail;
	
	/** the mail of the project */
	private String projectAPIEmail;

	/** the request logic */
	private RequestLogic requestLogic;
	
	/** the response logic */
	private ResponseLogic responseLogic;
	
	/** the URL generator */
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
		
		final Map<String, String> replacements = HelpUtils.buildReplacementMap(this.projectName, theme, this.projectHome, this.projectEmail, this.projectNoSpamEmail, this.projectAPIEmail);
		// instantiate a new parser
		final Parser parser = new Parser(replacements, this.urlGenerator);
		
		final String requestedSearch = command.getSearch();
		if (present(requestedSearch)) {
			try {
				command.setSearchResults(this.search.search(language, requestedSearch));
				this.renderSidebar(command, language, parser);
				return Views.HELP_SEARCH;
			} catch (final InvalidSearchRequestException e) {
				this.errors.reject("search.invalid.query", "The entered search query is not valid.");
				return Views.ERROR;
			}
		}
		
		// help page request
		final String helpPage = command.getHelpPage();
		if (!present(helpPage)) {
			return new ExtendedRedirectView(this.urlGenerator.getHelpPage(HELP_HOME, language), true);
		}
		
		// if pageName does not already have the correct language, redirect
		if (present(language) && !language.equals(requestLanguage)) {
			final String localizedPageName = this.getLocalizedHelpPageName(helpPage, language, requestLanguage);
			if (present(localizedPageName)) {
				return new ExtendedRedirectView(this.urlGenerator.getHelpPage(localizedPageName, requestLanguage));
			}

			return new ExtendedRedirectView(this.urlGenerator.getHelpPage(HELP_HOME, requestLanguage));
		}
		
		// parse content
		final String markdownFile = this.getMarkdownLocation(language, helpPage);
		try (final BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(markdownFile), StringUtils.CHARSET_UTF_8))) {
			final String text = StringUtils.getStringFromReader(inputReader);
			
			final Matcher matcher = REDIRECT_PATTERN.matcher(text);
			if (matcher.find()) {
				final String redirectPage = matcher.group(1).trim();
				return new ExtendedRedirectView(this.urlGenerator.getHelpPage(redirectPage, requestLanguage), true);
			}

			command.setContent(parser.parseText(text, language));

			// build the title
			command.setHelpPageTitle(buildTitleFromFile(helpPage));
		} catch (final IOException e) {
			this.responseLogic.setHttpStatus(HttpServletResponse.SC_NOT_FOUND);
			command.setPageNotFound(true);
		}
		
		this.renderSidebar(command, language, parser);
		
		return Views.HELP;
	}

	private static String buildTitleFromFile(String helpPage) {
		if (helpPage.contains("/")) {
			// title is the folder names in reverse order
			final List<String> pathElements = Arrays.asList(helpPage.split("/"));

			final StringBuilder titleBuilder = new StringBuilder();

			final ListIterator<String> listIterator = pathElements.listIterator(pathElements.size()); // start at the end
			while (listIterator.hasPrevious()) {
				titleBuilder.append(listIterator.previous());

				if (listIterator.hasPrevious()) {
					titleBuilder.append(" | ");
				}
			}
			return titleBuilder.toString();
		}

		return helpPage;
	}

	/**
	 * @param command
	 * @param language
	 * @param parser
	 */
	private void renderSidebar(final HelpPageCommand command, final String language, final Parser parser) {
		// parse sidebar
		try {
			final String sidebarLocation = this.getMarkdownLocation(language, HelpUtils.HELP_SIDEBAR_NAME);
			try (final BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(sidebarLocation), StringUtils.CHARSET_UTF_8))) {
				final String text = StringUtils.getStringFromReader(inputReader);
				command.setSidebar(parser.parseText(text, language));
			}
		} catch (final IOException e) {
			command.setSidebar("Error: sidebar for language " + language + " not found.");
		}
	}
	
	/**
	 * Returns the location of the markdown source of the localized help page 
	 * @param language
	 * @param pageName The name of the help page
	 * @return the location of the source file
	 */
	private String getMarkdownLocation(String language, String pageName) {
		return this.helpPath + language + File.separator + pageName + HelpUtils.FILE_SUFFIX;
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
			final BufferedReader buf = new BufferedReader(new InputStreamReader(stream, StringUtils.CHARSET_UTF_8));
			
			/*
			 * Try to find a line of form "<!-- language: localized page name -->"
			 * in the original markdown source and return "localized page name"
			 */
			String line = null;
			final Pattern p = Pattern.compile("<!--\\s" + requestLanguage + ":\\s(.*)\\s*-->");
			while ((line = buf.readLine()) != null) {
				final Matcher m = p.matcher(line);
				if (m.find()) {
					return m.group(1).trim();
				}
			}
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
	 * @param responseLogic the responseLogic to set
	 */
	@Override
	public void setResponseLogic(ResponseLogic responseLogic) {
		this.responseLogic = responseLogic;
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

	/**
	 * @param projectEmail the projectEmail to set
	 */
	public void setProjectEmail(String projectEmail) {
		this.projectEmail = projectEmail;
	}
	
	/**
	 * @param projectNoSpamEmail the projectNoSpamEmail to set
	 */
	public void setProjectNoSpamEmail(String projectNoSpamEmail) {
		this.projectNoSpamEmail = projectNoSpamEmail;
	}

	/**
	 * @param projectAPIEmail the projectAPIEmail to set
	 */
	public void setProjectAPIEmail(String projectAPIEmail) {
		this.projectAPIEmail = projectAPIEmail;
	}

	/**
	 * @param search the search to set
	 */
	public void setSearch(HelpSearch search) {
		this.search = search;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
}
