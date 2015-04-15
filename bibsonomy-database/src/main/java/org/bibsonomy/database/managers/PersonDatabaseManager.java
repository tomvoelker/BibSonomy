package org.bibsonomy.database.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 * @author Christian Pfeiffer / eisfair
 */
public class PersonDatabaseManager  extends AbstractDatabaseManager {
	private static final Log log = LogFactory.getLog(PersonDatabaseManager.class);

	private final static PersonDatabaseManager singleton = new PersonDatabaseManager();

	public static PersonDatabaseManager getInstance() {
		return singleton;
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
	 * @param mainName
	 * @param session
	 */
	public void createPersonName(PersonName mainName, DBSession session) {
		session.beginTransaction();
		try {
			this.insert("insertName", mainName, session);
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
	 * @param mainName
	 * @param session
	 */
	public void updatePersonName(PersonName mainName, DBSession session) {
		session.beginTransaction();
		try {
			this.insert("updatePersonName", mainName, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
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
			personName.withLastName(lastName.trim() + "%");
			if (StringUtils.isBlank(firstName) == false) {
				personName.withFirstName(firstName.trim().substring(0, 1) + "%");
			} else {
				personName.withFirstName("%");
			}
		} else {
			if (StringUtils.isBlank(firstName) == false) {
				personName.withFirstName(firstName.trim() + "%");
				personName.withLastName("%");
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
	 * @param personNameId
	 * @param databaseSession 
	 */
	public void removePersonName(Integer personNameId, DBSession databaseSession) {
		databaseSession.beginTransaction();
		try {
			this.delete("removePersonName", personNameId, databaseSession);
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
	
	public List<ResourcePersonRelation> getResourcePersonRelationsByPost(Post<? extends Resource> post,
			DBSession databaseSession) {
		ResourcePersonRelation param = new ResourcePersonRelation();
		param.setPubOwner(post.getUser().getName());
		param.setSimhash1(post.getResource().getInterHash());
		param.setSimhash2(post.getResource().getIntraHash());
		return (List<ResourcePersonRelation>) this.queryForList("getResourcePersonRelationsByPost", param, databaseSession);
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
}
