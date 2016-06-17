package org.bibsonomy.webapp.controller;

import java.io.IOException;
import java.util.HashMap;

import org.bibsonomy.webapp.command.HelpPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.markdown.Parser;
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
	
	/** The project name */
	private String projectName;
	
	/** The project theme */
	private String projectTheme;
	
	/** The request logic */
	private RequestLogic requestLogic;

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
		return HELP_MARKDOWN_ROOT + pageName + "." + requestLogic.getLocale().getLanguage() + ".md";
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

}
