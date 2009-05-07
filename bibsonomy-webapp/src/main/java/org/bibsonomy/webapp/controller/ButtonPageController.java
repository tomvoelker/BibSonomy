package org.bibsonomy.webapp.controller;

import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author mwa
 * @version $Id$
 */
public class ButtonPageController implements MinimalisticController<BaseCommand>{

	@Override
	public BaseCommand instantiateCommand() {
		return new BaseCommand();
	}

	@Override
	public View workOn(BaseCommand command) {
		return Views.BUTTONS;
	}

	

	

}
