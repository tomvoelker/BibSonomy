package org.bibsonomy.ibatis.db.impl;

import java.util.List;

import org.bibsonomy.ibatis.db.AbstractDatabaseManager;
import org.bibsonomy.ibatis.params.BookmarkParam;
import org.bibsonomy.model.Tag;

/**
 * Used to retrieve set Tags from the database.
 * 
 * @author Christian Schenk
 * @author mgr
 */

public class TagDatabaseManager extends AbstractDatabaseManager {
	/*
	 * only a maximum of 10 tags can be set by the user
	 * it serves to restrict the system behaviour in case of e.g. 200 Tags. 
	 * Only a maximum of 10X10 Tag-Combinations can be computed
	 */
	
	private static final int MAX_TAGS_TO_INSERT = 10;
	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
	 */
	TagDatabaseManager() {
	}

	public Tag getTagById(final int param) {
		return (Tag) this.queryForObject("getTagById", param);
	}

	public List<Tag> getTagByCount(final int param) {
		// TODO not tested
		return this.tagList("getTagByCount", param);
	}
	
	
	public void insertTags(final Tag tag) {
        // TODO not tested
        this.insert("insertTags", tag);
	}
	
	public void insertTagTag(final Tag tag){
		 // TODO not tested
		this.insert("insertTagTag",tag);
		
	}
	
	public void updateTagTagInc(final Tag tag){
		 // TODO not tested
		this.update("updateTagTagInc",tag);
		
		}
	
	public void updateTagTagDec(final Tag tag){
		 // TODO not tested
		this.update("updateTagTagDec",tag);
		
		}
	
	public void updateTagInc(final Tag tag){
		 // TODO not tested
		this.update("updateTagInc",tag);
		
		}
	
	public void updateTagDec(final Tag tag){
		 // TODO not tested
		this.update("updateTagDec",tag);
		
		}
	
	public void insertTagTagBatch(final Tag tag){
		// TODO not tested
		this.insert("insertTagTagBatch", tag);
	}
}