package org.bibsonomy.webapp.command.actions;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author mwa
 * @version $Id$
 */
public class ImportCommand extends BaseCommand{
	
	/** when true, duplicate entries will be overwritten **/
	private boolean overWrite;
	
	/** the import-type describes which kind of import will be used 
	 *  e.g. FireFox import, Delicious import etc.. **/
	private String importType;
	
	/** in case of an import from a remote service 
	 *  userName and passWord are required **/
	private String userName;
	private String passWord;
	
	/** the grouping-type, private or public **/
	private String grouping;
	
	/** the file to import **/
	private File file;
	
	private Map<String, String> newBookmarks = null;

	private Map<String, String> updatedBookmarks = null;

	private List<String> nonCreatedBookmarks = null;
	
	/**
	 * @return true if duplicate entries shall be overwritten
	 */
	public boolean isOverWrite() {
		return this.overWrite;
	}
	/**
	 * @param overWrite
	 */
	public void setOverWrite(boolean overWrite) {
		this.overWrite = overWrite;
	}
	/**
	 * @return the actual import-type
	 */
	public String getImportType() {
		return this.importType;
	}
	/**
	 * @param importType
	 */
	public void setImportType(String importType) {
		this.importType = importType;
	}
	/**
	 * @return the userName, required for importing resources form a remote service
	 */
	public String getUserName() {
		return this.userName;
	}
	/**
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the user's password
	 */
	public String getPassWord() {
		return this.passWord;
	}
	/**
	 * @param passWord
	 */
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	/**
	 * 
	 * @return a Map containing the URLs of all created bookmarks
	 */
	public Map<String, String> getNewBookmarks() {
		return this.newBookmarks;
	}
	/**
	 * 
	 * @param newBookmarks
	 */
	public void setNewBookmarks(Map<String, String> newBookmarks) {
		this.newBookmarks = newBookmarks;
	}
	/**
	 * 
	 * @return a Map containing the URLs of all updated bookmarks
	 */
	public Map<String, String> getUpdatedBookmarks() {
		return this.updatedBookmarks;
	}
	/**
	 * 
	 * @param updatedBookmarks
	 */
	public void setUpdatedBookmarks(Map<String, String> updatedBookmarks) {
		this.updatedBookmarks = updatedBookmarks;
	}
	/**
	 * 
	 * @return a Map containing the URLs of all non created bookmarks
	 */
	public List<String> getNonCreatedBookmarks() {
		return this.nonCreatedBookmarks;
	}
	/**
	 * 
	 * @param nonCreatedBookmarks
	 */
	public void setNonCreatedBookmarks(List<String> nonCreatedBookmarks) {
		this.nonCreatedBookmarks = nonCreatedBookmarks;
	}
	public String getGrouping() {
		return this.grouping;
	}
	public void setGrouping(String grouping) {
		this.grouping = grouping;
	}
	public File getFile() {
		return this.file;
	}
	public void setFile(File file) {
		this.file = file;
	}
		
}
