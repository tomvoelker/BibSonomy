package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.tag.TagChain;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.params.beans.TagTagBatchParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * Used to retrieve tags from the database.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class TagDatabaseManager extends AbstractDatabaseManager {

	private static final Log log = LogFactory.getLog(TagDatabaseManager.class);

	private final static TagDatabaseManager singleton = new TagDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final TagRelationDatabaseManager tagRelDb;
	private final DatabasePluginRegistry plugins;
	private static final TagChain chain = new TagChain();
	
	/** interface to a resource searcher for building an author's tag cloud */
	private ResourceSearch<BibTex> authorSearch;

	/**
	 * Only a maximum of 10 tags can be set by the user. It serves to restrict
	 * the system behaviour in case of e.g. 200 Tags. Only a maximum of 10X10
	 * Tag-Combinations can be computed
	 */
	// private static final int MAX_TAGS_TO_INSERT = 10;

	/**
	 * Constructor
	 */
	private TagDatabaseManager() {
		super();
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.tagRelDb = TagRelationDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	/**
	 * @return a singleton instance of the TagDatabaseManager
	 */
	public static TagDatabaseManager getInstance() {
		return singleton;
	}

	/** 
	 * Return tag for given tagId
	 *  
	 * @param tagId 
	 * @param session 
	 * @return tag for given id
	 */
	public Tag getTagById(final Integer tagId, final DBSession session) {
		return this.queryForObject("getTagById", tagId, Tag.class, session);
	}

	/**
	 * Return all tags for a given tag count
	 * @param param 
	 * @param session 
	 * @return list of tags
	 */
	// FIXME a single tag should be returned instead of a list, shouldn't it?
	public List<Tag> getTagByCount(final TagParam param, final DBSession session) {
		// TODO not tested
		return this.queryForList("getTagByCount", param, Tag.class, session);
	}

	/**
	 * Return all tags for a given contentId
	 */
	/*private List<Tag> getTasByContendId(final TagParam param, final DBSession session) {
		// FIXME this query doesn't exist
		// return this.queryForList("getTasByTagName", param, Tag.class, session);
		return null;
	}*/

	// FIXME: unused
	/*private void updateTagTagInc(final TagParam param, final DBSession session) {
		// TODO not tested
		this.update("updateTagTagInc", param, session);
	}*/

	// FIXME: unused
	/*private void updateTagTagDec(Tag tagFirst, Tag tagSecond, TagParam param, final DBSession session) {
		param.setTag(tagFirst);
		param.setTag(tagSecond);
		this.update("updateTagTagDec", param, session);
	}*/

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

	/**
	NEW INSERTTAS (MULTIPLE GROUPS FOR A POST)
	 * @param param
	 * @param session
	 */
	public void insertTas(final TagParam param, final DBSession session) {
		for (final Tag tag : param.getTags()) {
			param.setTag(tag);
			/*
			 * if a post is visible for more than one group, 
			 * only insert an entry for the first group in the tas table.
			 * otherwise param.getGroups has length = 1.
			 */
			if(param.getGroups().get(0) != null){
				final Integer groupId = param.getGroups().get(0); 
				param.setGroupId(groupId);
				param.setTasId(generalDb.getNewContentId(ConstantID.IDS_TAS_ID, session));
				this.insert("insertTas", param, session);
			}
		}

	}

	/**
	 * OLD METHOD INSERTAS
	 * @param param
	 * @param session
	 **/
	/*public void insertTas(final TagParam param, final DBSession session) {
		for (final Tag tag : param.getTags()) {
			param.setTag(tag);
			for (final Integer groupId : param.getGroups()) {
				param.setGroupId(groupId);
				param.setTasId(generalDb.getNewContentId(ConstantID.IDS_TAS_ID, session));
				this.insert("insertTas", param, session);
			}
		}
	}*/


	/**
	 * For each group a post is visible, store an entry in the grouptas table.
	 * @param param
	 * @param session
	 */
	public void insertGroupTas(final TagParam param, final DBSession session) {
		for (final Tag tag : param.getTags()) {
			param.setTag(tag);
			for (final Integer groupId : param.getGroups()) {
				param.setGroupId(groupId);
				param.setTasId(generalDb.getNewContentId(ConstantID.IDS_GROUPTAS_ID, session));
				this.insert("insertGroupTas", param, session);
			}
		}
	}

	/**
	 * Deletes the tags from the given post.
	 * 
	 * @param post
	 * @param session
	 */
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
			/*
			 * Too much tags: batch the job
			 * 
			 * A note regarding tag batch processing: the batch table has four
			 * columns: content_id, tags, toinc and isactive - the batch
			 * processor first sets the "isactive" column of a row to TRUE (1)
			 * and then inserts all tags into the tagtag table, afterwards it
			 * deletes the row from the batch table
			 * 
			 * IMPORTANT: getting rows and then setting them to active has to be
			 * done in a transaction, otherwise they could get removed in
			 * between
			 */
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
		this.deleteTas(post.getContentId(), session);
		this.deleteGroupTas(post.getContentId(), session);
	}

	private void deleteTas(final Integer contentId, final DBSession session) {
		this.delete("deleteTas", contentId, session);
	}

	private void deleteGroupTas(final Integer contentId, final DBSession session) {
		this.delete("deleteGroupTas", contentId, session);
	}

	/**
	 * Inserts the tags from the given post.
	 * 
	 * @param post
	 * @param session
	 */
	public void insertTags(final Post<?> post, final DBSession session) {
		final TagParam tagParam = new TagParam();
		tagParam.setTags(post.getTags());
		tagParam.setNewContentId(post.getContentId());
		tagParam.setContentTypeByClass(post.getResource().getClass());
		tagParam.setUserName(post.getUser().getName());
		tagParam.setDate(post.getDate());
		tagParam.setDescription(post.getDescription());
		final List<Integer> groups = new ArrayList<Integer>();
		/*
		 * copy the groups' ids into the param
		 */
		for (final Group group : post.getGroups()) {
			groups.add(group.getGroupId());
		}
		tagParam.setGroups(groups);
		this.insertTags(tagParam, session);
	}

	/** Updates the posts by replacing all tags as described in {@link LogicInterface#updateTags(User, List, List)}.
	 * 
	 * 
	 * @param user
	 * @param tagsToReplace
	 * @param replacementTags
	 * @param session 
	 * @return The number of posts which got updated.
	 */
	public int updateTags(final User user, final List<Tag> tagsToReplace, final List<Tag> replacementTags, final DBSession session) {
		/*
		 * we might need the empty tag for posts where no tags remain ...
		 */
		final Tag emptyTag = TagUtils.getEmptyTag();
		/*
		 * First: get all posts which need to be updated (i.e., which have all tags from tagsToReplace assigne)
		 * since we're not interested in the resource, we need only data from the TAS table, i.e., we need TAS.
		 */
		final TagParam param = new TagParam();
		for (final Tag tag: tagsToReplace) {
			param.addTagName(tag.getName());
		}
		param.setUserName(user.getName());
		final List<Post> posts = this.queryForList("getTASByTagNames", param, Post.class, session);
		log.debug("################################################################################");
		log.debug(posts);
		log.debug("################################################################################");

		/*
		 * FIXME: shall getting the posts be included in the transaction?
		 */
		session.beginTransaction();
		try {

			/*
			 * iterate over all posts and exchange their tags
			 */
			for (final Post post: posts) {
				log.debug("handling post with content id " + post.getContentId() + " and groups " + post.getGroups());

				final Set<Tag> tags = post.getTags();
				log.debug("  current tags: " + tags);
				/*
				 * removing tags
				 * 
				 * TODO: Case is important here, e.g., "kassel" is not removed, 
				 * if "KASSEL" is contained in tagsToReplace.
				 * 
				 * Probably this is the way it should work - we have to discuss this.
				 * (Although, it might be nice to have a switch to say "ignore case".)
				 */
				tags.removeAll(tagsToReplace);
				/*
				 * adding tags
				 */
				tags.addAll(replacementTags);
				log.debug("  new tags: " + tags);
				/*
				 * Since replacementTags is allowed to be empty (i.e., to remove certain tags),
				 * we must check here, if the post still contains some tags. If not - we add 
				 * the empty tag.
				 */
				if (tags.isEmpty())	tags.add(emptyTag);
				/*
				 * Finally: delete the TAS and insert the new TAS.
				 */
				this.deleteTas(post.getContentId(), session);

				final TagParam tagParam = new TagParam();
				tagParam.setTags(post.getTags());
				tagParam.setNewContentId(post.getContentId());
				final Class<? extends Resource> class1 = post.getResource().getClass();
				log.debug("  post has class " + class1);
				tagParam.setContentTypeByClass(class1);
				tagParam.setUserName(post.getUser().getName());
				tagParam.setDate(post.getDate());
				/*
				 * FIXME: we don't have the groups from the grouptas available ... :-(
				 */
				final List<Integer> groups = new ArrayList<Integer>();
				final Set<Group> groups2 = post.getGroups();
				for (final Group group : groups2) {
					groups.add(group.getGroupId());
				}
				tagParam.setGroups(groups);

				this.insertTas(tagParam, session);
			}


			session.commitTransaction();
		} finally {
			session.endTransaction();
		}

		/*
		 * test: check tags
		 */
		final TagParam paramNew = new TagParam();
		for (final Tag tag: replacementTags) {
			paramNew.addTagName(tag.getName());
		}
		paramNew.setUserName(user.getName());
		final List<Post> postsNew = this.queryForList("getTASByTagNames", paramNew, Post.class, session);
		log.debug("################################################################################");
		log.debug(postsNew);
		log.debug("################################################################################");


		return 0;
	}

	/**
	 * Insert a set of tags for a content (into tas table and what else is
	 * required)
	 */
	private void insertTags(final TagParam param, final DBSession session) {
		// generate a list of tags
		final Collection<Tag> allTags = param.getTags();
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

			/* 
			 * if post is visible for a non exclusive group, store for each group
			 * and each tag one entry in the grouptas table 
			 */
			final int firstGroup = param.getGroups().iterator().next();
			if (!GroupUtils.isExclusiveGroup(firstGroup)) {
				/*
				 * first group found is neither public nor private ... so we have to fill the group tas table!
				 */
				this.insertGroupTas(param, session);
			}

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
	 * 
	 * @param tag 
	 * @param session 
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

	/**
	 * @param param
	 * @param session
	 * @return tag occurrences
	 */
	public int getTagOccurrences(final TagParam param, final DBSession session) {
		return this.queryForObject("getTagOccurrences", param, Integer.class, session);
	}

	/**
	 * @param param
	 * @param session
	 * @return list of sub tags
	 */
	public List<Tag> getSubtagsOfTag(final TagParam param, final DBSession session) {
		return this.queryForList("getSubtagsOfTag", param, Tag.class, session);
	}

	/**
	 * @param param
	 * @param session
	 * @return list of super tags
	 */
	public List<Tag> getSupertagsOfTag(final TagParam param, final DBSession session) {
		return this.queryForList("getSupertagsOfTag", param, Tag.class, session);
	}

	/**
	 * @param param
	 * @param session
	 * @return list of correlated tags
	 */
	public List<Tag> getCorrelatedTagsOfTag(final TagParam param, final DBSession session) {
		return this.queryForList("getCorrelatedTagsOfTag", param, Tag.class, session);
	}

	/**
	 * Returns all tags.
	 * 
	 * @param param
	 * @param session
	 * @return all tags
	 */
	public List<Tag> getAllTags(final TagParam param, final DBSession session) {
		return this.queryForList("getAllTags", param, Tag.class, session);
	}

	/**
	 * Return a tag by its tag name
	 * 
	 * @param param
	 * @param session
	 * @return tag
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
	 * @param param
	 * @param session
	 * @return the tag's details, null else
	 */
	public Tag getTagDetails(final TagParam param, final DBSession session) {
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

		// XXX: this is just a hack as long as we don't supply separate user
		// counts for each tag, DB
		if (tag != null) {
			tag.setUsercount(tag.getGlobalcount());
		}

		return tag;
	}

	/**
	 * Get all tags of a given user.
	 * 
	 * @param param 
	 * @param session 
	 * @return list of tags
	 */
	public List<Tag> getTagsByUser(final TagParam param, final DBSession session) {
		/* 
		 * another DBLP extra sausage - don't query DB for tags (as only "dblp"
		 * will be returned anyways), but return that directly
		 *
		 * TODO: maybe we put all the DBLP stuff into one class?
		 * Then we could do here:
		 * if (DBLP.isDBLPUser(param.getRequestedUserName()) {
		 *    return DBLP.getDBLPTag();
		 * }
		 */
		if ("dblp".equals(param.getRequestedUserName().toLowerCase())) {
			final ArrayList<Tag> tags = new ArrayList<Tag>();
			final Tag dblp = new Tag();
			dblp.setName("dblp");
			dblp.setGlobalcount(1000000);
			dblp.setUsercount(1000000);
			tags.add(dblp);
			return tags;
		}

		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByUser", param, Tag.class, session);
	}

	/**
	 * Get all tags of a an author, which assigned to the authors
	 * entries/currently, the cloud is ordered alphabetically.
	 * 
	 * @param param
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getTagsByAuthor(final TagParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		final long starttimeQuery = System.currentTimeMillis();
		List<Tag> retVal = this.queryForList("getTagsByAuthor", param, Tag.class, session);
		final long endtimeQuery = System.currentTimeMillis();
		log.debug("DB author tag cloud query time: " + (endtimeQuery-starttimeQuery) + " ms");
		
		return retVal;
	}

	/**
	 * Get all tags of a an author, which assigned to the authors
	 * 
	 * @param search
	 * @param groupId
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param limit
	 * @param offset
	 * @param simHash
	 * @param tagIndex
	 * @param session
	 * @return
	 */
	public List<Tag> getTagsByAuthorLucene(final String search, final int groupId, final String requestedUserName, final String requestedGroupName, final String year, final String firstYear, final String lastYear, final int simHash, final List<String> tagIndex, final DBSession session) {
		final List<Tag> retVal;
		
		if (present(authorSearch)) {
			final GroupDatabaseManager groupDb = GroupDatabaseManager.getInstance();
			String group = groupDb.getGroupNameByGroupId(groupId, session);
			
			final long starttimeQuery = System.currentTimeMillis();
			// FIXME: we arbitrarily choose a tag cloud limit of 1000
			retVal = authorSearch.getTagsByAuthor(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagIndex, 1000);
			final long endtimeQuery = System.currentTimeMillis();
			log.debug("Lucene author tag cloud query time: " + (endtimeQuery-starttimeQuery) + " ms");
		} else {
			retVal = new LinkedList<Tag>();
			log.error("No author searcher available.");
		}
			
		return retVal;
	}
	
	/**
	 * Get all tags of a given group
	 * 
	 * @param param
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getTagsByGroup(final TagParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		return this.queryForList("getTagsByGroup", param, Tag.class, session);
	}

	/**
	 * Get all tags of a given regular expression
	 * 
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
	 * 
	 * @param param
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getRelatedTagsForGroup(TagParam param, DBSession session) {
		// check maximum number of tags
		if (this.exceedsMaxSize(param.getTagIndex())) {
			return new ArrayList<Tag>();
		}		
		return this.queryForList("getRelatedTagsForGroup", param, Tag.class, session);
	}

	/**
	 * Tet related tags from a given user and a given list of tags.
	 * 
	 * @param userName 
	 * @param requestedUserName
	 * @param tagIndex
	 * @param visibleGroupIDs
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getRelatedTagsForUser(final String userName, final String requestedUserName, final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, int limit, int offset, final DBSession session) {
		final TagParam param = new TagParam();
		param.setUserName(userName);
		param.setRequestedUserName(requestedUserName);
		param.addGroups(visibleGroupIDs);
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.queryForList("getRelatedTagsRestricted", param, Tag.class, session);
	}

	/**
	 * Get related tags for a given tag.
	 * 
	 * @param param
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getRelatedTagsViewable(TagParam param, DBSession session) {
		// check maximum number of tags
		if (this.exceedsMaxSize(param.getTagIndex())) {
			return new ArrayList<Tag>();
		}
		if (GroupID.isSpecialGroupId(param.getGroupId()) == true) {
			// for special groups, check additionally if tag is "owned"
			// by the logged-in user
			param.setRequestedUserName(param.getUserName());
		}
		return this.queryForList("getRelatedTagsViewable", param, Tag.class, session);
	}

	/**
	 * Main function (called from the Logic Interface) to retrieve tags - this
	 * function actually starts the chain of responsibility
	 * 
	 * @param param
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getTags(final TagParam param, final DBSession session) {
		final List<Tag> tags = chain.getFirstElement().perform(param, session);
		return this.setUsercountToGlobalCount(tags);
	}

	/**
	 * This is just a hack as long as we don't supply separate user counts for
	 * each tag, dbe
	 * 
	 * @param tags
	 *            a list of tags
	 * @return list of tags with usercount set to globalcount for each tag
	 */
	private List<Tag> setUsercountToGlobalCount(final List<Tag> tags) {
		for (final Tag tag : tags) {
			if (tag.getUsercount() == 0) {
				tag.setUsercount(tag.getGlobalcount());
			}
		}
		return tags;
	}

	/**
	 * Helper function to print a list of tags to stdout
	 * 
	 * @param tags
	 */
	/*private void printTags(List<Tag> tags) {
		if (tags != null)
		for (final Tag tag : tags) {
			System.out.println(tag.getName());
		}
	}*/

	/**
	 * Retrieve related tags.
	 * 
	 * @param param
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getRelatedTags(TagParam param, DBSession session) {
		// check maximum number of tags
		if (this.exceedsMaxSize(param.getTagIndex())) {
			return new ArrayList<Tag>();
		}
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getRelatedTags", param, Tag.class, session);
	}

	/**
	 * Retrieve related tags orderey by folkrank.
	 * 
	 * @param param
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getRelatedTagsOrderedByFolkrank(TagParam param, DBSession session) {
		// check maximum number of tags
		if (this.exceedsMaxSize(param.getTagIndex())) {
			return new ArrayList<Tag>();
		}
		return this.queryForList("getRelatedTagsOrderedByFolkrank", param, Tag.class, session);
	}

	/**
	 * Retrieve tags attached to a bookmark with a given hash.
	 * 
	 * @param loginUserName
	 * @param hash
	 * @param visibleGroupIDs 
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of tags attached to the bookmark with the given hash
	 */
	public List<Tag> getTagsByBookmarkHash(final String loginUserName, final String hash, final List<Integer> visibleGroupIDs, int limit, int offset, final DBSession session) {
		final TagParam param = new TagParam();
		param.setHash(hash);
		param.setUserName(loginUserName);
		param.addGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByBookmarkHash", param, Tag.class, session);
	}

	/**
	 * Retrieve tags attached to a bookmark with a given hash for a given user.
	 * 
	 * @param loginUserName
	 * @param requestedUserName
	 * @param hash
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of tags attached to the given user's bookmark with the given hash
	 */
	public List<Tag> getTagsByBookmarkHashForUser(final String loginUserName, final String requestedUserName, final String hash, final List<Integer> visibleGroupIDs, int limit, int offset, final DBSession session) {
		final TagParam param = new TagParam();
		param.setHash(hash);
		param.setUserName(loginUserName);
		param.addGroups(visibleGroupIDs);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByBookmarkHash", param, Tag.class, session);
	}

	/**
	 * Retrieve tags attached to a bibtex with the given hash.
	 * 
	 * @param loginUserName
	 * @param hash
	 * @param hashId
	 * @param visibleGroupIDs 
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of tags attached to a bibtex with the given hash
	 */
	public List<Tag> getTagsByBibtexHash(final String loginUserName, final String hash, final HashID hashId, final List<Integer> visibleGroupIDs, int limit, int offset, final DBSession session) {
		final TagParam param = new TagParam();
		param.setHash(hash);
		param.setSimHash(hashId);
		param.setUserName(loginUserName);
		param.addGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByBibtexHash", param, Tag.class, session);
	}

	/**
	 * Retrieve tags attached to a bibtex of a given user with the given hash.
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
	public List<Tag> getTagsByBibtexHashForUser(final String loginUserName, final String requestedUserName, final String hash, final HashID hashId, final List<Integer> visibleGroupIDs, int limit, int offset, final DBSession session) {
		final TagParam param = new TagParam();
		param.setHash(hash);
		param.setSimHash(hashId);
		param.addGroups(visibleGroupIDs);		
		param.setUserName(loginUserName);
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		return this.queryForList("getTagsByBibtexHash", param, Tag.class, session);
	}	

	/**
	 * Helper function to check maximum number of tags for which related tags
	 * are to be computed.
	 * 
	 * @param index
	 * @return true if maximum number is exeeded, false otherwise
	 */
	private Boolean exceedsMaxSize(final List<TagIndex> index) {
		// FIXME: why don't we use a "private static final" variable here
		// instead of the "10"?
		return index != null && index.size() > 5;
	}

	/**
	 * Returns a list of similar tags.
	 * 
	 * @param tagIndex
	 * @param visibleGroupIDs
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of tags
	 */
	public List<Tag> getSimilarTags(final List<TagIndex> tagIndex, final List<Integer> visibleGroupIDs, int limit, int offset, final DBSession session) {
		final TagParam param = new TagParam();
		param.setTagName(tagIndex.get(0).getTagName()); // index 0 is always present, because otherwise the calling chain element won't answer
		param.setGroups(visibleGroupIDs);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.queryForList("getSimilarTags", param, Tag.class, session);
	} 

	/**
	 * See getAllTags
	 * 
	 * @param param
	 * @param session
	 * @return
	 */
	public List<Tag> getTagsPopular(final TagParam param, final DBSession session){
		return this.queryForList("getTagsPopular", param, Tag.class, session);
	}

	/**
	 * Gets list of global popular tags (no restriction in days)
	 * 
	 * @param param
	 * @param session
	 * @return list of popular tags
	 */
	public List<Tag> getPopularTags(final TagParam param, final DBSession session){
		return this.queryForList("getPopularTags", param, Tag.class, session);
	}

	/**
	 * @param param
	 * @param session
	 * @return list of tags from a given friend of a given user
	 */
	public List<Tag> getTagsByFriendOfUser(TagParam param, DBSession session) {
		return this.queryForList("getTagsByFriendOfUser", param, Tag.class, session);
	}
	
	/**
	 * Retrieve tags for a given bibtexkey
	 * 
	 * @param bibtexKey
	 * 			- the requested key
	 * @param visibleGroupIDs
	 * 			- the groups the logged-in user is allowed to see
	 * @param requestedUserName
	 * 			- retrieve only tags of this user  
	 * @param session
	 * 			- the DB session
	 * @param limit 
	 * @param offset
	 * @return a list of tags, used to annotate the bibtex(s) with the given bibtex key (eventually by the requested user)
	 */
	public List<Tag> getTagsByBibtexkey(final String bibtexKey, final List<Integer> visibleGroupIDs, final String requestedUserName, final String loginUserName, final int limit, final int offset, DBSession session) {
		TagParam param = new TagParam();
		param.setBibtexKey(bibtexKey);
		param.setGroups(visibleGroupIDs);
		param.setUserName(loginUserName);		
		param.setRequestedUserName(requestedUserName);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.queryForList("getTagsByBibtexkey", param, Tag.class, session);
	}
	
	/**
	 * Retrieve tags from a resource of a specific author tagged with a tag
	 * @param param
	 * @param session
	 * @return a list of tags
	 */
	public List<Tag> getRelatedTagsByAuthorAndTag(final TagParam param, final DBSession session){
		return this.queryForList("getRelatedTagsByAuthorAndTag", param, Tag.class, session);
	}

	public void setAuthorSearch(ResourceSearch<BibTex> authorSearch) {
		this.authorSearch = authorSearch;
	}

	public ResourceSearch<BibTex> getAuthorSearch() {
		return authorSearch;
	}
	
}