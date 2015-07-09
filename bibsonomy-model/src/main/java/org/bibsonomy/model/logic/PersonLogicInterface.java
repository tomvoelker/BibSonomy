package org.bibsonomy.model.logic;

import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * Interface for person entity logic.
 * 
 * @author jil
 */
public interface PersonLogicInterface {

	public void addResourceRelation(ResourcePersonRelation resourcePersonRelation);

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
	 * @param personName
	 * @return
	 */
	public Map<Person, BibTex> getQualifyingPublications(String personName);

	/**
	 * @param withPersonId
	 */
	public void createOrUpdatePersonName(PersonName withPersonId);

	/**
	 * @param hash
	 * @param role
	 * @param authorIndex
	 * @return List<ResourcePersonRelation>
	 */
	public List<ResourcePersonRelation> getResourceRelations(String hash, PersonResourceRelationType role, Integer authorIndex);


	/**
	 * @param person
	 * @return
	 */
	public List<ResourcePersonRelation> getResourceRelations(Person person);

	/**
	 * @param post
	 * @return
	 */
	public List<ResourcePersonRelation> getResourceRelations(Post<? extends BibTex> post);
	
	/**
	 * @param queryString a search string coming from an autocomplete field. Planned but not yet implemented: May contain an incomplete word, which will be internally autocompleted before searching persons
	 * @return
	 */
	public List<ResourcePersonRelation> getPersonSuggestion(String queryString);


}
