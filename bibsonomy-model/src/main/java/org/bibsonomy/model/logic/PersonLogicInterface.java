package org.bibsonomy.model.logic;

import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;

/**
 * Interface for person entity logic.
 * 
 * @author jil
 */
public interface PersonLogicInterface {

	public void addResourceRelation(ResourcePersonRelation resourcePersonRelation) throws ResourcePersonAlreadyAssignedException;

	public void removeResourceRelation(int resourceRelationId);

	/**
	 * sets id for new persons
	 * 
	 * @param person the person to be saved or updated
	 */
	public void createOrUpdatePerson(Person person);
	
	public Person getPersonById(PersonIdType idType, String id);
	
	public void removePersonName(Integer personNameId);

	/**
	 * @param withPersonId
	 */
	public void createOrUpdatePersonName(PersonName withPersonId);
	
	/**
	 * @param queryString a search string coming from an autocomplete field. Planned but not yet implemented: May contain an incomplete word, which will be internally autocompleted before searching persons
	 * @return a builder object fo optional parameters
	 */
	public PersonSuggestionQueryBuilder getPersonSuggestion(String queryString);

	/**
	 * @return a querybuilder object by which options for the query can be specified
	 */
	public ResourcePersonRelationQueryBuilder getResourceRelations();


}
