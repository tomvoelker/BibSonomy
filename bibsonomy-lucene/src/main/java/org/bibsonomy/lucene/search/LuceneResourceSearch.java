/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.lucene.search;

import static org.bibsonomy.lucene.util.LuceneBase.CFG_LUCENE_FIELD_SPECIFIER;
import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
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
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.es.EsResourceSearch;
import org.bibsonomy.lucene.database.LuceneInfoLogic;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.lucene.search.collector.TagCountCollector;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.es.SearchType;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * lucene search for all supported resources
 * 
 * @author fei
 * 
 * @param <R>
 *            resource type
 */
public class LuceneResourceSearch<R extends Resource> implements ResourceSearch<R> {
	private static final Log log = LogFactory.getLog(LuceneResourceSearch.class);
	private static final Pattern NON_DIGIT_PATTERN = Pattern.compile("\\D");

	/**
	 * logic interface for retrieving data from bibsonomy (friends, groups
	 * members)
	 */
	private LuceneInfoLogic dbLogic;
	
	/** default field analyzer */
	private Analyzer analyzer;

	/** default junction of search terms */
	private Operator defaultSearchTermJunctor = null;

	/** post model converter */
	private LuceneResourceConverter<R> resourceConverter;

	/** the index the searcher is currently using */
	private LuceneResourceIndex<R> index;
	
	private EsResourceSearch<R> sharedResourceSearch;

	/**
	 * config values
	 */
	private boolean tagCloudEnabled;
	private int tagCloudLimit;

	/**
	 * constructor
	 */
	public LuceneResourceSearch() {
		this.defaultSearchTermJunctor = Operator.AND;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.services.searcher.ResourceSearch#getPosts(java.lang.String,
	 * java.lang.String, java.lang.String, java.util.Collection,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * java.util.Collection, java.lang.String, java.lang.String,
	 * java.lang.String, int, int)
	 */
	@Override
	public ResultList<Post<R>> getPosts(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, Order order, final int limit, final int offset) {
		// build query
		final QuerySortContainer query = this.buildQuery(userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, tagIndex, year, firstYear, lastYear, negatedTags, order);
		// perform search query
		return this.searchLucene(query, limit, offset);
	}
	
	/*
	 * (non-Javadoc)
	 *  
	 * @see
	 * org.bibsonomy.services.searcher.ResourceSearch#getTags(java.lang.String,
	 * java.lang.String, java.lang.String, java.util.Collection,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * java.util.Collection, java.lang.String, java.lang.String,
	 * java.lang.String, int, int)
	 */
	@Override
	public List<Tag> getTags(final String userName, final String requestedUserName, final String requestedGroupName, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final List<String> negatedTags, final int limit, final int offset) {
		if (!this.tagCloudEnabled) {
			return new LinkedList<Tag>();
		}
		
		// build query
		final QuerySortContainer qf = this.buildQuery(userName, requestedUserName, requestedGroupName, null, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, tagIndex, year, firstYear, lastYear, negatedTags, null);
		final Map<Tag, Integer> tagCounter = new HashMap<Tag, Integer>();
		
		IndexSearcher searcher = null;
		try {
			//Aquire searcher
			searcher = this.index.aquireIndexSearcher();
			log.debug("Starting tag collection");
			final TopDocs topDocs = searcher.search(qf.getQuery(), null, this.tagCloudLimit, qf.getSort());
			log.debug("Done collecting tags");
			/*
			 * extract tags from top n documents
			 * number of posts to consider for building the tag cloud are configurated
			 * by the tagCloudLimit property
			 */
			final int hitsLimit = ((this.tagCloudLimit < topDocs.totalHits) ? (this.tagCloudLimit) : topDocs.totalHits);
			for (int i = 0; i < hitsLimit; i++) {
				/*
				 * get document from index and
				 * convert document to bibsonomy post model	
				 */
				final Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
				final Post<R> post = this.resourceConverter.writePost(doc);
		
				// set tag count
				if (present(post.getTags())) {
					for (final Tag tag : post.getTags()) {
						/*
						 * we remove the requested tags because we assume
						 * that related tags are requested
						 */
						if (present(tagIndex) && tagIndex.contains(tag.getName())) {
							continue;
						}
						Integer oldCnt = tagCounter.get(tag);
						if (!present(oldCnt)) {
							oldCnt = 1;
						} else {
							oldCnt += 1;
						}
						tagCounter.put(tag, oldCnt);
					}
				}
			}
		} catch (final IOException e) {
			log.error("Error building full text tag cloud for query " + qf.getQuery().toString(), e);
		} finally {
			this.index.releaseIndexSearcher(searcher);
		}
		
		final List<Tag> tags = new LinkedList<Tag>();
		// extract all tags
		for (final Map.Entry<Tag, Integer> entry : tagCounter.entrySet()) {
			final Tag tag = entry.getKey();
			tag.setUsercount(entry.getValue());
			tag.setGlobalcount(entry.getValue()); // FIXME: we set user==global count
			tags.add(tag);
		}
		log.debug("Done calculating tag statistics");
		
		// all done.
		return tags;
	}

