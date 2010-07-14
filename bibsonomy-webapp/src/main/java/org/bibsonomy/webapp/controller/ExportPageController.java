package org.bibsonomy.webapp.controller;

import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.webapp.command.ExportPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian
 * @version $Id$
 */
public class ExportPageController implements MinimalisticController<ExportPageCommand> {
	
	private JabrefLayoutRenderer layoutRenderer;
	private RequestLogic requestLogic;
	
	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public ExportPageCommand instantiateCommand() {		
		return new ExportPageCommand();
	}

	/** Main method which does the registration.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.BaseCommand)
	 */
	@Override
	public View workOn(final ExportPageCommand command) {
		command.setLayoutMap(this.layoutRenderer.getJabrefLayouts());
		command.setLang(this.requestLogic.getLocale().getLanguage());
		
		return Views.EXPORT;
	}
	
	/**
	 * @param layoutRenderer
	 */
	public void setLayoutRenderer(final JabrefLayoutRenderer layoutRenderer) {
		this.layoutRenderer = layoutRenderer;
	}

	/**
	 * 
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

}
