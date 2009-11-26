package org.bibsonomy.lucene.search;

import static org.apache.lucene.util.Version.LUCENE_24;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
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
import org.bibsonomy.lucene.util.LucenePostConverter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.util.ValidationUtils;

/**
 * FIXME: document me
 * 
 * @author fei
 *
 */
public class LuceneSearchBibTex extends LuceneResourceSearch<BibTex> {
	final Log log = LogFactory.getLog(LuceneSearchBibTex.class);

	private final static LuceneSearchBibTex singleton = new LuceneSearchBibTex();
	
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
			//searchTerms        = parseToken(FLD_AUTHOR, searchTerms);
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
		if (PARAM_RELEVANCE.equals(orderBy)) {
			sort = new Sort(new SortField[]{
					SortField.FIELD_SCORE,	
					new SortField(FLD_DATE,SortField.STRING,true)
  			});
		} else { 
			// orderBy=="date"
			sort = new Sort(new SortField(FLD_DATE, SortField.STRING,true));
		}
		qf.setSort(sort);
		
		//--------------------------------------------------------------------
		// build query
		//--------------------------------------------------------------------
		BooleanQuery mainQuery       = new BooleanQuery();

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
			Filter rangeFilter=new TermRangeFilter(FLD_YEAR , firstYear, lastYear, includeLowerBound, includeUpperBound);
			filteredQuery=new FilteredQuery(mainQuery,rangeFilter);
			qf.setQuery(filteredQuery);
		} else {
			qf.setQuery(mainQuery);
		}
		log.debug("Search query: " + qf.getQuery().toString());

		// all done.
		return qf;
	}
	
	@Override
	protected Class<BibTex> getResourceType() {
		return BibTex.class;
	}

	@Override
	protected Post<BibTex> convertToPostModel(Document doc) {
		return LucenePostConverter.writeBibTexPost(doc);
	}

	@Override
	protected ResultList<Post<BibTex>> createEmptyResultList() {
		return new ResultList<Post<BibTex>>();
	}
}