package org.bibsonomy.lucene.search;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ValidationUtils;

//FIXME: this needs further cleanup

public class LuceneSearchBibTex extends LuceneResourceSearch<BibTex> {
	private static final String CFG_RELEVANCE = "relevance";
	final Log LOGGER = LogFactory.getLog(LuceneSearchBibTex.class);
	final Log log = LogFactory.getLog(LuceneSearchBibTex.class);

	private final static LuceneSearchBibTex singleton = new LuceneSearchBibTex();
	
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

	/**
	 * constructor
	 */
	private LuceneSearchBibTex() {
		reloadIndex();
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
	/*
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
			// parse search_terms for forbidden characters
			// forbidden characters are those, which will harm the lucene query
			// forbidden characters are & | ( ) { } [ ] ~ * ^ ? : \
			//
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
	*/

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
	protected QuerySortContainer buildAuthorQuery(
			String group,  
			String searchTerms, 
			String requestedUserName, String requestedGroupName, 
			String year, String firstYear, String lastYear, 
			List<String> tagList) {
		// FIXME: configure this
//		String orderBy = "relevance"; 
		String orderBy = "date"; 
		
		// prepare input (analyze and escape)
		try {
			group              = parseToken(FLD_GROUP, group);
			searchTerms        = parseToken(FLD_MERGEDFIELDS, searchTerms);
			requestedUserName  = parseToken(FLD_USER, requestedUserName);
			requestedGroupName = parseToken(FLD_GROUP, requestedGroupName);
			year               = parseToken(FLD_YEAR, year);
			firstYear          = parseToken(FLD_YEAR, firstYear);
			lastYear           = parseToken(FLD_YEAR, lastYear);
			// parse each tag name
			if(ValidationUtils.present(tagList)) {
				List<String> tags = new LinkedList<String>();
				for(String tagName : tagList) {
					tags.add(parseToken(FLD_GROUP, tagName)); 
				}
				tagList = tags;
			}
		} catch (IOException e) {
			log.error("Error analyzing input", e);
		}
		QuerySortContainer qf = new QuerySortContainer();
		
		//--------------------------------------------------------------------
		// set ordering
		//--------------------------------------------------------------------
		Sort sort = null;
		if (CFG_RELEVANCE.equals(orderBy)) {
			sort = new Sort(new SortField[]{
					SortField.FIELD_SCORE,	
					new SortField(FLD_DATE,true)
  			});
		} else { 
			// orderBy=="date"
			sort = new Sort("date",true);
		}
		qf.setSort(sort);
		
		//--------------------------------------------------------------------
		// build query
		//--------------------------------------------------------------------
		BooleanQuery mainQuery       = new BooleanQuery();
		BooleanQuery accessModeQuery = new BooleanQuery();
		BooleanQuery privatePostQuery= new BooleanQuery();
		//--------------------------------------------------------------------
		// search terms
		//--------------------------------------------------------------------
		// we parse the (escaped) search term for enabling advanced lucene 
		// search queries 
		QueryParser searchTermParser = new QueryParser(FLD_AUTHOR, getAnalyzer());
		if (CFG_RELEVANCE.equals(orderBy)) {
			searchTermParser.setDefaultOperator(QueryParser.Operator.OR); // is default
		} else { 
			// orderBy=="date"
			searchTermParser.setDefaultOperator(QueryParser.Operator.AND);
		}
		Query authorQuery = null;
		if( ValidationUtils.present(searchTerms) ) {
			try {
				authorQuery = searchTermParser.parse(searchTerms);
			} catch (ParseException e) {
				authorQuery = new TermQuery(new Term(FLD_AUTHOR, searchTerms) );
			}
			mainQuery.add(authorQuery, Occur.MUST);
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
		// FIXME: this isn't used - what is the difference between 
		//        'requestedGroupName' and 'group'?
		//--------------------------------------------------------------------
		/*
		if ( ValidationUtils.present(requestedGroupName) ) {
			mainQuery.add(
					new TermQuery(new Term(FLD_GROUP, requestedGroupName)),
					Occur.MUST
					);
		}
		*/
		//--------------------------------------------------------------------
		// post owned by group
		//--------------------------------------------------------------------
		if ( ValidationUtils.present(group) ) {
			mainQuery.add( new TermQuery(new Term(FLD_GROUP, group)), Occur.MUST );
		}
		
		//--------------------------------------------------------------------
		// exact year query
		// FIXME: this wasn't used
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
		
		//--------------------------------------------------------------------
		// restrict to given tags
		//--------------------------------------------------------------------
		BooleanQuery tagQuery = new BooleanQuery();
		if( ValidationUtils.present(tagList) ) {
			for ( String tagItem : tagList){
				tagQuery.add(new TermQuery(new Term(FLD_TAS, tagItem)), Occur.MUST);
			}
			mainQuery.add(tagQuery, Occur.MUST);
		}
		
		//--------------------------------------------------------------------
		// build final query
		//--------------------------------------------------------------------
		if (includeLowerBound || includeUpperBound) {
			// if upper or lower bound is given, then use filter
			FilteredQuery filteredQuery = null;
			RangeFilter rangeFilter=new RangeFilter(FLD_YEAR , firstYear, lastYear, includeLowerBound, includeUpperBound);
			filteredQuery=new FilteredQuery(mainQuery,rangeFilter);
			qf.setQuery(filteredQuery);
		} else {
			qf.setQuery(mainQuery);
		}
		log.debug("Search query: " + qf.getQuery().toString());

		// all done.
		return qf;
	}
	
	/*
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
		if (CFG_RELEVANCE.equals(orderBy)) {
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
		
		buildAuthorQuery(group, search, requestedUserName, requestedGroupName, year, firstYear, lastYear, tagList);
		
		return qf;
	}
	*/

	/** get ArrayList of strings of field id from lucene index
	 * 
	 * for pagination see http://www.gossamer-threads.com/lists/lucene/general/70516#70516
	 * 
	 * FIXME: refactor this method
	 * 
	 * @param String idname fieldname of returning value
	 * */
	@Override
	protected ResultList<Post<BibTex>> searchLucene(QuerySortContainer qs, int limit, int offset) {
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


	@Override
	protected Class<BibTex> getResourceType() {
		return BibTex.class;
	}
}