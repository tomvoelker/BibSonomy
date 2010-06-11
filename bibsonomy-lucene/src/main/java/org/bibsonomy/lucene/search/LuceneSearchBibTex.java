package org.bibsonomy.lucene.search;

import static org.apache.lucene.util.Version.LUCENE_24;
import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.lucene.search.collector.TagCountCollector;
import org.bibsonomy.model.BibTex;

/**
 * class for bibtex search
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneSearchBibTex extends LuceneResourceSearch<BibTex> {
	private static final Log log = LogFactory.getLog(LuceneSearchBibTex.class);

	private final static LuceneSearchBibTex singleton = new LuceneSearchBibTex();
	
	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBibTex getInstance() {
		return singleton;
	}
	
	/**
	 * constructor
	 */
	private LuceneSearchBibTex() {
		this.reloadIndex(0);
	}
	
	@Override
	protected BooleanQuery buildSearchQuery(String userName, String searchTerms, String titleSearchTerms, String authorSearchTerms, Collection<String> tagIndex) {
		final BooleanQuery searchQuery = super.buildSearchQuery(userName, searchTerms, titleSearchTerms, authorSearchTerms, tagIndex);
		
		// search author
		if( present(authorSearchTerms) ) {
			final Query authorQuery = this.parseSearchQuery(FLD_AUTHOR, authorSearchTerms);
			searchQuery.add(authorQuery, Occur.MUST);
		}
		
		return searchQuery;
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
	@Override
	protected QuerySortContainer buildAuthorQuery(String group, String searchTerms, String requestedUserName, String requestedGroupName, List<String> groupMembers, String year, String firstYear, String lastYear, List<String> tagList) {
		// FIXME: configure this
//		String orderBy = "relevance"; 
		final String orderBy = "date"; 
		
		// prepare input parameters
		final List<String> tags = new LinkedList<String>();
		if( present(tagList) ) {
			for( String tag : tagList ) {
				try {
					tags.add(parseToken(FLD_TAS, tag));
				} catch (IOException e) {
					log.error("Error parsing input tag " + tag + " ("+e.getMessage()+")");
					tags.add(tag);
				}
			}
			tagList = tags;
		}
		
		final QuerySortContainer qf = new QuerySortContainer();
		
		//--------------------------------------------------------------------
		// set ordering
		//--------------------------------------------------------------------
		final Sort sort;
		if (PARAM_RELEVANCE.equals(orderBy)) {
			sort = new Sort(SortField.FIELD_SCORE, new SortField(FLD_DATE,SortField.STRING,true));
		} else { 
			// orderBy=="date"
			sort = new Sort(new SortField(FLD_DATE, SortField.STRING,true));
		}
		qf.setSort(sort);
		
		//--------------------------------------------------------------------
		// build query
		//--------------------------------------------------------------------
		final BooleanQuery mainQuery = new BooleanQuery();
		final BooleanQuery groupMemberQuery = new BooleanQuery();

		//--------------------------------------------------------------------
		// search terms
		//--------------------------------------------------------------------
		// we parse the (escaped) search term for enabling advanced lucene 
		// search queries 
		QueryParser searchTermParser = new QueryParser(LUCENE_24, FLD_AUTHOR, getAnalyzer());
		if (PARAM_RELEVANCE.equals(orderBy)) {
			searchTermParser.setDefaultOperator(QueryParser.Operator.OR); // is default
		} else { 
			// orderBy=="date"
			searchTermParser.setDefaultOperator(QueryParser.Operator.AND);
		}
		
		if( present(searchTerms) ) {
			Query authorQuery = null;
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
		if ( present(requestedUserName) ) {
			mainQuery.add(new TermQuery(new Term(FLD_USER, requestedUserName)), Occur.MUST);
		}
		//--------------------------------------------------------------------
		// restrict to group members
		//--------------------------------------------------------------------
		if ( present(requestedGroupName) && present(groupMembers) ) {
			for ( String member: groupMembers ) {
				Query memberQuery = new TermQuery(new Term(FLD_USER, member));
				groupMemberQuery.add(memberQuery, Occur.SHOULD);
			}
			mainQuery.add(groupMemberQuery, Occur.MUST);
		}

		//--------------------------------------------------------------------
		// post owned by group
		//--------------------------------------------------------------------
		if ( present(group) ) {
			mainQuery.add( new TermQuery(new Term(FLD_GROUP, group)), Occur.MUST );
		}
		
		//--------------------------------------------------------------------
		// exact year query
		// FIXME: this wasn't used
		//--------------------------------------------------------------------
		boolean includeLowerBound = false;
		boolean includeUpperBound = false;

		if ( present(year) ) {
			year = year.replaceAll("\\D", "");
			mainQuery.add( new TermQuery(new Term(FLD_YEAR, year)), Occur.MUST );
		} else {
		//--------------------------------------------------------------------
		// range query
		//--------------------------------------------------------------------
			// firstYear != null, lastYear != null
			if( present(firstYear) ) {
				firstYear = firstYear.replaceAll("\\D", "");
				includeLowerBound = true;
			}
			// firstYear == null, lastYear != null
			if( present(lastYear) ) {
				lastYear = lastYear.replaceAll("\\D", "");
				includeUpperBound = true; 
			}
		}
		
		//--------------------------------------------------------------------
		// restrict to given tags
		//--------------------------------------------------------------------
		BooleanQuery tagQuery = new BooleanQuery();
		if( present(tagList) ) {
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
			final Filter rangeFilter = new TermRangeFilter(FLD_YEAR , firstYear, lastYear, includeLowerBound, includeUpperBound);
			final FilteredQuery filteredQuery = new FilteredQuery(mainQuery,rangeFilter);
			qf.setQuery(filteredQuery);
		} else {
			qf.setQuery(mainQuery);
		}
		log.debug("[Author] Search query: " + qf.getQuery().toString());
		
		// set up collector
		TagCountCollector collector;
		try {
			collector = new TagCountCollector(null, CFG_TAG_CLOUD_LIMIT, qf.getSort());
		} catch (IOException e) {
			log.error("Error building tag cloud collector");
			collector = null;
		}
		qf.setTagCountCollector(collector);

		// all done.
		return qf;
	}
	
	@Override
	protected String getResourceName() {
		return BibTex.class.getSimpleName();
	}
}