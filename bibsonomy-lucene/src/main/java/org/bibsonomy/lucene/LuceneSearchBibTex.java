package org.bibsonomy.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.RAMDirectory;
import org.bibsonomy.common.enums.GroupID;


public class LuceneSearchBibTex {

	private final static LuceneSearchBibTex singleton = new LuceneSearchBibTex();
	private IndexSearcher searcher; 
	private PerFieldAnalyzerWrapper analyzer;
		


	private LuceneSearchBibTex() throws RuntimeException {
		final Logger LOGGER = Logger.getLogger(LuceneSearchBibTex.class);
		try {

			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");

			Boolean loadIndexIntoRAM = (Boolean) envContext.lookup("luceneIndexPublicationsLoadIntoRAM");
			
			
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
				LOGGER.debug("LuceneBibTex: load index into RAM");
				long starttime = System.currentTimeMillis();
				RAMDirectory BibTexIndexRAM = new RAMDirectory ((String) envContext.lookup("luceneIndexPathPublications"));
				this.searcher = new IndexSearcher( BibTexIndexRAM );
				long endtime = System.currentTimeMillis();
				LOGGER.debug("LuceneBibTex: index loaded into RAM in "+ (endtime-starttime) + "ms");
			}
			else
			{	
				// load and hold index on physical hard disk
				LOGGER.debug("LuceneBookmark: use index from disk");
				this.searcher = new IndexSearcher( (String) envContext.lookup("luceneIndexPathPublications") );
			}
		} catch (final NamingException e) {
			System.out.println("NamingException in LuceneSearchBibTex.LuceneSearchBibTex()");
			/*
			 * FIXME: rethrowing the exception as runtime ex is maybe not the best solution
			 */
			e.printStackTrace();
//			throw new RuntimeException(e);
		} catch (CorruptIndexException e) {
			System.out.println("CorruptIndexException in LuceneSearchBibTex.LuceneSearchBibTex()");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException in LuceneSearchBibTex.LuceneSearchBibTex()");
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
					LOGGER.debug("LuceneBibTex: doc.get("+idname+")="+doc.get(idname));
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
