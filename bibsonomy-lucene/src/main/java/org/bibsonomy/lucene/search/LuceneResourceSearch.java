package org.bibsonomy.lucene.search;

import static org.apache.lucene.util.Version.LUCENE_24;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.lucene.search.collector.TagCountCollector;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LuceneResourceConverter;
import org.bibsonomy.lucene.util.Utils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.bibsonomy.util.ValidationUtils;

/**
 * abstract parent class for lucene search
 * 
 * @author fei
 *
 * @param <R> resource type
 */
public abstract class LuceneResourceSearch<R extends Resource> extends LuceneBase implements ResourceSearch<R> {
	private static final Logger log = Logger.getLogger(LuceneResourceSearch.class);
	
	/**
	 *  read/write lock, allowing multiple searcher or exclusive an index update
	 *	TODO: we should use an implementation, which prefers writers for obtaining the lock 
	 */
	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	/** write lock, used for blocking  index searcher */
	private Lock w = lock.writeLock();
	/** read lock, used for blocking the index update */
	private Lock r = lock.readLock();
	
	/** logic interface for retrieving data from bibsonomy */
	private LuceneDBInterface<R> dbLogic;
	
	/** known resource types */
	List<Class<? extends Resource>> resourceTypes = new LinkedList<Class<? extends Resource>>();

	/** path to the managed resource index */
	protected String luceneIndexPath;
	
	/** global reference to the lucene searcher */
	protected IndexSearcher searcher; 

	/** default field analyzer */
	private Analyzer analyzer; 
	
	/** flag indicating whether the index was loaded successfully */
	private boolean isReady = false;
	
	/** default junction of search terms */
	private Operator defaultSearchTermJunctor = null;
	
	/** post model converter */
	private LuceneResourceConverter<R> resourceConverter;
	
	/** id for identifying redundant resource indeces */
	private int indexId;
	
	/**
	 * constructor
	 */
	public LuceneResourceSearch() {
		this.defaultSearchTermJunctor = Operator.AND;
	}
	
	//------------------------------------------------------------------------
	// search interface
	//------------------------------------------------------------------------

