package org.bibsonomy.search.es.management.util;

import java.net.URI;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.ESConstants;

/**
 * util methods for managing the index
 *
 * @author dzo
 */
public final class ElasticSearchUtils {
	private ElasticSearchUtils() {}
	
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
}
