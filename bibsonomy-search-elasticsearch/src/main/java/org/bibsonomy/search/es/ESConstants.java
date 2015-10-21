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
package org.bibsonomy.search.es;

/**
 * The Class for elastic search engine constants.
 * 
 * @author lutful
 */
public final class ESConstants {

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
	
	/** phdthesis+type resolved to habil, phd, master, bachelor*/
	public static final String NORMALIZED_ENTRY_TYPE_FIELD_NAME = "entryTypeNorm";
	
	/** current full names (including titles) of the author person-entities */
	public static final String AUTHOR_ENTITY_NAMES_FIELD_NAME = "authorEntityNames";
	
	/** Ids of the associated author person-entities */
	public static final String AUTHOR_ENTITY_IDS_FIELD_NAME = "authorEntityIds";

	/** current full names (including titles) of the associated authors, editors, supervisors, etc */
	public static final String PERSON_ENTITY_NAMES_FIELD_NAME = "personEntityNames";
	
	/**
	 * prefix for temporary index
	 */
	public static final String TEMP_INDEX_PREFIX = "TempIndex";
	/**
	 * prefix for temporary index
	 */
	public static final String TEMP_ON_PROCESS_INDEX_PREFIX = "TempIndexOnProcess";

	public static interface Fields {
		/** the content id of the post */
		public static final String CONTENT_ID = "content_id";
		/** the name of the user of the post */
		public static final String USER_NAME = "user_name";
		/** the groups of the post */
		public static final String GROUPS = "groups";
		/** the tags of the post */
		public static final String TAGS = "tags";
		/** field name in th index schema */
		public static final String SYSTEM_URL = "systemUrl";
		/** the date (creation) of the post */
		public static final String DATE = "date";
		/** the latest date of the post */
		public static final String CHANGE_DATE = "change_date";
		/** the description */
		public static final String DESCRIPTION = "description";
		/** Ids of the associated authors, editors, supervisors, etc */
		public static final String PERSON_ENTITY_IDS_FIELD_NAME = "personEntityIds";
		
		public static interface Resource {
			/** the title of the resource */
			public static final String TITLE = "title";
			/** the inter hash of the resource */ 
			public static final String INTERHASH = "interhash";
			/** the intra hash of the resource */
			public static final String INTRAHASH = "intrahash";
		}
		
		public static interface Bookmark {
			/** the url of the bookmark */
			public static final String URL = "url";
		}
		
		public static interface Publication {
			public static final String AUTHOR = "author";
			public static final String SCHOOL = "school";
			/** the publication's year */
			public static final String YEAR = "year";
			/** the bibtex key field name */
			public static final String BIBTEXKEY = "bibtexkey";
			public static final String ADDRESS = "address";
		}
	}
}
