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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
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
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.services.searcher.ResourceSearch;

//FIXME: remove this comment (used only for triggering cvs-commit)

public class LuceneSearchBookmarks extends LuceneSearch<Bookmark> {

	private final static LuceneSearchBookmarks singleton = new LuceneSearchBookmarks();
	private IndexSearcher searcher;
	private PerFieldAnalyzerWrapper analyzer = null;
	private String lucenePath = null;

	private LuceneSearchBookmarks() {
		reloadIndex();
	}

	public void reloadIndex() {
		final Log LOGGER = LogFactory.getLog(LuceneSearchBookmarks.class);
		try {

			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			lucenePath = (String) envContext.lookup("luceneIndexPathBookmark");

			
			LOGGER.debug("LuceneBookmark: use index: " + lucenePath);

			/*
			 * set current path to lucene index, given by environment parameter
			 * in tomcat's context.xml
			 * 
			 * <Environment name="luceneIndexPath" type="java.lang.String"
			 * value="/home/bibsonomy/lucene"/>
			 */

			if (this.analyzer == null) {
				/** lucene analyzer, must be the same as at indexing */
				// SimpleAnalyzer analyzer = new SimpleAnalyzer();
				this.analyzer = new PerFieldAnalyzerWrapper(
						new SimpleAnalyzer());

				// let field group of analyzer use SimpleKeywordAnalyzer
				// numbers will be deleted by SimpleAnalyser but group has only
				// numbers, therefore use SimpleKeywordAnalyzer
				this.analyzer.addAnalyzer("group", new SimpleKeywordAnalyzer());
				// usernames also might contain numbers - 
				// as the user_name field is not analyzed (which makes pretty sense)
				// the user_name shouldn't be normalized as well
				this.analyzer.addAnalyzer("user_name", new SimpleKeywordAnalyzer());
			}

			// close searcher if opened before
			try {
				if (null != this.searcher)
					this.searcher.close();
			} catch (IOException e) {
				LOGGER.debug("LuceneBookmark: IOException on searcher.close: "
						+ e.getMessage());
			} catch (RuntimeException e) {
				LOGGER
						.debug("LuceneBookmark: RuntimeException on searcher.close: "
								+ e.getMessage());
			}

			// load and hold index on physical hard disk
			LOGGER.debug("LuceneBookmark: use index from disk");
			LOGGER.debug("this.searcher-0: " + this.searcher);
			this.searcher = new IndexSearcher(lucenePath);
			LOGGER.debug("this.searcher-1: " + this.searcher);
			// }
		} catch (final NamingException e) {
			LOGGER.error("LuceneBookmark: NamingException "
					+ e.getExplanation() + " ## " + e.getMessage());
			LOGGER
					.error("Environment variable luceneIndexPathBoomarks not present.");
			throw new LuceneException("error.lucene");
		} catch (CorruptIndexException e) {
			LOGGER.error("LuceneBookmark: CorruptIndexException "
					+ e.getMessage());
			throw new LuceneException("error.lucene");
		} catch (IOException e) {
			LOGGER.error("LuceneBookmark: IOException " + e.getMessage());
			throw new LuceneException("error.lucene");
		} catch (RuntimeException e) {
			LOGGER.warn("LuceneBookmark: RuntimeException " + e.getMessage());
			throw new LuceneException("error.lucene");
		}
	}

	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBookmarks getInstance() {
		return singleton;
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
	public ResultList<Post<Bookmark>> searchPosts(String group,
			String searchTerms, String requestedUserName, String UserName,
			Set<String> GroupNames, int limit, int offset) {
		final Log LOGGER = LogFactory.getLog(LuceneSearchBookmarks.class);

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
			LOGGER.error("LuceneBibTex: searcher is NULL!");

		}

		ResultList<Post<Bookmark>> postBookmarkList = new ResultList<Post<Bookmark>>();

		// sucheergebnis darf einträge, die die gruppe "private" beinhalten
		// nicht anzeigen, es sei denn, sie gehören dem angemeldeten benutzer

		LOGGER.debug("LuceneBookmark: group  " + group);
		LOGGER.debug("LuceneBookmark: UserName  " + UserName);
		LOGGER.debug("LuceneBookmark: GroupNames.toString()  "
				+ GroupNames.toString());

		// declare ArrayList cidsArray for list of String to return
		final ArrayList<Integer> cidsArray = new ArrayList<Integer>();

		// do not search for nothing in lucene index
		if ((searchTerms != null) && (!searchTerms.isEmpty())) {
			/*
			 * parse search_terms for forbidden characters forbidden characters
			 * are those, which will harm the lucene query forbidden characters
			 * are & | ( ) { } [ ] ~ * ^ ? : \
			 */
			searchTerms = Utils.replaceSpecialLuceneChars(searchTerms);

			int allowedGroupsIterator = 0;
			for (String groupName : GroupNames) {
				if (allowedGroupsIterator > 0)
					allowedGroupNames += " OR ";
				allowedGroupNames += groupName;
				allowedGroupsIterator++;
			}

			LOGGER.debug("LuceneBookmark: allowedGroups: " + allowedGroupNames);

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

			LOGGER.debug("LuceneBookmark-Querystring (assembled): "
					+ querystring);

			// open lucene index
			// IndexReader reader = IndexReader.open(luceneIndexPath);

			QueryParser myParser = new QueryParser(lField_desc, analyzer);
			Query query;
			/*
			 * sort first by date and then by score. This is not necessary,
			 * because there are no or only few entries with same date (date is
			 * with seconds) Sort sort = new Sort(new SortField[]{ new
			 * SortField("date",true), SortField.FIELD_SCORE });
			 */

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

			LOGGER.debug("LuceneBookmark: QueryParser.DefaultOperator: "
					+ myParser.getDefaultOperator());

			try {
				query = myParser.parse(querystring);
				LOGGER.debug("LuceneBookmark-Querystring (analyzed):  "
						+ query.toString());
				LOGGER
						.debug("LuceneBookmark-Query will be sorted by:  "
								+ sort);

				LOGGER.debug("LuceneBookmark: searcher:  " + searcher);

				long starttimeQuery = System.currentTimeMillis();
				final TopDocs topDocs = searcher.search(query, null, offset
						+ limit, sort);

				long endtimeQuery = System.currentTimeMillis();
				LOGGER.debug("LuceneBookmark pure query time: "
						+ (endtimeQuery - starttimeQuery) + "ms");

				int hitslimit = (((offset + limit) < topDocs.totalHits) ? (offset + limit)
						: topDocs.totalHits);

				postBookmarkList.setTotalCount(topDocs.totalHits);

				LOGGER
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
						LOGGER.debug("LuceneBibTex: ParseException: " + e.getMessage());
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
					bookmark.setCount(this.searcher.docFreq(new Term(
							"intrahash", doc.get("intrahash"))));
					long endtime2Query = System.currentTimeMillis();

					postBookmark.setDate(date);
					postBookmark.setDescription(doc.get(lField_ext));
					postBookmark.setResource(bookmark);
					postBookmark.setUser(new User(doc.get(lField_user)));

					postBookmarkList.add(postBookmark);

				}

			} catch (ParseException e) {
				LOGGER.debug("LuceneBibTex: ParseException: " + e.getMessage());
			} catch (IOException e) {
				LOGGER.debug("LuceneBibTex: IOException: " + e.getMessage());
			}

		}

		return postBookmarkList;
	}

	public LuceneIndexStatistics getStatistics() {
		return Utils.getStatistics(lucenePath);
	}

	public ResultList<Post<Bookmark>> searchAuthor(String group, String search,
			String requestedUserName, String requestedGroupName, String year,
			String firstYear, String lastYear, List<String> tagList, int limit,
			int offset) {
		throw new UnsupportedOperationException("Author search not available for bookmarks");
	}
	@Override
	protected Class<Bookmark> getResourceType() {
		return Bookmark.class;
	}
}