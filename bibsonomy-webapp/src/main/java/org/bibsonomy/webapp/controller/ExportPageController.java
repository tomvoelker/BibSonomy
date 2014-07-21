package org.bibsonomy.webapp.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

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
		this.layouts = new StandardLayouts(); 
		try {
			this.layouts.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		final ExportPageCommand exportPageCommand = new ExportPageCommand();
		LayoutComparator c = new LayoutComparator(); 
		exportPageCommand.setLayoutSet(new TreeSet<Layout>(c));
		return exportPageCommand;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final ExportPageCommand command) {
		command.addStandardLayoutMap(this.layouts.getLayoutMap());
		command.addJabrefLayoutMap(this.layoutRenderer.getLayouts());
		
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
	
	private static final class LayoutComparator implements Comparator<Layout>{

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Layout o1, Layout o2) {
			return o1.getDisplayName().compareTo(o2.getDisplayName());
		}
		
	}
}


