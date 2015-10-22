package org.bibsonomy.search.es.management.util;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.update.SearchIndexState;

/**
 * util methods for managing the index
 *
 * @author dzo
 */
public final class ElasticSearchUtils {
	private ElasticSearchUtils() {}
	
	private static final Log log = LogFactory.getLog(ElasticSearchUtils.class);
	
	
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
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
	 * returns the alias used commonly for all systems indexes of the resource 
	 * 
	 * @param resourceType
	 * @param isActiveIndex
	 * @return returns the alias name
	 */
	@Deprecated // TODO: use system uri as param
	public static String getGlobalAliasForResource(final Class<? extends Resource> resourceType, final boolean isActiveIndex) {
		if (isActiveIndex) {
			return ElasticSearchUtils.ACTIVE_INDEX_ALIAS + "-" + ResourceFactory.getResourceName(resourceType).toLowerCase();
		}
		return ElasticSearchUtils.INACTIVE_INDEX_ALIAS + "-" + ResourceFactory.getResourceName(resourceType).toLowerCase();
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
		// TODO: handle Systemhome? TODODZO return (((long) systemHome.hashCode()) << 32l) + contentId.longValue();
		return String.valueOf(contentId);
	}

	/**
	 * @param state
	 * @return
	 */
	public static Map<String, Object> serializeSearchIndexState(SearchIndexState state) {
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
	public static SearchIndexState deserializeSearchIndexState(Map<String, Object> source) {
		final SearchIndexState searchIndexState = new SearchIndexState();
		searchIndexState.setLast_tas_id((Integer) source.get(LAST_TAS_KEY));
		final Long dateAsTime = (Long) source.get(LAST_LOG_DATE_KEY);
		searchIndexState.setLast_log_date(new Date(dateAsTime.longValue()));
		
		searchIndexState.setLastPersonChangeId(((Integer) source.get(LAST_PERSON_CHANGE_ID_KEY)).longValue());
		return searchIndexState;
	}

	/**
	 * @param dateAsString
	 * @return the date
	 */
	public static Date parseDate(String dateAsString) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		try {
			return dateFormat.parse(dateAsString);
		} catch (final ParseException e) {
			log.error("can't parse '" + dateAsString + "'.", e);
		}
		
		return null;
	}
}
