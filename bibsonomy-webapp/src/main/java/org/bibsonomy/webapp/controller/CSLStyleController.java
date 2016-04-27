package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.CSLStyles;
import org.bibsonomy.webapp.command.CSLStyleCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class CSLStyleController implements MinimalisticController<CSLStyleCommand>{

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public CSLStyleCommand instantiateCommand() {
		return new CSLStyleCommand();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(CSLStyleCommand command) {
		CSLStyles Style = CSLStyles.valueOf(command.getStyle());
		command.setXml(Style.getXML());
		return Views.CSL_STYLE;
	}
}
