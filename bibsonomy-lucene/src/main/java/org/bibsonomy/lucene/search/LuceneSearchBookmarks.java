package org.bibsonomy.lucene.search;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.bibsonomy.common.exceptions.LuceneException;
import org.bibsonomy.lucene.index.analyzer.SimpleKeywordAnalyzer;
import org.bibsonomy.lucene.param.LuceneIndexStatistics;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.lucene.util.Utils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.services.searcher.ResourceSearch;

//FIXME: this needs further cleanup

public class LuceneSearchBookmarks extends LuceneResourceSearch<Bookmark> {
	private static final Log log = LogFactory.getLog(LuceneSearchBookmarks.class);
	
	private final static LuceneSearchBookmarks singleton = new LuceneSearchBookmarks();

	private static final String FLD_URL = "url";

	private static final String FLD_DESC = "desc";

	private static final String FLD_TAS = "tas";

	private static final String FLD_CONTENT_ID = "content_id";

	private static final String FLD_INTRAHASH = "intrahash";

	private static final String FLD_EXT = "ext";
	
	/**
	 * constructor
	 */
	private LuceneSearchBookmarks() {
		reloadIndex();
	}

	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBookmarks getInstance() {
		return singleton;
	}

	/**
	 * FIXME: refactor this method
	 */
	@Override
	protected ResultList<Post<Bookmark>> searchLucene(
			QuerySortContainer qf, int limit, int offset) {
		ResultList<Post<Bookmark>> postBookmarkList = new ResultList<Post<Bookmark>>();
		
		Query query = qf.getQuery();
		Sort sort = qf.getSort();

		try {
			log.debug("LuceneBookmark-Querystring (analyzed):  "+ query.toString());
			log.debug("LuceneBookmark-Query will be sorted by: "+ sort);
			log.debug("LuceneBookmark: searcher:  " + searcher);

			long starttimeQuery = System.currentTimeMillis();
			final TopDocs topDocs = searcher.search(query, null, offset
					+ limit, sort);

			long endtimeQuery = System.currentTimeMillis();
			log.debug("LuceneBookmark pure query time: "
					+ (endtimeQuery - starttimeQuery) + "ms");

			int hitslimit = (((offset + limit) < topDocs.totalHits) ? (offset + limit)
					: topDocs.totalHits);

			postBookmarkList.setTotalCount(topDocs.totalHits);

			log
			.debug("LuceneBookmark:  offset / limit / hitslimit / hits.length():  "
					+ offset + " / " + limit + " / " + hitslimit + " / " + topDocs.totalHits);

			for (int i = offset; i < hitslimit; i++) {
				Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
				Bookmark bookmark = new Bookmark();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd H:m:s.S");

				Post<Bookmark> postBookmark = new Post<Bookmark>();
				Date date = new Date();
				try {
					date = dateFormat.parse(doc.get(FLD_DATE));
				} catch (java.text.ParseException e) {
					log.debug("LuceneBibTex: ParseException: " + e.getMessage());
				}
				bookmark.setUrl(doc.get(FLD_URL));
				bookmark.setTitle(doc.get(FLD_DESC));

				for (String g : doc.get(FLD_GROUP).split(",")) {
					postBookmark.addGroup(g);
				}

				for (String tag : doc.get(FLD_TAS).split(" ")) {
					postBookmark.addTag(tag);
				}

				postBookmark.setContentId(Integer.parseInt(doc
						.get(FLD_CONTENT_ID)));
				bookmark.setIntraHash(doc.get("intrahash"));
				bookmark.setInterHash(doc.get("intrahash")); // same as
				// intrahash

				postBookmark.setContentId(Integer.parseInt(doc
						.get(FLD_CONTENT_ID)));
				long starttime2Query = System.currentTimeMillis();
				if( doc.get(FLD_INTRAHASH)!=null ) {
					bookmark.setCount(this.searcher.docFreq(new Term(
							FLD_INTRAHASH, doc.get(FLD_INTRAHASH))));
				} else {
					bookmark.setCount(1);
				}
				long endtime2Query = System.currentTimeMillis();

				postBookmark.setDate(date);
				postBookmark.setDescription(doc.get(FLD_EXT));
				postBookmark.setResource(bookmark);
				postBookmark.setUser(new User(doc.get(FLD_USER)));

				postBookmarkList.add(postBookmark);

			}

		} catch (IOException e) {
			log.debug("LuceneBibTex: IOException: " + e.getMessage());
		}
		return postBookmarkList;
	}	

