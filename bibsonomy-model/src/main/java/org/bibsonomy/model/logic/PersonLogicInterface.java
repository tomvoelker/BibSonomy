package org.bibsonomy.model.logic;

import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelation;

/**
 * Interface for person entity logic.
 * 
 * @author jil
 */
public interface PersonLogicInterface {

	/**
	 * @param searchString a serach string coming from an autocomplete field. May contain an incomplete word, which will be internally autocompleted before searching persons
	 * @return a list of Persons
	 */
	public List<Person> getPersonSuggestion(String searchString);

	/**
	 * @param longHash publication hash with prefix 1 or 2. Can be null for instance when searching persons just by name.
	 * @param publicationOwner owner of the publication post. May be null which, e.g., is reasonable for inter- or null-hashes. 
	 * @param personName exact name of the person as appearing in the resource. Can be null, when searching for all persons related to a resource
	 * @param rel type of relation. null means all 
	 * @return non-null list of all persons matching all given non-null criteria
	 */
	public List<Person> getPersons(String longHash, String publicationOwner, PersonName personName, PersonResourceRelation rel);

	public void addPersonRelation(String longHash, String publicationOwner, String person_ID, PersonResourceRelation rel);

	public void removePersonRelation(String longHash, String publicationOwner, String person_ID, PersonResourceRelation rel);

	/**
	 * sets id for new persons
	 * 
	 * @param person the person to be saved or updated
	 */
	public void createOrUpdatePerson(Person person);
	
	public Person getPersonById(int id);

	/**
	 * @param personName
	 * @return
	 */
	public Map<Person, BibTex> getQualifyingPublications(String personName);


}
