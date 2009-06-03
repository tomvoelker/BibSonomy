package org.bibsonomy.lucene;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;

public class LuceneSearchBibTex {

	private final static LuceneSearchBibTex singleton = new LuceneSearchBibTex();
	private IndexSearcher searcher; 
	private PerFieldAnalyzerWrapper analyzer;
	private String lucenePath;
		

	private LuceneSearchBibTex() {
		reloadIndex();
	}
	

	public void reloadIndex() throws RuntimeException {
		final Logger LOGGER = Logger.getLogger(LuceneSearchBibTex.class);
		try {

			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			Boolean loadIndexIntoRAM = (Boolean) envContext.lookup("luceneIndexPublicationsLoadIntoRAM");
			String lucenePath = (String) envContext.lookup("luceneIndexPathPublications");
			this.lucenePath = lucenePath;
			
			LOGGER.debug("LuceneBibTex: use index: " + lucenePath);

			/* set current path to lucene index, given by environment parameter in tomcat's context.xml
			 * 
			 *   <Environment name="luceneIndexPath" type="java.lang.String" value="/home/bibsonomy/lucene"/>
			 */


			if (this.analyzer == null)
			{
				/** lucene analyzer, must be the same as at indexing */
				//SimpleAnalyzer analyzer = new SimpleAnalyzer();
				this.analyzer = new PerFieldAnalyzerWrapper(new SimpleAnalyzer());
	
				// let field group of analyzer use SimpleKeywordAnalyzer
				// numbers will be deleted by SimpleAnalyser but group has only numbers, therefore use SimpleKeywordAnalyzer 
				this.analyzer.addAnalyzer("group", new SimpleKeywordAnalyzer());
			}
			
			
			try {
				if (null != this.searcher) this.searcher.close();
			} catch (IOException e) {
				LOGGER.debug("LuceneBibTex: IOException on searcher.close: "+ e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e)
			{
				LOGGER.debug("LuceneBibTex: RuntimeException on searcher.close: "+ e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			if (loadIndexIntoRAM) {
				// load a copy of index in memory
				// changes will have no effect on original index!
				// make sure complete index fill fit into memory!
				LOGGER.debug("LuceneBibTex: load index into RAM");
				long starttime = System.currentTimeMillis();
				RAMDirectory BibTexIndexRAM = new RAMDirectory ( lucenePath );
				this.searcher = new IndexSearcher( BibTexIndexRAM );
				long endtime = System.currentTimeMillis();
				LOGGER.debug("LuceneBibTex: index loaded into RAM in "+ (endtime-starttime) + "ms");
			}
			else
			{	
				// load and hold index on physical hard disk
				LOGGER.debug("LuceneBibTex: use index from disk");
				LOGGER.debug("this.searcher-0: " + this.searcher);
				this.searcher = new IndexSearcher( lucenePath );
				LOGGER.debug("this.searcher-1: " + this.searcher);
			}
		} catch (final NamingException e) {
			LOGGER.debug("LuceneBookmark: NamingException "+ e.getExplanation() + " ## " + e.getMessage());
			/*
			 * FIXME: rethrowing the exception as runtime ex is maybe not the best solution
			 */
			throw new RuntimeException(e);
		} catch (CorruptIndexException e) {
			LOGGER.debug("LuceneBookmark: CorruptIndexException "+ e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.debug("LuceneBookmark: IOException "+ e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			LOGGER.debug("LuceneBookmark: RuntimeException "+ e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBibTex getInstance() {
		return singleton;
	}



	/** get ArrayList of strings of field id from lucene index
	 * 
	 * for pagination see http://www.gossamer-threads.com/lists/lucene/general/70516#70516
	 * 
	 * @param String idname fieldname of returning value
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * */
	public ArrayList<Integer> searchLucene(String idname, String search_terms, int groupId, int limit, int offset) throws IOException {
		final Logger LOGGER = Logger.getLogger(LuceneSearchBibTex.class);
			

		if (this.searcher == null)
		{
			LOGGER.error("LuceneBibTex: searcher is NULL!");
			
		}
		
		Boolean debug = false;
		String queryFields = "";
		String querystring = "";


		ArrayList<String> bibTexField = new ArrayList<String> ();
		ArrayList<String> bibTexFieldAll = new ArrayList<String> ();

		bibTexField.add("user_name");
		bibTexField.add("author");
		bibTexField.add("editor");
		bibTexField.add("title");
		bibTexField.add("journal");
		bibTexField.add("booktitle");
		bibTexField.add("volume");
		bibTexField.add("number");
		bibTexField.add("chapter");
		bibTexField.add("edition");
		bibTexField.add("month");
		bibTexField.add("day");
		bibTexField.add("howPublished");
		bibTexField.add("institution");
		bibTexField.add("organization");
		bibTexField.add("publisher");
		bibTexField.add("address");
		bibTexField.add("school");
		bibTexField.add("series");
		bibTexField.add("bibtexKey");
		bibTexField.add("url");
		bibTexField.add("type");
		bibTexField.add("description");
		bibTexField.add("annote");
		bibTexField.add("note");
		bibTexField.add("pages");
		bibTexField.add("bKey");
		bibTexField.add("crossref");
		bibTexField.add("misc");
		bibTexField.add("bibtexAbstract");
		bibTexField.add("year");
		bibTexField.add("tas");		
		
		bibTexFieldAll.addAll(bibTexField);
		bibTexFieldAll.add("content_id");
		bibTexFieldAll.add("group");
		bibTexFieldAll.add("date");
		bibTexFieldAll.add("entrytype");
		bibTexFieldAll.add("interhash");
		bibTexFieldAll.add("intrahash");

		
		for (String btField:bibTexField)
		{
			queryFields += btField + ":("+ search_terms +") ";
		}
		if (GroupID.INVALID.getId() == groupId)
		{
			// query without groupID
			querystring = queryFields;
		}
		else
		{
			// query with groupID
			querystring = "group:\""+groupId+"\" AND ("+queryFields+")" ;
		}

		LOGGER.debug("LuceneBibTex-Querystring (assembled): " + querystring);

		// declare ArrayList cidsArray for list of String to return
		final ArrayList<Integer> cidsArray = new ArrayList<Integer>();


		// do not search for nothing in lucene index
		if ( (search_terms != null) && (!search_terms.isEmpty()) )
		{


			// open lucene index
			//IndexReader reader = IndexReader.open(luceneIndexPath);


			QueryParser myParser = new QueryParser("description", analyzer);
			Query query;
			Sort sort = new Sort("date",true);
/* sort first by date and then by score. This is not necessary, because there are 
 * no or only few entries with same date (date is with seconds) 			
  			Sort sort = new Sort(new SortField[]{
												new SortField("date",true),
												SortField.FIELD_SCORE	
						});
*/			
			try {
				query = myParser.parse(querystring);
				LOGGER.debug("LuceneBibTex-Querystring (analyzed):  " + query.toString());
				LOGGER.debug("LuceneBibTex-Query will be sorted by:  " + sort);
				
				LOGGER.debug("LuceneBibTex: searcher:  " + searcher);
				

				long starttimeQuery = System.currentTimeMillis();
				final Hits hits = this.searcher.search(query,sort);
				long endtimeQuery = System.currentTimeMillis();
				LOGGER.debug("LuceneBibTex pure query time: " + (endtimeQuery-starttimeQuery) + "ms");

				int hitslimit = (((offset+limit)<hits.length())?(offset+limit):hits.length());

				LOGGER.debug("LuceneBibTex:  offset / limit / hitslimit / hits.length():  " + offset + " / " + limit + " / " + hitslimit + " / " + hits.length());
				
				for(int i = offset; i < hitslimit; i++){
					Document doc = hits.doc(i);
					cidsArray.add(Integer.parseInt(doc.get(idname)));
				}	 

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		


		}

		return cidsArray;
	};

	
	
	/** get ArrayList of strings of field id from lucene index
	 * 
	 * for pagination see http://www.gossamer-threads.com/lists/lucene/general/70516#70516
	 * 
	 * @param String idname fieldname of returning value
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * */
	public ResultList<Post<BibTex>> searchLucene(int groupId, String search_terms, final String requestedUserName, String UserName, Set<String> GroupNames, int limit, int offset) throws IOException {
		final Logger LOGGER = Logger.getLogger(LuceneSearchBibTex.class);
			

		String lField_contentid = "content_id";
		String lField_group = "group";
		String lField_date = "date";
		String lField_user = "user_name";
		String lField_desc = "desc";
		String lField_ext = "ext";
		String lField_url = "url";
		String lField_tas = "tas";
		String lField_merged = "mergedfields";

		String allowedGroupNames = "";
		String allowedGroupNamesQuery = "";
		String mergedFiledQuery = "";
		String requestedUserNameQuery = "";
		String userQuery = "";
		String privateGroupQuery = "";
		String groupIdQuery = "";
		String querystring = "";
		
		if (this.searcher == null)
		{
			LOGGER.error("LuceneBibTex: searcher is NULL!");
			
/*
  			LOGGER.error("LuceneBibTex: trying to get a searcher ... !");
 			
			
			LOGGER.debug("LuceneBibTex: searcher:  " + searcher);
 */			
		}

		
		ResultList<Post<BibTex>> postBibTexList = new ResultList<Post<BibTex>>();

		LOGGER.debug("LuceneBibTex: groupID  " + groupId);
		LOGGER.debug("LuceneBibTex: UserName  " + UserName);
		LOGGER.debug("LuceneBibTex: GroupNames.toString()  " + GroupNames.toString());



		// do not search for nothing in lucene index
		if ( (search_terms != null) && (!search_terms.isEmpty()) )
		{

			int allowedGroupsIterator = 0;
			for ( String groupName : GroupNames){
				if (allowedGroupsIterator>0) allowedGroupNames += " ";
				allowedGroupNames += groupName;
				allowedGroupsIterator++;
			}
			
			LOGGER.debug("LuceneBibTex: allowedGroups: " + allowedGroupNames);		

			mergedFiledQuery = lField_merged + ":("+ search_terms +") ";
			allowedGroupNamesQuery = lField_group+":("+allowedGroupNames+")";
			privateGroupQuery = lField_group+":(private)";
				
			if ( (UserName != null) && (!UserName.isEmpty()) )
			{
				userQuery  = lField_user + ":("+ UserName +")";
			}

			if ( (requestedUserName != null) && (!requestedUserName.isEmpty()) )
			{
				requestedUserNameQuery  = " AND " + lField_user + ":("+ requestedUserName +")";
			}

			if ( (UserName != null) && (!UserName.isEmpty()) )
			{
				userQuery  = lField_user + ":("+ UserName +")";
			}

			if (GroupID.INVALID.getId() != groupId)
			{
				groupIdQuery = " AND " + lField_group+":("+groupId+")";
			}

			// assemble query string 
			querystring = mergedFiledQuery + requestedUserNameQuery + groupIdQuery ;
			if (!userQuery.isEmpty()) { // logged in user 
				querystring += " AND ( " + allowedGroupNamesQuery + " OR ("+privateGroupQuery+" AND "+userQuery+") ) ";
			}
			else
			{
				querystring += " AND " + allowedGroupNamesQuery;
			}
			
				
			LOGGER.debug("LuceneBibTex-Querystring (assembled): " + querystring);			
			
			

			// open lucene index
			//IndexReader reader = IndexReader.open(luceneIndexPath);


			QueryParser myParser = new QueryParser("description", analyzer);
			Query query;
			Sort sort = new Sort("date",true);
/* sort first by date and then by score. This is not necessary, because there are 
 * no or only few entries with same date (date is with seconds) 			
  			Sort sort = new Sort(new SortField[]{
												new SortField("date",true),
												SortField.FIELD_SCORE	
						});
*/			
			try {
				query = myParser.parse(querystring);
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					
					bibTex.setAbstract(doc.get("bibtexAbstract"));
					bibTex.setAddress(doc.get("address"));
					bibTex.setAnnote(doc.get("annote"));
					bibTex.setAuthor(doc.get("author"));
					bibTex.setBibtexKey(doc.get("bibtexKey"));
					bibTex.setBKey(doc.get("bKey"));
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
					LOGGER.debug("LuceneBibTex query time for postcount: " + (endtime2Query-starttime2Query) + "ms");
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

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		


		}

		//return cidsArray;
		return postBibTexList;
	};

}
