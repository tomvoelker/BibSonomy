package org.bibsonomy.database.managers;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.beans.TagTagBatchParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * Used to retrieve tags from the database.
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class TagDatabaseManager extends AbstractDatabaseManager {

	/** Singleton */
	private final static TagDatabaseManager singleton = new TagDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final TagRelationDatabaseManager tagRelDb;
	/**
	 * Only a maximum of 10 tags can be set by the user. It serves to restrict
	 * the system behaviour in case of e.g. 200 Tags. Only a maximum of 10X10
	 * Tag-Combinations can be computed
	 */
	// private static final int MAX_TAGS_TO_INSERT = 10;

	private TagDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagRelDb = TagRelationDatabaseManager.getInstance();
	}

	public static TagDatabaseManager getInstance() {
		return singleton;
	}

	/** Return tag for given tagId */
	public Tag getTagById(final Integer tagId, final Transaction session) {
		return this.queryForObject("getTagById", tagId, Tag.class, session);
	}

	/** Return all tags for a given tag count */
	// FIXME a single tag should be returned instead of a list, shouldn't it?
	public List<Tag> getTagByCount(final TagParam param, final Transaction session) {
		// TODO not tested
		return this.queryForList("getTagByCount", param, Tag.class, session);
	}

	/** Return all tags for a given contentId */
	public List<Tag> getTasByContendId(final TagParam param, final Transaction session) {
		// FIXME this query doesn't exist
		// return this.queryForList("getTasByTagName", param, Tag.class, session);
		return null;
	}

	public void updateTagTagInc(final TagParam param, final Transaction session) {
		// TODO not tested
		this.update("updateTagTagInc", param, session);
	}

	public void updateTagTagDec(Tag tagFirst, Tag tagSecond, TagParam param, final Transaction session) {
		param.setTag(tagFirst);
		param.setTag(tagSecond);
		this.update("updateTagTagDec", param, session);
	}

	public void updateTagDec(final Tag param, final Transaction session) {
		// TODO not tested
		this.update("updateTagDec", param, session);
	}

	public void insertTagTagBatch(final int contentId, final Iterable<Tag> tags, TagTagBatchParam.Job job, final Transaction session) {
		final TagTagBatchParam batchParam = new TagTagBatchParam();
		batchParam.setContentId(contentId);
		batchParam.setTagList(TagDatabaseManager.tagsToString(tags));
		batchParam.setJob(job);
		this.insertTagTagBatch(batchParam, session);
	}

	public void insertTagTagBatch(final TagTagBatchParam param, final Transaction session) {
		// TODO not tested
		this.insert("insertTagTagBatch", param, session);
	}

	public void insertTas(final TagParam param, final Transaction session) {
		for (final Tag tag : param.getTags()) {
			param.setTag(tag);
			for (final Integer groupId : param.getGroups()) {
				param.setGroupId(groupId);
				param.setTasId(generalDb.getNewContentId(ConstantID.IDS_TAS_ID, session));
				this.insert("insertTas", param, session);
			}
		}
	}

	public void deleteTas(final Integer contentId, final Transaction session) {
		this.delete("deleteTas", contentId, session);
	}

	public void insertLogTas(final TagParam param, final Transaction session) {
		// TODO not tested
		this.insert("insertLogTas", param, session);
	}

	public void deleteTags(final Post<?> post, final Transaction session) {
		// get tags for this contentId
		// FIXME param.getResource().setTags(getTasByContendId(param));
		final boolean batchIt = true;
		// param.getResource().getTags();

		// add these tags to list and decrease counter in tag table
		for (final Tag tag : post.getTags()) {
			// decrease counter in tag table
			updateTagDec(tag, session);
		}

		if (batchIt == true) {
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
			insertTagTagBatch(post.getContentId(), post.getTags(), TagTagBatchParam.Job.DECREMENT, session);
		} else {
			throw new UnsupportedOperationException();
			// compute all tag-tag combinations with o(n_2)
			/* TODO: implement updateTagTagDec and do this:
			for (final Tag tag1 : tagSet) {
				for (final Tag tag2 : tagSet) {
					if (!tag1.equals(tag2)) {
						updateTagTagDec(tag1, tag2, param, session);
					}
				}
			}*/
		}

		// TODO: log all tas related to this post -> this.insertLogTas(...)
		// delete all tas related to this bookmark
		deleteTas(post.getContentId(), session);
	}

	public void insertTags(final Post<?> post, final Transaction session) {
		final TagParam tagParam = new TagParam();
		tagParam.setTags(post.getTags());
		tagParam.setNewContentId(post.getContentId());
		tagParam.setContentTypeByClass(post.getResource().getClass());
		tagParam.setUserName(post.getUser().getName());
		tagParam.setDate(post.getDate());
		tagParam.setDescription(post.getDescription());
		List<Integer> groups = new ArrayList<Integer>();
		for (final Group group : post.getGroups()) {
			groups.add(group.getGroupId());
		}
		tagParam.setGroups(groups);
		insertTags(tagParam, session);
	}

	/**
	 * Insert a set of tags for a content (into tas table and what else is
	 * required)
	 */
	public void insertTags(final TagParam param, final Transaction session) {
		// generate a list of tags
		final List<Tag> allTags = param.getTags();
		// TODO: use this and implement nonbatch-tagtag-inserts:
		// (allTags.size() > MAX_TAGS_TO_INSERT);
		final boolean batchIt = true;

		session.beginTransaction();
		try {
			if (batchIt == true) {
				// if there're too many tags, do it in a batch job
				// FIXME: someone misused newContentId for the tasId.
				// requestedContentId is the real new contentId here
				insertTagTagBatch(param.getNewContentId(), param.getTags(), TagTagBatchParam.Job.DECREMENT, session);
			}
			insertTas(param, session);
			for (final Tag tag : param.getTags()) {
				this.tagRelDb.insertRelations(tag, param.getUserName(), session);				
			}
			for (final Tag tag1 : allTags) {
				insertTag(tag1, session);
				if (batchIt == false) {
					throw new UnsupportedOperationException();
					/* TODO: implement nonbatch-tagtag-inserts
					for (final Tag tag2 : allTags) {
						if (!tag1.equals(tag2)) {
							insertTagTag(tag1, tag2);
						}
					}*/
				}
			}
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Builds a string from a list of tags. The tags are separated by space in
	 * the string.
	 * 
	 * @param tags
	 *            some tags
	 * @return the string of white space separated tags
	 */
	private static String tagsToString(final Iterable<Tag> tags) {
		final StringBuffer s = new StringBuffer();
		for (final Tag tag : tags) {
			s.append(tag.getName()).append(" ");
		}
		return s.toString().trim();
	}

	/**
	 * Increases the tag counter in the tag table for the given tag. If this tag
	 * does not exist inside the tag table, inserts it with count 1.
	 */
	public void insertTag(final Tag tag, final Transaction session) {
		// TODO not tested
		this.insert("insertTag", tag, session);
	}

	/** Insert Tag-Tag Combination */
	/* FIXME: SQL looks weird
	public void insertTagTag(Tag tag1, Tag tag2, final Transaction session) {
		// check if the two first elements of tag taglist contains tag-entries
		if (tag1 == null || tag2 == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Two tags needed");
		} else {
			this.insert("insertTagTag", new Tag[] { tag1, tag2 }, session);
		}
	}*/

	public int getTagOccurrences(final Tag tag, final Transaction session) {
		return this.queryForObject("getTagOccurrences", tag, Integer.class, session);
	}

	public List<Tag> getSubtagsOfTag(final Tag tag, final Transaction session) {
		return this.queryForList("getSubtagsOfTag", tag, Tag.class, session);
	}

	public List<Tag> getSupertagsOfTag(final Tag tag, final Transaction session) {
		return this.queryForList("getSupertagsOfTag", tag, Tag.class, session);
	}

	public List<Tag> getCorrelatedTagsOfTag(final Tag tag, final Transaction session) {
		return this.queryForList("getCorrelatedTagsOfTag", tag, Tag.class, session);
	}

	/**
	 * Returns all tags.
	 */
	public List<Tag> getAllTags(final TagParam param, final Transaction session) {
		return this.queryForList("getAllTags", param, Tag.class, session);
	}

	/**
	 * Returns details about a tag. Those details are:
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
	public Tag getTagDetails(final String authUserName, final String tagName, final Transaction session) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get all tags of a given user
	 */
	public List<Tag> getTagsByUser(final TagParam param, final Transaction session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByUser", param, Tag.class, session);
	}

	/**
	 * Get all tags of a given group
	 */
	public List<Tag> getTagsByGroup(final TagParam param, final Transaction session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.queryForList("getTagsByGroup", param, Tag.class, session);
	}

	/**
	 * Get all tags of a given regular expression
	 */
	public List<Tag> getTagsByExpression(final TagParam param, final Transaction session) {
		return this.queryForList("getTagsByExpression", param, Tag.class, session);
	}

	public List<Tag> getTagsViewable(final TagParam param, final Transaction session) {
		return this.queryForList("getTagsViewable", param, Tag.class, session);
	}

	public List<Tag> getTags(final String authUser, final GroupingEntity grouping, final String groupingName, final String regex, final int start, final int end, final Transaction session) {
		// TODO: this is only a hack to provide tag-support. feel free to delete
		// the code and implement it with a nice and complete handler chain.
		// --> from revision 1.27 - where's the hack?

		final TagParam param = LogicInterfaceHelper.buildParam(TagParam.class, authUser, grouping, groupingName, null, null, null, start, end);

		final List<Tag> tags;
		if (grouping == GroupingEntity.ALL) {
			tags = this.queryForList("getAllTags", param, Tag.class, session);
		} else {
			DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
			tags = this.queryForList("getTagsByUser", param, Tag.class, session);
		}
		for (final Tag tag : tags) {
			tag.setUsercount(tag.getGlobalcount());
		}
		return tags;
	}
}