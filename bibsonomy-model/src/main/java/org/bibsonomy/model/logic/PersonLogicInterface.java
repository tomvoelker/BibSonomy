package org.bibsonomy.model.logic;

import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelation;

/**
 * Interface for person entity logic.
 * 
 * @author jil
 */
public interface PersonLogicInterface {

	/**
	 * @param searchString a serach string coming from an autocomplete field. May contain an incomplete word, which will be internally autocompleted before searching persons
	 * @param string 
	 * @return a list of Persons
	 */
	public List<PersonName> getPersonSuggestion(String lastName, String firstName);
	public List<PersonName> getPersonSuggestion(PersonName personName);

	/**
	 * @param longHash publication hash with prefix 1 or 2. Can be null for instance when searching persons just by name.
	 * @param publicationOwner owner of the publication post. May be null which, e.g., is reasonable for inter- or null-hashes. 
	 * @param personName exact name of the person as appearing in the resource. Can be null, when searching for all persons related to a resource
	 * @param rel type of relation. null means all 
	 * @return non-null list of all persons matching all given non-null criteria
	 */

	public void addResourceRelation(ResourcePersonRelation rpr);

	public void removeResourceRelation(int resourceRelationId);

	/**
	 * sets id for new persons
	 * 
	 * @param person the person to be saved or updated
	 */
	public void createOrUpdatePerson(Person person);
	
	public Person getPersonById(int id);
	public PersonName getPersonNameById(int id);
	
	public void removePersonName(Integer personNameId);

	/**
	 * @param personName
	 * @return
	 */
	public Map<Person, BibTex> getQualifyingPublications(String personName);
	
	public List<ResourcePersonRelation> getResourceRelations(int personNameId);
	public List<ResourcePersonRelation> getResourceRelations(Person person);
	public List<ResourcePersonRelation> getResourceRelations(PersonName person);
	

	/**
	 * @param pn
	 * @param requestedHash
	 * @param requestedUser
	 * @param relatorCode
	 * @return
	 */
	public List<ResourcePersonRelation> getResourceRelations(PersonName pn,
			String interHash, String intraHash, String requestedUser, PersonResourceRelation relatorCode);

	/**
	 * @param withPersonId
	 */
	public void createOrUpdatePersonName(PersonName withPersonId);



}
