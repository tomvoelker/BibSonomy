package org.bibsonomy.webapp.controller;

import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.webapp.command.ExportPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Creates a JSON list about the available JabRef layouts on the /layoutinfo page.
 * 
 * @author mwa, dbe
 * @version $Id$
 */
public class ExportLayoutController implements MinimalisticController<ExportPageCommand> {
	
	/** layout renderer */
	private JabrefLayoutRenderer layoutRenderer;
	/** request logic */
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
	
	/** 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.BaseCommand)
	 */
	@Override
	public View workOn(ExportPageCommand command) {
		command.setLayoutMap(this.layoutRenderer.getJabrefLayouts());
		command.setLang(this.requestLogic.getLocale().getLanguage());
		
		return Views.EXPORTLAYOUTS;
	}
	
	/**
	 * @param layoutRenderer
	 */
	public void setLayoutRenderer(JabrefLayoutRenderer layoutRenderer) {
		this.layoutRenderer = layoutRenderer;
	}

	/**
	 * 
	 * @param requestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
}
