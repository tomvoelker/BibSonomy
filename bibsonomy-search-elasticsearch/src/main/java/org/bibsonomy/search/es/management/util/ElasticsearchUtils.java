/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.es.management.util;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.update.SearchIndexSyncState;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * util methods for managing the index
 *
 * @author dzo
 */
public final class ElasticsearchUtils {
	private ElasticsearchUtils() {}
	
	private static final Log log = LogFactory.getLog(ElasticsearchUtils.class);
	
	
	private static final DateTimeFormatter DATE_OPTIONAL_TIME_FORMATTER = ISODateTimeFormat.dateOptionalTimeParser();
	private static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();
	
	private static final String LAST_PERSON_CHANGE_ID_KEY = "last_person_change_id";
	private static final String LAST_LOG_DATE_KEY = "last_log_date";
	private static final String LAST_TAS_KEY = "last_tas_id";
	
	/** Alias for the inactive index */
	private static final String INACTIVE_INDEX_ALIAS = "inactiveIndex";
	
	/** Alias for the active index */
	private static final String ACTIVE_INDEX_ALIAS = "activeIndex";

	/**
	 * returns the temporary alias used commonly for all newly built indices which are still in build
	 * 
	 * @param resourceType
	 * @return returns temporary the alias name
	 */
	public static String getTempAliasForResource(final Class<? extends Resource> resourceType) {
		return ESConstants.TEMP_INDEX_PREFIX + "-" + ResourceFactory.getResourceName(resourceType).toLowerCase();
	}

	/**
	 * returns the index name based on the home url and resource type
	 * Index Name: systemurl + ResourceType + Unix time stamp
	 * @param systemHome
	 * @param resourceType
	 * @return returns the indexName based on the parameters
	 */
	public static String getIndexNameWithTime(URI systemHome, Class<? extends Resource> resourceType) {
		final String indexName = getIndexName(systemHome, resourceType);
		long timeStamp = System.currentTimeMillis();
		return indexName + "-" + timeStamp;
	}

	/**
	 * returns the alias of the local system for the resource
	 * 
	 * @param resourceType
	 * @param systemHome 
	 * @param isActiveIndex
	 * @return returns the alias name
	 */
	public static String getLocalAliasForResource(final Class<? extends Resource> resourceType, final URI systemHome, final boolean isActiveIndex) {
		if (isActiveIndex) {
			return ACTIVE_INDEX_ALIAS + "-" + getIndexName(systemHome, resourceType);
		}
		
		return INACTIVE_INDEX_ALIAS + "-" + getIndexName(systemHome, resourceType);
	}

	/**
	 * @param systemHome
	 * @param resourceType
	 * @return returns the index name
	 */
	public static String getIndexName(final URI systemHome, Class<? extends Resource> resourceType) {
		final String hostname = normSystemHome(systemHome);
		return hostname + "_" + ResourceFactory.getResourceName(resourceType).toLowerCase();
	}

	/**
	 * @param systemHome
	 * @return the normed system home
	 */
	public static String normSystemHome(final URI systemHome) {
		return (systemHome.getHost() + systemHome.getPath()).replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
	}

	/**
	 * @param contentId
	 * @return the id for the elastic search index
	 */
	public static String createElasticSearchId(int contentId) {
		return String.valueOf(contentId);
	}

	/**
	 * @param state
	 * @return
	 */
	public static Map<String, Object> serializeSearchIndexState(SearchIndexSyncState state) {
		final Map<String, Object> values = new HashMap<>();
		values.put(LAST_TAS_KEY, state.getLast_tas_id());
		values.put(LAST_LOG_DATE_KEY, Long.valueOf(state.getLast_log_date().getTime()));
		values.put(LAST_PERSON_CHANGE_ID_KEY, Long.valueOf(state.getLastPersonChangeId()));
		return values;
	}

	/**
	 * @param source
	 * @return the search index state
	 */
	public static SearchIndexSyncState deserializeSearchIndexState(Map<String, Object> source) {
		final SearchIndexSyncState searchIndexState = new SearchIndexSyncState();
		searchIndexState.setLast_tas_id((Integer) source.get(LAST_TAS_KEY));
		final Long dateAsTime = (Long) source.get(LAST_LOG_DATE_KEY);
		searchIndexState.setLast_log_date(new Date(dateAsTime.longValue()));
		
		searchIndexState.setLastPersonChangeId(((Integer) source.get(LAST_PERSON_CHANGE_ID_KEY)).longValue());
		return searchIndexState;
	}
	
	/**
	 * @param date 
	 * @return the date as string
	 */
	public static String dateToString(final Date date) {
		return DATE_TIME_FORMATTER.print(date.getTime());
	}

	/**
	 * @param dateAsString
	 * @return the date
	 */
	public static Date parseDate(String dateAsString) {
		try {
			return DATE_OPTIONAL_TIME_FORMATTER.parseDateTime(dateAsString).toDate();
		} catch (final IllegalArgumentException e) {
			log.error("can't parse '" + dateAsString + "'.", e);
		}
		
		return null;
	}
}
