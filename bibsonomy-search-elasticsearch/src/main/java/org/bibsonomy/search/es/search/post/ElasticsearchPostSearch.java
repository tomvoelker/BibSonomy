/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.es.search.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.search.SearchInfoLogic;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.index.converter.post.ResourceConverter;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.util.Sets;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

/**
 * This class performs a search in the Shared Resource Indices based on the
 * search term
 * 
 * @author lutful
 * @author dzo
 * @param <R>
 */
public class ElasticsearchPostSearch<R extends Resource> implements ResourceSearch<R> {
	private static final Log log = LogFactory.getLog(ElasticsearchPostSearch.class);

	/** post model converter */
	private ResourceConverter<R> resourceConverter;

	/**
	 * logic interface for retrieving data from the main database
	 * (friends, groups members)
	 */
	protected SearchInfoLogic infoLogic;
	
	private ElasticsearchManager<R, ?> manager;

	@Override
	public ResultList<Post<R>> getPosts(final User loggedinUser, PostSearchQuery<?> postQuery) {
		final int offset = BasicQueryUtils.calcOffset(postQuery);
		final int limit = BasicQueryUtils.calcLimit(postQuery);
		return ElasticsearchIndexSearchUtils.callSearch(() -> {
			final ResultList<Post<R>> posts = new ResultList<>();
			final Set<String> allowedUsers = this.getUsersThatShareDocuments(loggedinUser.getName());
			final QueryBuilder queryBuilder = this.buildQuery(loggedinUser, allowedUsers, postQuery);
			if (queryBuilder == null) {
				return posts;
			}

			final List<Pair<String, SortOrder>> sortParameters = this.buildResourceSpecificSortParameters(postQuery.getSortCriteria());
			final SearchHits hits = this.manager.search(queryBuilder, sortParameters, offset, limit, null, null);

			if (hits != null) {
				posts.setTotalCount((int) hits.getTotalHits());

				for (final SearchHit hit : hits) {
					final Post<R> post = this.resourceConverter.convert(hit.getSourceAsMap(), allowedUsers);
					final R resource = post.getResource();

					final long count = this.manager.getDocumentCount(QueryBuilders.termQuery(Fields.Resource.INTERHASH, resource.getInterHash()));

					resource.setCount((int) count);

					/*
					 * remove all other users than the logged in user from the list of users
					 * that have this resource in their collection
					 */
					final List<User> users = post.getUsers();
					final Stream<User> filteredUsers = users.stream().filter(user -> user.equals(loggedinUser));
					post.setUsers(filteredUsers.collect(Collectors.toList()));
					posts.add(post);
				}
			}

			return posts;
		}, new ResultList<>());
	}

	@Override
	public Statistics getStatistics(final User loggedinUser, final PostSearchQuery<?> postQuery) {
		final Set<String> allowedUsers = this.getUsersThatShareDocuments(loggedinUser.getName());
		final QueryBuilder query = this.buildQuery(loggedinUser, allowedUsers, postQuery);
		if (query == null) {
			return new Statistics();
		}

		final Statistics statistics = new Statistics();
		return ElasticsearchIndexSearchUtils.callSearch(() -> {
			final long documentCount = this.manager.getDocumentCount(query);
			statistics.setCount((int) documentCount);
			return statistics;
		}, statistics);
	}

