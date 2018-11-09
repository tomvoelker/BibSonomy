package org.bibsonomy.search.es.index.converter.project;

/**
 * all fields of a {@link org.bibsonomy.model.cris.Project} in the elasticsearch index
 * @author dzo
 */
public interface ProjectFields {
	/** the external id */
	String EXTERNAL_ID = "external_id";
	/** the title */
	String TITLE = "title";
	/** the subtitle */
	String SUB_TITLE = "sub_title";
	/** the description */
	String DESCRIPTION = "description";
	/** type */
	String TYPE = "type";
	/** budget */
	String BUDGET = "budget";
	/** the start date of the project */
	String START_DATE = "start_date";
	/** the end date of the project */
	String END_DATE = "end_date";
	/** the id of the parent */
	String PARENT = "parent_id";

}
