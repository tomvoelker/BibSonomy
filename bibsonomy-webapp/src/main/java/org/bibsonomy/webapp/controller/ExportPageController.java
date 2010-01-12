package org.bibsonomy.webapp.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.jabref.JabrefLayout;
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
	//http://dev.bibsonomy.org/maven2/org/bibsonomy/bibsonomy-layout/
	private String layoutDownloadURL;
	
	private static final Log log = LogFactory.getLog(ExportPageController.class);
	
	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public ExportPageCommand instantiateCommand() {
		
		return new ExportPageCommand();
	}

	/** Main method which does the registration.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(ExportPageCommand command) {
		
		command.setLayoutMap(this.layoutRenderer.getJabrefLayouts());
		command.setLang(this.requestLogic.getLocale().getLanguage());
		this.buildDownloadPath(command);
		
		return Views.EXPORT;
	}
	
	/**
	 * Method builds the download-URL for jabref-layouts
	 * @param command
	 */
	private void buildDownloadPath(ExportPageCommand command){
		String url = this.layoutDownloadURL;

		//detect path of the layout snapshot
		String layoutJarFilePath = JabrefLayout.class.getProtectionDomain().getCodeSource().getLocation().toString().replace("\\", "/");
		
		//extract filename from path
		String jarName = layoutJarFilePath.substring(layoutJarFilePath.lastIndexOf("/")).replace("/", "");
		
		/*
		 * extract the folderName from the fileName, the folder is named
		 * by the version of the actual snapshot
		 */
		Pattern pattern = Pattern.compile("\\d+");
		Matcher m = pattern.matcher(jarName);
		
		/* 
		 * to extract version number from fileName, find the 
		 * first occurrence of a number
		 */
		String firstNumber = null;
		if(m.find()){
			firstNumber = m.group();
		}else{
			log.error("ExportPageController - Unable to build Download-URL for JabRef-Layouts");
		}
		
		String folderName = jarName.substring(jarName.indexOf(firstNumber),jarName.indexOf(".jar"));
				
		//build the DOWNLOAD-URL: serverPath + folderName + jarFile
		url += folderName+"/"+jarName;
		command.setLayoutDownloadURL(url);
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

	/**
	 * 
	 * @param layoutDownloadURL
	 */
	public void setLayoutDownloadURL(String layoutDownloadURL) {
		this.layoutDownloadURL = layoutDownloadURL;
	}

}
