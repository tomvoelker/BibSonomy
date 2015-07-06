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
package org.bibsonomy.es;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.database.LuceneInfoLogic;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.model.Tag;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.sort.SortBuilder;

/**
 * The Class for building queries for Shared Resource Search based on Elasticsearch.
 *
 * @author lutful
 * 
 */

public abstract class ESQueryBuilder {

    private Log log = LogFactory.getLog(ESQueryBuilder.class);

	/**
	 * logic interface for retrieving data from bibsonomy (friends, groups
	 * members)
	 */
	private LuceneInfoLogic dbLogic;
	
	/**
	 * build the overall elasticsearch query term
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
	 * @param bibtexKey 
	 * @param tagIndex 
	 * @param year 
	 * @param firstYear 
	 * @param lastYear 
	 * @param negatedTags
	 * @return overall elasticsearch query
	 */
	protected BoolQueryBuilder buildQuery(final String userName, final String requestedUserName, final String requestedGroupName, final List<String> requestedRelationNames, final Collection<String> allowedGroups, final String searchTerms, final String titleSearchTerms, final String authorSearchTerms, final String bibtexKey,final Collection<String> tagIndex, final String year, final String firstYear, final String lastYear, final Collection<String> negatedTags) {

		BoolQueryBuilder mainQueryBuilder = QueryBuilders.boolQuery();

		// --------------------------------------------------------------------
		// build the query
		// --------------------------------------------------------------------
		// the resulting main query
		if (present(searchTerms)) {
			QueryBuilder queryBuilder = QueryBuilders.queryString(searchTerms);
			mainQueryBuilder.must(queryBuilder);
		}

		if (present(titleSearchTerms)) {
			QueryBuilder titleSearchQuery = termQuery(LuceneFieldNames.TITLE, titleSearchTerms);
			mainQueryBuilder.must(titleSearchQuery);		}
		
		if (present(authorSearchTerms)) {
			QueryBuilder authorSearchQuery = termQuery(LuceneFieldNames.AUTHOR, authorSearchTerms);
			mainQueryBuilder.must(authorSearchQuery);			
		}
		
		if(present(bibtexKey)){
			QueryBuilder bibtexKeyQuery = termQuery(LuceneFieldNames.BIBTEXKEY, bibtexKey);
			mainQueryBuilder.must(bibtexKeyQuery);			
		}
		
		// Add the requested tags
		if (present(tagIndex) || present(negatedTags)) {
			this.addTagQuerries(tagIndex, negatedTags, mainQueryBuilder);
		}

		// restrict result to given group
		if (present(requestedGroupName)) {
			//TODO	
		}
		
		if(allowedGroups!=null){
			final BoolQueryBuilder groupSearchQuery = new BoolQueryBuilder();
			for(String allowedGroup:allowedGroups){
				groupSearchQuery.should(termQuery(LuceneFieldNames.GROUP, allowedGroup));
			}
			mainQueryBuilder.must(groupSearchQuery);
		}else{
			QueryBuilder groupSearchQuery = termQuery(LuceneFieldNames.GROUP, "public");
			mainQueryBuilder.must(groupSearchQuery);			
		}
		// restricting access to posts visible to the user

		// --------------------------------------------------------------------
		// post owned by user 
		// Use this restriction iff there is no user relation
		// --------------------------------------------------------------------
		if (present(requestedUserName) && !present(requestedRelationNames)) {
			QueryBuilder requesterUserSearchQuery = termQuery(LuceneFieldNames.USER, requestedUserName);
			mainQueryBuilder.must(requesterUserSearchQuery);			
		}
		// If there is at once one relation then restrict the results only 
		// to the users in the given relations (inclduing posts of the logged in users)
		else if (present(requestedRelationNames)) {
			// for all relations: TODO

		}
		
		// all done
		log.debug("Search query: " + mainQueryBuilder.toString());

		return mainQueryBuilder;
	}
	
	private void addTagQuerries(final Collection<String> tagIndex, final Collection<String> negatedTags, final BoolQueryBuilder mainQuery) {
		/*
		 * Process normal tags
		 */
		if (present(tagIndex)) {
			for (final String tag : tagIndex) {
				// Is the tag string a concept name?
				if (tag.startsWith(Tag.CONCEPT_PREFIX)) {
					final String conceptTag = tag.substring(2);
					// get related tags:
					final BoolQueryBuilder conceptTags = new BoolQueryBuilder();
					QueryBuilder termQuery = termQuery(LuceneFieldNames.TAS, conceptTag);
					conceptTags.must(termQuery);
					for (final String t : this.dbLogic.getSubTagsForConceptTag(conceptTag)) {
						conceptTags.should(termQuery(LuceneFieldNames.TAS, t));
					}
					mainQuery.must(conceptTags);
				} else {
					mainQuery.must(termQuery(LuceneFieldNames.TAS, tag));
				}
			}
		}
		/*
		 * Process negated Tags
		 */
		
		if (present(negatedTags)) {
			for (final String negatedTag : negatedTags) {
				final QueryBuilder negatedSearchQuery = termQuery(LuceneFieldNames.TAS, negatedTag);
				mainQuery.mustNot(negatedSearchQuery);
			}
		}

	}	
}
