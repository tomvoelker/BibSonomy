package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Used to retrieve tags from the database.
 * 
 * @author Christian Schenk
 * @author mgr
 * @version $Id$
 */
public class TagDatabaseManager extends AbstractDatabaseManager {

	/** Singleton */
	private final static TagDatabaseManager singleton = new TagDatabaseManager();
	private final GeneralDatabaseManager generalDb = GeneralDatabaseManager.getInstance();
	/**
	 * Only a maximum of 10 tags can be set by the user. It serves to restrict
	 * the system behaviour in case of e.g. 200 Tags. Only a maximum of 10X10
	 * Tag-Combinations can be computed
	 */
	private static final int MAX_TAGS_TO_INSERT = 10;

	private TagDatabaseManager() {
	}

	public static TagDatabaseManager getInstance() {
		return singleton;
	}
    
	/** Return tag for given tagId */
	public Tag getTagById(final TagParam param, final Transaction transaction) {
		return this.queryForObject("getTagById", param, Tag.class, transaction);
	}

	/** Return all tags for a given tag count */
	// FIXME a single tag should be returned instead of a list, shouldn't it?  
	public List<Tag> getTagByCount(final TagParam param, final Transaction transaction) {
		// TODO not tested
		return this.queryForList("getTagByCount", param, Tag.class, transaction);
	}

	/** Return all tags for a given contentId */
	public List<Tag> getTasByContendId(final TagParam param, final Transaction transaction) {
		// FIXME this query doesn't exist
		// return this.queryForList("getTasByTagName", param, Tag.class, transaction);
		return null;
	}

	public void updateTagTagInc(final TagParam param, final Transaction transaction) {
		// TODO not tested
		this.update("updateTagTagInc", param, transaction);
	}

	public void updateTagTagDec(Tag tagFirst, Tag tagSecond, TagParam param, final Transaction transaction) {
		param.setTag(tagFirst);
		param.setTag(tagSecond);
		this.update("updateTagTagDec", param, transaction);
	}

	public void updateTagDec(final Tag param, final Transaction transaction) {
		// TODO not tested
		this.update("updateTagDec", param, transaction);
	}

	public void insertTagTagBatch(final TagParam param, final Transaction transaction) {
		// TODO not tested
		this.insert("insertTagTagBatch", param, transaction);
	}

	public void insertTas(final TagParam param, final Transaction transaction) {
		this.insert("insertTas", param, transaction);
	}

	public void deleteTas(final GenericParam param, final Transaction transaction) {
		this.delete("deleteTas", param, transaction);
	}

	public void updateTasId(final int param, final Transaction transaction) {
		// TODO not tested
		this.update("updateTasId", param, transaction);
	}

	public void insertLogTas(final TagParam param, final Transaction transaction) {
		// TODO not tested
		this.insert("insertLogTas", param, transaction);
	}

	public List<Tag> deleteTags(final TagParam param, final Transaction transaction) {
		// get tags for this contentId
		// FIXME param.getResource().setTags(getTasByContendId(param));
		final List<Tag> tagSet = null; // FIXME !!!
										// param.getResource().getTags();

		// add these tags to list and decrease counter in tag table
		for (final Tag tag : tagSet) {
			// decrease counter in tag table
			updateTagDec(tag, transaction);
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
			insertTagTagBatch(param, transaction);
		} else {
			// compute all tag-tag combinations with o(n_2)
			for (final Tag tag1 : tagSet) {
				for (final Tag tag2 : tagSet) {
					if (!tag1.equals(tag2)) {
						updateTagTagDec(tag1, tag2, param, transaction);
					}
				}
			}
		}

		// log all tas related to this bookmark
		insertLogTas(param, transaction);
		// delete all tas related to this bookmark
		deleteTas(param, transaction);

		return tagSet;
	}

	/** Insert a set of tags */
	public void insertTags(final TagParam param, final Transaction transaction) {
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
	public void insertTag(Tag tag, final Transaction transaction) {
		// TODO not tested
		this.insert("insertTag", tag, transaction);
	}

	/** Insert Tag-Tag Combination */
	public void insertTagTag(Tag tag1, Tag tag2, final Transaction transaction) {
		// check if the two first elements of tag taglist contains tag-entries
		if (tag1 == null || tag2 == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Two tags needed");
		} else {
			this.insert("insertTagTag", new Tag[] { tag1, tag2 }, transaction);
		}
	}

	/*
	 * single requests for method get detailled information of a tag
	 */
	public int getTagOccurrences(final Tag tag, final Transaction transaction) {
		return this.queryForObject("getTagOccurrences", tag, Integer.class, transaction);
	}

	public List<Tag> getSubtagsOfTag(final Tag tag, final Transaction transaction) {
		return this.queryForList("getSubtagsOfTag", tag, Tag.class, transaction);
	}

	public List<Tag> getSupertagsOfTag(final Tag tag, final Transaction transaction) {
		return this.queryForList("getSupertagsOfTag", tag, Tag.class, transaction);
	}

	public List<Tag> getCorrelatedTagsOfTag(final Tag tag, final Transaction transaction) {
		return this.queryForList("getCorrelatedTagsOfTag", tag, Tag.class, transaction);
	}

	/**
	 * Return all tags from the system 
	 */
	public List<Tag> getAllTags(final TagParam param, final Transaction transaction) {
		return this.queryForList("getAllTags", param, Tag.class, transaction);
	}

	/**
	 * returns details about a tag. those details are:
	 * <ul>
	 * <li>details about the tag itself, like number of occurrences etc</li>
	 * <li>list of subtags</li>
	 * <li>list of supertags</li>
	 * <li>list of correlated tags</li>
	 * </ul>
	 * 
	 * @param authUserName
	 *            name of the authenticated user
	 * @param tagName
	 *            name of the tag
	 * @return the tag's details, null else
	 */
	public Tag getTagDetails(String authUserName, String tagName, final Transaction transaction) {
		return null;
	}	

	/**
	 * Get all tags of a given user
	 */
	public List<Tag> getTagsByUser(final TagParam param, final Transaction transaction) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, transaction);
		return this.queryForList("getTagsByUser", param, Tag.class, transaction);
	} 

	/**
	 * Get all tags of a given group
	 */
	public List<Tag> getTagsByGroup(final TagParam param, final Transaction transaction) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, transaction);
		return this.queryForList("getTagsByGroup", param, Tag.class, transaction);
	}

	/**
	 * Get all tags of a given regular expression 
	 */
	public List<Tag> getTagsByExpression(final TagParam param, final Transaction transaction) {
		return this.queryForList("getTagsByExpression", param, Tag.class, transaction);
	} 

	public List<Tag> getTagsViewable(final TagParam param, final Transaction transaction) {
		return this.queryForList("getTagsViewable", param, Tag.class, transaction);
	} 

	public List<Tag> getTags(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end, final Transaction transaction) {
		return null;
	}
}