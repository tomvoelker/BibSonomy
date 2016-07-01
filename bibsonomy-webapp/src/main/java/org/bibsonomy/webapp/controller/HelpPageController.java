package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.HelpPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
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
public class HelpPageController implements MinimalisticController<HelpPageCommand> {
	
	/** The root location the markdown files */
	public static String HELP_MARKDOWN_ROOT = "help/";
	
	/** The name of the markdown file of the sidebar */
	public static String HELP_SIDEBAR_NAME = "sidebar";
	
	/** The prefix of all help URLS */
	public static String HELP_URL_PREFIX = "help/";
	
	/** The help home page */
	public static String HELP_HOME = "Main";
	
	/** Directory of the images */
	public static String HELP_IMG_DIR = "img";
	
	/** Name of the default project theme */
	public static String DEFAULT_PROJECT_THEME = "default";
	
	/** The project name */
	private String projectName;
	
	/** The project theme */
	private String projectTheme;
	
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
	public View workOn(HelpPageCommand command) {
		String language = requestLogic.getLocale().getLanguage();
		
		/* Image request */
		String image = command.getFilename();
		if (present(image)) {
			// Project specific image
			String filename = HELP_MARKDOWN_ROOT + language + "/" + HELP_IMG_DIR + "/" + projectName + "/" + image;
			URL url = Parser.class.getClassLoader().getResource(filename);
			
			// Take default image if there is no project specific image
			if (url == null) {
				filename = HELP_MARKDOWN_ROOT + language + "/" + HELP_IMG_DIR + "/" + DEFAULT_PROJECT_THEME + "/" + image;
				url = Parser.class.getClassLoader().getResource(filename);
			}
			
			// If there is no image, trigger 404
			if (url == null) {
				throw new ObjectNotFoundException(image);
			}
			
			String path = "";
			try {
				path = URLDecoder.decode(url.getPath(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new ObjectNotFoundException(image);
			}
			
			command.setPathToFile(path);
			command.setContentType(FileUtil.getContentType(image));
			
			return Views.DOWNLOAD_FILE;
		}
		
		/* Help page request */
		
		// If pageName does not already have the correct language, redirect
		if (! command.getHelpPage().matches(language + ".*")) {
			final String localizedPageName = getLocalizedHelpPageName(command.getHelpPage(), language);
			// Localization does not exist, redirect to home page
			if (localizedPageName == null) {
				return new ExtendedRedirectView(urlGenerator.getAbsoluteUrl(HELP_URL_PREFIX + language + "/" + HELP_HOME));
			}
			// Redirect to localized URL
			if (! localizedPageName.equals(command.getHelpPage())) {
				return new ExtendedRedirectView(urlGenerator.getAbsoluteUrl(HELP_URL_PREFIX + localizedPageName));		
			}
		}
		
		// Build HashMap for variable replacement
		HashMap<String, String> replacements = new HashMap<>();
		replacements.put("project-name", projectName);
		replacements.put("project-theme", projectTheme);
		
		// Instantiate a new Parser
		Parser parser = new Parser(replacements);
		
		// Parse content
		try {
			command.setContent(parser.parseFile(getMarkdownLocation(command.getHelpPage())));
		} catch (IOException e) {
			command.setPageNotFound(true);
		}
		
		// Parse sidebar
		String filename = getMarkdownLocation(language + "/" + HELP_SIDEBAR_NAME);
		try {
			command.setSidebar(parser.parseFile(filename));
		} catch (IOException e) {
			command.setSidebar("Error: " + filename + " not found.");
		}
		
		return Views.HELP;
	}
	
	/**
	 * Returns the location of the markdown source of the localized help page 
	 * @param pageName The name of the help page
	 * @return the location of the source file
	 */
	private static String getMarkdownLocation(String pageName) {
		return HELP_MARKDOWN_ROOT + pageName + ".md";
	}
	
	/**
	 * Tries to determine the name of the given help page in the given language
	 * @param pageName the name of the help page
	 * @param language the requested language
	 * @return the name of the help page in the given language, if it could be
	 * found, <code>null</code> otherwise
	 */
	private static String getLocalizedHelpPageName(String pageName, String language) {
		final String filename = getMarkdownLocation(pageName);
		
		try {
			final InputStream stream = Parser.class.getClassLoader().getResourceAsStream(filename);
			final BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
			
			/*
			 * Try to find a line of form "<!-- language: localized page name -->"
			 * in the orignal markdown source and return "localized page name"
			 */
			String line = null;				
			Pattern p = Pattern.compile("<!--\\s" + language + ":\\s(.*)\\s*-->");
			Matcher m;
			while ((line = buf.readLine()) != null) {
				m = p.matcher(line);
				if (m.find()) {
					buf.close();
					return language + "/" + m.group(1);
				}
			}
			buf.close();
		} catch (Exception e) {
			return null;
		}
		
		// Nothing found
		return null;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return this.projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the projectTheme
	 */
	public String getProjectTheme() {
		return this.projectTheme;
	}

	/**
	 * @param projectTheme the projectTheme to set
	 */
	public void setProjectTheme(String projectTheme) {
		this.projectTheme = projectTheme;
	}

	/**
	 * @return the requestLogic
	 */
	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	/**
	 * @param requestLogic the requestLogic to set
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @return the urlGenerator
	 */
	public URLGenerator getUrlGenerator() {
		return this.urlGenerator;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

}