	/**
	 * query index for documents and create result list of post models
	 */
	private ResultList<Post<R>> searchLucene(final QuerySortContainer qf, final int limit, final int offset) {
		if (limit == 0) {
			return new ResultList<Post<R>>();
		}
		
		IndexSearcher searcher = null;
		final ResultList<Post<R>> postList = new ResultList<Post<R>>();
		try {
			
			try {
				searcher = this.index.aquireIndexSearcher();
			} catch (IllegalStateException e) {
				throw new InternServerException(e);
			}
			// initialize data
			final Query query = qf.getQuery();
			final Sort sort = qf.getSort();
			log.debug("Querystring:  " + query.toString() + " sorted by: " + sort);
			/*
			 * querying the index
			 */
			long starttimeQuery = System.currentTimeMillis();
			final TopDocs topDocs = searcher.search(query, null, offset + limit, sort);

			// determine number of posts to display
			final int hitslimit = (((offset + limit) < topDocs.totalHits) ? (offset + limit) : topDocs.totalHits);
			log.debug("offset / limit / hitslimit / hits.length():  " + offset + " / " + limit + " / " + hitslimit + " / " + topDocs.totalHits);
			log.debug("Query time: " + (System.currentTimeMillis() - starttimeQuery) + "ms");

			postList.setTotalCount(topDocs.totalHits);

			/*
			 * extract posts
			 */
			for (int i = offset; i < hitslimit; i++) {
				// get document from index
				final Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
				// convert document to bibsonomy model
				final Post<R> post = this.resourceConverter.writePost(doc);

				// set post frequency
				starttimeQuery = System.currentTimeMillis();
				int postFreq = 1;
				final String interHash = doc.get(LuceneFieldNames.INTERHASH);
				if (interHash != null) {
					//Count documents for interHash
					postFreq = searcher.getIndexReader().docFreq(new Term(LuceneFieldNames.INTERHASH, interHash));
				}
				log.debug("PostFreq query time: " + (System.currentTimeMillis() - starttimeQuery) + "ms");
				post.getResource().setCount(postFreq);

				postList.add(post);
			}

		} catch (final IOException e) {
			log.debug("LuceneResourceSearch: IOException: " + e.getMessage());
		} finally {
			this.index.releaseIndexSearcher(searcher);
		}

		return postList;
	}

	/**
	 * parse given search term for allowing lucene's search syntax
	 * 
	 * @param searchTerms
	 *            a lucene search query
	 * @return the parsed query term
	 */
	protected Query buildFulltextSearchQuery(final String searchTerms) {
		return this.parseSearchQuery(LuceneFieldNames.MERGED_FIELDS, searchTerms);
	}

	/**
	 * parse given search term for allowing lucene's search syntax on the title
	 * field
	 * 
	 * @param searchTerms
	 *            a lucene search query
	 * @return the parsed query term
	 */
	protected Query buildTitleSearchQuery(final String searchTerms) {
		return this.parseSearchQuery(LuceneFieldNames.TITLE, searchTerms);
	}

	/**
	 * build query to search for posts who's private notes field matches to the
	 * given search terms
	 * 
	 * @param userName
	 * @param searchTerms 
	 * @return the private notes query for the user
	 */
	protected Query buildPrivateNotesQuery(final String userName, final String searchTerms) {
		final BooleanQuery privateSearchQuery = new BooleanQuery();

		if (present(userName)) {
			final Query privateSearchTermQuery = this.parseSearchQuery(LuceneFieldNames.PRIVATE_FIELDS, searchTerms);
			privateSearchQuery.add(privateSearchTermQuery, Occur.MUST);
			privateSearchQuery.add(new TermQuery(new Term(LuceneFieldNames.USER, userName)), Occur.MUST);
		}

		return privateSearchQuery;
	}