	@Override
	public List<Tag> getTags(User loggedinUser, PostSearchQuery<?> postQuery) {
		final List<String> requestedTags = postQuery.getTags();
		final QueryBuilder query = this.buildQuery(loggedinUser, this.getUsersThatShareDocuments(loggedinUser.getName()), postQuery);
		if (query == null) {
			return new LinkedList<>();
		}

		final Map<Tag, Integer> tagCounter = ElasticsearchIndexSearchUtils.callSearch(() -> {
			final Map<Tag, Integer> tagCounterMap = new HashMap<>();
			final int offset = BasicQueryUtils.calcOffset(postQuery);
			final int limit = BasicQueryUtils.calcLimit(postQuery);
			final SearchHits hits = this.manager.search(query, null, offset, limit, null, Collections.singleton(Fields.TAGS));

			for (int i = 0; i < Math.min(limit, hits.getTotalHits() - offset); ++i) {
				final SearchHit hit = hits.getAt(i);
				final Map<String, Object> result = hit.getSourceAsMap();
				final Set<Tag> tags = this.resourceConverter.onlyConvertTags(result);
				// set tag count
				if (present(tags)) {
					for (final Tag tag : tags) {
						/*
						 * we remove the requested tags because we assume
						 * that related tags are requested
						 */
						if (present(requestedTags) && requestedTags.contains(tag.getName())) {
							continue;
						}
						Integer oldCnt = tagCounterMap.get(tag);
						if (!present(oldCnt)) {
							oldCnt = Integer.valueOf(1);
						} else {
							oldCnt++;
						}
						tagCounterMap.put(tag, oldCnt);
					}
				}
			}
			return tagCounterMap;
		}, Collections.emptyMap());

		final List<Tag> tags = new LinkedList<>();
		// extract all tags
		for (final Map.Entry<Tag, Integer> entry : tagCounter.entrySet()) {
			final Tag tag = entry.getKey();
			final int count = entry.getValue().intValue();
			tag.setUsercount(count);
			tag.setGlobalcount(count); // FIXME: we set user==global count
			tags.add(tag);
		}

		log.debug("Done calculating tag statistics");
		// all done.
		return tags;
	}

	/**
	 * @param userName
	 * @return
	 */
	private Set<String> getUsersThatShareDocuments(final String userName) {
		if (present(userName)) {
			return this.infoLogic.getUserNamesThatShareDocumentsWithUser(userName);
		}
		return new HashSet<>();
	}

	protected final QueryBuilder buildFilter(final User loggedinUser, final Set<String> usersThatShareDocs, final PostSearchQuery<?> postQuery) {
		final String loggedinUserName = loggedinUser.getName();
		final Set<String> allowedGroups = UserUtils.getListOfGroups(loggedinUser).stream().map(Group::getName).collect(Collectors.toSet());

		final BoolQueryBuilder mainFilterBuilder = QueryBuilders.boolQuery();
		final List<String> tags = postQuery.getTags();
		// Add the requested tags
		if (present(tags)) {
			mainFilterBuilder.must(this.buildTagFilter(tags));
		}

		final List<String> negatedTags = postQuery.getNegatedTags();
		if (present(negatedTags)) {
			mainFilterBuilder.must(buildNegatedTags(negatedTags));
		}

		final GroupingEntity grouping = postQuery.getGrouping();
		final String groupingName = postQuery.getGroupingName();

		if (present(grouping)) {
			switch (grouping) {
				case GROUP:
					// restrict result to given group
					// by appending a filter for all members of the group
					final QueryBuilder groupMembersFilter = this.buildGroupMembersFilter(groupingName);
					if (groupMembersFilter != null) {
						mainFilterBuilder.must(groupMembersFilter);
					} else {
						return null;
					}
					break;
				case USER:
					// post owned by user
					// Use this restriction iff there is no user relation
					final QueryBuilder requestedUserFilter = QueryBuilders.termQuery(Fields.USER_NAME, groupingName);
					mainFilterBuilder.must(requestedUserFilter);
					break;
			}
		}

		// restricting access to posts visible to the user
		final BoolQueryBuilder groupFilter = buildGroupFilter(allowedGroups);
		if (present(loggedinUserName)) {
			final TermQueryBuilder privateGroupFilter = QueryBuilders.termQuery(Fields.GROUPS, GroupUtils.buildPrivateGroup().getName());
			final TermQueryBuilder userFilter = QueryBuilders.termQuery(Fields.USER_NAME, loggedinUserName);
			groupFilter.should(QueryBuilders.boolQuery().must(userFilter).must(privateGroupFilter));
		}
		mainFilterBuilder.must(groupFilter);

		return this.buildResourceSpecificFilters(mainFilterBuilder, loggedinUserName, allowedGroups, usersThatShareDocs, postQuery);
	}

	protected BoolQueryBuilder buildResourceSpecificFilters(BoolQueryBuilder mainFilterBuilder, String loggedinUser, Set<String> allowedGroups, Set<String> usersThatShareDocs, PostSearchQuery<?> postQuery) {
		return mainFilterBuilder;
	}

