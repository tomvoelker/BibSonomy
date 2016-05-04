package org.bibsonomy.webapp.controller;

import java.io.File;

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
		// returns the XML for a given style which will be looked up in a enum

		CSLStyles Style = null;
		if (command.getStyle() == null || command.getStyle().isEmpty()) {
			command.setXml(readStyles());
			return Views.CSL_STYLE; 
		}
		
		try {
			Style = CSLStyles.valueOf(command.getStyle().toUpperCase());
		} catch (java.lang.IllegalArgumentException iae) {
			command.setXml("No such style available: " + command.getStyle());
			return Views.CSL_STYLE;
		}
		
		// leave layout as xml
		command.setXml(Style.getXML());
		return Views.CSL_STYLE;
	}
	private String readStyles(){
		final String directory = "/bibsonomy-webapp/src/main/webapp/resources/styles";
		File f = new File(directory);
		if (f.exists()) {
		   return "gefunden";
		}
		//String basePath = new File("").getAbsolutePath();
	    
		return "nicht gefunden :(";
	}
}
