package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.HelpPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.markdown.Parser;
import org.bibsonomy.webapp.util.spring.controller.ServletWrappingController;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * The controller for the help pages.
 *
 * @author Johannes Blum
 */
public class HelpPageController extends ServletWrappingController implements MinimalisticController<HelpPageCommand> {
	
	/** The root location the markdown files */
	public static String HELP_MARKDOWN_ROOT = "help/";
	
	/** The name of the markdown file of the sidebar */
	public static String HELP_SIDEBAR_NAME = "sidebar";
	
	/** The prefix of all help URLS */
	public static String HELP_URL_PREFIX = "help/";
	
	/** The prefix of all image URLS */
	public static String HELP_IMAGE_PREFIX = "resources/image/help/";
	
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
		
		// If image requested redirect to the correct image URL
		String image = command.getImage();
		if (present(image)) {
			String prefix = HELP_IMAGE_PREFIX + language + "/";
			// Try to find project specific image
			String path = prefix + projectName + "/" + image;
			String realPath = getServletContext().getRealPath("../" + path);
			File f = new File(realPath);
			if (f.exists() && ! f.isDirectory())
				return new ExtendedRedirectView(urlGenerator.getAbsoluteUrl(path));
			// Project specific image not found, return default image
			path = prefix + DEFAULT_PROJECT_THEME + "/" + image;
			return new ExtendedRedirectView(urlGenerator.getAbsoluteUrl(path));
			
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
			String localizedPageName = findLocalizedHelpPageName(command.getHelpPage(), language);
			if (localizedPageName != null)
				return new ExtendedRedirectView(urlGenerator.getAbsoluteUrl(HELP_URL_PREFIX + localizedPageName));
			
			command.setPageNotFound(true);
		}
		
		// Parse sidebar
		String filename = getMarkdownLocation(HELP_SIDEBAR_NAME);
		String sidebar;
		try {
			sidebar = parser.parseFile(filename);
		} catch (IOException e) {
			sidebar = "Error: " + filename + " not found.";
		}
		command.setSidebar(sidebar);
		
		return Views.HELP;
	}
	
	/**
	 * Returns the location of the markdown source of the localized help page 
	 * @param pageName The name of the help page
	 * @return the location of the source file
	 */
	private String getMarkdownLocation(String pageName) {
		return HELP_MARKDOWN_ROOT + requestLogic.getLocale().getLanguage() + "/" + pageName + ".md";
	}
	
	/**
	 * Tries to find the name of the given help page in the given language
	 * @param pageName the name of the help page
	 * @param language the requested language
	 * @return the name of the help page in the given language, if it could be
	 * found, <code>null</code> otherwise
	 */
	private static String findLocalizedHelpPageName(String pageName, String language) {
		String languages[] = {"en", "de", "ru"};
		for (String l : languages) {
			// Try to find the markdown source of the page in the original language
			String filename = HELP_MARKDOWN_ROOT + l + "/" + pageName + ".md";
			
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
					if (m.find())
						return m.group(1);
				}
				buf.close();
			} catch (Exception e) {
				// Try next original language
				continue;
			}
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
