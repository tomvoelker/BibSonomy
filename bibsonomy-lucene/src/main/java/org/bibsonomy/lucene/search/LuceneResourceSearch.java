package org.bibsonomy.lucene.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.exceptions.LuceneException;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.Utils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.util.ValidationUtils;

/**
 * abstract parent class for lucene search
 * 
 * @author fei
 *
 * @param <R> resource type
 */
public abstract class LuceneResourceSearch<R extends Resource> extends LuceneBase {
	private static final Logger log = Logger.getLogger(LuceneResourceSearch.class);
	
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

	//------------------------------------------------------------------------
	// search interface
	//------------------------------------------------------------------------
	/**
	 * TODO: document me
	 */
	public ResultList<Post<R>> searchPosts(String group,
			String searchTerms, String requestedUserName, String UserName,
			Set<String> GroupNames, int limit, int offset) {
		return searchLucene(buildFulltextQuery(group, searchTerms, requestedUserName, UserName, GroupNames), limit, offset);
	}
	
	/**
	 * TODO: document me
	 * FIXME: This should be just a variant of searchPosts
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
		return searchLucene(buildAuthorQuery(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagList), limit, offset);
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

			long starttimeQuery = System.currentTimeMillis();
			final TopDocs topDocs = searcher.search(query, null, offset+limit, sort);
			long endtimeQuery = System.currentTimeMillis();
			log.debug("Query time: " + (endtimeQuery - starttimeQuery) + "ms");
			
			// determine number of posts to display
			int hitslimit = (((offset+limit) < topDocs.totalHits) ? (offset+limit) : topDocs.totalHits);
			postList.setTotalCount(topDocs.totalHits);

			log.debug("offset / limit / hitslimit / hits.length():  "
					+ offset + " / " + limit + " / " + hitslimit + " / " + topDocs.totalHits);

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
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup(CONTEXT_ENV_NAME);
			
			String contextPathName = CONTEXT_INDEX_PATH+getResourceName();
			this.luceneIndexPath = (String) envContext.lookup(contextPathName);
			log.debug("Using index " + luceneIndexPath);
		} catch (NamingException e) {
			log.error("NamingException requesting JNDI environment variables ' ("+e.getMessage()+")", e);
		}

	}
	
	/** reload the index -- has to be called after each index change */
	public void reloadIndex() {
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
			this.searcher = new IndexSearcher( luceneIndexPath );
		} catch (Exception e) {
			log.error("Error reloading index", e);
			throw new LuceneException("Error reloading index"+e.getMessage());
		}
	}
	
	public LuceneIndexStatistics getStatistics() {
		return Utils.getStatistics(luceneIndexPath);
	}	
	
	//------------------------------------------------------------------------
	// private helper
	//------------------------------------------------------------------------
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
		
		try {
			userName          = parseToken(FLD_USER, userName);
			requestedUserName = parseToken(FLD_USER, requestedUserName);
			group             = parseToken(FLD_GROUP, group);
			searchTerms       = parseToken(FLD_AUTHOR, searchTerms);
			// parse each group name
			Set<String> groups= new TreeSet<String>();
			for(String groupName : allowedGroups) {
				groups.add(parseToken(FLD_GROUP, groupName)); 
			}
			allowedGroups = groups;
		} catch (IOException e) {
			log.error("Error analyzing input", e);
		}

		BooleanQuery mainQuery       = new BooleanQuery();
		BooleanQuery accessModeQuery = new BooleanQuery();
		BooleanQuery privatePostQuery= new BooleanQuery();
		//--------------------------------------------------------------------
		// search terms
		//--------------------------------------------------------------------
		// parse search terms for handling phrase search
		QueryParser searchTermParser = new QueryParser(FLD_MERGEDFIELDS, getAnalyzer());
		Query searchTermQuery = null;
		try {
			searchTermQuery = searchTermParser.parse(searchTerms);
		} catch (ParseException e) {
			searchTermQuery = new TermQuery(new Term(FLD_MERGEDFIELDS, searchTerms) );
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
			sort = new Sort(new SortField[]{SortField.FIELD_SCORE,new SortField(FLD_DATE,true)
  			});
		} else { 
			// orderBy=="date"
			// FIXME: why does the default operator depend on the ordering
			// myParser.setDefaultOperator(QueryParser.Operator.AND);
			sort = new Sort(FLD_DATE,true);
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
	 * analyzes given input parameter
	 * 
	 * @param group
	 * @param strToken a reusable token for efficacy 
	 * @return
	 * @throws IOException
	 */
	protected String parseToken(String fieldName, String param) throws IOException {
		Token strToken = new Token();
		if( ValidationUtils.present(param) ) {
			this.getAnalyzer().tokenStream(fieldName, new StringReader(param)).next(strToken);
			return QueryParser.escape(strToken.term());
		} else
			return "";
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
}
