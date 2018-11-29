package org.bibsonomy.search.es.index.converter.person;

/**
 * field names of a {@link org.bibsonomy.model.Person} entry in the fulltext search
 *
 * @author dzo
 */
public interface PersonFields {
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
}