	/**
	 * restrict result list to posts with given tag assignments
	 * 
	 * @param tagIndex
	 *            list of tags
	 * @return search query for restricting posts to given tag assignments
	 */
	protected Query buildTagSearchQuery(final Collection<String> tagIndex) {
		final BooleanQuery tagQuery = new BooleanQuery();
		// --------------------------------------------------------------------
		// prepare input parameters
		// --------------------------------------------------------------------
		if (present(tagIndex)) {
			this.addTagQuerries(tagIndex, null, tagQuery);
		}

		// all done
		return tagQuery;
	}

	private String parseTag(final String tag) {
		try {
			return this.parseToken(LuceneFieldNames.TAS, tag);
		} catch (final IOException e) {
			log.error("Error parsing input tag " + tag + " (" + e.getMessage() + ")");
			return tag;
		}
	}

	/**
	 * restrict result list to posts owned by one of the given group members
	 * 
	 * @param requestedGroupName
	 * 
	 * @return the group search query
	 */
	protected BooleanQuery buildGroupSearchQuery(final String requestedGroupName) {
		// get given group's members
		final Collection<String> groupMembers = this.dbLogic.getGroupMembersByGroupName(requestedGroupName);

		// --------------------------------------------------------------------
		// restrict to group members
		// --------------------------------------------------------------------
		final BooleanQuery groupMemberQuery = new BooleanQuery();
		if (present(requestedGroupName) && present(groupMembers)) {
			for (final String member : groupMembers) {
				final Query memberQuery = new TermQuery(new Term(LuceneFieldNames.USER, member));
				groupMemberQuery.add(memberQuery, Occur.SHOULD);
			}
		}
		return groupMemberQuery;
	}

	private static String removeNonDigits(String s) {
		return NON_DIGIT_PATTERN.matcher(s).replaceAll("");
	}
	
	/**
	 * restrict given query to posts belonging to a given time range
	 * 
	 * @param mainQuery
	 * @param year
	 * @param firstYear
	 * @param lastYear
	 * @return time range query
	 */
	protected Query makeTimeRangeQuery(final BooleanQuery mainQuery, final String year, String firstYear, String lastYear) {
		
		//exact year query
		if (present(year)) {
			mainQuery.add(new TermQuery(new Term(LuceneFieldNames.YEAR, removeNonDigits(year))), Occur.MUST);
			return mainQuery;
		}
		
		//range query
		boolean includeLowerBound = false;
		boolean includeUpperBound = false;
		BytesRef firstYearBR = null;
		BytesRef lastYearBR = null;
		
		if (present(firstYear)) {
				firstYear = removeNonDigits(firstYear);
				firstYearBR = new BytesRef(firstYear);
				includeLowerBound = true;
		}
		if (present(lastYear)) {
				lastYear = removeNonDigits(lastYear);
				lastYearBR = new BytesRef(lastYear);
				includeUpperBound = true;
		}

		if (includeLowerBound || includeUpperBound) {
			// if upper or lower bound is given, then use filter
			final Filter rangeFilter = new TermRangeFilter(LuceneFieldNames.YEAR, firstYearBR, lastYearBR, includeLowerBound, includeUpperBound);
			return new FilteredQuery(mainQuery, rangeFilter);
		}

		return mainQuery;
	}

