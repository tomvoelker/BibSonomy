package org.bibsonomy.webapp.controller;

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
		//TODO: absoluter BS
//		CSLFilesManager CSLFilesManager = new CSLFilesManager();
//		if (command.getStyle() == null || command.getStyle().isEmpty()) {
//			command.setXml(CSLFilesManager.getJSONString());
//			return Views.CSL_STYLE; 
//		}
//		command.setXml(CSLFilesManager.getXML(command.getStyle()));
		return Views.CSL_STYLE;
	}
}
