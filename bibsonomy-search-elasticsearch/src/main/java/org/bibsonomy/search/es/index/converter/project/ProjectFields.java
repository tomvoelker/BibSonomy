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
package org.bibsonomy.search.es.index.converter.project;

/**
 * all fields of a {@link org.bibsonomy.model.cris.Project} in the elasticsearch index
 * @author dzo
 */
public interface ProjectFields {

	/*+ the project mapping type */
	String PROJECT_DOCUMENT_TYPE = "project";

	/** the external id */
	String EXTERNAL_ID = "external_id";
	/** the title */
	String TITLE = "title";
	/** the lowercase title for prefix match */
	String TITLE_PREFIX = "title_prefix";
	/** the subtitle */
	String SUB_TITLE = "sub_title";
	/** the description */
	String DESCRIPTION = "description";
	/** type */
	String TYPE = "type";
	/** sponsor */
	String SPONSOR = "sponsor";
	/** budget */
	String BUDGET = "budget";
	/** the start date of the project */
	String START_DATE = "start_date";
	/** the end date of the project */
	String END_DATE = "end_date";
	/** the id of the parent */
	String PARENT = "parent_id";
	/** the join field for the person field */
	String JOIN_FIELD = "persons";
	/** the type of a {@link org.bibsonomy.model.cris.Project} */
	String TYPE_PROJECT = "project";
}
