package org.bibsonomy.lucene.search;

import static org.apache.lucene.util.Version.LUCENE_24;
import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.lucene.search.collector.TagCountCollector;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LuceneResourceConverter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
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
	 * search for posts using the lucene index
	 * 
	 * @param userName
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param allowedGroups
	 * @param friends
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param tagIndex
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @return
	 */
	public ResultList<Post<R>> getPosts(
			final String userName, final String requestedUserName, String requestedGroupName, 
			final Collection<String> allowedGroups,
			final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex,
			final String year, final String firstYear, final String lastYear, int limit, int offset) {
		// build query
		QuerySortContainer query = buildQuery(
				userName, requestedUserName, requestedGroupName,
				allowedGroups, 
				searchTerms, titleSearchTerms, authorSearchTerms, tagIndex,
				year, firstYear, lastYear);
		// perform search query
		return doSearch(query, limit, offset);
	}
	
	public List<Tag> getTags(
			final String userName, final String requestedUserName, String requestedGroupName, 
			final Collection<String> allowedGroups,
			final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex,
			final String year, final String firstYear, final String lastYear, int limit, int offset) {
		// build query
		QuerySortContainer qf = buildQuery(
				userName, requestedUserName, requestedGroupName,
				allowedGroups, 
				searchTerms, titleSearchTerms, authorSearchTerms, tagIndex,
				year, firstYear, lastYear);
		// limit number of posts to consider for building the tag cloud
		qf.setLimit(getTagCloudLimit());
		// query index
		return doTagSearch(qf);
	}	
	
	/**
	 * TODO: document me
	 * 
	 * @param group
	 * @param search
	 * @param requestedUserName
	 * @param requestedGroupName
	 * @param groupMembers group members
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @param tagList
	 * @param limit
	 * @param offset
	 * @return
	 */
	public ResultList<Post<R>> searchAuthor(String group, String search,
			String requestedUserName, String requestedGroupName,  
			String year, String firstYear, String lastYear, List<String> tagList, int limit,
			int offset) {
		//--------------------------------------------------------------------
		// query bibsonomy's database for missing data
		//--------------------------------------------------------------------
		// get given groups members
		List<String> groupMembers = this.dbLogic.getGroupMembersByGroupName(requestedGroupName);
		
		// build query
		QuerySortContainer authorQuery = buildAuthorQuery(group, search, requestedUserName, requestedGroupName, groupMembers, year, firstYear, lastYear, tagList);
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
			String requestedUserName, String requestedGroupName, 
			String year, String firstYear, String lastYear, List<String> tagList) {
		//--------------------------------------------------------------------
		// query bibsonomy's database for missing data
		//--------------------------------------------------------------------
		// get given groups members
		List<String> groupMembers = this.dbLogic.getGroupMembersByGroupName(requestedGroupName);

		// build query
		QuerySortContainer qf = buildAuthorQuery(group, search, requestedUserName, requestedGroupName, groupMembers, year, firstYear, lastYear, tagList);
		// limit number of posts to consider for building the tag cloud
		qf.setLimit(getTagCloudLimit());
		// query index
		return doTagSearch(qf);
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
		QuerySortContainer qf = buildFulltextQuery(group, searchTerms, requestedUserName, UserName, GroupNames);
		// limit number of posts to consider for building the tag cloud
		qf.setLimit(getTagCloudLimit());
		// collect tags
		return doTagSearch(qf);
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
	    return new LuceneIndexStatistics();
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
	 * get tag assignments of top n relevant documents
	 * 
	 * @param qf
	 * @return
	 */
	private List<Tag> doTagSearch(QuerySortContainer qf) {
		List<Tag> retVal = new LinkedList<Tag>();
		if( !isEnabled() && !getEnableTagClouds() ) {
			return retVal;
		}
		Map<Tag,Integer> tagCounter = new HashMap<Tag,Integer>();
		
		r.lock();
		try {
			// gather tags used by the author's posts
			log.debug("Starting tag collection");
			final TopDocs topDocs = searcher.search(qf.getQuery(), null, qf.getLimit(), qf.getSort());
			log.debug("Done collecting tags");
			//----------------------------------------------------------------
			// extract tags from top n documents
			//----------------------------------------------------------------
			int hitsLimit = ((qf.getLimit() < topDocs.totalHits) ? (qf.getLimit()) : topDocs.totalHits);
			for (int i = 0; i < hitsLimit; i++) {
				// get document from index
				Document       doc  = searcher.doc(topDocs.scoreDocs[i].doc);
				// convert document to bibsonomy post model
				Post<R> post = this.resourceConverter.writePost(doc); 

				// set tag count
				if( ValidationUtils.present(post.getTags()) ) {
					for(Tag tag : post.getTags()) {
						Integer oldCnt = tagCounter.get(tag);
						if( !ValidationUtils.present(oldCnt) )
							oldCnt=1;
						else
							oldCnt+=1;
						tagCounter.put(tag, oldCnt);
					}
				}						
			}
		} catch (IOException e) {
			log.error("Error building full text tag cloud for query " + qf.getQuery().toString());
		} finally {
			r.unlock();
		}
		// extract all tags
		for( Map.Entry<Tag,Integer> entry : tagCounter.entrySet() ) {
			Tag tag = entry.getKey();
			tag.setUsercount(entry.getValue());
			// FIXME: we set user==global count
			tag.setGlobalcount(entry.getValue());
			retVal.add(tag);
		}
		log.debug("Done calculating tag statistics");
		
		// all done.
		return retVal;	
	}
	
	/**
	 * collect all tags assigned to relevant documents
	 */
	/* FIXME: unused
	private List<Tag> doTagCollection(QuerySortContainer qf) {
		if( !getEnableTagClouds() ) {
			return new LinkedList<Tag>();
		}
		List<Tag> retVal = null;
		
		r.lock();
		try {
			if( isEnabled() ) {
				// gather tags used by the author's posts
				TagCountCollector tagCollector = qf.getTagCountCollector();
				if( tagCollector!=null ) {
					try {
						log.debug("Starting tag collection");
						searcher.search(qf.getQuery(), null, tagCollector);
						log.debug("Done collecting tags");
						retVal = tagCollector.getTags(searcher);
						log.debug("Done calculating tag statistics");
					} catch (IOException e) {
						log.error("Error building full text tag cloud for query " + qf.getQuery().toString());
					}
				}
			}
			
			if( retVal==null )
				retVal = new LinkedList<Tag>();
		} finally {
			r.unlock();
		}
		
		// all done.
		return retVal;
	}
	*/
	
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
	 * parse given search term for allowing lucene's search syntax
	 * 
	 * @param searchTerms a lucene search query
	 * @return the parsed query term
	 */
	protected Query buildFulltextSearchQuery(final String searchTerms) {
		Query searchTermQuery = parseSearchQuery(FLD_MERGEDFIELDS, searchTerms);
		return searchTermQuery;
	}

	/**
	 * parse given search term for allowing lucene's search syntax on the title field
	 * 
	 * @param searchTerms a lucene search query
	 * @return the parsed query term
	 */
	protected Query buildTitleSearchQuery(final String searchTerms) {
		Query searchTermQuery = parseSearchQuery(FLD_TITLE, searchTerms);
		return searchTermQuery;
	}
	
	/**
	 * build query to search for posts who's private notes field matches to the given search terms
	 * @param userName
	 * @return
	 */
	protected Query buildPrivateNotesQuery(String userName, String searchTerms) {
		BooleanQuery privateSearchQuery = new BooleanQuery();
		
		if( ValidationUtils.present(userName) ) {
			Query privateSearchTermQuery    = parseSearchQuery(FLD_PRIVATEFIELDS, searchTerms);
			privateSearchQuery.add(privateSearchTermQuery, Occur.MUST);
			privateSearchQuery.add(new TermQuery(new Term(FLD_USER, userName)), Occur.MUST);
		}
		
		return privateSearchQuery;
	}

	/**
	 * restrict result list to posts with given tag assignments
	 * 
	 * @param tagIndex list of tags 
	 * @return search query for restricting posts to given tag assignments
	 */
	protected Query buildTagSearchQuery(Collection<String> tagIndex) {
		//--------------------------------------------------------------------
		// prepare input parameters
		//--------------------------------------------------------------------
		List<String> tags = new LinkedList<String>();
		if( ValidationUtils.present(tagIndex) ) {
			for( String tag : tagIndex) {
				try {
					tags.add(parseToken(FLD_TAS, tag));
				} catch (IOException e) {
					log.error("Error parsing input tag " + tag + " ("+e.getMessage()+")");
					tags.add(tag);
				}
			}
			tagIndex = tags;
		}
		
		//--------------------------------------------------------------------
		// restrict to given tags
		//--------------------------------------------------------------------
		BooleanQuery tagQuery = new BooleanQuery();
		if( ValidationUtils.present(tags) ) {
			for ( String tagItem : tags){
				tagQuery.add(new TermQuery(new Term(FLD_TAS, tagItem)), Occur.MUST);
			}
		}
		
		// all done
		return tagQuery;
	}	

	protected abstract Query buildAuthorSearchQuery(final String autherSearchTerms);
	
	/**
	 * restrict result list to posts owned by one of the given group members 
	 * @param requestedGroupName
	 * @param groupMembers
	 * @return
	 */
	protected BooleanQuery buildGroupSearchQuery(final String requestedGroupName) {
		// get given group's members
		Collection<String> groupMembers = this.dbLogic.getGroupMembersByGroupName(requestedGroupName);
		
		//--------------------------------------------------------------------
		// restrict to group members
		//--------------------------------------------------------------------
		BooleanQuery groupMemberQuery = new BooleanQuery();
		if ( ValidationUtils.present(requestedGroupName) && ValidationUtils.present(groupMembers) ) {
			for ( String member: groupMembers ) {
				Query memberQuery = new TermQuery(new Term(FLD_USER, member));
				groupMemberQuery.add(memberQuery, Occur.SHOULD);
			}
		}
		return groupMemberQuery;
	}
	
	/**
	 * restrict given query to posts belonging to a given time range
	 * 
	 * @param mainQuery
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @return
	 */
	protected Query makeTimeRangeQuery(BooleanQuery mainQuery, String year, String firstYear, String lastYear) {
		//--------------------------------------------------------------------
		// exact year query
		//--------------------------------------------------------------------
		boolean includeLowerBound = false;
		boolean includeUpperBound = false;

		if ( ValidationUtils.present(year) ) {
			year = year.replaceAll("\\D", "");
			mainQuery.add( new TermQuery(new Term(FLD_YEAR, year)), Occur.MUST );
		} else {
		//--------------------------------------------------------------------
		// range query
		//--------------------------------------------------------------------
			// firstYear != null, lastYear != null
			if( ValidationUtils.present(firstYear) ) {
				firstYear = firstYear.replaceAll("\\D", "");
				includeLowerBound = true;
			}
			// firstYear == null, lastYear != null
			if( ValidationUtils.present(lastYear) ) {
				lastYear = lastYear.replaceAll("\\D", "");
				includeUpperBound = true; 
			}
		}
		if (includeLowerBound || includeUpperBound) {
			// if upper or lower bound is given, then use filter
			FilteredQuery filteredQuery = null;
			Filter rangeFilter = new TermRangeFilter(FLD_YEAR , firstYear, lastYear, includeLowerBound, includeUpperBound);
			filteredQuery = new FilteredQuery(mainQuery,rangeFilter);
			return filteredQuery;
		} else {
			return mainQuery;
		}
	}
	
	/**
	 * restrict result to posts which are visible to the user
	 * 
	 * @param userName the logged in user's name
	 * @param allowedGroups list of groups of which the logged in user is a member
	 * @param friends list of users who's private post the logged in user may see
	 * @return a query term which restricts the result to posts, which are visible to the user
	 */
	protected Query buildAccessModeQuery(final String userName, final Collection<String> allowedGroups) {
		//--------------------------------------------------------------------
		// get missing information from bibsonomy's database
		//--------------------------------------------------------------------
		final Collection<String> friends = this.dbLogic.getFriendsForUser(userName);
		
		BooleanQuery accessModeQuery  = new BooleanQuery();
		BooleanQuery privatePostQuery = new BooleanQuery();

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

		if( ValidationUtils.present(friends) ) {
			BooleanQuery friendPostQuery= new BooleanQuery();
			friendPostQuery.add(new TermQuery(new Term(FLD_GROUP, GroupID.FRIENDS.name().toLowerCase())), Occur.MUST);

			BooleanQuery friendPostAllowanceQuery= new BooleanQuery();
			// the post owner's friend may read the post
			for( String friend : friends ) {
				friendPostAllowanceQuery.add(new TermQuery(new Term(FLD_USER, friend)), Occur.SHOULD);
			}

			friendPostQuery.add(friendPostAllowanceQuery, Occur.MUST);
			accessModeQuery.add(friendPostQuery, Occur.SHOULD);
		}
				
		
		// all done
		return accessModeQuery; 
	}
	
	
	/**
	 * build the overall lucene serach query term
	 * @param userName
	 * @param requestedUserName restrict the resulting posts to those which are owned by this user name
	 * @param requestedGroupName restrict the resulting posts to those which are owned this group
	 * @param searchTerms
	 * @return
	 */
	protected QuerySortContainer buildQuery(
			final String userName, final String requestedUserName, String requestedGroupName, 
			final Collection<String> allowedGroups,
			final String searchTerms, final String titleSearchTerms, final String authorSearchTerms,
			final Collection<String> tagIndex,
			final String year, final String firstYear, final String lastYear) {		
		//--------------------------------------------------------------------
		// build the query
		//--------------------------------------------------------------------
		// the resulting main query
		BooleanQuery mainQuery       = new BooleanQuery();
		BooleanQuery searchQuery     = new BooleanQuery();
		
		// search full text 
		if( present(searchTerms) ) {
			Query fulltextQuery     = buildFulltextSearchQuery(searchTerms);
			searchQuery.add(fulltextQuery, Occur.SHOULD);
		}
		
		// search private nodes
		if( present(userName) && present(searchTerms) ) {
			Query privateNotesQuery = buildPrivateNotesQuery(userName, searchTerms);
			searchQuery.add(privateNotesQuery, Occur.SHOULD);
		}

		// search title 
		if( present(titleSearchTerms) ) {
			Query titleQuery        = buildTitleSearchQuery(titleSearchTerms);
			searchQuery.add(titleQuery, Occur.MUST);
		}
		
		// search author
		if( present(authorSearchTerms) ) {
			Query authorQuery       = buildAuthorSearchQuery(authorSearchTerms);
			searchQuery.add(authorQuery, Occur.MUST);
		}
		
		// search tag assignments
		if( present(tagIndex) ) {
			Query tagQuery          = buildTagSearchQuery(tagIndex);
			searchQuery.add(tagQuery, Occur.MUST);
		}
		
		// restrict result to given group
		if( present(requestedGroupName) ) {
			BooleanQuery groupQuery  = buildGroupSearchQuery(requestedGroupName);
			if( groupQuery.getClauses().length >= 1 ) {
				mainQuery.add(groupQuery, Occur.MUST);
			}
		}
		
		// restricting access to posts visible to the user
		Query accessModeQuery = buildAccessModeQuery(userName, allowedGroups);
		
		//--------------------------------------------------------------------
		// post owned by user
		//--------------------------------------------------------------------
		if ( ValidationUtils.present(requestedUserName) ) {
			mainQuery.add(new TermQuery(new Term(FLD_USER, requestedUserName)), Occur.MUST);
		}

		//--------------------------------------------------------------------
		// post owned by group
		//--------------------------------------------------------------------
		/*
		 * FIXME: THIS IS DEAD CODE!
		if ( false && ValidationUtils.present(requestedGroupName) ) {
			mainQuery.add(new TermQuery(new Term(FLD_GROUP, requestedGroupName)), Occur.MUST);
		}
		*/
		
		//--------------------------------------------------------------------
		// build final query
		//--------------------------------------------------------------------

		// combine query terms
		mainQuery.add(searchQuery, Occur.MUST);
		mainQuery.add(accessModeQuery, Occur.MUST);

		// set ordering
		Sort sort = new Sort(new SortField(FLD_DATE,SortField.LONG,true));
		
		// all done
		log.debug("[Full text] Search query: " + mainQuery.toString());
		QuerySortContainer qf = new QuerySortContainer();
		qf.setQuery(makeTimeRangeQuery(mainQuery, year, firstYear, lastYear));
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
	 * FIXME: shouldn't this query also respect 'visible for friends'?
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
	 * @param groupMembers group members
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
			List<String> groupMembers,
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
	}

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
	protected Query parseSearchQuery(String fieldName, String searchTerms) {
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
