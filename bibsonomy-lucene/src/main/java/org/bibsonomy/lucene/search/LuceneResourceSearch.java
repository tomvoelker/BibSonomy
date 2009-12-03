package org.bibsonomy.lucene.search;

import static org.apache.lucene.util.Version.LUCENE_24;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.lucene.search.collector.TagCountCollector;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.Utils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.ValidationUtils;

/**
 * abstract parent class for lucene search
 * 
 * FIXME: this class now should be thread safe - only one issue might retain:
 *        what happens, if the update manager updated the index, but theire are
 *        still resource searchers active - do their index searcher work correctly???
 * 
 * @author fei
 *
 * @param <R> resource type
 */
public abstract class LuceneResourceSearch<R extends Resource> extends LuceneBase {
	private static final Logger log = Logger.getLogger(LuceneResourceSearch.class);
	
	/**
	 *  read/write lock, allowing multiple searcher or exclusive an index update
	 *	FIXME: we should use an implementation, which prefers writers for obtaining the lock 
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
	 */
	public ResultList<Post<R>> searchPosts(String group,
			String searchTerms, String requestedUserName, String UserName,
			Set<String> GroupNames, int limit, int offset) {
		ResultList<Post<R>> retVal = null;

		r.lock();
		try {
			if( isEnabled() ) {
				retVal = searchLucene(
						buildFulltextQuery(group, searchTerms, requestedUserName, UserName, GroupNames), 
						limit, offset);
			} else
				retVal = createEmptyResultList();
		} finally {
			r.unlock();
		}

		// all done.
		return retVal;
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
		ResultList<Post<R>> retVal = null;
		
		r.lock();
		try {
			if( isEnabled() ) {
				retVal = searchLucene(
						buildAuthorQuery(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagList, CFG_TAG_CLOUD_LIMIT), 
						limit, offset);
			} else 
				retVal = createEmptyResultList();
		} finally {
			r.unlock();
		}
		
		// all done.
		return retVal;
	}
	
