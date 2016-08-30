package org.bibsonomy.webapp.controller;

import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.webapp.command.CSLStyleCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * 
 *
 * @author jp
 */
public class CSLStyleController implements MinimalisticController<CSLStyleCommand> {
	
	protected CSLFilesManager cslFilesManager;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public CSLStyleCommand instantiateCommand() {
		return new CSLStyleCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.
	 * webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(CSLStyleCommand command) {
		if (command.getStyle() == null || command.getStyle().isEmpty()) {
			command.setXml(cslFilesManager.getJSONString());
			return Views.CSL_STYLE; 
		}
		command.setXml(cslFilesManager.getXML(command.getStyle()));
		return Views.CSL_STYLE;
	}

	/**
	 * @return the cslFilesManager
	 */
	public CSLFilesManager getCslFilesManager() {
		return this.cslFilesManager;
	}

	/**
	 * @param cslFfilesManager the cslFilesManager to set
	 */
	public void setCslFilesManager(CSLFilesManager cslFilesManager) {
		this.cslFilesManager = cslFilesManager;
	}
}