	/**
	 * build the overall elasticsearch query term
	 * 
	 * @param loggedinUser
	 * @param usersThatShareDocs all users that the logged in user is allowed to access
	 * @param postQuery the query
	 * @return overall elasticsearch query
	 */
	protected final QueryBuilder buildQuery(final User loggedinUser, final Set<String> usersThatShareDocs, final PostSearchQuery<?> postQuery) {
		final BoolQueryBuilder mainQueryBuilder = QueryBuilders.boolQuery();

		final String loggedinUserName = loggedinUser.getName();
		final Set<String> allowedGroups = UserUtils.getListOfGroups(loggedinUser).stream().map(Group::getName).collect(Collectors.toSet());

		// here we exclude the logged in user; the docs are already queried using the private fields
		final Set<String> usersToQueryForDocuments = new HashSet<>(usersThatShareDocs);
		if (present(loggedinUserName)) {
			usersToQueryForDocuments.remove(loggedinUserName);
		}

		final String searchTerms = postQuery.getSearch();

		/*
		 * build the query
		 * the resulting main query
		 */
		if (present(searchTerms)) {
			final QueryBuilder queryBuilder = buildStringQueryForSearchTerms(searchTerms, this.manager.getPublicFields());
			
			if (present(loggedinUserName)) {
				// private field
				final TermQueryBuilder userFilter = QueryBuilders.termQuery(Fields.USER_NAME, loggedinUserName);
				final QueryStringQueryBuilder privateFieldSearchQuery = buildStringQueryForSearchTerms(searchTerms, this.manager.getPrivateFields());
				final BoolQueryBuilder privateFieldQueryFiltered = QueryBuilders.boolQuery().must(privateFieldSearchQuery).filter(userFilter);
				
				final BoolQueryBuilder query = QueryBuilders.boolQuery().should(queryBuilder).should(privateFieldQueryFiltered);
				
				if (present(usersToQueryForDocuments)) {
					// document field
					final QueryStringQueryBuilder docFieldSearchQuery = buildStringQueryForSearchTerms(searchTerms, Sets.asSet(Fields.Publication.ALL_DOCS));
					// restrict to users that share documents and to the visible posts (group)
					final BoolQueryBuilder filterQuery = QueryBuilders.boolQuery().must(buildUserQuery(usersThatShareDocs)).must(buildGroupFilter(allowedGroups));
					query.should(QueryBuilders.boolQuery().must(docFieldSearchQuery).filter(filterQuery));
				}
				
				mainQueryBuilder.must(query);
			} else {
				mainQueryBuilder.must(queryBuilder);
			}
		}

		final String titleSearchTerms = postQuery.getTitleSearchTerms();
		if (present(titleSearchTerms)) {
			// we have search terms for title autocompletion, build a phrase prefix query for the title search terms
			final QueryBuilder titleSearchQuery = QueryBuilders.matchPhrasePrefixQuery(Fields.Resource.TITLE, titleSearchTerms);
			mainQueryBuilder.must(titleSearchQuery);
		}
		
		this.buildResourceSpecificQuery(mainQueryBuilder, loggedinUserName, postQuery);

		final QueryBuilder mainFilterBuilder = this.buildFilter(loggedinUser, allowedGroups, postQuery);
		if (!present(mainFilterBuilder)) {
			return null;
		}

		// all done
		log.debug("Search query: '" + mainQueryBuilder.toString() + "' and filters: '" + mainFilterBuilder.toString() + "'");
		return QueryBuilders.boolQuery().must(mainQueryBuilder).filter(mainFilterBuilder);
	}

	protected void buildResourceSpecificQuery(BoolQueryBuilder mainQueryBuilder, String loggedinUser, PostSearchQuery<?> postQuery) {
		// noop
	}

	/**
	 * Takes a list of sort orders and creates a list of sort parameters.
	 * These are pairs contain the attribute names in the searchindex and
	 * the ascending or descending enum from elasticsearch.
	 *
	 * This method only supports Order.TITLE and Order.DATE for building sorting parameters for any resource index.
	 *
	 * @param 	sortCriteria		list of sort criteria
	 * @return	list of sort parameters
	 */
	protected List<Pair<String, SortOrder>> buildResourceSpecificSortParameters(final List<SortCriteria> sortCriteria) {
		final List<Pair<String, SortOrder>> sortParameters = new ArrayList<>();
		if (!present(sortCriteria)) {
			return sortParameters;
		}
		for (SortCriteria sortCrit : sortCriteria) {
			SortOrder esSortOrder = SortOrder.fromString(sortCrit.getSortOrder().toString());
			switch (sortCrit.getSortKey()) {
				// only supported order type for bookmarks
				case TITLE:
					sortParameters.add(new Pair<>(Fields.Sort.TITLE, esSortOrder));
					break;
				case DATE:
					sortParameters.add(new Pair<>(Fields.DATE, esSortOrder));
					break;
				default:
					break;
			}
		}
		return sortParameters;
	}

