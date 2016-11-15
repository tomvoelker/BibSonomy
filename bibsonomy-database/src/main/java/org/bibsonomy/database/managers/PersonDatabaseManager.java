/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import java.util.Date;
import java.util.List;

import org.bibsonomy.common.exceptions.DuplicateEntryException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.util.PersonUtils;
import org.bibsonomy.services.searcher.PersonSearch;

/**
 * database manger for handling {@link Person} related actions
 *
 * @author jensi
 * @author Christian Pfeiffer / eisfair
 */
public class PersonDatabaseManager  extends AbstractDatabaseManager {

	private final static PersonDatabaseManager singleton = new PersonDatabaseManager();
	
	private final GeneralDatabaseManager generalManager;
	private final DatabasePluginRegistry plugins;
	private PersonSearch personSearch;
	
	// TODO: remove
	@Deprecated // in favor of spring bean config
	public static PersonDatabaseManager getInstance() {
		return singleton;
	}
	
	private PersonDatabaseManager() {
		this.generalManager = GeneralDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
	}
	
	/**
	 * Inserts a {@link Person} into the database.
	 * 
	 * @param person 
	 * @param session 
	 */
	public void createPerson(final Person person, final DBSession session) {
		session.beginTransaction();
		final String tempPersonId = this.generatePersonId(person, session);
		person.setPersonId(tempPersonId);
		try {
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("insertPerson", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	private String generatePersonId(final Person person, final DBSession session) {
		int counter = 1;
		final String newPersonId = PersonUtils.generatePersonIdBase(person);
		String tempPersonId = newPersonId;
		do {
			final Person tempPerson = this.getPersonById(tempPersonId, session);
			if (tempPerson != null) {
				if (counter < 1000000) {
					tempPersonId = newPersonId + "." + counter;
				} else {
					throw new RuntimeException("Too many person id occurences");
				}
			} else {
				break;
			}
			counter++;
		} while(true);
		return tempPersonId;
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
			name.setPersonNameChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
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
			this.plugins.onPersonUpdate(person.getPersonId(), session);
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("updatePerson", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	/**
	 * Update the OrcID of a person
	 * @param person
	 * @param session
	 */
	public void updateOrcid(Person person, DBSession session) {
		session.beginTransaction();
		try {
			this.plugins.onPersonUpdate(person.getPersonId(), session);
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("updateOrcid", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	/**
	 * Update the academic degree of a person
	 * @param person
	 * @param session
	 */
	public void updateAcademicDegree(Person person, DBSession session) {
		session.beginTransaction();
		try {
			this.plugins.onPersonUpdate(person.getPersonId(), session);
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("updateAcademicDegree", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	/**
	 * Update the College of a person
	 * @param person
	 * @param session
	 */
	public void updateCollege(Person person, DBSession session) {
		session.beginTransaction();
		try {
			this.plugins.onPersonUpdate(person.getPersonId(), session);
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("updateCollege", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	/**
	 * Update the Email of a person
	 * @param person
	 * @param session
	 */
	public void updateEmail(Person person, DBSession session) {
		session.beginTransaction();
		try {
			this.plugins.onPersonUpdate(person.getPersonId(), session);
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("updateEmail", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	/**
	 * Update the Homepage of a person
	 * @param person
	 * @param session
	 */
	public void updateHomepage(Person person, DBSession session) {
		session.beginTransaction();
		try {
			this.plugins.onPersonUpdate(person.getPersonId(), session);
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("updateHomepage", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	

	/**
	 * @param resourcePersonRelation
	 * @param session 
	 */
	public boolean addResourceRelation(ResourcePersonRelation resourcePersonRelation, DBSession session) {
		session.beginTransaction();
		try {
			resourcePersonRelation.setPersonRelChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("addResourceRelation", resourcePersonRelation, session);
			session.commitTransaction();
			return true;
		} catch (final DuplicateEntryException ex) {
			// ignore
			return false;
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * @param personRelChangeId
	 * @param loginUser 
	 * @param databaseSession
	 */
	public void removeResourceRelation(int personRelChangeId, String loginUser, DBSession databaseSession) {
		databaseSession.beginTransaction();
		try {
			ResourcePersonRelation rel = new ResourcePersonRelation();
			rel.setPersonRelChangeId(personRelChangeId);
			rel.setChangedBy(loginUser);
			rel.setChangedAt(new Date());
			this.plugins.onPubPersonDelete(rel, databaseSession);
			this.delete("removeResourceRelation", Integer.valueOf(personRelChangeId), databaseSession);
			databaseSession.commitTransaction();
		} finally {
			databaseSession.endTransaction();
		}
	}


	/**
	 * @param personNameChangeId
	 * @param databaseSession 
	 */
	public void removePersonName(int personNameChangeId, String loginUser, DBSession databaseSession) {
		databaseSession.beginTransaction();
		try {
			PersonName person = new PersonName();
			person.setPersonNameChangeId(personNameChangeId);
			person.setChangedAt(new Date());
			person.setChangedBy(loginUser);
			this.plugins.onPersonNameDelete(person, databaseSession);
			this.delete("removePersonName", Integer.valueOf(personNameChangeId), databaseSession);
			databaseSession.commitTransaction();
		} finally {
			databaseSession.endTransaction();
		}
	}

	// TODO: write testcase for this method and test whether groupBy of OR-mapping works as expected 
	public List<ResourcePersonRelation> getResourcePersonRelationsByPublication(String interHash, DBSession databaseSession) {
		return this.queryForList("getResourcePersonRelationsByPublication", interHash, ResourcePersonRelation.class, databaseSession);
	}

	/**
	 * @param username
	 */
	public void unlinkUser(String username, DBSession session) {
		session.beginTransaction();
		try {
			this.plugins.onPersonUpdateByUserName(username, session);
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
		if (authorIndex != null) {
			rpr.setPersonIndex(authorIndex.intValue());
		} else {
			rpr.setPersonIndex(-1);
		}
		rpr.setRelationType(role);
			
		return this.getResourcePersonRelationByResourcePersonRelation(rpr, session);
	}
	
	private List<ResourcePersonRelation> getResourcePersonRelationByResourcePersonRelation(ResourcePersonRelation rpr, DBSession session) {
		return this.queryForList("getResourcePersonRelationByResourcePersonRelation", rpr, ResourcePersonRelation.class, session);
	}


	/**
	 * @param person
	 * @param loginUser 
	 * @param publicationType 
	 * @param session
	 * @return List<ResourcePersonRelation>
	 */
	public List<ResourcePersonRelation> getResourcePersonRelationsWithPosts(
			String personId, User loginUser, Class<? extends BibTex> publicationType, DBSession session) {
		
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, null, null, null, null, null, 0, Integer.MAX_VALUE, null, null, null, null, loginUser);
		final ResourcePersonRelation personRelation = new ResourcePersonRelation();
		personRelation.setPerson(new Person());
		personRelation.getPerson().setPersonId(personId);
		param.setPersonRelation(personRelation);
		
		if (publicationType == GoldStandardPublication.class) {
			return this.queryForList("getComunityBibTexRelationsForPerson", param, ResourcePersonRelation.class, session);
		} else {
			return this.queryForList("getBibTexRelationsForPerson", param, ResourcePersonRelation.class, session);
		}
	}

	/**
	 * @param interhash
	 * @param session
	 * @return
	 */
	public List<ResourcePersonRelation> getResourcePersonRelationsWithPersonsByInterhash(String interhash, DBSession session) {
		return (List<ResourcePersonRelation>) this.queryForList("getResourcePersonRelationsWithPersonsByInterhash", interhash, ResourcePersonRelation.class, session);
	}

	/**
	 * @param queryString
	 * @return
	 */
	public List<ResourcePersonRelation> getPersonSuggestion(PersonSuggestionQueryBuilder options) {
		return this.personSearch.getPersonSuggestion(options);
	}

	public void setPersonSearch(PersonSearch personSearch) {
		this.personSearch = personSearch;
	}

	/**
	 * @param personId
	 * @param session
	 * @return
	 */
	public List<PersonName> getPersonNames(String personId, DBSession session) {
		return this.queryForList("getNames", personId, PersonName.class, session);
	}

	/**
	 * @param personNameChangeId
	 * @param newName
	 * @param session
	 */
	public void updatePersonName(PersonName newNameWithOldId, DBSession session) {
		session.beginTransaction();
		try {
			this.plugins.onPersonNameUpdate(newNameWithOldId.getPersonNameChangeId(), session);
			this.delete("removePersonName", newNameWithOldId.getPersonNameChangeId(), session);
			this.createPersonName(newNameWithOldId, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

}
