package org.bibsonomy.lucene;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.bibsonomy.common.exceptions.LuceneException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.services.searcher.LuceneSearch;

public class LuceneSearchBibTex implements LuceneSearch<BibTex> {

	private final static LuceneSearchBibTex singleton = new LuceneSearchBibTex();
	private IndexSearcher searcher; 
	private PerFieldAnalyzerWrapper analyzer;
	private String lucenePath = null;
	
	
	String lField_contentid = "content_id";
	String lField_group = "group";
	String lField_date = "date";
	String lField_user = "user_name";
	String lField_desc = "desc";
	String lField_ext = "ext";
	String lField_url = "url";
	String lField_tas = "tas";
	String lField_author = "author";
	String lField_year = "year";
	String lField_merged = "mergedfields";	

	private LuceneSearchBibTex() {
		reloadIndex();
	}
	

	public void reloadIndex() {
		final Log LOGGER = LogFactory.getLog(LuceneSearchBibTex.class);
		try {

			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			lucenePath = (String) envContext.lookup("luceneIndexPathPublications");
			
			LOGGER.debug("LuceneBibTex: use index: " + lucenePath);

			/* set current path to lucene index, given by environment parameter in tomcat's context.xml
			 * 
			 *   <Environment name="luceneIndexPathPublications" type="java.lang.String" value="/home/bibsonomy/lucene"/>
			 */


			if (this.analyzer == null)
			{
				/** lucene analyzer, must be the same as at indexing */
				//SimpleAnalyzer analyzer = new SimpleAnalyzer();
				this.analyzer = new PerFieldAnalyzerWrapper(new SimpleAnalyzer());
	
				// let field group of analyzer use SimpleKeywordAnalyzer
				// numbers will be deleted by SimpleAnalyser but group has only numbers, therefore use SimpleKeywordAnalyzer 
				this.analyzer.addAnalyzer("group", new SimpleKeywordAnalyzer());
				// usernames also might contain numbers - 
				// as the user_name field is not analyzed (which makes pretty sense)
				// the user_name shouldn't be normalized as well
				this.analyzer.addAnalyzer("user_name", new SimpleKeywordAnalyzer());
			}
			
			
			// if there is already a searcher
			try {
				if (null != this.searcher) this.searcher.close();
			} catch (IOException e) {
				LOGGER.debug("LuceneBibTex: IOException on searcher.close: "+ e.getMessage());
			} catch (RuntimeException e)
			{
				LOGGER.debug("LuceneBibTex: RuntimeException on searcher.close: "+ e.getMessage());
			}

			// load and hold index on physical hard disk
			LOGGER.debug("LuceneBibTex: use index from disk");
			LOGGER.debug("this.searcher-0: " + this.searcher);
			this.searcher = new IndexSearcher( lucenePath );
			LOGGER.debug("this.searcher-1: " + this.searcher);

		} catch (final NamingException e) {
			LOGGER.error("LuceneBibTex: NamingException "+ e.getExplanation() + " ## " + e.getMessage());
			LOGGER.error("Environment variable luceneIndexPathPublications not present.");
			throw new LuceneException("error.lucene");
		} catch (CorruptIndexException e) {
			LOGGER.error("LuceneBibTex: CorruptIndexException "+ e.getMessage());
			throw new LuceneException("error.lucene");
		} catch (IOException e) {
			LOGGER.error("LuceneBibTex: IOException "+ e.getMessage());
			throw new LuceneException("error.lucene");
		} catch (RuntimeException e) {
			LOGGER.warn("LuceneBibTex: RuntimeException "+ e.getMessage());
			throw new LuceneException("error.lucene");
		}
	}

	
	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBibTex getInstance() {
		return singleton;
	}

	
	/**
	 * full text search for search:all and search:username
	 * 
	 * @param groupId
	 * @param searchTerms
	 * @param requestedUserName
	 * @param UserName
	 * @param GroupNames
	 * @return queryString
	 */
	
