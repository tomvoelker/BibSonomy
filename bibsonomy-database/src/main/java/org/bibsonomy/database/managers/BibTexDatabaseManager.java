package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.ScraperMetadata;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * Used to create, read, update and delete BibTexs from the database.
 * 
 * FIXME: why do some methods use loginUserName and some methods not? Shouldn't all methods
 * need loginUserName?
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @author Daniel Zoller
 * 
 * @version $Id$
 */
public class BibTexDatabaseManager extends PostDatabaseManager<BibTex, BibTexParam> {
	private static final Log log = LogFactory.getLog(BibTexDatabaseManager.class);
	
	private static final BibTexDatabaseManager singleton = new BibTexDatabaseManager();
	
	private static final HashID[] hashRange = HashID.getAllHashIDs();
	private static final FirstChainElement<Post<BibTex>, BibTexParam> chain = new BibTexChain();
	
	/**
	 * @return BibTexDatabaseManager
	 */
	public static BibTexDatabaseManager getInstance() {
		return singleton;
	}
	
	/** database manager */
	private final BibTexExtraDatabaseManager extraDb;
	private final DocumentDatabaseManager docDb;
	
	private BibTexDatabaseManager() {
		this.docDb = DocumentDatabaseManager.getInstance();
		this.extraDb = BibTexExtraDatabaseManager.getInstance();
	}
	
	/*
	 * FIXME: fix sql statment in BibTex.xml for getBibTexSearchForGroup
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getPostsSearchForGroup(int, java.util.List, java.lang.String, java.lang.String, int, int, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	public List<Post<BibTex>> getPostsSearchForGroup(final int groupId, List<Integer> visibleGroups, final String search, final String requestedUserName, final int limit, final int offset, Collection<SystemTag> systemTags, final DBSession session) {
		/*
		 * do lucene search
		 */
		if (this.isDoLuceneSearch()) {
			return super.getPostsSearchForGroup(groupId, visibleGroups, search, requestedUserName, limit, offset, systemTags, session);
		}
		/*
		 * do database search
		 */
		final BibTexParam param = this.createParam(null, requestedUserName, limit, offset);
		param.setGroupId(groupId);
		param.setSearch(search);

