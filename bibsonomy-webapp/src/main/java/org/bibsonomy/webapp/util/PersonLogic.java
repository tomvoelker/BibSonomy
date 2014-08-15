package org.bibsonomy.webapp.util;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.logic.PersonLogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.util.spring.security.rememberMeServices.CookieBasedRememberMeServices;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Some methods to help handling Persons.
 * 
 * @author rja
 */
public class PersonLogic implements PersonLogicInterface {
	private static final Log log = LogFactory.getLog(PersonLogic.class);

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonSuggestion(java.lang.String)
	 */
	@Override
	public List<Person> getPersonSuggestion(String searchString) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonCandidates(java.lang.String, java.lang.String, org.bibsonomy.model.PersonName)
	 */
	@Override
	public List<Person> getPersonCandidates(String longHash,
			String publicationOwner, PersonName personName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getRelatedPerson(java.lang.String, java.lang.String, org.bibsonomy.model.PersonName, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public Person getRelatedPerson(String longHash, String publicationOwner,
			PersonName personName, PersonResourceRelation rel) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#addRelatedPerson(java.lang.String, java.lang.String, org.bibsonomy.model.Person, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public void addRelatedPerson(String longHash, String publicationOwner,
			Person person, PersonResourceRelation rel) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#removeRelatedPerson(java.lang.String, java.lang.String, org.bibsonomy.model.Person, org.bibsonomy.model.enums.PersonResourceRelation)
	 */
	@Override
	public void removeRelatedPerson(String longHash, String publicationOwner,
			Person person, PersonResourceRelation rel) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#createPerson(org.bibsonomy.model.Person)
	 */
	@Override
	public int createPerson(Person person) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#setPersonUserName(org.bibsonomy.model.Person, java.lang.String)
	 */
	@Override
	public int setPersonUserName(Person person, String userName) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#updatePerson(org.bibsonomy.model.Person)
	 */
	public int updatePerson(Person person) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.PersonLogicInterface#getPersonName(java.lang.String, java.lang.String)
	 */

	public PersonName getPersonName(String firstName, String lastName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Person getPerson(String personId) {
		// TODO
		return null;
	}
	
	
}