	private QuerySortContainer getFulltextQueryFilter (String group, String searchTerms, String requestedUserName, String UserName, Set<String> GroupNames) {
		final Log LOGGER = LogFactory.getLog(LuceneSearchBibTex.class);

//		String orderBy = "relevance"; 
		String orderBy = "date"; 
		
		String allowedGroupNames = "";
		String allowedGroupNamesQuery = "";
		String mergedFiledQuery = "";
		String requestedUserNameQuery = "";
		String userQuery = "";
		String privateGroupQuery = "";
		String groupIdQuery = "";
		String queryString = "";

		QuerySortContainer qf = new QuerySortContainer();
	
		int allowedGroupsIterator = 0;
		for ( String groupName : GroupNames){
			if (allowedGroupsIterator>0) allowedGroupNames += " OR ";
			allowedGroupNames += groupName;
			allowedGroupsIterator++;
		}
		
		LOGGER.debug("LuceneBibTex: allowedGroups: " + allowedGroupNames);		

		if ( (searchTerms != null) && (!searchTerms.isEmpty()) )
		{
			/* parse search_terms for forbidden characters
			 * forbidden characters are those, which will harm the lucene query
			 * forbidden characters are & | ( ) { } [ ] ~ * ^ ? : \
			 */
			searchTerms = Utils.replaceSpecialLuceneChars(searchTerms);
			mergedFiledQuery = lField_merged + ":("+ searchTerms +") ";
		}
		allowedGroupNamesQuery = lField_group+":("+allowedGroupNames+")";
		privateGroupQuery = lField_group+":(private)";
			
		if ( (UserName != null) && (!UserName.isEmpty()) )
		{
			UserName = Utils.replaceSpecialLuceneChars(UserName);
			userQuery  = lField_user + ":("+ UserName +")";
		}

		if ( (requestedUserName != null) && (!requestedUserName.isEmpty()) )
		{
			requestedUserName = Utils.replaceSpecialLuceneChars(requestedUserName);
			requestedUserNameQuery  = " AND " + lField_user + ":("+ requestedUserName +")";
		}

		if ((null!=group) && (!group.isEmpty()))
		{
			groupIdQuery = " AND " + lField_group+":("+group+")";
		}

		// assemble query string 
		queryString = mergedFiledQuery + requestedUserNameQuery + groupIdQuery ;
		if (!userQuery.isEmpty()) { // logged in user 
			queryString += " AND ( " + allowedGroupNamesQuery + " OR ("+privateGroupQuery+" AND "+userQuery+") ) ";
		}
		else
		{
			queryString += " AND " + allowedGroupNamesQuery;
		}

		QueryParser myParser = new QueryParser("description", analyzer);
		Query query = null;

		Sort sort = null;
		if ("relevance".equals(orderBy)) {
			myParser.setDefaultOperator(QueryParser.Operator.OR); // is default
			sort = new Sort(new SortField[]{
					SortField.FIELD_SCORE,	
					new SortField("date",true)
  			});
		}
		else 
		{ // orderBy=="date"
			myParser.setDefaultOperator(QueryParser.Operator.AND);
			sort = new Sort("date",true);
		}

		LOGGER.debug("LuceneSearchBibTex: QueryParser.DefaultOperator: "+ myParser.getDefaultOperator() );

		
		try {
			query = myParser.parse(queryString);
		} catch (ParseException e) {
			LOGGER.debug("LuceneSearchBibTex: ParseException: "+ e.getMessage());
		}

		qf.setQuery(query);
		qf.setSort(sort);
		
		return qf;
		
	}
	
