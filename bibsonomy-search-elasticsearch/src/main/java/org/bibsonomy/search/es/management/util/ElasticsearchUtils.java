/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.model.SearchIndexState;
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
	@Deprecated
	public static String getIndexNameWithTime(URI systemHome, Class<? extends Resource> resourceType) {
		final String indexName = getIndexName(systemHome, resourceType);
		long timeStamp = System.currentTimeMillis();
		return indexName + "-" + timeStamp;
	}

	/**
	 * returns the index name based on the home url and resource type
	 * Index Name: systemurl + ResourceType + Unix time stamp
	 * @param type
	 * @param type
	 * @return returns the indexName based on the parameters
	 */
	public static String getIndexNameWithTime(URI systemHome, final String type) {
		final String indexName = getIndexName(systemHome, type);
		long timeStamp = System.currentTimeMillis();
		return indexName + "-" + timeStamp;
	}

	public static String getLocalAliasForType(final String type, final URI systemHome, final SearchIndexState state) {
		final String prefix = getPrefixForState(state);

		return prefix + "-" + getIndexName(systemHome, type);
	}

	private static final String getIndexName(final URI systemHome, final String type) {
		final String hostname = normSystemHome(systemHome);
		return hostname + "_" + type;
	}

	private static String getPrefixForState(SearchIndexState state) {
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
		return prefix;
	}

	/**
	 * returns the alias of the local system for the resource
	 * 
	 * @param resourceType
	 * @param systemHome 
	 * @param state
	 * @return returns the alias name
	 */
	@Deprecated
	public static String getLocalAliasForResource(final Class<? extends Resource> resourceType, final URI systemHome, final SearchIndexState state) {
		final String prefix = getPrefixForState(state);

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
	 * @param date 
	 * @return the date as string
	 */
	public static String dateToString(final Date date) {
		if (!present(date)) {
			return null;
		}
		return DATE_TIME_FORMATTER.print(date.getTime());
	}

	/**
	 * @param dateAsString
	 * @return the date
	 */
	public static Date parseDate(String dateAsString) {
		if (!present(dateAsString)) {
			return null;
		}

		try {
			return DATE_OPTIONAL_TIME_FORMATTER.parseDateTime(dateAsString).toDate();
		} catch (final IllegalArgumentException e) {
			log.error("can't parse '" + dateAsString + "'.", e);
		}
		
		return null;
	}

	/**
	 * the index used for saving the index sync states
	 * @param systemURI the uri of the system (maybe more than one system is sharing a elasticsearch instance)
	 * @return
	 */
	public static String getSearchIndexStateIndexName(final URI systemURI) {
		return "." + normSystemHome(systemURI) + "_system_info";
	}

	/**
	 * @param source
	 * @param key
	 * @return the date
	 */
	public static Date parseDate(Map<String, Object> source, String key) {
		final String dateAsString = (String) source.get(key);
		return parseDate(dateAsString);
	}

	public static String escapeQueryString(final String queryString) {
		return QueryParserUtil.escape(queryString);
	}
}