	private static QueryStringQueryBuilder buildStringQueryForSearchTerms(String searchTerms, final Set<String> fields) {
		final QueryStringQueryBuilder builder = QueryBuilders.queryStringQuery(searchTerms).tieBreaker(1f);
		// set the fields where the string query should search for the string
		fields.forEach(builder::field);
		// set the type to phrase prefix match
		builder.analyzeWildcard(true);
		return builder;
	}

	/**
	 * @param usersThatShareDocs
	 * @return
	 */
	private static BoolQueryBuilder buildUserQuery(Set<String> usersThatShareDocs) {
		final BoolQueryBuilder groupFilter = QueryBuilders.boolQuery();
		for (final String user : usersThatShareDocs){
			groupFilter.should(QueryBuilders.termQuery(Fields.USER_NAME, user));
		}
		return groupFilter;
	}

	/**
	 * @param allowedGroups
	 * @return
	 */
	private static BoolQueryBuilder buildGroupFilter(Collection<String> allowedGroups) {
		final BoolQueryBuilder groupFilter = QueryBuilders.boolQuery();
		for (final String allowedGroup : allowedGroups){
			groupFilter.should(QueryBuilders.termQuery(Fields.GROUPS, allowedGroup));
		}
		return groupFilter;
	}

	/**
	 * @param negatedTags
	 * @return
	 */
	private static QueryBuilder buildNegatedTags(Collection<String> negatedTags) {
		final BoolQueryBuilder tagFilter = QueryBuilders.boolQuery();
		
		for (final String negatedTag : negatedTags) {
			final QueryBuilder negatedSearchQuery = QueryBuilders.termQuery(Fields.TAGS, negatedTag);
			tagFilter.mustNot(negatedSearchQuery);
		}
		
		return tagFilter;
	}

	/**
	 * @param tagIndex
	 * @return
	 */
	private QueryBuilder buildTagFilter(Collection<String> tagIndex) {
		final BoolQueryBuilder tagsFilter = QueryBuilders.boolQuery();
		for (final String tag : tagIndex) {
			// is the tag string a concept name?
			if (tag.startsWith(Tag.CONCEPT_PREFIX)) {
				final String conceptTag = tag.substring(Tag.CONCEPT_PREFIX.length());
				// get related tags:
				final BoolQueryBuilder conceptTags = QueryBuilders.boolQuery();
				// TODO: must the tag be included? TODODZO
				final QueryBuilder termQuery = QueryBuilders.termQuery(Fields.TAGS, conceptTag);
				conceptTags.should(termQuery);
				for (final String subTagString : this.infoLogic.getSubTagsForConceptTag(conceptTag)) {
					conceptTags.should(QueryBuilders.termQuery(Fields.TAGS, subTagString));
				}
				tagsFilter.must(conceptTags);
			} else {
				tagsFilter.must(QueryBuilders.termQuery(Fields.TAGS, tag));
			}
		}
		return tagsFilter;
	}

	/**
	 * @param requestedGroupName
	 * @return 
	 */
	private QueryBuilder buildGroupMembersFilter(String requestedGroupName) {
		final Collection<String> groupMembers = this.infoLogic.getGroupMembersByGroupName(requestedGroupName);
		if (present(requestedGroupName) && present(groupMembers)) {
			final BoolQueryBuilder groupMemberFilter = QueryBuilders.boolQuery();
			for (final String member : groupMembers) {
				final QueryBuilder memberFilter = QueryBuilders.termQuery(Fields.USER_NAME, member);
				groupMemberFilter.should(memberFilter);
			}
			
			return groupMemberFilter;
		}
		return null;
	}
	
	/**
	 * @param resourceConverter the resourceConverter to set
	 */
	public void setResourceConverter(final ResourceConverter<R> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}

	/**
	 * @param infoLogic the infoLogic to set
	 */
	public void setInfoLogic(SearchInfoLogic infoLogic) {
		this.infoLogic = infoLogic;
	}

	/**
	 * @param manager the manager to set
	 */
	public void setManager(ElasticsearchManager<R, ?> manager) {
		this.manager = manager;
	}
}