	private QuerySortContainer getAuthorQueryFilter (String group,  String search, String requestedUserName, String requestedGroupName, String year, String firstYear, String lastYear, List<String> tagList) {
		final Log LOGGER = LogFactory.getLog(LuceneSearchBibTex.class);

//		String orderBy = "relevance"; 
		String orderBy = "date"; 

		String searchQuery = "";
		String requestedUserNameQuery = "";
		String requestedGroupNameQuery = "";
		String groupIdQuery  = "";
		String yearQuery = "";
		String tagListQuery = ""; 
		Boolean includeLowerBound = false;
		Boolean includeUpperBound = false;

		String queryString = "";

		// debug
		//firstYear = "1900"; 
		//lastYear = "2222";
		
		QuerySortContainer qf = new QuerySortContainer();

		LOGGER.debug("-----group:    " + group);
		LOGGER.debug("-----search:       " + search);
		LOGGER.debug("-----reqUserName:  " + requestedUserName);
		LOGGER.debug("-----reqGroupName: " + requestedGroupName);
		LOGGER.debug("-----year:         " + year);
		LOGGER.debug("-----firstYear:    " + firstYear);
		LOGGER.debug("-----lastYear:     " + lastYear);
		
//		queryString = "+(+mergedfields:telefon) +((group:kde group:public) (+group:private +user_name:bugsbunny))";
		
		
		if ( (search != null) && (!search.isEmpty()) )
		{
			/* parse search_terms for forbidden characters
			 * forbidden characters are those, which will harm the lucene query
			 * forbidden characters are & | ( ) { } [ ] ~ * ^ ? : \
			 */
			search = Utils.replaceSpecialLuceneChars(search);
			searchQuery = lField_author + ":("+ search +")";
		}

		if ( (requestedUserName != null) && (!requestedUserName.isEmpty()) )
		{
			requestedUserNameQuery = Utils.replaceSpecialLuceneChars(requestedUserNameQuery);
			requestedUserNameQuery  = " AND " + lField_user + ":("+ requestedUserName +")";
		}
		
		if ( (requestedGroupName != null) && (!requestedGroupName.isEmpty()) )
		{
			requestedGroupNameQuery  = " AND " + lField_group + ":("+ requestedGroupName +")";
		}

		if ((null!=group) && (!group.isEmpty()))
		{
			groupIdQuery = " AND " + lField_group+":("+group+")";
		}

		
		if ( (year != null) && (!year.isEmpty()) )
		{
			year = year.replaceAll("\\D", "");
			yearQuery = " AND " + lField_year + ":"+ year;
		}
		else
		{

			// firstYear != null, lastYear != null
			if ( (firstYear != null) && (lastYear != null) )
			{
				if ( (!firstYear.isEmpty()) && (!lastYear.isEmpty()) )
				{
					firstYear = firstYear.replaceAll("\\D", "");
					lastYear = lastYear.replaceAll("\\D", "");
					includeLowerBound = true; 
					includeUpperBound = true;
				}
			}
	
			// firstYear != null, lastYear == null
			if ( (firstYear != null) && (lastYear == null) )
			{
				if (!firstYear.isEmpty())
				{
					firstYear = firstYear.replaceAll("\\D", "");
					includeLowerBound = true; 
				}
				else
				{
					includeLowerBound = false; 
				}
				includeUpperBound = false; 
			}
	
			// firstYear == null, lastYear != null
			if ( (firstYear == null) && (lastYear != null) )
			{
				includeLowerBound = false; 
				if (!lastYear.isEmpty())
				{
					lastYear = lastYear.replaceAll("\\D", "");
					includeUpperBound = true; 
				}
				else
				{
					includeUpperBound = false; 
				}
			}
		}

		// TagIndex
		if ( (tagList != null) && (!tagList.isEmpty()) )
		{
			int tagListIterator = 0;
			LOGGER.debug("LuceneSearchBibTex: tagList == "+ Utils.replaceSpecialLuceneChars(tagList.toString()) );
			for ( String tagItem : tagList){
				if (tagListIterator>0) tagListQuery += " ";
				tagListQuery += Utils.replaceSpecialLuceneChars(tagItem);
				tagListIterator++;
			}

			tagListQuery = " AND " + lField_tas + ":(" + tagListQuery + ")";

		}
		else
		{
			LOGGER.debug("LuceneSearchBibTex: tagList == null!");
		}
		
		
		// assemble query string 
		queryString = searchQuery + requestedUserNameQuery + groupIdQuery + tagListQuery;

		QueryParser myParser = new QueryParser(lField_desc, analyzer);
		Query query = null;
		

		Sort sort = null;
		if ("relevance".equals(orderBy)) {
			myParser.setDefaultOperator(QueryParser.Operator.OR); // is default
			sort = new Sort(new SortField[]{
					SortField.FIELD_SCORE,	
					new SortField("date",true)
  			});
		}
		else 
		{ // orderBy=="date"
			myParser.setDefaultOperator(QueryParser.Operator.AND);
			sort = new Sort("date",true);
		}

		LOGGER.debug("LuceneSearchBibTex: QueryParser.DefaultOperator: "+ myParser.getDefaultOperator() );

		try {
			query = myParser.parse(queryString);
		} catch (ParseException e) {
			LOGGER.debug("LuceneSearchBibTex: ParseException: "+ e.getMessage());
		}
		
		FilteredQuery filteredQuery = null;

		// if upper or lower bound is given, then use filter
		if (includeLowerBound || includeUpperBound)
		{
			RangeFilter rangeFilter=new RangeFilter(lField_year , firstYear, lastYear, includeLowerBound, includeUpperBound);
			filteredQuery=new FilteredQuery(query,rangeFilter);
			qf.setQuery(filteredQuery);
		}
		else
		{
			qf.setQuery(query);
		}
    		
		qf.setSort(sort);


// resourceType, groupingEntity, groupingName, tags, hash, order, filter, start, start + itemsPerPage, search		
/*
author
group
UserName
GroupName
year

#requestedUserName#

SELECT b.address, b.annote, b.booktitle, b.chapter, b.crossref, b.edition, b.howpublished,
b.institution, b.journal, b.bkey, b.month, b.note, b.number, b.organization, b.pages, b.publisher,
b.school, b.series, b.type, b.volume, b.day, b.url, b.content_id, b.description, b.bibtexKey, b.misc,
b.bibtexAbstract, b.user_name, b.date, b.title, b.author, b.editor, b.year, b.entrytype, b.scraperid,
b.simhash1 AS interHash, b.simhash2 AS intraHash, t.tag_name, h.ctr as count,NULL AS `group`, NULL AS group_name
.toString()
FROM bibtex b, tas t, bibhash h,  

~~~
~	    tas t1
~		<iterate property="tagIndex">
~			<isGreaterThan property="tagIndex[].index" compareValue="1">
~				JOIN tas t$tagIndex[].index$ USING (content_id)
~			</isGreaterThan>
~		</iterate>

?? tas t1 JOIN tas t2 USING (content_id) JOIN tas t3 USING (content_id) JOIN tas t4 USING (content_id) ??

~~~ 
	(SELECT content_id FROM search_bibtex s
	WHERE MATCH (s.author) AGAINST (#search# IN BOOLEAN MODE) 
		AND s.group = #groupType#
~~~		AND s.content_id = t1.content_id

isNotNull "requestedUserName"
		AND s.user_name = #requestedUserName#

isNotNull "requestedGroupName"
		AND s.user_name IN (SELECT user_name 
			FROM groupids g JOIN groups gs ON (g.group = gs.group)
	        	WHERE g.group_name = #requestedGroupName#)


~~~
~ 		AND 
~		<iterate property="tagIndex" conjunction="AND">
~		  <isEqual property="caseSensitiveTagNames" compareValue="true">
~		    t$tagIndex[].index$.tag_name = #tagIndex[].tagName#
~		  </isEqual>
~		  <isEqual property="caseSensitiveTagNames" compareValue="false">
~		    t$tagIndex[].index$.tag_lower = lower(#tagIndex[].tagName#)
~		  </isEqual>
~		</iterate>

?? AND t1.tag_name = #tagIndex[].tagName# AND t2.tag_name = #tagIndex[].tagName# AND t3.tag_name = #tagIndex[].tagName# AND t4.tag_name = #tagIndex[].tagName#
??     tX.tag_lower= lower(#tagIndex[].tagName#)

~~~

	ORDER BY s.date DESC LIMIT #limit# OFFSET #offset#) AS tt 
WHERE b.content_id = tt.content_id    
	AND t.content_id = tt.content_id 

isNotNull "year"
	CAST(b.year AS SIGNED) = #year#

isNotNull "firstYear" AND isNotNull "lastYear"
	CAST(b.year AS SIGNED) BETWEEN #firstYear# AND #lastYear#

isNotNull "firstYear" AND isNull "lastYear"
	CAST(b.year AS SIGNED) >= #firstYear#

isNotNull "lastYear" AND isNull "firstYear"
	CAST(b.year AS SIGNED) <= #lastYear#

	AND b.simhash$simHash$ = h.hash AND h.type = $simHash$


ORDER BY b.date DESC, b.content_id DESC;
 */
		
		
		return qf;
	}

