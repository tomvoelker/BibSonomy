package org.bibsonomy.ibatis.db.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bibsonomy.ibatis.db.AbstractDatabaseManager;
import org.bibsonomy.ibatis.params.GenericParam;
import org.bibsonomy.ibatis.util.ExceptionUtils;
import org.bibsonomy.model.Resource;
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
	/*******return all tags for given TagId***********/
	public Tag getTagById(final int param) {
		return (Tag) this.queryForObject("getTagById", param);
	}
	
    /*******return all tags for a given tag_crt**********/
	public List<Tag> getTagByCount(final int param) {
		// TODO not tested
		return this.tagList("getTagByCount", param);
	}
	
	/*********return all tags for a given contentId*************/
	public List<Tag> getTasByContendId(final GenericParam param) {
		//TODO not tested
		return this.tagList("getTasByTagName", param);
     }
	
	public void insertTagTag(final Tag tagParam){
		// TODO not tested
		this.insert("insertTagTag",tagParam);
	}
	
	
	/****************************
	 * update increments and decrements for Tag 
	 * and Tag-Tag combinations
	 * *************************/
	
	// GenPara übernehmen
	public void updateTagTagInc(final GenericParam param){
		 // TODO not tested
		this.update("updateTagTagInc",param);
		
	}
	
	public void updateTagTagDec(final Tag tagParam){
		 // TODO not tested
		this.update("updateTagTagDec",tagParam);
		
	}
	
	
	public void updateTagDec(final Tag tagParam){
		 // TODO not tested
		this.update("updateTagDec",tagParam);
		
	}
	
	public void insertTagTagBatch(final GenericParam  param){
		// TODO not tested
		this.insert("insertTagTagBatch", param);
	}
	
	/***********return a new TASId by given IDD_TAS_ID(1) as constant ***********/
	public Integer getNewTasId(final GenericParam param) {
      	//TODO not tested
		return (Integer) this.queryForObject("getNewTasId", param);
	}

 
	public void updateTasId(final int param){
	 // TODO not tested
	this.update("updateTasId", param);
	
	}
 
	public void insertTas(final Tag tag,final GenericParam param){
	 // TODO not tested
	this.insert("insertTas", param);
	
	}

	public void deleteTas(final GenericParam param){
    //TODO not tested	
    this.delete("deleteTas", param);	
	
	}

	public void insertLogTas(final GenericParam param){
    // //TODO not tested	
	this.insert("insertLogTas", param);
	
	}
	public List <Tag> deleteTags (GenericParam param) throws SQLException {
		/*** get tags for this content_id ***/
		param.getResource().setTags(getTasByContendId(param));
		final List<Tag> tagSet = param.getResource().getTags();
        /***add these tags to list and decrease counter in tag table***/
		for(Tag  tag: tagSet){
	   /***decrease counter in tag table***/
			updateTagDec(tag);
		}							
		if (tagSet.size() > MAX_TAGS_TO_INSERT) {
			
			/*** too much tags: batch the job**********/
			/** a note regarding tag batch processing:
			 * the batch table has four columns:
			 * content_id  tags  toinc  isactive  
			 * - the batch processor first sets the "isactive" column of a row to TRUE (1) 
			 *   and then inserts all tags into the tagtag table, afterwards it deletes the
			 *   row from the batch table
			 *   IMPORTANT: getting rows and then setting them to active has to be done in 
			 *   a transaction, otherwise they could get removed in between
			 *   IMPORTANT: read further to end of this note!
			 ***/
			/******** schedule job for decrement*******/
			
			insertTagTagBatch(param);
			
		} else {
			/*****compute all Tag-Tag combinations with o(n_2)********/
			for(Tag tag1:  tagSet){
				for(Tag tag2: tagSet){
					if(!tag1.equals(tag2)){
						updateTagTagDec(tag1,tag2);
					}
				}
			}
		}
		/********** log all tas related to this bookmark********/ 
		insertLogTas(param);
		/********** delete all tas related to this bookmark*******/
		deleteTas(param);
		return tagSet;
	}
	
	public void insertTags (GenericParam param) throws SQLException{
		List<Tag> allTags=param.getTags();
		int tasId;
		HashMap<Tag,Integer> tasIDs = new HashMap<Tag,Integer>();
		if (allTags.size() > MAX_TAGS_TO_INSERT) {
			/*
			 * do it in a batch job
			 */
			
			insertTagTagBatch(param);
     			for(Tag tagfirst: allTags){
     				tasId=insertMyTas(tagfirst, param);
     				insertTag(tagfirst);
     				/********remember tasId for tagtagrelation*************/
     				tasIDs.put(tagfirst, tasId);
				
			}
		} 	else {
			/*
			 * do it here
			 */
			for(Tag tagfirst: allTags){
				/*********not correct**********/
				tasId = insertMyTas(tagfirst, param);
				insertTag(tagfirst);
				/********remember tasId for tagtagrelation*************/
				tasIDs.put(tagfirst, tasId);
				/******* update tagtag table *******/
				for(Tag tagsecond: allTags){
					if(!tagfirst.equals(tagsecond)){
						insertMyTagTag(tagfirst,tagsecond);
					}
				}
			}
		}
	}
	
	/*************insert tag_name into tags***********/
	public void insertTag(Tag tag) throws SQLException {
		// TODO not tested
		this.update("insertTag",tag);
		
		}
	
	
	
	/****insert Tag-Tag Combination****/
	public void insertMyTagTag(Tag tag1,  Tag tag2)throws SQLException{
	     	if(tag1==null||tag2== null){
	     		if (tag1.getName() )
	     		
	     		
	     	}
		if (param.getResource().getTags().get(0) == null || param.getResource().getTags().get(1) == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Two tags needed");
		}
		
		/*****TODO not correct*************/
		updateTagTagInc(param);
		/*****dasselbe wie updateTagInc??****/
		/********TODO pehaps execute an other sql-statement like
		 * INSERT INTO ... ON DUPLICATE UPDATE ...
		 *  **********/
		
	}
	
	/********TODO not correct**************/
	private int insertMyTas(Tag tag, GenericParam param) throws SQLException {
		/* get tas_id for this tas */
		int tas_id =getNewTasId(param);
		/* check, if we got an id */
			insertTas(param);
			return tas_id;
	}
}


