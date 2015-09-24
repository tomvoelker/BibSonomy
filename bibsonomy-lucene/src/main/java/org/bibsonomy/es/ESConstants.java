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

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;

/**
 * The Class for elastic search engine constants.
 * 
 * @author lutful
 */
public final class ESConstants {

	/**
	 * Alias for the active index
	 */
	private static final String ACTIVE_INDEX_ALIAS = "activeIndex";
	/**
	 * Alias for the inactive index
	 */
	private static final String INACTIVE_INDEX_ALIAS = "inactiveIndex";

	/**
	 * system url field name
	 */
	public static final String SYSTEMURL_FIELD = "systemUrl";

	/**
	 * BATCH size to fetch results
	 */
	public static final int BATCHSIZE = 30000;

	/**
	 * Path of Elasticsearch configuration file.
	 */
	public static final String PATH_CONF = "path.conf";

	/**
	 * Path of names.txt file.
	 */
	public static final String NAMES_TXT = "/org.bibsonomy.es/";

	/**
	 * Elasticsearch client SNIFF property.
	 */
	public static final String SNIFF = "client.transport.sniff";

	/**
	 * Elasticsearch Node name
	 */
	public static final String ES_NODE_NAME = "bibsonomy_client";

	/**
	 * Index type for the system information
	 */
	public static final String SYSTEM_INFO_INDEX_TYPE = "SystemInformation";
	
	/** field name in th index schema */
	public static final String SYSTEM_URL_FIELD_NAME = "systemUrl";
	
	/** phdthesis+type resolved to habil, phd, master, bachelor*/
	public static final String NORMALIZED_ENTRY_TYPE_FIELD_NAME = "entryTypeNorm";
	
	/** current full names (including titles) of the author person-entities */
	public static final String AUTHOR_ENTITY_NAMES_FIELD_NAME = "authorEntityNames";
	
	/** Ids of the associated author person-entities */
	public static final String AUTHOR_ENTITY_IDS_FIELD_NAME = "authorEntityIds";

	/** current full names (including titles) of the associated authors, editors, supervisors, etc */
	public static final String PERSON_ENTITY_NAMES_FIELD_NAME = "personEntityNames";
	
	/** Ids of the associated authors, editors, supervisors, etc */
	public static final String PERSON_ENTITY_IDS_FIELD_NAME = "personEntityIds";

	
	/**
	 * prefix for temporary index
	 */
	public static final String TEMP_INDEX_PREFIX = "TempIndex";
	/**
	 * prefix for temporary index
	 */
	public static final String TEMP_ON_PROCESS_INDEX_PREFIX = "TempIndexOnProcess";
	
	/**
	 * bibtex
	 */
	public static final String BIBTEX = BibTex.class.getSimpleName();
	/**
	 * bookmark
	 */
	public static final String BOOKMARK = Bookmark.class.getSimpleName();
	/**
	 * gold standard publication
	 */
	public static final String GOLD_STANDARD = GoldStandardPublication.class.getSimpleName();

	/**
	 * returns the index name based on the home url and resource type
	 * Index Name: systemurl + ResourceType + Unix time stamp
	 * @param systemHome
	 * @param resourceType
	 * @return returns the indexName based on the parameters
	 */
	public static String getIndexNameWithTime(String systemHome, String resourceType) {
		String indexName = getIndexName(systemHome, resourceType);
		long timeStamp = System.currentTimeMillis();
		return indexName + "-" + timeStamp;
	}

	/**
	 * returns the alias used commonly for all systems indexes of the resource 
	 * 
	 * @param resourceType
	 * @param isActiveIndex
	 * @return returns the alias name
	 */
	public static String getGlobalAliasForResource(final String resourceType, final boolean isActiveIndex) {
		if (isActiveIndex) {
			return ACTIVE_INDEX_ALIAS + "-" + resourceType.toLowerCase();
		}
		return INACTIVE_INDEX_ALIAS+ "-" + resourceType.toLowerCase();
	}
	
	/**
	 * returns the temporary alias used commonly for all newly built indices which are still in build
	 * 
	 * @param resourceType
	 * @return returns temporary the alias name
	 */
	public static String getTempAliasForResource(final String resourceType) {
		return TEMP_INDEX_PREFIX + "-" + resourceType.toLowerCase();
	}

	/**
	 * returns the alias of the local system for the resource
	 * 
	 * @param resourceType
	 * @param systemHome 
	 * @param isActiveIndex
	 * @return returns the alias name
	 */
	public static String getLocalAliasForResource(final String resourceType, final String systemHome, final boolean isActiveIndex) {
		if (isActiveIndex) {
			return ACTIVE_INDEX_ALIAS + "-" + systemHome.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() + "-" +resourceType.toLowerCase();
		}
		return INACTIVE_INDEX_ALIAS+ "-" + systemHome.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() + "-" + resourceType.toLowerCase();
	}
	
	/**
	 * @param systemHome
	 * @param resourceType
	 * @return returns the index name
	 */
	public static String getIndexName(String systemHome, String resourceType) {
		String indexName = systemHome.replaceAll("[^a-zA-Z0-9]", "");
		if (resourceType.equalsIgnoreCase(BIBTEX)) {
			indexName += "_" + BIBTEX;
		} else if (resourceType.equalsIgnoreCase(BOOKMARK)) {
			indexName += "_" + BOOKMARK;
		} else if (resourceType.equalsIgnoreCase(GOLD_STANDARD)) {
			indexName += "_" + GOLD_STANDARD;
		}else {
			return null;
		}
		return indexName.toLowerCase();
		
	}	
}
