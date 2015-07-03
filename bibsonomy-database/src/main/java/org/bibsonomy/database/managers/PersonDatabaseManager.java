package org.bibsonomy.database.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.services.searcher.PersonSearch;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 * @author Christian Pfeiffer / eisfair
 */
public class PersonDatabaseManager  extends AbstractDatabaseManager {
	private static final Log log = LogFactory.getLog(PersonDatabaseManager.class);

	private final static PersonDatabaseManager singleton = new PersonDatabaseManager();
	private final GeneralDatabaseManager generalManager;
	private PersonSearch personSearch;

	public static PersonDatabaseManager getInstance() {
		return singleton;
	}
	
	public PersonDatabaseManager() {
		this.generalManager = GeneralDatabaseManager.getInstance();
	}
	
	/**
	 * Inserts a {@link Person} into the database.
	 * 
	 * @param person 
	 * @param session 
	 */
	public void createPerson(final Person person, final DBSession session) {
		session.beginTransaction();
		try {
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("insertPerson", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}


	/**
	 * @param user
	 * @param session 
	 * @return Person
	 */
	public Person getPersonByUser(String user, final DBSession session) {
		return (Person) this.queryForObject("getPersonByUser", user, session);
	}


	/**
	 * @param id
	 * @param session
	 * @return Person
	 */
	public Person getPersonById(String id, DBSession session) {
		return (Person) this.queryForObject("getPersonById", id, session);
	}


	/**
	 * @param dnbid
	 * @param session
	 * @return Person
	 */
	public Person getPersonByDnbId(String dnbId, DBSession session) {
		return (Person) this.queryForObject("getPersonByDnbId", dnbId, session);
	}

	/**
	 * @param mainName
	 * @param session
	 */
	public void createPersonName(PersonName name, DBSession session) {
		session.beginTransaction();
		try {
			name.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("insertName", name, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}


	/**
	 * @param person
	 * @param session
	 */
	public void updatePerson(Person person, DBSession session) {
		session.beginTransaction();
		try {
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("updatePerson", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}


	/**
	 * @param id
	 * @param session
	 * @return Set<PersonName>
	 */
	public List<?> getAlternateNames(int id, DBSession session) {
		return this.queryForList("getAlternateNames", id, session);
	}


	/**
	 * @param id
	 * @param session
	 * @return PersonName
	 */
	public PersonName getPersonNameById(int id, DBSession session) {
		return (PersonName) this.queryForObject("getPersonNameById", id, session);
	}

	/**
	 * @param name 
	 * @param firstName 
	 * @param session 
	 * @return List<PersonName>
	 * 
	 */
	public List<PersonName> findPersonNames(String lastName, String firstName, DBSession session) {
		PersonName personName = new PersonName();
		if (StringUtils.isBlank(lastName) == false) {
			personName.setLastName(lastName.trim() + "%");
			if (StringUtils.isBlank(firstName) == false) {
				personName.setFirstName(firstName.trim().substring(0, 1) + "%");
			} else {
				personName.setFirstName("%");
			}
		} else {
			if (StringUtils.isBlank(firstName) == false) {
				personName.setFirstName(firstName.trim() + "%");
				personName.setLastName("%");
			} else {
				return new ArrayList<>();
			}
		}
		
		
		return (List<PersonName>) this.queryForList("findPersonNames", personName, session);
	}


	/**
	 * @param resourcePersonRelation
	 * @param session 
	 */
	public void addResourceRelation(ResourcePersonRelation resourcePersonRelation, DBSession session) {
		session.beginTransaction();
		try {
			resourcePersonRelation.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("addResourceRelation", resourcePersonRelation, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		
	}


	/**
	 * @param resourceRelationId
	 * @param databaseSession
	 */
	public void removeResourceRelation(int resourceRelationId,
			DBSession databaseSession) {
		databaseSession.beginTransaction();
		try {
			this.delete("removeResourceRelation", resourceRelationId, databaseSession);
			databaseSession.commitTransaction();
		} finally {
			databaseSession.endTransaction();
		}
		
	}


	/**
	 * @param personChangeId
	 * @param databaseSession 
	 */
	public void removePersonName(Integer personChangeId, DBSession databaseSession) {
		databaseSession.beginTransaction();
		try {
			this.delete("removePersonName", personChangeId, databaseSession);
			databaseSession.commitTransaction();
		} finally {
			databaseSession.endTransaction();
		}	
	}


	/**
	 * @param personNameId
	 * @param databaseSession 
	 */
	public void deleteAllNamesOfPerson(String personId, DBSession databaseSession) {
		databaseSession.beginTransaction();
		try {
			this.delete("deleteAllNamesOfPerson", personId, databaseSession);
			databaseSession.commitTransaction();
		} finally {
			databaseSession.endTransaction();
		}	
	}


	/**
	 * @param personNameId
	 * @param databaseSession
	 * @return
	 */
	public List<ResourcePersonRelation> getResourceRelations(int personNameId,
			DBSession databaseSession) {
		return (List<ResourcePersonRelation>) this.queryForList("getResourceRelations", personNameId, databaseSession);
	}
	
	public List<ResourcePersonRelation> getResourceRelations(ResourcePersonRelation resourcePersonRelation,
			DBSession databaseSession) {
		return (List<ResourcePersonRelation>) this.queryForList("getResourceRelationsByResourcePersonRelation", resourcePersonRelation, databaseSession);
	}
	
	// TODO: write testcase for this method and test whether groupBy of OR-mapping works as expected 
	public List<ResourcePersonRelation> getResourcePersonRelationsByPublication(String interHash, DBSession databaseSession) {
		return (List<ResourcePersonRelation>) this.queryForList("getResourcePersonRelationsByPublication", interHash, databaseSession);
	}

	/**
	 * @param longHash
	 * @param publicationOwner
	 * @param personNameId
	 * @param rel
	 * @return
	 */
	public String getLastResourceRelationId(ResourcePersonRelation resourcePersonRelation, DBSession session) {
		return (String) this.queryForObject("getLastResourceRelationId", resourcePersonRelation, session);
	}


	/**
	 * @param username
	 */
	public void unlinkUser(String username, DBSession session) {
		session.beginTransaction();
		try {
			this.update("unlinkUser", username, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	/**
	 * @param hash
	 * @param authorIndex
	 * @param role 
	 * @param session
	 * @return List<ResourcePersonRelation>
	 */
	public List<ResourcePersonRelation> getResourcePersonRelations(final String interhash, final Integer authorIndex, final PersonResourceRelationType role, final DBSession session) {
		final ResourcePersonRelation rpr = new ResourcePersonRelation();
		Post<BibTex> post = new Post<>();
		post.setResource(new BibTex());
		post.getResource().setInterHash(interhash);
		rpr.setPost(post);
		rpr.setPersonIndex(authorIndex);
		rpr.setRelationType(role);
			
			return this.getResourcePersonRelationByResourcePersonRelation(rpr, session);
	}
	
	private List<ResourcePersonRelation> getResourcePersonRelationByResourcePersonRelation(ResourcePersonRelation rpr, DBSession session) {
		session.beginTransaction();
		try {
			return (List<ResourcePersonRelation>) this.queryForList("getResourcePersonRelationByResourcePersonRelation", rpr, session);
		} finally {
			session.endTransaction();
		}
	}


	/**
	 * @param person
	 * @param loginUser 
	 * @param publicationType 
	 * @param session
	 * @return List<ResourcePersonRelation>
	 */
	public List<ResourcePersonRelation> getResourcePersonRelationsWithPosts(
			Person person, User loginUser, Class<? extends BibTex> publicationType, DBSession session) {
		
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, null, null, null, null, null, 0, Integer.MAX_VALUE, null, null, null, null, loginUser);
		final ResourcePersonRelation personRelation = new ResourcePersonRelation();
		personRelation.setPerson(person);
		param.setPersonRelation(personRelation);
		
		session.beginTransaction();
		try {
			if (publicationType == GoldStandardPublication.class) {
				return this.queryForList("getComunityBibTexRelationsForPerson", param, ResourcePersonRelation.class, session);
			} else {
				return this.queryForList("getBibTexRelationsForPerson", param, ResourcePersonRelation.class, session);
			}
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * @param person
	 * @param session
	 * @return List<ResourcePersonRelation>
	 */
	public List<ResourcePersonRelation> getResourcePersonRelations(
			Person person, DBSession session) {
		session.beginTransaction();
		try {
			return (List<ResourcePersonRelation>) this.queryForList("getResourcePersonRelationsByPersonId", person.getPersonId(), session);
		} finally {
			session.endTransaction();
		}
	}


	/**
	 * @param post
	 * @return
	 */
	public List<ResourcePersonRelation> getResourcePersonRelations(
			Post<? extends BibTex> post, DBSession session) {
		session.beginTransaction();
		try {
			return (List<ResourcePersonRelation>) this.queryForList("getResourcePersonRelationsByInterhash", post.getResource().getInterHash(), session);
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * @param queryString
	 * @return
	 */
	public List<ResourcePersonRelation> getPersonSuggestion(String queryString) {
		return this.personSearch.getPersonSuggestion(queryString);
	}

	public void setPersonSearch(PersonSearch personSearch) {
		this.personSearch = personSearch;
	}
}