		return this.postList("getBibTexSearch", param, session);
	}
	
	/**
	 * TODO: check method
	 * 
	 * @param groupId
	 * @param tagIndex
	 * @param simHash
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getPostsViewableByTag(final int groupId, final List<TagIndex> tagIndex, final HashID simHash, final int limit, final int offset, final DBSession session) {
		final BibTexParam param = this.createParam(limit, offset);
		param.setGroupId(groupId);
		param.setTagIndex(tagIndex);
		param.setSimHash(simHash);
		
		if (GroupID.isSpecialGroupId(param.getGroupId())) {
			// show users own bookmarks, which are private, public or for friends
			param.setRequestedUserName(param.getUserName());
			return this.getPostsByTagNamesForUser(param, session);
		}
		
		return this.postList("getBibTexViewableByTag", param, session);
	}
	
	
	/**
	 * Prepares a query which returns all BibTex posts with the provided title.
	 * 
	 * @param title
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getPostsByTitle(final String title, final int limit, final int offset, final DBSession session){
		final BibTexParam param = this.createParam(limit, offset);
		param.setTitle(title);
		param.setGrouping(GroupingEntity.ALL);
		return this.postList("getBibTexByTitle", param, session);
	}
	
	/**
	 * TODO: document me
	 * 
	 * @param search
	 * @param groupId
	 * @param requestedUserName
	 * @param userName 
	 * @param requestedGroupName 
	 * @param limit
	 * @param offset
	 * @param session
	 * @return list of publication entries
	 */
	public List<Post<BibTex>> getPostsByTitleLucene(final String search, final int groupId, final String requestedUserName, final String userName, final Set<String> requestedGroupName, final int limit, final int offset, final DBSession session) {
		final ResourceSearch<BibTex> resourceSearch = this.getResourceSearch();
		if (present(resourceSearch)) {
			final GroupDatabaseManager groupDb = GroupDatabaseManager.getInstance();
			String group = groupDb.getGroupNameByGroupId(groupId, session);
			
			final long starttimeQuery = System.currentTimeMillis();
			//String group, String searchTerms, String requestedUserName, String UserName, Set<String> GroupNames, int limit, int offset
			final List<Post<BibTex>> posts = resourceSearch.getPostsByTitle(group, search, requestedUserName, userName, requestedGroupName, limit, offset);

			final long endtimeQuery = System.currentTimeMillis();
			log.debug("LuceneBibTex complete query time: " + (endtimeQuery-starttimeQuery) + "ms");
			return posts;
		}
		
		log.error("No resource searcher available.");	
		return new ResultList<Post<BibTex>>();
	}

	/**
	 * Prepares a query which returns all duplicate BibTex posts of the
	 * requested user. Duplicates are BibTex posts which have the same simhash1,
	 * but a different simhash0 (the latter is always true within the posts of a
	 * single user).
	 * 
	 * @param requestedUserName
	 * @param visibleGroupIDs
	 * @param simHash
	 * @param session
	 * @param systemTags
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getPostsDuplicate(final String requestedUserName, final List<Integer> visibleGroupIDs, final HashID simHash, final DBSession session, Collection<SystemTag> systemTags) {
		final BibTexParam param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);
		param.setGroups(visibleGroupIDs);
		param.setSimHash(simHash);
		param.addAllToSystemTags(systemTags);
		
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.postList("getBibTexDuplicate", param, session);
	}
	
	/**
	 * TODO: check method
	 * 
	 * Returns the number of duplicates (i.e. BibTex posts) of a given user.
	 * 
	 * @param requestedUserName
	 * @param session
	 * @return number of duplicates
	 */
	public Integer getPostsDuplicateCount(final String requestedUserName, final DBSession session) {
		final BibTexParam param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);
		
		final Integer result = this.queryForObject("getBibTexDuplicateCount", param, Integer.class, session);
		return present(result) ? result : 0;
	}
	
	/**
	 * adds document retrieval to {@link PostDatabaseManager#getPostsForUser(ResourceParam, DBSession)}
	 */
	@Override
	protected List<Post<BibTex>> getPostsForUser(final BibTexParam param, final DBSession session) {		
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		
		// document retrieval
		final FilterEntity filter = param.getFilter();
		if (present(filter)) {
			switch (filter) {
				case JUST_PDF:
					// retrieve only entries with a document attached
					return this.postList("getJustBibTexForUserWithPDF", param, session);
				case DUPLICATES:
					// retrieve duplicate entries
					return this.getPostsDuplicate(param.getRequestedUserName(), param.getGroups(), HashID.getSimHash(param.getSimHash()), session, null);
				case POSTS_WITH_DOCUMENTS:
					// posts including documents
					return this.postList("getBibTexForUserWithPDF", param, session);
				case JUST_POSTS:
					return super.getPostsForUser(param, session);
				default:
					throw new IllegalArgumentException("Filter " + filter.name() + " not supported");
			}
		}
		
		// posts only
		return super.getPostsForUser(param, session);
	}
	
	/**
	 * adds document retrieval to {@link PostDatabaseManager#getPostsForGroup(ResourceParam, DBSession)}
	 */
	@Override
	protected List<Post<BibTex>> getPostsByTagNamesForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		HashID.getSimHash(param.getSimHash()); // ensures correct simHash is set (exception would be thrown otherwise)
		
		// if user wants to retrieve documents
		final FilterEntity filter = param.getFilter();
		if (present(filter)) {
			switch (filter) {
				case JUST_PDF:
					return this.postList("getJustBibTexByTagNamesForUserWithPDF", param, session);
				case POSTS_WITH_DOCUMENTS:
					// posts including documents
					return this.postList("getBibTexByTagNamesForUserWithPDF", param, session);
				case JUST_POSTS:
					return super.getPostsByTagNamesForUser(param, session);
				default: 
					throw new IllegalArgumentException("Filter " + filter.name() + " not supported");
			}
		}
		
		// posts only
		return super.getPostsByTagNamesForUser(param, session);
	}
	
	/**
	 * adds document retrieval to {@link PostDatabaseManager#getPostsForGroupByTag(ResourceParam, DBSession)}
	 */
	@Override
	protected List<Post<BibTex>> getPostsForGroupByTag(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		
		final FilterEntity filter = param.getFilter();
		// if user wants to retrieve documents
		if (present(filter)) {
			switch (filter) {
				case JUST_PDF:
					return this.postList("getJustBibTexForGroupByTagWithPDF", param, session);
				case POSTS_WITH_DOCUMENTS:
					return this.postList("getBibTexForGroupByTagWithPDF", param, session);
				case JUST_POSTS:
					return super.getPostsByTagNamesForUser(param, session);
				default:
					throw new IllegalArgumentException("Filter " + filter.name() + " not supported");
			}
		}
		
		// posts only
		return super.getPostsForGroupByTag(param, session);
	}
	
	/**
	 * adds document retrieval to {@link PostDatabaseManager#getPostsForGroup(ResourceParam, DBSession)}
	 */
	@Override
	protected List<Post<BibTex>> getPostsForGroup(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);
		// document retrieval
		final FilterEntity filter = param.getFilter();
		if (present(filter)) {
			switch(filter) {
				case JUST_PDF:
					// just entries with document attached
					return this.postList("getJustBibTexForGroupWithPDF", param, session);
				case POSTS_WITH_DOCUMENTS:
					// posts including documents
					return this.postList("getBibTexForGroupWithPDF", param, session);
				case JUST_POSTS:
					return super.getPostsForGroup(param, session);
				default:
					throw new IllegalArgumentException("Filter " + filter.name() + " not supported");
			}
		}
		
		// posts only
		return super.getPostsForGroup(param, session);
	}
	
	private List<Post<BibTex>> getLoggedPostsByHashForUser(final String loginUserName, final String intraHash, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session, final HashID hashType) {
		final BibTexParam param = this.createParam(loginUserName, requestedUserName);
		param.addGroups(visibleGroupIDs);
		param.setHash(intraHash);
		param.setSimHash(hashType);
		
		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);		
		return this.postList("getLoggedHashesByHashForUser", param, session);
	}
	
	/** 
	 * <em>/author/MaxMustermann</em><br/><br/>
	 * This method prepares queries which retrieve all publications for a given
	 * author name (restricted by group public).
	 * 
	 * @param search
	 * @param groupId
	 * @param requestedUserName
	 * @param requestedGroupName 
	 * @param limit
	 * @param offset
	 * @param systemTags
	 * @param session
	 * @return list of bibtex entries
	 */
	public List<Post<BibTex>> getPostsByAuthor(final String search, final int groupId, final String requestedUserName, final String requestedGroupName, final int limit, final int offset, final Collection<SystemTag> systemTags, final DBSession session){
		final BibTexParam param = this.createParam(null, requestedUserName, limit, offset);
		param.setSearch(search);
		param.setGroupId(groupId);
		param.setRequestedGroupName(requestedGroupName);
		param.addAllToSystemTags(systemTags);
		param.setSimHash(HashID.INTER_HASH);
		
		return this.postList("getBibTexByAuthor", param, session);
	}

	/**
	 * TODO: replace param firstYear LastYear Year with a SystemTag
	 * TODO: improve doc
	 * FIXME: check method
	 * TODO: move time logging into Lucene!? @see {@link PostDatabaseManager#getPostsSearchLucene(int, String, String, String, java.util.Set, int, int, DBSession)}
	 * 
	 * FIXME: align method signature with {@link #getPostsByAuthor(String, int, String, String, int, int, Collection, DBSession)}
	 * FIXME: remove tagIndex param
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
	 * @return list of bibtex entries
	 */
	public List<Post<BibTex>> getPostsByAuthorLucene(final String search, final int groupId, final String requestedUserName, final String requestedGroupName, final String year, final String firstYear, final String lastYear, final int limit, final int offset, final int simHash, final List<String> tagIndex, final DBSession session){
		final ResultList<Post<BibTex>> postBibtexList;
		final ResourceSearch<BibTex> resourceSearch = this.getResourceSearch();
		if (present(resourceSearch)) {
			final GroupDatabaseManager groupDb = GroupDatabaseManager.getInstance();
			String group = groupDb.getGroupNameByGroupId(groupId, session);
			
			final long starttimeQuery = System.currentTimeMillis();

			postBibtexList = resourceSearch.searchAuthor(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagIndex, limit, offset);

			final long endtimeQuery = System.currentTimeMillis();
			log.debug("LuceneBibTex complete query time: " + (endtimeQuery-starttimeQuery) + "ms");
		} else {
			postBibtexList = new ResultList<Post<BibTex>>();
			log.error("No resource searcher available.");
		}
			
		return postBibtexList;
	}
    
    /** 
     * <em>/author/MaxMustermann</em><br/><br/>
	 * This method prepares queries which retrieve all publications for a given
	 * author name and TagName(restricted by group public).
	 * 
     * @param search
     * @param groupId
     * @param requestedUserName 
     * @param requestedGroupName 
     * @param tagIndex
     * @param limit
     * @param offset
     * @param systemTags
     * @param session
     * @return list of bibtex entries
     */
    public List<Post<BibTex>> getPostsByAuthorAndTag(final String search, final int groupId, final String requestedUserName, final String requestedGroupName, final List<TagIndex> tagIndex, final int limit, final int offset, final Collection<SystemTag> systemTags, final DBSession session){
		final BibTexParam param = this.createParam(null, requestedUserName, limit, offset);
		param.setSearch(search);
		param.setGroupId(groupId);
		param.setRequestedGroupName(requestedGroupName);
		param.setTagIndex(tagIndex);
		param.setSimHash(HashID.INTER_HASH);
		param.addAllToSystemTags(systemTags);
		
		return this.postList("getBibTexByAuthorAndTag", param, session);
	}
	
	/** 
	 * <em>/bibtexkey/KEY</em> Returns a list of bibtex posts for a given
	 * bibtexKey
	 * 
	 * @param bibtexKey 
	 * @param requestedUserName 
	 * @param groupId 
	 * @param limit 
	 * @param offset 
	 * @param systemTags
	 * @param session	a database session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getPostsByKey(final String bibtexKey, final String requestedUserName, final int groupId, final int limit, final int offset, final Collection<SystemTag> systemTags, final DBSession session) {
		final BibTexParam param = this.createParam(requestedUserName, requestedUserName, limit, offset);
		param.setBibtexKey(bibtexKey);
		param.setGroupId(groupId);
		param.addAllToSystemTags(systemTags);
		
		return this.postList("getBibTexByKey",param,session);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getPosts(org.bibsonomy.database.params.ResourcesParam, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	public List<Post<BibTex>> getPosts(final BibTexParam param, final DBSession session) {
		final List<Post<BibTex>> posts = super.getPosts(param, session);
		
		if (posts != null) {
			for (final Post<BibTex> post : posts) {
				if (post.getUser().getName().equals(param.getUserName())) {
					post.getResource().setPrivnote(extraDb.getBibTexPrivnoteForUser(post.getResource().getIntraHash(), param.getUserName(), session));
				}
			}
		}
		
		return posts;
	}
	
	/**
	 * Gets the details of a post, including all extra data like documents, extra urls and private notes
	 * given the INTRA-HASH of the post and the user name.
	 * 
	 * <ul>
	 * <li>extra URLs</li>
	 * <li>private notes (if userName = loginUserName)</li>
	 * <li>private PDFs (if requirements are met)<li>
	 * </ul>
	 * 
	 */
	@Override
	public Post<BibTex> getPostDetails(final String authUser, final String resourceHash, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) {
		// get post from database
		final Post<BibTex> post = super.getPostDetails(authUser, resourceHash, userName, visibleGroupIDs, session);
		
		if (present(post)) {
			if (this.permissionDb.isAllowedToAccessPostsDocuments(userName, post, session)) {
				post.getResource().setDocuments(this.docDb.getDocumentsForPost(userName, resourceHash, session));
			}
			
			// add private note
			if (authUser != null && authUser.equalsIgnoreCase(userName)) {
				post.getResource().setPrivnote(extraDb.getBibTexPrivnoteForUser(resourceHash, userName, session));
			}
			
			// add extra URLs
			post.getResource().setExtraUrls(extraDb.getURL(resourceHash, userName, session));
			
			return post;
		}
		
		/*
		 * post null => not found => second try: look into logging table
		 */
		final List<Post<BibTex>> loggedList = this.getLoggedPostsByHashForUser(authUser, resourceHash, userName, visibleGroupIDs, session, HashID.INTRA_HASH);
		if (present(loggedList)) {
			if (loggedList.size() > 1) {
				// user has multiple posts with the same hash
				log.warn("multiple logged BibTeX-posts from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' found ->returning first");
			}
			/*
			 * Resource has been changed and thus could be found in logging table. We send back the new resource hash. 
			 */
			final Post<BibTex> loggedPost = loggedList.get(0);
			if (!resourceHash.equals(loggedPost.getResource().getIntraHash())) {
				/*
				 * TODO: quick fix to break loops when the hash has not changed.
				 * This does not help in the case of more complex change patterns 
				 * (e.g., A -> B -> A). To fix that, we have to respect the 
				 * date given to the exception and implement a query which 
				 * returns the latest post with given hash+username whose 
				 * posting date is smaller or equal to the given date.  
				 */
				throw new ResourceMovedException(resourceHash, loggedPost.getResource().getIntraHash(), userName, loggedPost.getDate());
			}
		}
		
		return null;
	}
	
	/* TODO: check implementation
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#insertPost(org.bibsonomy.database.params.ResourcesParam, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void insertPost(BibTexParam param, DBSession session) {
		/*
		 * store scraper meta data
		 */
		final ScraperMetadata scraperMetadata = param.getResource().getScraperMetadata();
		if (present(scraperMetadata)) {
			session.beginTransaction();
			try {
				/*
				 * get a scraper id
				 */
				final int id = this.generalDb.getNewContentId(ConstantID.IDS_SCRAPER_METADATA, session);
				/*
				 * store id in metadata
				 */
				scraperMetadata.setId(id);
				/*
				 * store the metadata
				 */
				insertScraperMetadata(scraperMetadata, session);
				/*
				 * store the id in the post
				 */
				param.getResource().setScraperId(id);
				session.commitTransaction();
			} finally {
				session.endTransaction();
			}
		}

		/*
		 * store the post
		 */
		super.insertPost(param, session); // insert post and update/insert hashes
	}
	
	private void insertScraperMetadata(final ScraperMetadata scraperMetadata, final DBSession session) {
		this.insert("insertScraperMetadata", scraperMetadata, session);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#onPostUpdate(java.lang.Integer, java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostUpdate(Integer oldContentId, Integer newContentId, DBSession session) {
		this.plugins.onBibTexUpdate(oldContentId, newContentId, session);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#onPostDelete(java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostDelete(Integer contentId, DBSession session) {
		this.plugins.onBibTexDelete(contentId, session);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getChain()
	 */
	@Override
	protected FirstChainElement<Post<BibTex>, BibTexParam> getChain() {
		return chain;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getHashRange()
	 */
	@Override
	protected HashID[] getHashRange() {
		return hashRange;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getInsertParam(org.bibsonomy.model.Post, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected BibTexParam getInsertParam(Post<? extends BibTex> post, DBSession session) {
		final BibTexParam insert = this.getNewParam();
		insert.setResource(post.getResource());
		insert.setRequestedContentId(post.getContentId());
		insert.setDescription(post.getDescription());
		insert.setDate(post.getDate());
		insert.setUserName(((post.getUser() != null) ? post.getUser().getName() : ""));
		
		// in field group in table bibtex, insert the id for PUBLIC, PRIVATE or the id of the FIRST group in list
		final int groupId = post.getGroups().iterator().next().getGroupId();
		insert.setGroupId(groupId);
		
		return insert;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getNewParam()
	 */
	@Override
	protected BibTexParam getNewParam() {
		return new BibTexParam();
	}
}