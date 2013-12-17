package org.bibsonomy.webapp.controller;

import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.webapp.command.ExportPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian
  */
public class ExportPageController implements MinimalisticController<ExportPageCommand> {
	
	private JabrefLayoutRenderer layoutRenderer;
	
	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public ExportPageCommand instantiateCommand() {
		return new ExportPageCommand();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final ExportPageCommand command) {
		command.setLayoutMap(this.layoutRenderer.getLayouts());
		
		if (command.getFormatEmbedded()) {
			return Views.EXPORT_EMBEDDED;
		}
		
		if ("json".equals(command.getFormat())) {
			/*
			 * JSON list about the available JabRef layouts on the /layoutinfo
			 */
			return Views.EXPORTLAYOUTS;
		}
		
		return Views.EXPORT;
	}
	
	/**
	 * @param layoutRenderer
	 */
	public void setLayoutRenderer(final JabrefLayoutRenderer layoutRenderer) {
		this.layoutRenderer = layoutRenderer;
	}
}
