package org.bibsonomy.lucene;

import java.io.IOException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.RAMDirectory;
import org.bibsonomy.common.enums.GroupID;


public class LuceneSearchBookmarks {

	private final static LuceneSearchBookmarks singleton = new LuceneSearchBookmarks();
	private IndexSearcher searcher; 
	private PerFieldAnalyzerWrapper analyzer;




	private LuceneSearchBookmarks() throws RuntimeException {
		final Logger LOGGER = Logger.getLogger(LuceneSearchBookmarks.class);
		try {

			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");

			Boolean loadIndexIntoRAM = (Boolean) envContext.lookup("luceneIndexBookmarksLoadIntoRAM");
			
			/* set current path to lucene index, given by environment parameter in tomcat's context.xml
			 * 
			 *   <Environment name="luceneIndexPath" type="java.lang.String" value="/home/bibsonomy/lucene"/>
			 */

			/** lucene analyzer, must be the same as at indexing */
			//SimpleAnalyzer analyzer = new SimpleAnalyzer();
			this.analyzer = new PerFieldAnalyzerWrapper(new SimpleAnalyzer());


			// let field group of analyzer use SimpleKeywordAnalyzer
			// numbers will be deleted by SimpleAnalyser but group has only numbers, therefore use SimpleKeywordAnalyzer 
			this.analyzer.addAnalyzer("group", new SimpleKeywordAnalyzer());
			
			if (loadIndexIntoRAM) {
				// load a copy of index in memory
				// changes will have no effect on original index!
				// make sure complete index fill fit into memory!
				LOGGER.debug("LuceneBookmark: load index into RAM");
				long starttime = System.currentTimeMillis();
				RAMDirectory BookmarkIndexRAM = new RAMDirectory ((String) envContext.lookup("luceneIndexPathBoomarks"));
				this.searcher = new IndexSearcher( BookmarkIndexRAM );
				long endtime = System.currentTimeMillis();
				LOGGER.debug("LuceneBookmark: index loaded into RAM in "+ (endtime-starttime) + "ms");
			}
			else
			{	
				// load and hold index on physical hard disk
				LOGGER.debug("LuceneBookmark: use index from disk");
				this.searcher = new IndexSearcher( (String) envContext.lookup("luceneIndexPathBoomarks") );
			}
		} catch (final NamingException e) {
			/*
			 * FIXME: rethrowing the exception as runtime ex is maybe not the best solution
			 */
			throw new RuntimeException(e);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBookmarks getInstance() {
		return singleton;
	}



	/** get ArrayList of strings of field id from lucene index
	 * 
	 * for pagination see http://www.gossamer-threads.com/lists/lucene/general/70516#70516
	 * 
	 * @param String idname fieldname of returning value
	 * @param char LuceneIndex lucene index to use b for bookmar, p for publications
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * */
	public ArrayList<Integer> searchLucene(String idname, String search_terms, int groupId, int limit, int offset) throws IOException {
		final Logger LOGGER = Logger.getLogger(LuceneSearchBookmarks.class);
			
		Boolean debug = false;
		// field names in Lucene index
		String lField_contentid = "contentid";
		String lField_group = "group";
		String lField_date = "date";
		String lField_user = "user";
		String lField_desc = "desc";
		String lField_ext = "ext";
		String lField_url = "url";
		String lField_tas = "tas";
		String lField_type = "type";

		String querystring = "";



		if (GroupID.INVALID.getId() == groupId)
		{
			// query without groupID
			querystring = lField_desc + ":("+ search_terms +") " + lField_tas + ":("+ search_terms +") " + lField_ext + ":("+ search_terms +") " + lField_url + ":("+ search_terms +")" ;
		}
		else
		{
			// query with groupID
			querystring = lField_group+":\""+groupId+"\" AND (" + lField_desc + ":("+ search_terms +") " + lField_tas + ":("+ search_terms +") " + lField_ext + ":("+ search_terms +") " + lField_url + ":("+ search_terms +") )" ;
		}

		LOGGER.debug("LuceneBookmark-Querystring (assembled): " + querystring);

		// declare ArrayList cidsArray for list of String to return
		final ArrayList<Integer> cidsArray = new ArrayList<Integer>();


		// do not search for nothing in lucene index
		if ( (search_terms != null) && (!search_terms.isEmpty()) )
		{


			// open lucene index
			//IndexReader reader = IndexReader.open(luceneIndexPath);


			QueryParser myParser = new QueryParser(lField_desc, analyzer);
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
				LOGGER.debug("LuceneBookmark-Querystring (analyzed):  " + query.toString());
				LOGGER.debug("LuceneBookmark-Query will be sorted by:  " + sort);

				LOGGER.debug("LuceneBookmark: searcher:  " + searcher);
				
				long starttimeQuery = System.currentTimeMillis();
				final Hits hits = searcher.search(query,sort);
				long endtimeQuery = System.currentTimeMillis();
				LOGGER.debug("LuceneBookmark pure query time: " + (endtimeQuery-starttimeQuery) + "ms");

				int hitslimit = (((offset+limit)<hits.length())?(offset+limit):hits.length());

				LOGGER.debug("LuceneBookmark:  offset / limit / hitslimit / hits.length():  " + offset + " / " + limit + " / " + hitslimit + " / " + hits.length());
				
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


}