	public List<Tag> getTagsByAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList, int limit) {
		List<Tag> retVal = null;
		
		r.lock();
		try {
			if( isEnabled() ) {
				QuerySortContainer qf = buildAuthorQuery(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagList, CFG_TAG_CLOUD_LIMIT);

				// gather tags used by the author's posts
				TagCountCollector tagCollector = qf.getTagCountCollector();
				if( tagCollector!=null ) {
					try {
						searcher.search(qf.getQuery(), null, tagCollector);
						retVal = tagCollector.getTags(searcher);
					} catch (IOException e) {
						log.error("Error building author tag cloud for " + search);
					}
				}
				// all done.
				// FIXME: we simply cut off the list 
				//      - we probably want get the n most popular tags
				retVal = retVal.subList(0, Math.min(limit, retVal.size()));
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
		ResultList<Post<R>> retVal = null;

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
		
		r.lock();
		try {
			if( isEnabled() ) {
				return searchLucene(
						buildGroupSearchQuery(groupName, visibleGroupNames, userGroupFriends, groupMembers, search, authUserName, limit, offset, systemTags),
						limit, offset);
			} else
				retVal = createEmptyResultList();
		} finally {
			r.unlock();
		}

		// all done.
		return retVal;
	}

	//------------------------------------------------------------------------
	// abstract interface
	//------------------------------------------------------------------------
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
				Post<R> post = convertToPostModel(doc); 
				
				// set post frequency
				starttimeQuery = System.currentTimeMillis();
				int postFreq = 1;
				if( doc.get(FLD_INTRAHASH)!=null ) {
					postFreq = this.searcher.docFreq(new Term(FLD_INTRAHASH, doc.get(FLD_INTRAHASH)));
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
	 * create bibsonomy post model from given lucene document
	 * @param doc
	 * @return
	 */
	protected abstract Post<R> convertToPostModel(Document doc);

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
	public void reloadIndex() {
		w.lock();

		try {
			init();

			// if there is already a searcher
			try {
				if (null != this.searcher) this.searcher.close();
			} catch (IOException e) {
				log.debug("Error closing searcher.", e);
			}

			try {
				// load and hold index on physical hard disk
				log.debug("Reloading index from disk.");
				this.searcher = new IndexSearcher(FSDirectory.open(new File(luceneIndexPath)));
				enableIndex();
			} catch (Exception e) {
				log.error("Error reloading index, disabling searcher", e);
				disableIndex();
			}
		} finally {
			w.unlock();
		}
	}

	public LuceneIndexStatistics getStatistics() {
		return Utils.getStatistics(luceneIndexPath);
	}	
	
	//------------------------------------------------------------------------
	// private helper
	//------------------------------------------------------------------------
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
	 * (mergedfields:searchTerms) [AND user_name:requestedUsername]
	 *    AND (group:allowedGroup_1 OR ... OR group:allowedGroup_n OR (group:private AND user:userName))  
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
		BooleanQuery accessModeQuery = new BooleanQuery();
		BooleanQuery privatePostQuery= new BooleanQuery();
		//--------------------------------------------------------------------
		// search terms
		//--------------------------------------------------------------------
		Query searchTermQuery = parseSearchQuery(searchTerms);
		
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
		privatePostQuery.add(new TermQuery(new Term(FLD_GROUP, GroupID.PRIVATE.name().toLowerCase())), Occur.MUST);
		privatePostQuery.add(new TermQuery(new Term(FLD_USER, userName)), Occur.MUST);
		accessModeQuery.add(privatePostQuery, Occur.SHOULD);

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
		mainQuery.add(searchTermQuery, Occur.MUST);
		mainQuery.add(accessModeQuery, Occur.MUST);
		
		log.debug("Search query: " + mainQuery.toString());

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
			List<String> tagList, int tagCntLimit);
	
	/**
	 * construct lucene query filter for full text search 'search:group'
	 * 
	 * (mergedfields:searchTerms) 
	 *    AND (user_name:groupMember_1 OR ... OR user_name:groupMember_n)
	 *    AND (
	 *       group:allowedGroup_1 OR ... OR group:allowedGroup_n OR 
	 *       (group:private AND (user_name:userName OR user_name:groupFriend_1 OR ... OR user_name:groupFriend_n))
	 *    )  
	 * 
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

		BooleanQuery mainQuery       = new BooleanQuery();
		BooleanQuery groupMemberQuery = new BooleanQuery();
		BooleanQuery accessModeQuery = new BooleanQuery();
		//--------------------------------------------------------------------
		// search terms
		//--------------------------------------------------------------------
		Query searchTermQuery = parseSearchQuery(search);
		
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
		BooleanQuery privatePostQuery= new BooleanQuery();
		privatePostQuery.add(new TermQuery(new Term(FLD_GROUP, GroupID.PRIVATE.name().toLowerCase())), Occur.MUST);
		
		BooleanQuery privatePostAllowanceQuery= new BooleanQuery();
		// the post's owner may read the private post 
		privatePostAllowanceQuery.add(new TermQuery(new Term(FLD_USER, authUserName)), Occur.SHOULD);
		// the post's owner's friend may read the post
		for( String friend : userGroupFriends ) {
			privatePostAllowanceQuery.add(new TermQuery(new Term(FLD_USER, friend)), Occur.SHOULD);
		}
		
		privatePostQuery.add(privatePostAllowanceQuery, Occur.MUST);
		accessModeQuery.add(privatePostQuery, Occur.SHOULD);
		
		//--------------------------------------------------------------------
		// build final query
		//--------------------------------------------------------------------
		mainQuery.add(searchTermQuery, Occur.MUST);
		mainQuery.add(accessModeQuery, Occur.MUST);
		
		log.debug("Search query: " + mainQuery.toString());

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
	 * FIXME: do we need to analyze the parameter???
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

            // escape lucene's special characters for preventing lucene injection hacks
			return QueryParser.escape(analyzedString.trim());
		} else
			return "";
	}
	
	/**
	 * build full text query for given query string
	 * 
	 * @param searchTerms
	 * @return
	 */
	private Query parseSearchQuery(String searchTerms) {
		// parse search terms for handling phrase search
		QueryParser searchTermParser = new QueryParser(LUCENE_24, FLD_MERGEDFIELDS, getAnalyzer());
		// FIXME: configure default operator via spring
		searchTermParser.setDefaultOperator(getDefaultSearchTermJunctor());
		Query searchTermQuery = null;
		try {
			// disallow field specification in search query
			searchTerms = searchTerms.replace(CFG_LUCENE_FIELD_SPECIFIER, "\\"+CFG_LUCENE_FIELD_SPECIFIER);
			searchTermQuery = searchTermParser.parse(searchTerms);
		} catch (ParseException e) {
			searchTermQuery = new TermQuery(new Term(FLD_MERGEDFIELDS, searchTerms) );
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


}