	/**
	 * TODO: document me
	 *  
	 * @param group
	 * @param searchTerms
	 * @param requestedUserName
	 * @param UserName
	 * @param GroupNames
	 * @param limit
	 * @param offset
	 * @return
	 */
	public ResultList<Post<R>> searchPosts(String group,
			String searchTerms, String requestedUserName, String UserName,
			Set<String> GroupNames, int limit, int offset) {
		// build query
		QuerySortContainer fullTextQuery = buildFulltextQuery(group, searchTerms, requestedUserName, UserName, GroupNames);
		// perform search query
		return doSearch(fullTextQuery, limit, offset);
	}

	
	/**
	 * TODO: document me
	 * 
	 * @param group
	 * @param search
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param tagList
	 * @param limit
	 * @param offset
	 * @return
	 */
	public ResultList<Post<R>> searchAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList, int limit,
			int offset) {
		// build query
		QuerySortContainer authorQuery = buildAuthorQuery(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagList);
		// perform search query
		return this.doSearch(authorQuery, limit, offset);
	}
	
	/**
	 * <em>/search/ein+lustiger+satz+group%3AmyGroup</em><br/><br/>
	 * 
	 * @param groupId group to search
	 * @param visibleGroupIDs groups the user has access to
	 * @param search the search query
	 * @param userName
	 * @param limit number of posts to display
	 * @param offset first post in the result list
	 * @param systemTags NOT IMPLEMENTED 
	 * @return
	 */
	public ResultList<Post<R>> searchGroup(
			final int groupId, final List<Integer> visibleGroupIDs, 
			final String search, final String authUserName, 
			final int limit, final int offset, 
			Collection<? extends Tag> systemTags) {
		//--------------------------------------------------------------------
		// query bibsonomy's database for missing data
		//--------------------------------------------------------------------
		String groupName = this.dbLogic.getGroupNameByGroupId(groupId);
		
		// FIXME: didn't the chain's param object already contained the group name?
		//        if so, we should consider passing them to this function
		List<String> visibleGroupNames = new LinkedList<String>();
		for( Integer gid : visibleGroupIDs) {
			visibleGroupNames.add(this.dbLogic.getGroupNameByGroupId(gid));
		}
		
		// get given groups members
		List<String> groupMembers = this.dbLogic.getGroupMembersByGroupId(groupId);
		
		// get all members of the given group, which have the user as a friend
		List<String> userGroupFriends = this.dbLogic.getGroupFriendsByGroupIdForUser(groupId, authUserName);
		
		//--------------------------------------------------------------------
		// perform search query
		//--------------------------------------------------------------------
		QuerySortContainer groupSearchQuery = buildGroupSearchQuery(groupName, visibleGroupNames, userGroupFriends, groupMembers, search, authUserName, limit, offset, systemTags);
		return this.doSearch(groupSearchQuery, limit, offset);
	}

	/**
	 * get list of posts whose title contains a word with a given prefix
	 * 
	 * @param group in what group's to search for (String)
	 * @param searchTerms the search query
	 * @param requestedUserName
	 * @param UserName
	 * @param GroupNames groups to include
	 * @param limit number of posts to display
	 * @param offset first post in the result list
	 * @return
	 */
	public ResultList<Post<R>> getPostsByTitle(String group,
			String searchTerms, String requestedUserName, String UserName,
			Set<String> GroupNames, int limit, int offset) {
		// build search query
		QuerySortContainer titleQuery = buildTitleQuery(group, searchTerms, requestedUserName, UserName, GroupNames);
		// perform search 
		return this.doSearch(titleQuery, limit, offset);
	}	
	
	/**
	 * get tag cloud for given author
	 * 
	 * @param group
	 * @param search
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param tagList
	 * @param limit
	 * @return
	 */
	public List<Tag> getTagsByAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList) {
		// build query
		QuerySortContainer qf = buildAuthorQuery(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagList);
		// query index
		return doTagCollection(qf);
	}
	

	/**
	 * get all tags assigned to resources relevant to given search terms
	 * 
	 * @param group
	 * @param searchTerms
	 * @param requestedUserName
	 * @param UserName
	 * @param GroupNames
	 * @param limit
	 * @param offset
	 * @return
	 */
	public List<Tag> getTagsBySearchString(String group, String searchTerms,
			String requestedUserName, String UserName, Set<String> GroupNames) {
		// build query
		// FIXME: the tagparam's semantic is a bit special:
		//        tagOrder == FREQENCY means that top x popular tags should be returned
		//        tagOrder == null means, that tags are filtered by minFreq
		QuerySortContainer qf = buildFulltextQuery(group, searchTerms, requestedUserName, UserName, GroupNames);
		// collect tags
		return doTagCollection(qf);
	}
	
	//------------------------------------------------------------------------
	// abstract interface
	//------------------------------------------------------------------------

	/**
	 * create empty collection of managed post objects
	 * @return
	 */
	protected abstract ResultList<Post<R>> createEmptyResultList();

	/**
	 * get managed resource type
	 */
	protected abstract Class<? extends Resource> getResourceType();
	

	//------------------------------------------------------------------------
	// management interface
	//------------------------------------------------------------------------
	/**
	 * initialize internal data structures
	 */
	private void init() {
		LuceneBase.initRuntimeConfiguration();
		this.luceneIndexPath = getIndexBasePath()+CFG_LUCENE_INDEX_PREFIX+getResourceName();
	}
	
	/** reload the index -- has to be called after each index change */
	public void reloadIndex(int indexId) {
		this.setIndexId(indexId);
		//--------------------------------------------------------------------
		// open new index searcher
		//--------------------------------------------------------------------
		IndexSearcher newSearcher = null;
		init();
		try {
			// load and hold index on physical hard disk
			log.debug("Opening index " + indexId);
			String indexPath = luceneIndexPath+CFG_INDEX_ID_DELIMITER+indexId;
			newSearcher = new IndexSearcher(FSDirectory.open(new File(indexPath)));
		} catch (Exception e) {
			log.error("Error reloading index, disabling searcher ("+e.getMessage()+") - this should be the case while building a new index");
		}
 		
		//--------------------------------------------------------------------
		// switch searcher
		//--------------------------------------------------------------------
		IndexSearcher oldSearcher = null;
		w.lock();
		try {
			if( newSearcher==null ) {
				disableIndex();
			} else {
				oldSearcher = this.searcher;
				this.searcher = newSearcher;
				enableIndex();
			}
		} finally {
			w.unlock();
		}
		
		//--------------------------------------------------------------------
		// close old searcher
		//--------------------------------------------------------------------
		try {
			if( oldSearcher!=null ) 
				oldSearcher.close();
		} catch (IOException e) {
			log.debug("Error closing searcher.", e);
		}

	}

	public LuceneIndexStatistics getStatistics() {
		return Utils.getStatistics(luceneIndexPath);
	}	
	
	//------------------------------------------------------------------------
	// private helper
	//------------------------------------------------------------------------
	/**
	 * perform given query, respecting read/write locks
	 * 
	 * @param limit
	 * @param offset
	 * @param query
	 * @return
	 */
	private ResultList<Post<R>> doSearch(QuerySortContainer query, int limit, int offset) {
		ResultList<Post<R>> retVal = null;
		r.lock();
		try {
			if( isEnabled() ) {
				retVal = searchLucene(query, limit, offset);
			} else
				retVal = createEmptyResultList();
		} finally {
			r.unlock();
		}

		// all done.
		return retVal;
	}

	/**
	 * query index for documents and create result list of post models 
	 */
	protected ResultList<Post<R>> searchLucene(QuerySortContainer qf, int limit, int offset) {
		// initialize data structures
		ResultList<Post<R>> postList = createEmptyResultList();
		
		Query query = qf.getQuery();
		Sort  sort  = qf.getSort();
		log.debug("Querystring:  "+ query.toString() + " sorted by: "+ sort);

		try {
			//----------------------------------------------------------------
			// querying the index
			//----------------------------------------------------------------
			int hitslimit = 0;
			long starttimeQuery = System.currentTimeMillis();
			final TopDocs topDocs = searcher.search(query, null, offset+limit, sort);
			// determine number of posts to display
			hitslimit = (((offset+limit) < topDocs.totalHits) ? (offset+limit) : topDocs.totalHits);
			log.debug("offset / limit / hitslimit / hits.length():  "
					+ offset + " / " + limit + " / " + hitslimit + " / " + topDocs.totalHits);
			long endtimeQuery = System.currentTimeMillis();
			log.debug("Query time: " + (endtimeQuery - starttimeQuery) + "ms");
			

			postList.setTotalCount(topDocs.totalHits);

			//----------------------------------------------------------------
			// extract posts
			//----------------------------------------------------------------
			for (int i = offset; i < hitslimit; i++) {
				// get document from index
				Document       doc  = searcher.doc(topDocs.scoreDocs[i].doc);
				// convert document to bibsonomy post model
				Post<R> post = this.resourceConverter.writePost(doc); 
				
				// set post frequency
				starttimeQuery = System.currentTimeMillis();
				int postFreq = 1;
				if( doc.get(FLD_INTERHASH)!=null ) {
					postFreq = this.searcher.docFreq(new Term(FLD_INTERHASH, doc.get(FLD_INTERHASH)));
				}
				endtimeQuery = System.currentTimeMillis();
				log.debug("PostFreq query time: " + (endtimeQuery - starttimeQuery) + "ms");
				post.getResource().setCount(postFreq);
				
				postList.add(post);
			}

		} catch (IOException e) {
			log.debug("LuceneBibTex: IOException: " + e.getMessage());
		}
		return postList;
	}	

	
	/**
	 * collect all tags assigned to relevant documents
	 *  
	 */
	private List<Tag> doTagCollection(QuerySortContainer qf) {
		List<Tag> retVal = null;
		
		r.lock();
		try {
			if( isEnabled() ) {
				// gather tags used by the author's posts
				TagCountCollector tagCollector = qf.getTagCountCollector();
				if( tagCollector!=null ) {
					try {
						searcher.search(qf.getQuery(), null, tagCollector);
						retVal = tagCollector.getTags(searcher);
					} catch (IOException e) {
						log.error("Error building full text tag cloud for query " + qf.getQuery().toString());
					}
				}
			};
			
			if( retVal==null )
				retVal = new LinkedList<Tag>();
		} finally {
			r.unlock();
		}
		
		// all done.
		return retVal;
	}
	
	/**
	 * check whether index is ready for searching
	 */
	private boolean isEnabled() {
		return this.isReady;
	}

	/** 
	 * disable search  
	 */
	private void disableIndex() {
		this.isReady = false;
	}

	/** 
	 * enable search  
	 */
	private void enableIndex() {
		this.isReady = true;
	}
	
	/**
	 * construct lucene query filter for full text search 'search:all' and 'search:username':
	 * 
	 * (mergedfields:searchTerms OR (privatefields:searchTerms AND user:userName)) 
	 *   [AND user_name:requestedUsername]
	 *    AND ( 
	 *          group:allowedGroup_1 OR ... OR group:allowedGroup_n 
	 *          OR (group:private AND user:userName)
	 *        )  
	 *        
	 * FIXME: merge buildFulltextQuery and buildGroupSearchQuery
	 * 
	 * @param group group name from which posts should be searched
	 * @param searchTerms search query
	 * @param requestedUserName user name from whom posts should be searched
	 * @param userName login user name
	 * @param allowedGroups groups which the login user is member of
	 * 
	 * @return
	 */
	protected QuerySortContainer buildFulltextQuery(String group, String searchTerms, String requestedUserName, String userName, Set<String> allowedGroups) {
		// FIXME: configure this
		//	String orderBy = "relevance"; 
		String orderBy = FLD_DATE; 
		
		BooleanQuery mainQuery       = new BooleanQuery();
		BooleanQuery searchQuery     = new BooleanQuery();
		BooleanQuery accessModeQuery = new BooleanQuery();
		BooleanQuery privatePostQuery= new BooleanQuery();
		
		//--------------------------------------------------------------------
		// search terms
		//--------------------------------------------------------------------
		Query searchTermQuery = parseSearchQuery(FLD_MERGEDFIELDS, searchTerms);
		searchQuery.add(searchTermQuery, Occur.SHOULD);

		//--------------------------------------------------------------------
		// private search fields
		//--------------------------------------------------------------------
		if( ValidationUtils.present(userName) ) {
			BooleanQuery privateSearchQuery = new BooleanQuery();
			Query privateSearchTermQuery    = parseSearchQuery(FLD_PRIVATEFIELDS, searchTerms);
			privateSearchQuery.add(privateSearchTermQuery, Occur.MUST);
			privateSearchQuery.add(new TermQuery(new Term(FLD_USER, userName)), Occur.MUST);
			searchQuery.add(privateSearchQuery, Occur.SHOULD);
		}
		
		//--------------------------------------------------------------------
		// allowed groups
		//--------------------------------------------------------------------
		for ( String groupName : allowedGroups) {
			Query groupQuery = new TermQuery(new Term(FLD_GROUP, groupName));
			accessModeQuery.add(groupQuery, Occur.SHOULD);
		}
		
		//--------------------------------------------------------------------
		// private post query
		//--------------------------------------------------------------------
		if( ValidationUtils.present(userName) ) {
			BooleanQuery privatePostGroups = new BooleanQuery();
			privatePostGroups.add(new TermQuery(new Term(FLD_GROUP, GroupID.PRIVATE.name().toLowerCase())), Occur.SHOULD);
			privatePostGroups.add(new TermQuery(new Term(FLD_GROUP, GroupID.FRIENDS.name().toLowerCase())), Occur.SHOULD);
			privatePostQuery.add(privatePostGroups, Occur.MUST);
			privatePostQuery.add(new TermQuery(new Term(FLD_USER, userName)), Occur.MUST);
			accessModeQuery.add(privatePostQuery, Occur.SHOULD);
		}

		//--------------------------------------------------------------------
		// post owned by user
		//--------------------------------------------------------------------
		if ( ValidationUtils.present(requestedUserName) ) {
			mainQuery.add(
					new TermQuery(new Term(FLD_USER, requestedUserName)),
					Occur.MUST
					);
		}
		
		//--------------------------------------------------------------------
		// post owned by group
		//--------------------------------------------------------------------
		if ( ValidationUtils.present(group) ) {
			mainQuery.add( new TermQuery(new Term(FLD_GROUP, group)), Occur.MUST );
		}
		
		//--------------------------------------------------------------------
		// build final query
		//--------------------------------------------------------------------
		mainQuery.add(searchQuery, Occur.MUST);
		if( !(ValidationUtils.present(userName) && userName.equals(requestedUserName)) )
			mainQuery.add(accessModeQuery, Occur.MUST);
		
		log.debug("[Full text] Search query: " + mainQuery.toString());

		//--------------------------------------------------------------------
		// set ordering
		//--------------------------------------------------------------------
		Sort sort = null;
		if (PARAM_RELEVANCE.equals(orderBy)) {
			sort = new Sort(new SortField[]{SortField.FIELD_SCORE,new SortField(FLD_DATE, SortField.LONG,true)
  			});
		} else { 
			// orderBy=="date"
			// FIXME: why does the default operator depend on the ordering
			// myParser.setDefaultOperator(QueryParser.Operator.AND);
			sort = new Sort(new SortField(FLD_DATE,SortField.LONG,true));
		}
		
		// all done
		QuerySortContainer qf = new QuerySortContainer();
		qf.setQuery(mainQuery);
		qf.setSort(sort);
		
		// set up collector
		TagCountCollector collector;
		try {
			collector = new TagCountCollector(null, CFG_TAG_CLOUD_LIMIT, qf.getSort());
		} catch (IOException e) {
			log.error("Error building tag cloud collector");
			collector = null;
		}
		qf.setTagCountCollector(collector);
		
		return qf;
	}

	/**
	 * construct lucene query filter for searching posts matching a given title 
	 * 
	 * (title:searchTerms) 
	 *   [AND user_name:requestedUsername]
	 *    AND ( 
	 *          group:allowedGroup_1 OR ... OR group:allowedGroup_n 
	 *          OR (group:private AND user:userName)
	 *        )  
	 *        
	 * FIXME: merge buildFulltextQuery and buildGroupSearchQuery
	 * 
	 * @param group group name from which posts should be searched
	 * @param searchTerms search query
	 * @param requestedUserName user name from whom posts should be searched
	 * @param userName login user name
	 * @param allowedGroups groups which the login user is member of
	 * 
	 * @return
	 */
	protected QuerySortContainer buildTitleQuery(String group, String searchTerms, String requestedUserName, String userName, Set<String> allowedGroups) {
		// FIXME: configure this
		//	String orderBy = "relevance"; 
		String orderBy = FLD_DATE; 
		
		BooleanQuery mainQuery       = new BooleanQuery();
		BooleanQuery searchQuery     = new BooleanQuery();
		BooleanQuery accessModeQuery = new BooleanQuery();
		BooleanQuery privatePostQuery= new BooleanQuery();
		
		//--------------------------------------------------------------------
		// search terms
		//--------------------------------------------------------------------
		Query searchTermQuery = parseSearchQuery(FLD_TITLE, searchTerms);
		searchQuery.add(searchTermQuery, Occur.SHOULD);

		//--------------------------------------------------------------------
		// allowed groups
		//--------------------------------------------------------------------
		for ( String groupName : allowedGroups) {
			Query groupQuery = new TermQuery(new Term(FLD_GROUP, groupName));
			accessModeQuery.add(groupQuery, Occur.SHOULD);
		}
		
		//--------------------------------------------------------------------
		// private post query
		//--------------------------------------------------------------------
		if( ValidationUtils.present(userName) ) {
			BooleanQuery privatePostGroups = new BooleanQuery();
			privatePostGroups.add(new TermQuery(new Term(FLD_GROUP, GroupID.PRIVATE.name().toLowerCase())), Occur.SHOULD);
			privatePostGroups.add(new TermQuery(new Term(FLD_GROUP, GroupID.FRIENDS.name().toLowerCase())), Occur.SHOULD);
			privatePostQuery.add(privatePostGroups, Occur.MUST);
			privatePostQuery.add(new TermQuery(new Term(FLD_USER, userName)), Occur.MUST);
			accessModeQuery.add(privatePostQuery, Occur.SHOULD);
		}

		//--------------------------------------------------------------------
		// post owned by user
		//--------------------------------------------------------------------
		if ( ValidationUtils.present(requestedUserName) ) {
			mainQuery.add(
					new TermQuery(new Term(FLD_USER, requestedUserName)),
					Occur.MUST
					);
		}
		
		//--------------------------------------------------------------------
		// post owned by group
		//--------------------------------------------------------------------
		if ( ValidationUtils.present(group) ) {
			mainQuery.add( new TermQuery(new Term(FLD_GROUP, group)), Occur.MUST );
		}
		
		//--------------------------------------------------------------------
		// build final query
		//--------------------------------------------------------------------
		mainQuery.add(searchQuery, Occur.MUST);
		if( !(ValidationUtils.present(userName) && userName.equals(requestedUserName)) )
			mainQuery.add(accessModeQuery, Occur.MUST);
		
		log.debug("[Full text] Search query: " + mainQuery.toString());

		//--------------------------------------------------------------------
		// set ordering
		//--------------------------------------------------------------------
		Sort sort = null;
		if (PARAM_RELEVANCE.equals(orderBy)) {
			sort = new Sort(new SortField[]{SortField.FIELD_SCORE,new SortField(FLD_DATE, SortField.LONG,true)
  			});
		} else { 
			// orderBy=="date"
			// FIXME: why does the default operator depend on the ordering
			// myParser.setDefaultOperator(QueryParser.Operator.AND);
			sort = new Sort(new SortField(FLD_DATE,SortField.LONG,true));
		}
		
		// all done
		QuerySortContainer qf = new QuerySortContainer();
		qf.setQuery(mainQuery);
		qf.setSort(sort);
		
		return qf;
	}

	
	/**
     * <em>/author/MaxMustermann</em><br/><br/>
	 * This method prepares queries which retrieve all publications for a given
	 * author name (restricted by group public).
	 * 
	 * @param group
	 * @param searchTerms
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param tagList
	 * @return
	 */
	protected abstract QuerySortContainer buildAuthorQuery(
			String group,  
			String searchTerms, 
			String requestedUserName, String requestedGroupName, 
			String year, String firstYear, String lastYear, 
			List<String> tagList);
	
	/**
	 * construct lucene query filter for full text search 'search:group'
	 * 
	 * (mergedfields:searchTerms OR (privatefields:searchTerm AND user_name:userName)) 
	 *    AND (user_name:groupMember_1 OR ... OR user_name:groupMember_n)
	 *    AND (
	 *       group:allowedGroup_1 OR ... OR group:allowedGroup_n OR 
	 *       (group:private AND user_name:userName) OR
	 *       (group:friends AND (user_name:groupFriend_1 OR ... OR user_name:groupFriend_n))
	 *    )  
	 * 
	 * FIXME: merge buildFulltextQuery and buildGroupSearchQuery

	 * @param groupName the group to search 
	 * @param visibleGroups list of group names the user is member of 
	 * @param userGroupFriends list of users which are group members and have the user as friend 
	 * @param groupMembers group members
	 * @param search the search query
	 * @param authUserName the user name
	 * @param limit
	 * @param offset
	 * @param systemTags NOT IMPLEMENTED
	 * @return
	 */
	protected QuerySortContainer buildGroupSearchQuery(
			String groupName, List<String> visibleGroups,
			List<String> userGroupFriends, List<String> groupMembers,
			String search, String authUserName, 
			final int limit, final int offset, 
			Collection<? extends Tag> systemTags ) {
		// FIXME: configure this
		//	String orderBy = "relevance"; 
		String orderBy = FLD_DATE;

		BooleanQuery mainQuery        = new BooleanQuery();
		BooleanQuery searchQuery      = new BooleanQuery();
		BooleanQuery groupMemberQuery = new BooleanQuery();
		BooleanQuery accessModeQuery  = new BooleanQuery();
		//--------------------------------------------------------------------
		// search terms
		//--------------------------------------------------------------------
		Query searchTermQuery = parseSearchQuery(FLD_MERGEDFIELDS, search);
		searchQuery.add(searchTermQuery, Occur.SHOULD);
		
		//--------------------------------------------------------------------
		// private search fields
		//--------------------------------------------------------------------
		if( ValidationUtils.present(authUserName) ) {
			BooleanQuery privateSearchQuery = new BooleanQuery();
			Query privateSearchTermQuery    = parseSearchQuery(FLD_PRIVATEFIELDS, search);
			privateSearchQuery.add(privateSearchTermQuery, Occur.MUST);
			privateSearchQuery.add(new TermQuery(new Term(FLD_USER, authUserName)), Occur.MUST);
			searchQuery.add(privateSearchQuery, Occur.SHOULD);
		}
		
		//--------------------------------------------------------------------
		// restrict to group members
		//--------------------------------------------------------------------
		for ( String member: groupMembers ) {
			Query memberQuery = new TermQuery(new Term(FLD_USER, member));
			groupMemberQuery.add(memberQuery, Occur.SHOULD);
		}
		
		//--------------------------------------------------------------------
		// allowed groups
		//--------------------------------------------------------------------
		for ( String group : visibleGroups ) {
			Query groupQuery = new TermQuery(new Term(FLD_GROUP, group));
			accessModeQuery.add(groupQuery, Occur.SHOULD);
		}
		
		//--------------------------------------------------------------------
		// private post query
		//--------------------------------------------------------------------
		if( ValidationUtils.present(authUserName) ) {
			BooleanQuery privatePostQuery= new BooleanQuery();
			BooleanQuery privatePostGroups = new BooleanQuery();
			privatePostGroups.add(new TermQuery(new Term(FLD_GROUP, GroupID.PRIVATE.name().toLowerCase())), Occur.SHOULD);
			privatePostGroups.add(new TermQuery(new Term(FLD_GROUP, GroupID.FRIENDS.name().toLowerCase())), Occur.SHOULD);
			privatePostQuery.add(privatePostGroups, Occur.MUST);
			privatePostQuery.add(new TermQuery(new Term(FLD_USER, authUserName)), Occur.MUST);
			accessModeQuery.add(privatePostQuery, Occur.SHOULD);
		}
		if( ValidationUtils.present(userGroupFriends) ) {
			BooleanQuery friendPostQuery= new BooleanQuery();
			friendPostQuery.add(new TermQuery(new Term(FLD_GROUP, GroupID.FRIENDS.name().toLowerCase())), Occur.MUST);

			BooleanQuery friendPostAllowanceQuery= new BooleanQuery();
			// the post's owner's friend may read the post
			for( String friend : userGroupFriends ) {
				friendPostAllowanceQuery.add(new TermQuery(new Term(FLD_USER, friend)), Occur.SHOULD);
			}

			friendPostQuery.add(friendPostAllowanceQuery, Occur.MUST);
			accessModeQuery.add(friendPostQuery, Occur.SHOULD);
		}		
		
		//--------------------------------------------------------------------
		// build final query
		//--------------------------------------------------------------------
		mainQuery.add(searchQuery, Occur.MUST);
		mainQuery.add(groupMemberQuery, Occur.MUST);
		mainQuery.add(accessModeQuery, Occur.MUST);
		
		log.debug("[Group search] Search query: " + mainQuery.toString());

		//--------------------------------------------------------------------
		// set ordering
		//--------------------------------------------------------------------
		Sort sort = null;
		if (PARAM_RELEVANCE.equals(orderBy)) {
			sort = new Sort(new SortField[]{SortField.FIELD_SCORE,new SortField(FLD_DATE, SortField.LONG,true)
  			});
		} else { 
			// orderBy=="date"
			// FIXME: why does the default operator depend on the ordering
			// myParser.setDefaultOperator(QueryParser.Operator.AND);
			sort = new Sort(new SortField(FLD_DATE,SortField.LONG,true));
		}
		
		// all done
		QuerySortContainer qf = new QuerySortContainer();
		qf.setQuery(mainQuery);
		qf.setSort(sort);
		return qf;
	};

	/** 
	 * analyzes given input parameter
	 * 
	 * @param group
	 * @param strToken a reusable token for efficacy 
	 * @return
	 * @throws IOException
	 */
	protected String parseToken(String fieldName, String param) throws IOException {
		if( ValidationUtils.present(param) ) {
			// use lucene's new token stream api (see org.apache.lucene.analysis' javadoc at package level)
			TokenStream ts = this.getAnalyzer().tokenStream(fieldName, new StringReader(param));
		    TermAttribute termAtt = ts.addAttribute(TermAttribute.class);
		    ts.reset();

			// analyze the parameter - that is: concatenate its normalized tokens
		    String analyzedString = "";
            while (ts.incrementToken()) {
            	analyzedString+=" "+termAtt.term();
		    }

            return analyzedString.trim();
		} else
			return "";
	}
	
	/**
	 * build full text query for given query string
	 * 
	 * @param fieldName
	 * @param searchTerms
	 * @return
	 */
	private Query parseSearchQuery(String fieldName, String searchTerms) {
		// parse search terms for handling phrase search
		QueryParser searchTermParser = new QueryParser(LUCENE_24, fieldName, getAnalyzer());
		// FIXME: configure default operator via spring
		searchTermParser.setDefaultOperator(getDefaultSearchTermJunctor());
		Query searchTermQuery = null;
		try {
			// disallow field specification in search query
			searchTerms = searchTerms.replace(CFG_LUCENE_FIELD_SPECIFIER, "\\"+CFG_LUCENE_FIELD_SPECIFIER);
			searchTermQuery = searchTermParser.parse(searchTerms);
		} catch (ParseException e) {
			searchTermQuery = new TermQuery(new Term(fieldName, searchTerms) );
		}
		return searchTermQuery;
	}	
	
	/**
	 * get managed resource name
	 * @return
	 */
	private String getResourceName() {
		String name = getResourceType().getCanonicalName();
		if (name.lastIndexOf('.') > 0) {
	        name = name.substring(name.lastIndexOf('.')+1);
	    }
		
		return name;
	}

	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setDbLogic(LuceneDBInterface<R> dbLogic) {
		this.dbLogic = dbLogic;
	}

	public LuceneDBInterface<R> getDbLogic() {
		return dbLogic;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setDefaultSearchTermJunctor(Operator defaultSearchTermJunctor) {
		this.defaultSearchTermJunctor = defaultSearchTermJunctor;
	}

	public Operator getDefaultSearchTermJunctor() {
		return defaultSearchTermJunctor;
	}

	public void setResourceConverter(LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	public LuceneResourceConverter<R> getResourceConverter() {
		return resourceConverter;
	}

	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}

	public int getIndexId() {
		return indexId;
	}


}
