package org.bibsonomy.webapp.command;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Steffen Kress
 * @version $Id$
 */
//TODO
public class SettingsViewCommand extends TabsCommand<Object> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1211293063812357398L;
	
	private static final Log log = LogFactory.getLog(SettingsViewCommand.class);
	
	/** Indexes of definded tabs */
	
	public final static int MY_PROFILE_IDX = 0;
	public final static int SETTINGS_IDX = 1;
	public final static int IMPORTS_IDX = 2;
	
	private String grouping;
	
	private String importType;
	
	private boolean overwrite;
	
	private CommonsMultipartFile file;
	
	private String tagBoxStyle;
	
	private String tagSort;

	private Map<String, String> newBookmarks = null;

	private Map<String, String> updatedBookmarks = null;

	private List<String> nonCreatedBookmarks = null;
	
	/**
	 * name of the begin layout file
	 */
	private String beginName = null;
	
	/**
	 * hash of the begin layout file
	 */
	private String beginHash = null;
	
	/**
	 * name of the item layout file
	 */
	private String itemName = null;
	
	/**
	 * hash of the begin layout file
	 */
	private String itemHash = null;
	
	/**
	 * name of the end layout file
	 */
	private String endName = null;
	
	/**
	 * hash of the end layout file
	 */
	private String endHash = null;
	
	/**
	 * Constructor.
	 */
	public SettingsViewCommand() {
		addTab(MY_PROFILE_IDX, "navi.myprofile");
		addTab(SETTINGS_IDX, "navi.settings");
		addTab(IMPORTS_IDX, "navi.imports");
		setSelTab(MY_PROFILE_IDX);
	}

	public String getGrouping() {
		return this.grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
	}

	public String getImportType() {
		return this.importType;
	}

	public void setImportType(String importType) {
		this.importType = importType;
	}
	
	public boolean getOverwrite() {
		return this.overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public void setTagBoxStyle(String tagBoxStyle) {
		this.tagBoxStyle = tagBoxStyle;
	}

	public String getTagBoxStyle() {
		return tagBoxStyle;
	}

	public CommonsMultipartFile getFile() {
		return this.file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	public void setTagSort(String tagSort) {
		this.tagSort = tagSort;
	}

	public String getTagSort() {
		return tagSort;
	}

	public Map<String, String> getNewBookmarks() {
		return this.newBookmarks;
	}

	public Map<String, String> getUpdatedBookmark() {
		return this.updatedBookmarks;
	}

	public List<String> getNonCreatedBookmark() {
		return this.nonCreatedBookmarks;
	}

	public void setNewBookmarks(Map<String, String> newBookmarks) {
		this.newBookmarks = newBookmarks;
	}

	public void setUpdatedBookmarks(Map<String, String> updatedBookmarks) {
		this.updatedBookmarks = updatedBookmarks;
	}

	public void setNonCreatedBookmarks(List<String> nonCreatedBookmarks) {
		this.nonCreatedBookmarks = nonCreatedBookmarks;
	}

	public String getBeginName() {
		return this.beginName;
	}

	public String getBeginHash() {
		return this.beginHash;
	}

	public String getItemName() {
		return this.itemName;
	}

	public String getItemHash() {
		return this.itemHash;
	}

	public String getEndName() {
		return this.endName;
	}

	public String getEndHash() {
		return this.endHash;
	}

	public void setBeginName(String beginName) {
		this.beginName = beginName;
	}

	public void setBeginHash(String beginHash) {
		this.beginHash = beginHash;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setItemHash(String itemHash) {
		this.itemHash = itemHash;
	}

	public void setEndName(String endName) {
		this.endName = endName;
	}

	public void setEndHash(String endHash) {
		this.endHash = endHash;
	}


}