	/**
	 * restrict result to posts which are visible to the user
	 * 
	 * @param userName
	 *            the logged in user's name
	 * @param allowedGroups
	 *            list of groups of which the logged in user is a member
	 * @return a query term which restricts the result to posts, which are
	 *         visible to the user
	 */
	protected Query buildAccessModeQuery(final String userName, final Collection<String> allowedGroups) {
		// --------------------------------------------------------------------
		// get missing information from bibsonomy's database
		// --------------------------------------------------------------------
		final BooleanQuery accessModeQuery = new BooleanQuery();
		final BooleanQuery privatePostQuery = new BooleanQuery();

		final Collection<String> friends = this.dbLogic.getFriendsForUser(userName);

		// --------------------------------------------------------------------
		// allowed groups
		// --------------------------------------------------------------------
		for (final String groupName : allowedGroups) {
			final Query groupQuery = new TermQuery(new Term(LuceneFieldNames.GROUP, groupName));
			accessModeQuery.add(groupQuery, Occur.SHOULD);
		}

		// --------------------------------------------------------------------
		// private post query
		// --------------------------------------------------------------------
		if (present(userName)) {
			final BooleanQuery privatePostGroups = new BooleanQuery();
			privatePostGroups.add(new TermQuery(new Term(LuceneFieldNames.GROUP, GroupID.PRIVATE.name().toLowerCase())), Occur.SHOULD);
			privatePostGroups.add(new TermQuery(new Term(LuceneFieldNames.GROUP, GroupID.FRIENDS.name().toLowerCase())), Occur.SHOULD);
			privatePostQuery.add(privatePostGroups, Occur.MUST);
			privatePostQuery.add(new TermQuery(new Term(LuceneFieldNames.USER, userName)), Occur.MUST);
			accessModeQuery.add(privatePostQuery, Occur.SHOULD);
		}

		if (present(friends)) {
			final BooleanQuery friendPostQuery = new BooleanQuery();
			friendPostQuery.add(new TermQuery(new Term(LuceneFieldNames.GROUP, GroupID.FRIENDS.name().toLowerCase())), Occur.MUST);

			final BooleanQuery friendPostAllowanceQuery = new BooleanQuery();
			// the post owner's friend may read the post
			for (final String friend : friends) {
				friendPostAllowanceQuery.add(new TermQuery(new Term(LuceneFieldNames.USER, friend)), Occur.SHOULD);
			}

			friendPostQuery.add(friendPostAllowanceQuery, Occur.MUST);
			accessModeQuery.add(friendPostQuery, Occur.SHOULD);
		}

		// all done
		return accessModeQuery;
	}

	/**
	 * build the overall lucene search query term
	 * 
	 * @param userName
	 * @param requestedUserName
	 *            restrict the resulting posts to those which are owned by this
	 *            user name
	 * @param requestedGroupName
	 *            restrict the resulting posts to those which are owned this
	 *            group
	 * @param requestedRelationNames
	 * 				expand the search in the post of users which are defined by the given 
	 * 				relation names
	 * @param allowedGroups 
	 * @param searchTerms
	 * @param titleSearchTerms 
	 * @param authorSearchTerms 
	 * @param tagIndex 
	 * @param year 
	 * @param firstYear 
	 * @param lastYear 
	 * @param negatedTags
	 * @param order
	 * @return overall lucene search query
	 */
	protected QuerySortContainer buildQuery(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final Collection<String> negatedTags, Order order) {

		// --------------------------------------------------------------------
		// build the query
		// --------------------------------------------------------------------
		// the resulting main query
		final BooleanQuery mainQuery = new BooleanQuery();
		if (present(searchTerms) || present(titleSearchTerms) || present(authorSearchTerms)) {
			final BooleanQuery searchQuery = this.buildSearchQuery(userName, searchTerms, titleSearchTerms, authorSearchTerms);
			mainQuery.add(searchQuery, Occur.MUST);
		}

		// Add the requested tags
		if (present(tagIndex) || present(negatedTags)) {
			this.addTagQuerries(tagIndex, negatedTags, mainQuery);
		}

		// restrict result to given group
		if (present(requestedGroupName)) {
			final BooleanQuery groupQuery = this.buildGroupSearchQuery(requestedGroupName);
			if (groupQuery.getClauses().length >= 1) {
				mainQuery.add(groupQuery, Occur.MUST);
			}
		}

		// restricting access to posts visible to the user
		final Query accessModeQuery = this.buildAccessModeQuery(userName, allowedGroups);

		// --------------------------------------------------------------------
		// post owned by user 
		// Use this restriction iff there is no user relation
		// --------------------------------------------------------------------
		if (present(requestedUserName) && !present(requestedRelationNames)) {
			mainQuery.add(new TermQuery(new Term(LuceneFieldNames.USER, requestedUserName)), Occur.MUST);
		}
		// If there is at once one relation then restrict the results only 
		// to the users in the given relations (inclduing posts of the logged in users)
		else if (present(requestedRelationNames)) {
			// for all relations: 
			final BooleanQuery relationsQuery = this.buildUserRelationQuery(userName, requestedRelationNames);	
			mainQuery.add(relationsQuery, Occur.MUST);
		}

		// --------------------------------------------------------------------
		// build final query
		// --------------------------------------------------------------------

		// combine query terms
		mainQuery.add(accessModeQuery, Occur.MUST);
		
		
		// set ordering
		final Sort sort;
		if (Order.RANK.equals(order)) {
			sort = new Sort(SortField.FIELD_SCORE, new SortField(LuceneFieldNames.DATE, SortField.Type.LONG, true));
		} else {
			sort = new Sort(new SortField(LuceneFieldNames.DATE, SortField.Type.LONG, true));
		}
		// all done
		log.debug("[Full text] Search query: " + mainQuery.toString());

		final QuerySortContainer qf = new QuerySortContainer();
		qf.setQuery(this.makeTimeRangeQuery(mainQuery, year, firstYear, lastYear));
		qf.setSort(sort);
		qf.setTagCountCollector(new TagCountCollector());
		return qf;
	}

