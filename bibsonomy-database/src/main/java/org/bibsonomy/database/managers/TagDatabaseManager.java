package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Used to retrieve tags from the database.
 * 
 * @author Christian Schenk
 * @author mgr
 */
public class TagDatabaseManager extends AbstractDatabaseManager {

	/** Singleton */
	private final static TagDatabaseManager singleton = new TagDatabaseManager();

	/**
	 * Only a maximum of 10 tags can be set by the user. It serves to restrict
	 * the system behaviour in case of e.g. 200 Tags. Only a maximum of 10X10
	 * Tag-Combinations can be computed
	 */
	private static final int MAX_TAGS_TO_INSERT = 10;

	TagDatabaseManager() {
	}

	public static TagDatabaseManager getInstance() {
		return singleton;
	}
    
	
	
	
	
	
	/** Return all tags for given tagId */
	public Tag getTagById(final int param) {
		return (Tag) this.queryForObject("getTagById", param);
	}

	/** Return all tags for a given tag count */
	public List<Tag> getTagByCount(final int param) {
		// TODO not tested
		return this.tagList("getTagByCount", param);
	}

	/** Return all tags for a given contentId */
	public List<Tag> getTasByContendId(final GenericParam param) {
		// TODO not tested
		return this.tagList("getTasByTagName", param);
	}

	public void updateTagTagInc(final GenericParam param) {
		// TODO not tested
		this.update("updateTagTagInc", param);
	}

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

	/** Return a new tasId by given IDD_TAS_ID(1) as constant */

	public Integer getNewTasId(final GenericParam param) {
		// TODO not tested
		return (Integer) this.queryForObject("getNewTasId", param);
	}

	public void updateTasId(final int param) {
		// TODO not tested
		this.update("updateTasId", param);
	}

	public void insertTas(final GenericParam<Bookmark> genericParam) {
		this.insert("insertTas", genericParam);
	}

	public void deleteTas(final GenericParam genericParam) {
		this.delete("deleteTas", genericParam);
	}

	public void insertLogTas(final GenericParam param) {
		// TODO not tested
		this.insert("insertLogTas", param);
	}

	public List<Tag> deleteTags(final GenericParam param) {
		// get tags for this contentId
		// FIXME param.getResource().setTags(getTasByContendId(param));
		final List<Tag> tagSet = null; // FIXME !!!
										// param.getResource().getTags();

		// add these tags to list and decrease counter in tag table
		for (final Tag tag : tagSet) {
			// decrease counter in tag table
			updateTagDec(tag);
		}

		if (tagSet.size() > MAX_TAGS_TO_INSERT) {
			/** * too much tags: batch the job********* */
			/*******************************************************************
			 * a note regarding tag batch processing: the batch table has four
			 * columns: content_id tags toinc isactive - the batch processor
			 * first sets the "isactive" column of a row to TRUE (1) and then
			 * inserts all tags into the tagtag table, afterwards it deletes the
			 * row from the batch table IMPORTANT: getting rows and then setting
			 * them to active has to be done in a transaction, otherwise they
			 * could get removed in between IMPORTANT: read further to end of
			 * this note!
			 ******************************************************************/
			/** ****** schedule job for decrement****** */
			insertTagTagBatch(param);
		} else {
			// compute all tag-tag combinations with o(n_2)
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

	/** Insert a set of tags */
	public void insertTags(final GenericParam param) {
		// generate a list of tags
		// TODO implement this
//		List<Tag> allTags = param.getTags();
//		int tasId;
//		HashMap<Tag, Integer> tasIDs = new HashMap<Tag, Integer>();

		// if there're to many tags, do it in a batch job
		/*if (allTags.size() > MAX_TAGS_TO_INSERT) {
			insertTagTagBatch(param);
			for (final Tag tag1 : allTags) {
				tasId = insertTas(tag1, param);
				insertTag(tag1);
				// remember tasId for tagtagrelation
				tasIDs.put(tag1, tasId);
			}
		} else {
			// do it here
			for (final Tag tag1 : allTags) {
				// not correct
				tasId = insertTas(tag1, param);
				insertTag(tag1);
				// remember tasId for tagtagrelation
				tasIDs.put(tag1, tasId);
				// update tagtag table
				for (final Tag tag2 : allTags) {
					if (!tag1.equals(tag2)) {
						insertTagTag(tag1, tag2);
					}
				}
			}
		}*/
	}

	/**
	 * Increases the tag counter in the tag table for the given tag. If this tag does not exist
	 * inside the tag table, inserts it with count 1.*/
	public void insertTag(Tag tag) {
		// TODO not tested
		this.insert("insertTag", tag);
	}

	/** Insert Tag-Tag Combination */
	public void insertTagTag(Tag tag1, Tag tag2) {
		// check if the two first elements of tag taglist contains tag-entries
		if (tag1 == null || tag2 == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Two tags needed");
		} else {
			this.insert("insertTagTag", new Tag[] { tag1, tag2 });
		}
	}
	
	
	
	/*
	 * single requests for method get detailled information of a tag
	 */
	
	public int getTagOccurrences(final Tag tag) {
		return (Integer)this.queryForObject("getTagOccurrences", tag);
	}
	
	public List<Tag> getSubtagsOfTag(final Tag tag) {
		return this.tagList("getSubtagsOfTag", tag);
	}
	
	public List<Tag> getSupertagsOfTag(final Tag tag) {
		return this.tagList("getSupertagsOfTag", tag);
	}
	
	public List<Tag> getCorrelatedTagsOfTag(final Tag tag) {
		return this.tagList("getCorrelatedTagsOfTag", tag);
	}
	
	/*
	 * return all record tags of the system 
	 */
	
	public List<Tag> getAllTags(final User user) {
		return this.tagList("getAllTags", user);
	}
	
	public Tag getTagDetails(String authUserName, String tagName) {
		return null;
	}	
	
	/**
	 * returns a list of tags. the list can be filtered
	 * TODO: chain of responsibility for different combined arguments
	 **/ 
	 
	
	public List<Tag> getTags(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		return null;
	}
	
	
}