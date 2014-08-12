package org.bibsonomy.model.logic;

import java.util.List;

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

	public List<Person> getPersonCandidates(String longHash, String publicationOwner, PersonName personName);

	public Person getRelatedPerson(String longHash, String publicationOwner, PersonName personName, PersonResourceRelation rel);

	public void addRelatedPerson(String longHash, String publicationOwner, Person person, PersonResourceRelation rel);

	public void removeRelatedPerson(String longHash, String publicationOwner, Person person, PersonResourceRelation rel);

	public int createPerson(Person person);

	public int setPersonUserName(Person person, String userName);
	
	public int updatePerson(Person person);
	
	public PersonName getPersonName(String firstName, String lastName);
}
