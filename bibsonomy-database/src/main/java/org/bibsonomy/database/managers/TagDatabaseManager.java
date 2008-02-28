package org.bibsonomy.database.managers;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.tag.TagChain;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.beans.TagTagBatchParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
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

	private final static TagDatabaseManager singleton = new TagDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final TagRelationDatabaseManager tagRelDb;
	private final DatabasePluginRegistry plugins;
	private static final TagChain chain = new TagChain();
	
	/**
	 * Only a maximum of 10 tags can be set by the user. It serves to restrict
	 * the system behaviour in case of e.g. 200 Tags. Only a maximum of 10X10
	 * Tag-Combinations can be computed
	 */
	// private static final int MAX_TAGS_TO_INSERT = 10;

	private TagDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagRelDb = TagRelationDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	public static TagDatabaseManager getInstance() {
		return singleton;
	}

	/** Return tag for given tagId */
	public Tag getTagById(final Integer tagId, final DBSession session) {
		return this.queryForObject("getTagById", tagId, Tag.class, session);
	}

	/** Return all tags for a given tag count */
	// FIXME a single tag should be returned instead of a list, shouldn't it?
	public List<Tag> getTagByCount(final TagParam param, final DBSession session) {
		// TODO not tested
		return this.queryForList("getTagByCount", param, Tag.class, session);
	}

	/** Return all tags for a given contentId */
	private List<Tag> getTasByContendId(final TagParam param, final DBSession session) {
		// FIXME this query doesn't exist
		// return this.queryForList("getTasByTagName", param, Tag.class, session);
		return null;
	}

	private void updateTagTagInc(final TagParam param, final DBSession session) {
		// TODO not tested
		this.update("updateTagTagInc", param, session);
	}

	private void updateTagTagDec(Tag tagFirst, Tag tagSecond, TagParam param, final DBSession session) {
		param.setTag(tagFirst);
		param.setTag(tagSecond);
		this.update("updateTagTagDec", param, session);
	}

	private void updateTagDec(final String tagname, final DBSession session) {
		this.update("updateTagDec", tagname, session);
	}

	private void insertTagTagBatch(final int contentId, final Iterable<Tag> tags, TagTagBatchParam.Job job, final DBSession session) {
		final TagTagBatchParam batchParam = new TagTagBatchParam();
		batchParam.setContentId(contentId);
		batchParam.setTagList(TagDatabaseManager.tagsToString(tags));
		batchParam.setJob(job);
		this.insertTagTagBatch(batchParam, session);
	}

	private void insertTagTagBatch(final TagTagBatchParam param, final DBSession session) {
		// TODO not tested
		this.insert("insertTagTagBatch", param, session);
	}

	public void insertTas(final TagParam param, final DBSession session) {
		for (final Tag tag : param.getTags()) {
			param.setTag(tag);
			for (final Integer groupId : param.getGroups()) {
				param.setGroupId(groupId);
				param.setTasId(generalDb.getNewContentId(ConstantID.IDS_TAS_ID, session));
				this.insert("insertTas", param, session);
			}
		}
	}

	public void deleteTas(final Integer contentId, final DBSession session) {
		this.delete("deleteTas", contentId, session);
	}

	public void deleteTags(final Post<?> post, final DBSession session) {
		// get tags for this contentId
		// FIXME param.getResource().setTags(getTasByContendId(param));
		final boolean batchIt = true;
		// param.getResource().getTags();

		// add these tags to list and decrease counter in tag table
		for (final Tag tag : post.getTags()) {
			// decrease counter in tag table
			updateTagDec(tag.getName(), session);
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
		this.plugins.onTagDelete(post.getContentId(), session);
		// delete all tas related to this bookmark
		deleteTas(post.getContentId(), session);
	}

	public void insertTags(final Post<?> post, final DBSession session) {
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
	public void insertTags(final TagParam param, final DBSession session) {
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
				this.insertTagTagBatch(param.getNewContentId(), param.getTags(), TagTagBatchParam.Job.DECREMENT, session);
			}

			this.insertTas(param, session);

			for (final Tag tag : param.getTags()) {
				this.tagRelDb.insertRelations(tag, param.getUserName(), session);				
			}

			for (final Tag tag : allTags) {
				this.insertTag(tag, session);
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
	public void insertTag(final Tag tag, final DBSession session) {
		// TODO not tested
		this.insert("insertTag", tag, session);
	}

	/** Insert Tag-Tag Combination */
	/* FIXME: SQL looks weird
	public void insertTagTag(Tag tag1, Tag tag2, final DBSession session) {
		// check if the two first elements of tag taglist contains tag-entries
		if (tag1 == null || tag2 == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Two tags needed");
		} else {
			this.insert("insertTagTag", new Tag[] { tag1, tag2 }, session);
		}
	}*/

	public int getTagOccurrences(final TagParam param, final DBSession session) {
		return this.queryForObject("getTagOccurrences", param, Integer.class, session);
	}

	public List<Tag> getSubtagsOfTag(final TagParam param, final DBSession session) {
		return this.queryForList("getSubtagsOfTag", param, Tag.class, session);
	}

	public List<Tag> getSupertagsOfTag(final TagParam param, final DBSession session) {
		return this.queryForList("getSupertagsOfTag", param, Tag.class, session);
	}

	public List<Tag> getCorrelatedTagsOfTag(final TagParam param, final DBSession session) {
		return this.queryForList("getCorrelatedTagsOfTag", param, Tag.class, session);
	}

	/**
	 * Returns all tags.
	 */
	public List<Tag> getAllTags(final TagParam param, final DBSession session) {
		return this.queryForList("getAllTags", param, Tag.class, session);
	}

	/**
	 * Return a tag by its tag name
	 */
	public Tag getTagByName(final TagParam param, final DBSession session) {
		return this.queryForObject("getTagByName", param, Tag.class, session);
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
	public Tag getTagDetails(TagParam param, final DBSession session) {
		final Tag tag;
		param.setCaseSensitiveTagNames(true);
		tag = this.getTagByName(param, session);
		
		// retrieve all sub-/supertags
		param.setLimit(10000);
		param.setOffset(0);
		
		// check for sub-/supertags
		if (param.getNumSimpleConcepts() > 0) {
			List<Tag> subTags = this.getSubtagsOfTag(param, session);
			tag.setSubTags(setUsercountToGlobalCount(subTags));
		}
		if (param.getNumSimpleConceptsWithParent() > 0) {
			List<Tag> superTags = this.getSupertagsOfTag(param, session);
			tag.setSuperTags(setUsercountToGlobalCount(superTags));
		}
		if (param.getNumCorrelatedConcepts() > 0) {
			List<Tag> subTags = this.getSubtagsOfTag(param, session);
			tag.setSubTags(setUsercountToGlobalCount(subTags));
			List<Tag> superTags = this.getSupertagsOfTag(param, session);
			tag.setSuperTags(setUsercountToGlobalCount(superTags));			
		}
		
		// this is just a hack as long as we don't supply separate user counts for
		// each tag, DB
		tag.setUsercount(tag.getGlobalcount());
		
		return tag;
	}

	/**
	 * Get all tags of a given user
	 */
	public List<Tag> getTagsByUser(final TagParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByUser", param, Tag.class, session);
	}

	
	/**
	 * Get all tags of a an author, which assigned to the authors entries/currently, the cloud is ordered alphabetically
	 * @param param 
	 * @param session 
	 * @return list of tags
	 */
	public List<Tag> getTagsAuthor(final TagParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByAuthor", param, Tag.class, session);
	}
	
	/**
	 * Get all tags of a given group
	 */
	public List<Tag> getTagsByGroup(final TagParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.queryForList("getTagsByGroup", param, Tag.class, session);
	}

	/**
	 * Get all tags of a given regular expression
	 * @param param 
	 * @param session 
	 * @return list of tags
	 */
	public List<Tag> getTagsByExpression(final TagParam param, final DBSession session) {
		return this.queryForList("getTagsByExpression", param, Tag.class, session);
	}

	/**
	 * @param param
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getTagsViewable(final TagParam param, final DBSession session) {
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own tags, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return this.queryForList("getTagsViewableBySpecialGroup", param, Tag.class, session);
		}
		return this.queryForList("getTagsViewable", param, Tag.class, session);
	}
	
	/**
	 * Get related tags for a given tag or list of tags for a specified group
	 * @param param 
	 * @param session 
	 * @return list of tags
	 */
	public List<Tag> getRelatedTagsForGroup(TagParam param, DBSession session) {
		return this.queryForList("getRelatedTagsForGroup", param, Tag.class, session);
	}	
	
	/**
	 * Get related tags for a given tag 
	 * @param param
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getRelatedTagsViewable(TagParam param, DBSession session) {
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// show users own tags, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return this.queryForList("getRelatedTagsForSpecialGroup", param, Tag.class, session);
		}
		return this.queryForList("getRelatedTagsViewable", param, Tag.class, session);
	}	

	public List<Tag> getTags(final TagParam param, final DBSession session) {
		
		final List<Tag> tags;
		
		// start the chain
		tags = chain.getFirstElement().perform(param, session); 
		
		return this.setUsercountToGlobalCount(tags);		
	}
	
	/**
	 * This is just a hack as long as we don't supply separate user counts for each tag, dbe
	 * 
	 * @param tags a list of tags
	 * @return list of tags with usercount set to globalcount for each tag
	 */
	private List<Tag> setUsercountToGlobalCount(List<Tag> tags) {
		for (final Tag tag : tags) {
			tag.setUsercount(tag.getGlobalcount());
		}
		return tags;
	}
	
	/**
	 * Helper function to print a list of tags to stdout
	 * 
	 * @param tags
	 */
	private void printTags(List<Tag> tags) {
		if (tags != null)
		for (final Tag tag : tags) {
			System.out.println(tag.getName());
		}
	}
	
	public List<Tag> getRelatedTags(TagParam param, DBSession session){
		return this.queryForList("getRelatedTags", param, Tag.class, session);
	}

	public List<Tag> getRelatedTagsOrderedByFolkrank(TagParam param, DBSession session){
		return this.queryForList("getRelatedTagsOrderedByFolkrank", param, Tag.class, session);
	}
	
	/**
	 * retrieve tags attached to a bookmark with a given hash
	 * 
	 * @param loginUserName
	 * @param hash
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of tags attached to the bookmark with the given hash
	 */
	public List<Tag> getTagsByBookmarkHash(final String loginUserName, final String hash, int limit, int offset, final DBSession session) {
		TagParam param = new TagParam();
		param.setHash(hash);
		param.setUserName(loginUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByBookmarkHash", param, Tag.class, session);
	}
	
	/**
	 * retrieve tags attached to a bookmark with a given hash for a given user
	 * 
	 * @param loginUserName
	 * @param requestedUserName
	 * @param hash
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of tags attached to the given user's bookmark with the given hash
	 */
	public List<Tag> getTagsByBookmarkHashForUser(final String loginUserName, final String requestedUserName, final String hash, int limit, int offset, final DBSession session) {
		TagParam param = new TagParam();
		param.setHash(hash);
		param.setUserName(loginUserName);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByBookmarkHash", param, Tag.class, session);
	}
	
	/**
	 * retrieve tags attached to a bibtex with the given hash
	 * 
	 * @param loginUserName
	 * @param hash
	 * @param hashId
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of tags attached to a bibtex with the given hash
	 */
	public List<Tag> getTagsByBibtexHash(final String loginUserName, final String hash, final HashID hashId, int limit, int offset, final DBSession session) {
		TagParam param = new TagParam();
		param.setHash(hash);
		param.setHashId(hashId);
		param.setUserName(loginUserName);
		param.setLimit(limit);
		param.setOffset(offset);		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByBibtexHash", param, Tag.class, session);
	}
	
	/**
	 * retrieve tags attached to a bibtex of a given user with the given hash
	 * 
	 * @param loginUserName
	 * @param requestedUserName
	 * @param hash
	 * @param hashId
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of tags attached to a given user's bibtex with the given hash
	 */
	public List<Tag> getTagsByBibtexHashForUser(final String loginUserName, final String requestedUserName, final String hash, final HashID hashId, int limit, int offset, final DBSession session) {
		TagParam param = new TagParam();
		param.setHash(hash);
		param.setHashId(hashId);
		param.setUserName(loginUserName);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByBibtexHash", param, Tag.class, session);
	}	
}