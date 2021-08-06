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
package org.bibsonomy.search.es.index.converter.person;

/**
 * field names of a {@link org.bibsonomy.model.Person} entry in the fulltext search
 *
 * @author dzo
 */
public interface PersonFields {

	/** the field containing the database id (person_change_id) */
	String PERSON_DATABASE_ID = "database_id";

	/** the person id field */
	String PERSON_ID = "person_id";

	/** the academic_degree */
	String ACADEMIC_DEGREE = "academic_degree";

	/** the orcid id */
	String ORCID_ID = "orcid_id";

	/** the research id */
	String RESEARCHER_ID = "researcher_id";

	/** the user asscociated with the person */
	String USER_NAME = "user_name";

	/** the gender of the person */
	String GENDER = "gender";

	/** college */
	String COLLEGE = "college";

	/** homepage */
	String HOMEPAGE = "homepage";

	/** email */
	String EMAIL = "email";

	/** all names field containing all names of a person */
	String ALL_NAMES = "all_names";

	/** nested field with names of persons */
	String NAMES = "names";

	/** the name of the person */
	String NAME = "name";

	/** flag if this name is the current main name of the person */
	String MAIN = "main";

	/** the change date of the person */
	String CHANGE_DATE = "change_date";

	/** the joined field */
	String JOIN_FIELD = "resource_relations";

	/** the person type */
	String TYPE_PERSON = "person";

	/** the relation type */
	String TYPE_RELATION = "relation";

	/** the main name */
	String MAIN_NAME = "main_name";

	/** the main name (lowercase) */
	String MAIN_NAME_PREFIX = "main_name_prefix";

	interface RelationFields {
		/** the author index */
		String INDEX = "index";

		/** the relation type */
		String RELATION_TYPE = "relation_type";

		/** the post belonging to the relation */
		String POST = "post";
	}
}
