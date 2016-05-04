package org.bibsonomy.webapp.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;

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
			try {
				command.setXml(readStyles());
			} catch (IOException e) {
				command.setXml("sumtin went holibly wlong");
			}
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
	
	private String readStyles() throws IOException{
		final String directory = "org/bibsonomy/layout/csl/";
		final String cslFolderDirec = this.getClass().getClassLoader().getResource(directory).getPath();
		final File CSLFolder = new File(cslFolderDirec);
		String returner = "";
		
		//only reading .csl files
		FilenameFilter CSLFilter = new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".csl");
		    }
		};
		
		for(File f : CSLFolder.listFiles(CSLFilter)){
			
		}
	}
}
