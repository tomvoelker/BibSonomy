package org.bibsonomy.webapp.controller;

import java.io.IOException;
import java.util.TreeMap;

import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.layout.standard.StandardLayouts;
import org.bibsonomy.model.Layout;
import org.bibsonomy.webapp.command.ExportPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian, lsc
 */
public class ExportPageController implements MinimalisticController<ExportPageCommand> {
	
	private JabrefLayoutRenderer layoutRenderer;
	private StandardLayouts layouts;
	
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
		command.addLayoutMap(this.layoutRenderer.getLayouts());
		
		if ("json".equals(command.getFormat())) {
			/*
			 * JSON list about the available JabRef layouts on the /layoutinfo
			 */
			return Views.EXPORTLAYOUTS;
		}
		
		command.addLayoutMap(this.layouts.getLayoutMap());

		if (command.getFormatEmbedded()) {
			return Views.EXPORT_EMBEDDED;
		}
		
		return Views.EXPORT;
	}
	
	/**
	 * @param layoutRenderer
	 */
	public void setLayoutRenderer(final JabrefLayoutRenderer layoutRenderer) {
		this.layoutRenderer = layoutRenderer;
	}

	public StandardLayouts getLayouts() {
		return layouts;
	}

	public void setLayouts(StandardLayouts layouts) {
		this.layouts = layouts;
	}
}
