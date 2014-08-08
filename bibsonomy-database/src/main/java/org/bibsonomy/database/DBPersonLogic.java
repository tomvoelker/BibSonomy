package org.bibsonomy.database;

import java.util.List;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.logic.PersonLogicInterface;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class DBPersonLogic implements PersonLogicInterface {
	
	
	public List<Person> getPersonSuggestion(String searchString) {
		throw new UnsupportedOperationException();
	}
	
	public List<Person> getPersonCandidates(String longHash, String publicationOwner, PersonName personName) {
		throw new UnsupportedOperationException();
	}
	
	public Person getRelatedPerson(String longHash, String publicationOwner, PersonName personName, PersonResourceRelation rel) {
		// im databasemanager:
		// select p.* from pub_person pp, person p where pp.simhash1 = #interHash# AND pp.person_id = p.id <isNotNull property="rel"> AND pp.relator_code = #relCode# RequestDate#</isNotNull>
		// bijektive PersonResourceRelation abbildung auf relator codes über iBatis TypeHandlerCallBack
		throw new UnsupportedOperationException();
	}
	
	public void addRelatedPerson(String longHash, String publicationOwner, Person person, PersonResourceRelation rel) {
		// im databasemanager:
		// select p.* from pub_person pp, person p where pp.simhash1 = #interHash# AND pp.person_id = p.id <isNotNull property="rel"> AND pp.relator_code = #relCode# RequestDate#</isNotNull>
		// bijektive PersonResourceRelation abbildung auf relator codes über iBatis TypeHandlerCallBack
		throw new UnsupportedOperationException();
	}
	
	public void removeRelatedPerson(String longHash, String publicationOwner, Person person, PersonResourceRelation rel) {
		throw new UnsupportedOperationException();
	}
	
	public int createPerson(Person person) {
		throw new UnsupportedOperationException();
	}
	
	public int setPersonUserName(Person person, String userName) {
		throw new UnsupportedOperationException();
	}
}
