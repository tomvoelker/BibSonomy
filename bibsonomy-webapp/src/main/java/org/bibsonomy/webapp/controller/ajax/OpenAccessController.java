package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.openaccess.sherparomeo.SherpaRomeoImpl;
import org.bibsonomy.webapp.command.ajax.OpenAccessCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author clemens
 * @version $Id$
 */
public class OpenAccessController extends AjaxController implements MinimalisticController<OpenAccessCommand> {

	SherpaRomeoImpl sherpaLogic;
	
	@Override
	public OpenAccessCommand instantiateCommand() {
		return new OpenAccessCommand();
	}

	@Override
	public View workOn(OpenAccessCommand command) {
		this.sherpaLogic = new SherpaRomeoImpl();

		if(command.getPublisher() != null) {
			command.setResponseString(sherpaLogic.getPolicyForPublisher(command.getPublisher(), command.getqType()));
		}
		if (command.getjTitle() != null) {
			command.setResponseString(sherpaLogic.getPolicyForJournal(command.getjTitle(), command.getqType()));			
		}
		
		return Views.AJAX_JSON;
	}

}