	private BooleanQuery buildUserRelationQuery(final String userName, final List<String> requestedRelationNames) {
		final BooleanQuery relationsQuery = new BooleanQuery();
		for (final String relation: requestedRelationNames) {
			// Get all users in this relation:
			final Collection<String> userInRelation = this.dbLogic.getUsersByUserRelation(userName, relation);
			final BooleanQuery userInRelationQuery = new BooleanQuery();
			for (final String user: userInRelation) {
				userInRelationQuery.add(new TermQuery(new Term(LuceneFieldNames.USER, user)), Occur.SHOULD);
			}
			relationsQuery.add(userInRelationQuery, Occur.MUST);
		}
		return relationsQuery;
	}

	private void addTagQuerries(final Collection<String> tagIndex, final Collection<String> negatedTags, final BooleanQuery mainQuery) {
		/*
		 * Process normal tags
		 */
		if (present(tagIndex)) {
			for (final String tag : tagIndex) {
				// Is the tag string a concept name?
				if (tag.startsWith(Tag.CONCEPT_PREFIX)) {
					final String conceptTag = this.parseTag(tag.substring(2));
					// get related tags:
					final BooleanQuery conceptTags = new BooleanQuery();
					conceptTags.add(new TermQuery(new Term(LuceneFieldNames.TAS, this.parseTag(conceptTag))), Occur.SHOULD);
					for (final String t : this.dbLogic.getSubTagsForConceptTag(conceptTag)) {
						conceptTags.add(new TermQuery(new Term(LuceneFieldNames.TAS, this.parseTag(t))), Occur.SHOULD);
					}
					mainQuery.add(conceptTags, Occur.MUST);
				} else {
					mainQuery.add(new TermQuery(new Term(LuceneFieldNames.TAS, this.parseTag(tag))), Occur.MUST);
				}
			}
		}
		/*
		 * Process negated Tags
		 */
		
		if (present(negatedTags)) {
			for (final String negatedTag : negatedTags) {
				final Query negatedSearchQuery = this.parseSearchQuery(LuceneFieldNames.TAS, negatedTag);
				mainQuery.add(negatedSearchQuery, Occur.MUST_NOT);
			}
		}

	}

	/**
	 * 
	 * @param userName
	 * @param searchTerms
	 * @param titleSearchTerms
	 * @param authorSearchTerms
	 * @param tagIndex
	 * @return a search query for the search terms
	 */
	protected BooleanQuery buildSearchQuery(final String userName, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms) {
		final BooleanQuery searchQuery = new BooleanQuery();

		// search full text
		if (present(searchTerms)) {
			final Query fulltextQuery = this.buildFulltextSearchQuery(searchTerms);
			searchQuery.add(fulltextQuery, Occur.SHOULD);
		}

		// search private nodes
		if (present(userName) && present(searchTerms)) {
			final Query privateNotesQuery = this.buildPrivateNotesQuery(userName, searchTerms);
			searchQuery.add(privateNotesQuery, Occur.SHOULD);
		}

		// search title
		if (present(titleSearchTerms)) {
			final Query titleQuery = this.buildTitleSearchQuery(titleSearchTerms);
			searchQuery.add(titleQuery, Occur.MUST);
		}

		return searchQuery;
	}

