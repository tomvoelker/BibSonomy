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
package org.bibsonomy.search.es.management.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.model.SearchIndexState;
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
	private static final String LAST_DOCUMENT_DATE_KEY = "last_document_date";
	private static final String MAPPING_VERSION = "mapping_version";
	
	/** Alias for the inactive index */
	private static final String INACTIVE_INDEX_ALIAS = "inactiveIndex";
	
	/** Alias for the active index */
	private static final String ACTIVE_INDEX_ALIAS = "activeIndex";
	
	/** Alias for standby index */
	private static final String STANDBY_INDEX_ALIAS = "standbyIndex";

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
	 * @param state
	 * @return returns the alias name
	 */
	public static String getLocalAliasForResource(final Class<? extends Resource> resourceType, final URI systemHome, final SearchIndexState state) {
		final String prefix;
		switch (state) {
		case ACTIVE:
			prefix = ACTIVE_INDEX_ALIAS;
			break;
		case INACTIVE:
			prefix = INACTIVE_INDEX_ALIAS;
			break;
		case STANDBY:
			prefix = STANDBY_INDEX_ALIAS;
			break;
		case GENERATING:
			prefix = ESConstants.TEMP_INDEX_PREFIX;
			break;
		default:
			throw new IllegalArgumentException(state + " not supported");
		}
		
		return prefix + "-" + getIndexName(systemHome, resourceType);
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
	 * @return the serialized index state
	 */
	public static Map<String, Object> serializeSearchIndexState(SearchIndexSyncState state) {
		final Map<String, Object> values = new HashMap<>();
		values.put(LAST_TAS_KEY, state.getLast_tas_id());
		final Date lastLogDate = getDateForIndex(state.getLast_log_date());
		values.put(LAST_LOG_DATE_KEY, Long.valueOf(lastLogDate.getTime()));
		values.put(LAST_PERSON_CHANGE_ID_KEY, Long.valueOf(state.getLastPersonChangeId()));
		final Date lastDocumentDate = getDateForIndex(state.getLastDocumentDate());
		values.put(LAST_DOCUMENT_DATE_KEY, Long.valueOf(lastDocumentDate.getTime()));
		values.put(MAPPING_VERSION, state.getMappingVersion());
		return values;
	}

	/**
	 * @param state
	 * @return
	 */
	private static Date getDateForIndex(Date date) {
		if (!present(date)) {
			return new Date();
		}
		return date;
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
		
		final Long documentDateAsTime = (Long) source.get(LAST_DOCUMENT_DATE_KEY);
		final Date lastDocumentDate;
		if (present(documentDateAsTime)) {
			lastDocumentDate = new Date(documentDateAsTime.longValue());
		} else {
			lastDocumentDate = null;
		}
		searchIndexState.setLastDocumentDate(lastDocumentDate);
		
		// mapping version
		String mappingVersion = (String) source.get(MAPPING_VERSION);
		if (mappingVersion == null) {
			mappingVersion = "unknown";
		}
		searchIndexState.setMappingVersion(mappingVersion);
		
		searchIndexState.setLastPersonChangeId(((Integer) source.get(LAST_PERSON_CHANGE_ID_KEY)).longValue());
		return searchIndexState;
	}
	
	/**
	 * @param date 
	 * @return the date as string
	 */
	public static String dateToString(final Date date) {
		if (!present(date)) {
			return "";
		}
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
