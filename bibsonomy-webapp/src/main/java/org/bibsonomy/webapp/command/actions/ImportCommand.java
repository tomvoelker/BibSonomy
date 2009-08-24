package org.bibsonomy.webapp.command.actions;

import java.util.List;
import java.util.Map;

import org.bibsonomy.webapp.command.BaseCommand;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author mwa
 * @version $Id$
 */
public class ImportCommand extends BaseCommand{
	
	/** when true, duplicate entries will be overwritten **/
	private boolean overwrite;
	
	/** the import-type describes which kind of import will be used 
	 *  e.g. FireFox import, Delicious import etc.. **/
	private String type;
	
	/** in case of an import from a remote service 
	 *  userName and passWord are required **/
	private String userName;
	private String passWord;
	
	/** the group: private or public **/
	private String group;
	
	private int totalCount;
	
	/** the file to import **/
	private CommonsMultipartFile file;
	
	private Map<String, String> newBookmarks = null;

	private Map<String, String> updatedBookmarks = null;

	private List<String> nonCreatedBookmarks = null;
	
	private List<String> storedConcepts = null;
	

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
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	/**
	 * @return the actual import-type
	 */
	public String getImportType() {
		return this.type;
	}
	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
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
	/**
	 * 
	 * @return
	 */
	public String getGroup() {
		return this.group;
	}
	/**
	 * 
	 * @param group
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	/**
	 * 
	 * @return
	 */
	public CommonsMultipartFile getFile() {
		return this.file;
	}
	/**
	 * 
	 * @param file
	 */
	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}
	/**
	 * 
	 * @return
	 */
	public int getTotalCount() {
		return this.totalCount;
	}
	/**
	 * 
	 * @param totalCount
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	/**
	 * 
	 * @return
	 */
	public List<String> getStoredConcepts() {
		return this.storedConcepts;
	}
	/**
	 * 
	 * @param storedConcepts
	 */
	public void setStoredConcepts(List<String> storedConcepts) {
		this.storedConcepts = storedConcepts;
	}
	/**
	 * 
	 * @return
	 */
	public String getImportData() {
		return this.importData;
	}
	/**
	 * 
	 * @param importData
	 */
	public void setImportData(String importData) {
		this.importData = importData;
	}
		
}