package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.SettingsViewCommand;

/**
 * @author mwa
 * @version $Id$
 */
public class DeliciousPinCommand extends SettingsViewCommand {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -122487007022479732L;

	/** when true, duplicate entries will be overwritten **/
	private boolean overwrite;
	
	/** for delicious import only, import bookmarks or bundles? **/
	private String importData;
	
	/**
	 * @return true if duplicate entries shall be overwritten
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}
	
	/**
	 * @param overwrite
	 */
	@Override
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	
	/**
	 * @return the importData
	 */
	@Override
	public String getImportData() {
		return this.importData;
	}

	/**
	 * @param importData the importData to set
	 */
	@Override
	public void setImportData(String importData) {
		this.importData = importData;
	}
}