	@Override
	protected QuerySortContainer buildAuthorQuery(String group,
			String searchTerms, String requestedUserName,
			String requestedGroupName, String year, String firstYear,
			String lastYear, List<String> tagList) {
		throw new UnsupportedOperationException("Author search not available for bookmarks");
	}
	
	/**
	 * get List of postBookmark from lucene index
	 * 
	 * for pagination see
	 * http://www.gossamer-threads.com/lists/lucene/general/70516#70516
	 * 
	 * @param String
	 *            idname fieldname of returning value
	 * @param char
	 *            LuceneIndex lucene index to use b for bookmark, p for
	 *            publications
	 */
	/*
	public ResultList<Post<Bookmark>> searchPosts(String group,
			String searchTerms, String requestedUserName, String UserName,
			Set<String> GroupNames, int limit, int offset) {
		
		
		final Log log = LogFactory.getLog(LuceneSearchBookmarks.class);

		// String orderBy = "relevance";
		String orderBy = "date";

		// field names in Lucene index
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

		if (this.searcher == null) {
			log.error("LuceneBibTex: searcher is NULL!");

		}

		ResultList<Post<Bookmark>> postBookmarkList = new ResultList<Post<Bookmark>>();

		// sucheergebnis darf einträge, die die gruppe "private" beinhalten
		// nicht anzeigen, es sei denn, sie gehören dem angemeldeten benutzer

		log.debug("LuceneBookmark: group  " + group);
		log.debug("LuceneBookmark: UserName  " + UserName);
		log.debug("LuceneBookmark: GroupNames.toString()  "
				+ GroupNames.toString());

		// declare ArrayList cidsArray for list of String to return
		final ArrayList<Integer> cidsArray = new ArrayList<Integer>();

		// do not search for nothing in lucene index
		if ((searchTerms != null) && (!searchTerms.isEmpty())) {
			
			 // parse search_terms for forbidden characters forbidden characters
			 // are those, which will harm the lucene query forbidden characters
			 // are & | ( ) { } [ ] ~ * ^ ? : \
			 
			searchTerms = Utils.replaceSpecialLuceneChars(searchTerms);
			// FIXME: why not use readily available escape function?
			// String escaped = QueryParser.escape(userQuery);

			int allowedGroupsIterator = 0;
			for (String groupName : GroupNames) {
				if (allowedGroupsIterator > 0)
					allowedGroupNames += " OR ";
				allowedGroupNames += groupName;
				allowedGroupsIterator++;
			}

			log.debug("LuceneBookmark: allowedGroups: " + allowedGroupNames);

			mergedFiledQuery = lField_merged + ":(" + searchTerms + ") ";
			allowedGroupNamesQuery = lField_group + ":(" + allowedGroupNames
					+ ")";
			privateGroupQuery = lField_group + ":(private)";

			if ((UserName != null) && (!UserName.isEmpty())) {
				UserName = Utils.replaceSpecialLuceneChars(UserName);
				userQuery = lField_user + ":(" + UserName + ")";
			}

			if ((requestedUserName != null) && (!requestedUserName.isEmpty())) {
				requestedUserName = Utils
						.replaceSpecialLuceneChars(requestedUserName);
				requestedUserNameQuery = " AND " + lField_user + ":("
						+ requestedUserName + ")";
			}

			if ((null != group) && (!group.isEmpty())) {
				groupIdQuery = " AND " + lField_group + ":(" + group + ")";
			}

			// assemble query string
			querystring = mergedFiledQuery + requestedUserNameQuery
					+ groupIdQuery;
			if (!userQuery.isEmpty()) { // logged in user
				querystring += " AND ( " + allowedGroupNamesQuery + " OR ("
						+ privateGroupQuery + " AND " + userQuery + ") ) ";
			} else {
				querystring += " AND " + allowedGroupNamesQuery;
			}

			log.debug("LuceneBookmark-Querystring (assembled): "
					+ querystring);

			// open lucene index
			// IndexReader reader = IndexReader.open(luceneIndexPath);

			QueryParser myParser = new QueryParser(lField_desc, analyzer);
			Query query;
			 //
			 // sort first by date and then by score. This is not necessary,
			 // because there are no or only few entries with same date (date is
			 // with seconds) Sort sort = new Sort(new SortField[]{ new
			 // SortField("date",true), SortField.FIELD_SCORE });
			 //

			Sort sort = null;
			if ("relevance".equals(orderBy)) {
				myParser.setDefaultOperator(QueryParser.Operator.OR); // is
																		// default
				sort = new Sort(new SortField[] { SortField.FIELD_SCORE,
						new SortField("date", true) });
			} else { // orderBy=="date"
				myParser.setDefaultOperator(QueryParser.Operator.AND);
				sort = new Sort("date", true);
			}

			log.debug("LuceneBookmark: QueryParser.DefaultOperator: "
					+ myParser.getDefaultOperator());

			try {
				query = myParser.parse(querystring);
				log.debug("LuceneBookmark-Querystring (analyzed):  "
						+ query.toString());
				log
						.debug("LuceneBookmark-Query will be sorted by:  "
								+ sort);

				log.debug("LuceneBookmark: searcher:  " + searcher);

				long starttimeQuery = System.currentTimeMillis();
				final TopDocs topDocs = searcher.search(query, null, offset
						+ limit, sort);

				long endtimeQuery = System.currentTimeMillis();
				log.debug("LuceneBookmark pure query time: "
						+ (endtimeQuery - starttimeQuery) + "ms");

				int hitslimit = (((offset + limit) < topDocs.totalHits) ? (offset + limit)
						: topDocs.totalHits);

				postBookmarkList.setTotalCount(topDocs.totalHits);

				log
						.debug("LuceneBookmark:  offset / limit / hitslimit / hits.length():  "
								+ offset + " / " + limit + " / " + hitslimit + " / " + topDocs.totalHits);

				for (int i = offset; i < hitslimit; i++) {
					Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
					Bookmark bookmark = new Bookmark();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd H:m:s.S");

					Post<Bookmark> postBookmark = new Post<Bookmark>();
					Date date = new Date();
					try {
						date = dateFormat.parse(doc.get(lField_date));
					} catch (java.text.ParseException e) {
						log.debug("LuceneBibTex: ParseException: " + e.getMessage());
					}
					bookmark.setUrl(doc.get(lField_url));
					bookmark.setTitle(doc.get(lField_desc));

					for (String g : doc.get(lField_group).split(",")) {
						postBookmark.addGroup(g);
					}

					for (String tag : doc.get(lField_tas).split(" ")) {
						postBookmark.addTag(tag);
					}

					postBookmark.setContentId(Integer.parseInt(doc
							.get(lField_contentid)));
					bookmark.setIntraHash(doc.get("intrahash"));
					bookmark.setInterHash(doc.get("intrahash")); // same as
																	// intrahash

					postBookmark.setContentId(Integer.parseInt(doc
							.get(lField_contentid)));
					long starttime2Query = System.currentTimeMillis();
					if( doc.get("intrahash")!=null ) {
						bookmark.setCount(this.searcher.docFreq(new Term(
								"intrahash", doc.get("intrahash"))));
					} else {
						bookmark.setCount(1);
					}
					long endtime2Query = System.currentTimeMillis();

					postBookmark.setDate(date);
					postBookmark.setDescription(doc.get(lField_ext));
					postBookmark.setResource(bookmark);
					postBookmark.setUser(new User(doc.get(lField_user)));

					postBookmarkList.add(postBookmark);

				}

			} catch (ParseException e) {
				log.debug("LuceneBibTex: ParseException: " + e.getMessage());
			} catch (IOException e) {
				log.debug("LuceneBibTex: IOException: " + e.getMessage());
			}

		}

		return postBookmarkList;
	}
	*/

	@Override
	protected Class<Bookmark> getResourceType() {
		return Bookmark.class;
	}



}