	/** get ArrayList of strings of field id from lucene index
	 * 
	 * for pagination see http://www.gossamer-threads.com/lists/lucene/general/70516#70516
	 * 
	 * @param String idname fieldname of returning value
	 * */
	private ResultList<Post<BibTex>> searchLucene(QuerySortContainer qs, int limit, int offset) {
		final Log LOGGER = LogFactory.getLog(LuceneSearchBibTex.class);

		Query query = qs.getQuery(); 
		Sort sort = qs.getSort(); 
		
		
		if (this.searcher == null)
		{
			LOGGER.error("LuceneBibTex: searcher is NULL!");
		}
		
		ResultList<Post<BibTex>> postBibTexList = new ResultList<Post<BibTex>>();

		// do not search for nothing in lucene index
		if (query != null)
		{




/* sort first by date and then by score. This is not necessary, because there are 
 * no or only few entries with same date (date is with seconds) 			
  			Sort sort = new Sort(new SortField[]{
												new SortField("date",true),
												SortField.FIELD_SCORE	
						});
*/			
			try {
				//query = myParser.parse(querystring);
				LOGGER.debug("LuceneBibTex-Querystring (analyzed):  " + query.toString());
				LOGGER.debug("LuceneBibTex-Query will be sorted by:  " + sort);
				
				LOGGER.debug("LuceneBibTex: searcher:  " + searcher);
				
				long starttimeQuery = System.currentTimeMillis();
				final TopDocs topDocs = searcher.search(query, null , offset+limit, sort);
				long endtimeQuery = System.currentTimeMillis();
				LOGGER.debug("LuceneBibTex pure query time: " + (endtimeQuery-starttimeQuery) + "ms");

				int hitslimit = (((offset+limit)<topDocs.totalHits)?(offset+limit):topDocs.totalHits);

				postBibTexList.setTotalCount (topDocs.totalHits);
				
				LOGGER.debug("LuceneBibTex:  offset / limit / hitslimit / hits.length():  " + offset + " / " + limit + " / " + hitslimit + " / " + topDocs.totalHits);
				
				for(int i = offset; i < hitslimit; i++){
					Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
					//cidsArray.add(Integer.parseInt(doc.get(idname)));
					
					BibTex bibTex = new BibTex();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s.S");
					
					Post<BibTex> postBibTex = new Post();
					Date date = new Date();
					try {
						date = dateFormat.parse(doc.get(lField_date));
					} catch (java.text.ParseException e) {
						LOGGER.debug("LuceneBibTex: ParseException (date): "+ e.getMessage());
					}

					
					bibTex.setAbstract(doc.get("bibtexAbstract"));
					bibTex.setAddress(doc.get("address"));
					bibTex.setAnnote(doc.get("annote"));
					bibTex.setAuthor(doc.get("author"));
					bibTex.setBibtexKey(doc.get("bibtexKey"));
					bibTex.setKey(doc.get("bKey"));
					bibTex.setBooktitle(doc.get("booktitle"));
					bibTex.setChapter(doc.get("chapter"));
					//bibTex.setCount(doc.get(""));
					bibTex.setCrossref(doc.get("crossref"));
					bibTex.setDay(doc.get("day"));
					//bibTex.setDocuments(doc.get(""));
					bibTex.setEdition(doc.get("edition"));
					bibTex.setEditor(doc.get("editor"));
					bibTex.setEntrytype(doc.get("entrytype"));
					//bibTex.setExtraUrls(doc.get(""));
					bibTex.setHowpublished(doc.get("howPublished"));
					bibTex.setInstitution(doc.get("institution"));
					bibTex.setInterHash(doc.get("interhash"));
					bibTex.setIntraHash(doc.get("intrahash"));
					bibTex.setJournal(doc.get("journal"));
					bibTex.setMisc(doc.get("misc"));
					bibTex.setMonth(doc.get("month"));
					bibTex.setNote(doc.get("note"));
					bibTex.setNumber(doc.get("number"));
					//bibTex.setOpenURL(doc.get(""));
					bibTex.setOrganization(doc.get("organization"));
					bibTex.setPages(doc.get("pages"));
					//bibTex.setPosts(doc.get(""));
					//bibTex.setPrivnote(doc.get(""));
					bibTex.setPublisher(doc.get("publisher"));
					bibTex.setSchool(doc.get("school"));
					//bibTex.setScraperId(doc.get(""));
					bibTex.setSeries(doc.get("series"));
					bibTex.setTitle(doc.get("title"));
					bibTex.setType(doc.get("type"));
					bibTex.setUrl(doc.get("url"));
					bibTex.setVolume(doc.get("volume"));
					bibTex.setYear(doc.get("year"));
					
					/*
					 * finished filling bibtex object ... set hashes
					 */
					//bibTex.recalculateHashes();
					
					for (String group: doc.get(lField_group).split(",")) {
						postBibTex.addGroup(group);
					}

					for (String tag: doc.get(lField_tas).split(" ")) {
						postBibTex.addTag(tag);
					}
					
					postBibTex.setContentId(Integer.parseInt(doc.get(lField_contentid)));
					long starttime2Query = System.currentTimeMillis();
					bibTex.setCount(this.searcher.docFreq(new Term("intrahash", doc.get("intrahash"))));
					long endtime2Query = System.currentTimeMillis();
//					LOGGER.debug("LuceneBibTex query time for postcount: " + (endtime2Query-starttime2Query) + "ms");
//					LOGGER.debug("LuceneBibTex:  ContentID (intrahash) = bibTex.getCount:  " + postBibTex.getContentId() + " ("+ doc.get("intrahash") +") = " + bibTex.getCount());
					
					postBibTex.setDate(date);
					postBibTex.setDescription(doc.get(lField_ext));
					postBibTex.setResource(bibTex);
					postBibTex.setUser(new User(doc.get(lField_user)));

					
//					LOGGER.debug("LuceneBibTex:  doc.get("+lField_user+"): " + doc.get(lField_user));
//					LOGGER.debug("LuceneBibTex:  postBibTex.getUser().getName():  " + postBibTex.getUser().getName());

//					LOGGER.debug("LuceneBibTex:  postBibTex.getContentId:  " + postBibTex.getContentId());
//					LOGGER.debug("LuceneBibTex:  postBibTex.getDate:  " + postBibTex.getDate());
//					LOGGER.debug("LuceneBibTex:  postBibTex.getDescription:  " + postBibTex.getDescription());
//					LOGGER.debug("LuceneBibTex:  postBibTex.getGroups:  " + postBibTex.getContentId() + ": " + postBibTex.getGroups());
//					LOGGER.debug("LuceneBibTex:  postBibTex.getResource().getIntraHash():  " + postBibTex.getContentId() + ": " + postBibTex.getResource().getIntraHash()  );
//					LOGGER.debug("LuceneBibTex:  postBibTex.getResource:  " + postBibTex.getResource());
//					LOGGER.debug("LuceneBibTex:  postBibTex.getTags:    " + postBibTex.getContentId() + ": " + postBibTex.getTags());
//					LOGGER.debug("LuceneBibTex:  postBibTex.getUser:  " + postBibTex.getUser());
					
					
					postBibTexList.add(postBibTex);

					
				}	 

			} catch (IOException e) {
				LOGGER.debug("LuceneBibTex: IOException: "+ e.getMessage());
			}		


		}

		//return cidsArray;
		return postBibTexList;
	};

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.lucene.LuceneSearch#searchLucene(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Set, int, int)
	 */
	public ResultList<Post<BibTex>> searchLucene(String group, String search_terms, final String requestedUserName, String UserName, Set<String> GroupNames, int limit, int offset) {
		return searchLucene(getFulltextQueryFilter(group, search_terms, requestedUserName, UserName, GroupNames), limit, offset);
	}
	
	// resourceType, groupingEntity, groupingName, tags, hash, order, filter, start, start + itemsPerPage, search
/*
	 (Class<T>, GroupingEntity, String, List<String>, String, Order, FilterEntity, int, int, String)
*/
	
	public ResultList<Post<BibTex>> searchAuthor(String group, String search, String requestedUserName, String requestedGroupName, String year, String firstYear, String lastYear, List<String> tagList, int limit, int offset) {
		return searchLucene(getAuthorQueryFilter(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagList), limit, offset);
	}

	
	public LuceneIndexStatistics getStatistics() {
		return Utils.getStatistics(lucenePath);
	}
}