package org.bibsonomy.webapp.controller.opensocial;

import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.controller.SingleResourceListControllerWithTags;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Initial gadget container page for testing the open social interface
 * @author fei
 */
public class ContainerPageController extends SingleResourceListControllerWithTags implements MinimalisticController<BaseCommand> {
	
	@Override
	public View workOn(BaseCommand command) {
		return Views.GADGETCONTAINER;
	}
	
	@Override
	public BaseCommand instantiateCommand() {
		return new BaseCommand();
	}

}
