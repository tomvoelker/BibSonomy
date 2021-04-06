package org.bibsonomy.search.es.index.converter.project;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.bibsonomy.model.cris.Project;

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
