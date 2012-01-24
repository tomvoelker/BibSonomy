package org.bibsonomy.webapp.command.actions;

import java.util.List;
import java.util.Map;

import org.bibsonomy.webapp.command.BaseCommand;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author mwa
 * @version $Id$
 */
public class ImportCommand extends BaseCommand {
	
	/** when true, duplicate entries will be overwritten **/
	private boolean overwrite;
	
	/** the import-type describes which kind of import will be used 
	 *  e.g. FireFox import, Delicious import etc.. **/
	// TODO: introduce enum
	private String importType;
	
	/** 
	 * login credentials for service from which
	 * bookmarks are imported 
	 **/
	private String importUsername;
	private String importPassword;
	
	/** the group: private or public **/
	private String group;
	
	private int totalCount;
	
	/** the file to import **/
	private CommonsMultipartFile file;
	
	private Map<String, String> newBookmarks;

	private Map<String, String> updatedBookmarks;

	private Map<String, String> nonCreatedBookmarks;
	
	private List<String> storedConcepts;
	
	/** for delicious import only, import bookmarks or bundles? **/
	// TODO: introduce an enum class
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
	public void setOverwrite(final boolean overwrite) {
		this.overwrite = overwrite;
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
	public void setImportType(final String importType) {
		this.importType = importType;
	}

	/**
	 * @return the userName, required for importing resources form a remote service
	 */
	public String getImportUsername() {
		return this.importUsername;
	}
	
	/**
	 * @param userName
	 */
	public void setImportUsername(final String userName) {
		this.importUsername = userName;
	}
	
	/**
	 * @return the user's password
	 */
	public String getImportPassword() {
		return this.importPassword;
	}
	
	/**
	 * @param passWord
	 */
	public void setImportPassword(final String passWord) {
		this.importPassword = passWord;
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
	public void setNewBookmarks(final Map<String, String> newBookmarks) {
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
	public void setUpdatedBookmarks(final Map<String, String> updatedBookmarks) {
		this.updatedBookmarks = updatedBookmarks;
	}
	
	/**
	 * 
	 * @return a Map containing the URLs of all non created bookmarks
	 */
	public Map<String, String> getNonCreatedBookmarks() {
		return this.nonCreatedBookmarks;
	}
	
	/**
	 * 
	 * @param nonCreatedBookmarkEntries
	 */
	public void setNonCreatedBookmarks(final Map<String, String> nonCreatedBookmarkEntries) {
		this.nonCreatedBookmarks = nonCreatedBookmarkEntries;
	}
	
	/**
	 * @return the group
	 */
	public String getGroup() {
		return this.group;
	}
	
	/**
	 * @param group the group to set
	 */
	public void setGroup(final String group) {
		this.group = group;
	}
	
	/**
	 * @return the file
	 */
	public CommonsMultipartFile getFile() {
		return this.file;
	}
	
	/**
	 * @param file the file to set
	 */
	public void setFile(final CommonsMultipartFile file) {
		this.file = file;
	}
	
	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return this.totalCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(final int totalCount) {
		this.totalCount = totalCount;
	}
	
	/**
	 * @return the storedConcepts
	 */
	public List<String> getStoredConcepts() {
		return this.storedConcepts;
	}

	/**
	 * @param storedConcepts the storedConcepts to set
	 */
	public void setStoredConcepts(final List<String> storedConcepts) {
		this.storedConcepts = storedConcepts;
	}

	/**
	 * @return the importData
	 */
	public String getImportData() {
		return this.importData;
	}

	/**
	 * @param importData the importData to set
	 */
	public void setImportData(final String importData) {
		this.importData = importData;
	}
		
}