	/**
	 * analyzes given input parameter
	 * 
	 * @param fieldName
	 *            the name of the field
	 * @param param
	 *            the value of the field
	 * @return the analyzed string
	 * @throws IOException
	 */
	protected String parseToken(final String fieldName, final String param) throws IOException {
		if (present(param)) {
			// use lucene's new token stream api (see
			// org.apache.lucene.analysis' javadoc at package level)
			final TokenStream ts = this.analyzer.tokenStream(fieldName, param);
			/* This CharTermAttribute was formally the deprecated TermAttribute interface 
			 * The main difference in this case is that we now obtain a char[] buffer for 
			 * every term instead of a String object */
			final CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
			ts.reset();

			// analyze the parameter - that is: concatenate its normalized
			// tokens
			final StringBuilder analyzedString = new StringBuilder();
			while (ts.incrementToken()) {
				String term = new String(termAtt.buffer(),0,termAtt.length());
				analyzedString.append(" ").append(term);
			}
			
			ts.end();
			ts.close();
			
			return analyzedString.toString().trim();
		}

		return "";
	}

	/**
	 * build full text query for given query string
	 * 
	 * @param fieldName
	 * @param searchTerms
	 * @return the search query
	 */
	protected Query parseSearchQuery(final String fieldName, String searchTerms) {
		// parse search terms for handling phrase search
		final QueryParser searchTermParser = new QueryParser(Version.LUCENE_48, fieldName, this.analyzer);
		searchTermParser.setDefaultOperator(this.defaultSearchTermJunctor);
		searchTermParser.setAllowLeadingWildcard(true);
		try {
			// disallow field specification in search query
			searchTerms = searchTerms.replace(CFG_LUCENE_FIELD_SPECIFIER, "\\" + CFG_LUCENE_FIELD_SPECIFIER);
			return searchTermParser.parse(searchTerms);
		} catch (final ParseException e) {
			return new TermQuery(new Term(fieldName, searchTerms));
		}
	}

	/**
	 * @param dbLogic
	 *            the dbLogic to set
	 */
	public void setDbLogic(final LuceneInfoLogic dbLogic) {
		this.dbLogic = dbLogic;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(final LuceneResourceIndex<R> index) {
		this.index = index;
		this.index.reset();
	}

	/**
	 * @param analyzer
	 *            the analyzer to set
	 */
	public void setAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * @param defaultSearchTermJunctor
	 *            the defaultSearchTermJunctor to set
	 */
	public void setDefaultSearchTermJunctor(final Operator defaultSearchTermJunctor) {
		this.defaultSearchTermJunctor = defaultSearchTermJunctor;
	}

	/**
	 * @param resourceConverter
	 *            the resourceConverter to set
	 */
	public void setResourceConverter(final LuceneResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @param tagCloudEnabled
	 *            the tagCloudEnabled to set
	 */
	public void setTagCloudEnabled(final boolean tagCloudEnabled) {
		this.tagCloudEnabled = tagCloudEnabled;
	}

	/**
	 * @param tagCloudLimit
	 *            the tagCloudLimit to set
	 */
	public void setTagCloudLimit(final int tagCloudLimit) {
		this.tagCloudLimit = tagCloudLimit;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.services.searcher.ResourceSearch#getPosts(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.util.List, org.bibsonomy.model.enums.Order, int, int)
	 */
	@Override
	public List<Post<R>> getPosts(String userName,String requestedUserName, String requestedGroupName,List<String> requestedRelationNames,	Collection<String> allowedGroups,SearchType searchType,String searchTerms,String titleSearchTerms, String authorSearchTerms,
			Collection<String> tagIndex, String year, String firstYear,
			String lastYear, List<String> negatedTags, Order order, int limit,
			int offset) {
		if(searchType==SearchType.ELASTICSEARCH){
//			searchResource.setINDEX_TYPE(resourceType);
//			searchResource.setResourceConverter(this.resourceConverter);
			try {
				List<Post<R>> posts = this.sharedResourceSearch.fullTextSearch(searchTerms);
				return posts;
			} catch (IOException e) {
				log.error("Failed to search post from shared resource", e);
			}
		
			return null;
		}else if(searchType==SearchType.LUCENESEARCH){
			return this.getPosts(userName, requestedUserName, requestedGroupName, requestedRelationNames, allowedGroups, searchTerms, titleSearchTerms, authorSearchTerms, tagIndex, year, firstYear, lastYear, negatedTags, order, limit, offset);
		}
			return null;
	}

	public EsResourceSearch<R> getSharedResourceSearch() {
		return this.sharedResourceSearch;
	}

	public void setSharedResourceSearch(EsResourceSearch<R> sharedResourceSearch) {
		this.sharedResourceSearch = sharedResourceSearch;
	}

}
