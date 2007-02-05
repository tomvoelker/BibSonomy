package org.bibsonomy.database.managers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.util.ExceptionUtils;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
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
		// TODO not tested
		return this.tagList("getTasByTagName", param);
	}

	/****************************
	 * update increments and decrements for Tag 
	 * and Tag-Tag combinations
	 * *************************/
	
	// Generic Para übernehmen
	public void updateTagTagInc(final GenericParam param) {
		// TODO not tested
		this.update("updateTagTagInc", param);
	}

	/*********snot **************/
	public void updateTagTagDec(Tag tagFirst, Tag tagSecond, GenericParam param) {
		param.setTag(tagFirst);
		param.setTag(tagSecond);
		this.update("updateTagTagDec", param);
	}

	public void updateTagDec(final Tag tagParam) {
		// TODO not tested
		this.update("updateTagDec", tagParam);

	}

	public void insertTagTagBatch(final GenericParam param) {
		// TODO not tested
		this.insert("insertTagTagBatch", param);
	}
	
	/***********return a new TASId by given IDD_TAS_ID(1) as constant ***********/
	public Integer getNewTasId(final GenericParam param) {
      	//TODO not tested
		return (Integer) this.queryForObject("getNewTasId", param);
	}

	public void updateTasId(final int param) {
		// TODO not tested
		this.update("updateTasId", param);
	}

	public void deleteTas(final GenericParam param) {
		// TODO not tested
		this.delete("deleteTas", param);
	}

	public void insertLogTas(final GenericParam param) {
		// TODO not tested
		this.insert("insertLogTas", param);
	}

	public List<Tag> deleteTags(final GenericParam param) throws SQLException {
		// get tags for this contentId
		// FIXME param.getResource().setTags(getTasByContendId(param));
		final List<Tag> tagSet = param.getResource().getTags();

		// add these tags to list and decrease counter in tag table
		for (final Tag tag : tagSet) {
			// decrease counter in tag table
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
			for (final Tag tag1 : tagSet) {
				for (final Tag tag2 : tagSet) {
					if (!tag1.equals(tag2)) {
						updateTagTagDec(tag1, tag2, param);
					}
				}
			}
		}

		// log all tas related to this bookmark 
		insertLogTas(param);
		// delete all tas related to this bookmark
		deleteTas(param);

		return tagSet;
	}

	/**********insert a set of tags***********/
	public void insertTags(final GenericParam param) throws SQLException {
		// generate a list of tags
		 /*********wo erfolgt Abfrage aus DB?**************/
		List<Tag> allTags = param.getTags();
		int tasId;
		HashMap<Tag, Integer> tasIDs = new HashMap<Tag, Integer>();

		// if there're to many tags, do it in a batch job
		if (allTags.size() > MAX_TAGS_TO_INSERT) {
			insertTagTagBatch(param);
			for (Tag tagfirst : allTags) {
				tasId = insertTas(tagfirst, param);
				insertTag(tagfirst);
				// remember tasId for tagtagrelation
				tasIDs.put(tagfirst, tasId);
			}
		} else {
			/*
			 * do it here
			 */
			for (Tag tagfirst : allTags) {
				// not correct
				tasId = insertTas(tagfirst, param);
				insertTag(tagfirst);
				// remember tasId for tagtagrelation
				tasIDs.put(tagfirst, tasId);
				// update tagtag table
				for (Tag tagsecond : allTags) {
					if (!tagfirst.equals(tagsecond)) {
						insertTagTag(tagfirst, tagsecond);
					}
				}
			}
		}
	}
	
	
	
	/** ***********insert tag_name into tags********** */
	public void insertTag(Tag tag) {
		// TODO not tested
		this.insert("insertTag",tag);
		
		}

	/****insert Tag-Tag Combination****/
	public void insertTagTag(Tag tag1,Tag tag2){
	/*******check if the two first elements of tag taglist contains tag-entries*********/
    	if(tag1 ==null||tag2==null){
    		/*****if not*******/
    		
     		ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Two tags needed");
     	}
     	/**********not a optimal solution*************/
     	else{   /*****if the list two valid tag entries*********/
     		    
     		    /*****TODO check if elements are accessible via iBatis*********/
     			this.insert("insertTagTag", new Tag[] {tag1, tag2});
     	}
	
}
	
	public int insertTas(Tag tag,GenericParam param){
		param.setTag(tag);
		int tas_id=getNewTasId(param);
		param.setNewTasId(tas_id);
		this.insert("insertTas",param);
		return tas_id;
	}
	
	
}


