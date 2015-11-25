/**
 * BibSonomy - A blue social bookmark and publication sharing system.
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
package org.bibsonomy.lucene.index;

/**
 * 
 * @author fmi
 */
@Deprecated // TODO: remove lucene
public abstract class LuceneFieldNames {

	// FIXME: configure these fieldnames via spring
	public static final String MERGED_FIELDS  = "mergedfields";
	public static final String PRIVATE_FIELDS = "privatefields";
	public static final String INTRAHASH     = "intrahash";
	public static final String INTERHASH     = "interhash";
	public static final String GROUP         = "group";
	public static final String AUTHOR        = "author";
	public static final String USER          = "user_name";
	public static final String DATE          = "date";
	public static final String YEAR          = "year";
	public static final String TAS           = "tas";	
	public static final String ADDRESS       = "address";
	public static final String TITLE         = "title";	
	public static final String LAST_TAS_ID   = "last_tas_id";
	public static final String LAST_LOG_DATE = "last_log_date";
	public static final String USER_NAME     = "user_name";
	public static final String CONTENT_ID    = "content_id";
	public static final String SCHOOL        = "school";
	public static final String BIBTEXKEY 	 = "bibtexKey";
}
