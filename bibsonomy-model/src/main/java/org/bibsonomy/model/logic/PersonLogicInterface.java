package org.bibsonomy.model.logic;

import java.util.List;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelation;

/**
 * TODO: add documentation to this class
 *
 * @author jil
 */
public class PersonLogicInterface {
	
	public List<Person> getPersonCandidates(String interHash, PersonName personName) {
		throw new UnsupportedOperationException();
	}
	
	public Person getPersonRelatedToPublication(String interHash, PersonName personName, PersonResourceRelation rel) {
		// im databasemanager:
		// select p.* from pub_person pp, person p where pp.simhash1 = #interHash# AND pp.person_id = p.id <isNotNull property="rel"> AND pp.relator_code = #relCode# RequestDate#</isNotNull>
		// bijektive PersonResourceRelation abbildung auf relator codes über iBatis TypeHandlerCallBack
		throw new UnsupportedOperationException();
	}
}
