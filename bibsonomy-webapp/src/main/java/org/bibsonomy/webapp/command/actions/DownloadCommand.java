package org.bibsonomy.webapp.command.actions;

/**
 * @author ice
 * @version $Id$
 */
public interface DownloadCommand {

	/**
	 * @return pathToFile
	 */
	String getPathToFile();

	/**
	 * @return filename
	 */
	String getFilename();

	/**
	 * @return contentType
	 */
	String getContentType();



}