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

/**
 * The Class for elastic search engine constants.
 * 
 * @author lutful
 */
public final class ESConstants {
	/**
	 * Elasticsearch Index Name
	 */
	public static final String INDEX_NAME = "posts";
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
	
	/** field name in th index schema */
	public static final String QUALIFYING_DEGREE_FIELD_NAME = "qualifyingDegree";
}
