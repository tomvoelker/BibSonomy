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
package org.bibsonomy.search.es.index.converter.group;

/**
 * all fields of a group document
 *
 * @author dzo
 */
public interface GroupFields {

	/** the group name */
	String NAME = "name";

	/** the real name of the group */
	String REALNAME = "realname";

	/** the real name (lower case for prefix matching) */
	String REALNAME_PREFIX = "realname_prefix";

	/** the external id of the group */
	String INTERNAL_ID = "internal_id";

	/** flag if the group is a organization */
	String ORGANIZATION = "organization";

	/** homepage of the group */
	String HOMEPAGE = "homepage";

	/** the parent name */
	String PARENT_NAME = "parent_name";

	/** settings */

	/** allows join */
	String ALLOW_JOIN = "allow_join";

	/** shares documents */
	String SHARES_DOCUMENTS = "shares_docs";
}
