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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.DuplicateEntryException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.DenieMatchParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonMergeFieldConflict;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Gender;
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
	
	/**
	 * Generates a unique person ID (used for speaking URL)
	 * Concatinates the name and a counter variable
	 * @param person
	 * @param session
	 * @return
	 */
	private String generatePersonId(final Person person, final DBSession session) {
		int counter = 1;
		final String newPersonId = PersonUtils.generatePersonIdBase(person);
		String tempPersonId = newPersonId;
		// increment id until we find the first that is not used (for the current name)
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
	 * Returns a Person identified by it's linked username or
	 * null if the given User has not claimed a Person so far
	 * @param user
	 * @param session 
	 * @return Person
	 */
	public Person getPersonByUser(String user, final DBSession session) {
		return (Person) this.queryForObject("getPersonByUser", user, session);
	}

	/**
	 * Returns a Person identified by it's unique ID
	 * @param id
	 * @param session
	 * @return Person
	 */
	public Person getPersonById(String id, DBSession session) {
		return (Person) this.queryForObject("getPersonById", id, session);
	}

	/**
	 * Returns a Person identified by it's unique DNB ID
	 * @param dnbid
	 * @param session
	 * @return Person
	 */
	public Person getPersonByDnbId(String dnbId, DBSession session) {
		return (Person) this.queryForObject("getPersonByDnbId", dnbId, session);
	}

	/**
	 * Creates a new name and adds it to the specified Person
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
	 * Updates all fields of a given Person
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
	 * Update the OrcID of a Person
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
	 * Update the academic degree of a Person
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
	 * Update the College of a Person
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
	 * Update the Email of a Person
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
	 * Update the Homepage of a Person
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
	 * @return TODO: add documentation
	 */
	public boolean addResourceRelation(ResourcePersonRelation resourcePersonRelation, DBSession session) {
		session.beginTransaction();
		try {
			resourcePersonRelation.setPersonRelChangeId(this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("addResourceRelation", resourcePersonRelation, session);
			session.commitTransaction();
			return true;
		} catch (final DuplicateEntryException e) {
			session.commitTransaction(); // FIXME: only called to not cancel the transaction
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
	
	public List<PersonMatch> getMatches(DBSession session) {
		return this.queryForList("getMatches", null, PersonMatch.class, session);
	}
	
	public List<PersonMatch> getMatchesFor(String personid, DBSession session) {
		return this.queryForList("getMatchesFor", personid, PersonMatch.class, session);
	}
	
	/**
	 * checks if a merge can be performed
	 * 
	 * @param match
	 * @return
	 */
	private boolean mergeable(PersonMatch match, DBSession session){
		//check if a phd/habil conflict raises 	
		if(match.getPerson1().getPersonId().equals(match.getPerson2().getPersonId())){
			this.delete("removeMatchReasons", match.getMatchID(), session);
			this.delete("removePersonMatch", match.getMatchID(), session);
			return false;
		}
		
		BibTex habil1 = this.queryForObject("getHabilForPerson", match.getPerson1().getPersonId(), BibTex.class, session);
		BibTex habil2 = this.queryForObject("getHabilForPerson", match.getPerson2().getPersonId(), BibTex.class, session);
		if(habil1 != null && habil2 != null && habil1.getSimHash1()!= habil2.getSimHash1()){
			return false;
		}
		BibTex phd1 = this.queryForObject("getPHDForPerson", match.getPerson1().getPersonId(), BibTex.class, session);
		BibTex phd2 = this.queryForObject("getPHDForPerson", match.getPerson2().getPersonId(), BibTex.class, session);
		
		if(phd1 != null && phd2 != null && phd1.getSimHash1() != phd2.getSimHash1()) {
			return false;
		}
		
		//two different main names		
		PersonName person1MainName = match.getPerson1().getMainName();
		PersonName person2MainName = match.getPerson2().getMainName();
		
		if(!person1MainName.getFirstName().equals(person2MainName.getFirstName()) || !person1MainName.getLastName().equals(person2MainName.getLastName())) {
			return false;
		}

		//person with different attributes
		if(!match.getPerson1().equalsTo(match.getPerson2())) {
			return false;
		}

		return true;
	}
	
	/**
	 * Person pubs will be merged and the old relation will be logged
	 * 
	 * @param loginUser
	 * @param match
	 * @param session
	 */
	private void mergeAllPubs(PersonMatch match, String loginUser, DBSession session){
		//List<ResourcePersonRelation> allRelationsPerson1 = this.queryForList("getResourcePersonRelationsByPersonId", match.getPerson1ID(), ResourcePersonRelation.class, session);
		List<ResourcePersonRelation> allRelationsPerson2 = this.queryForList("getResourcePersonRelationsByPersonId", match.getPerson2().getPersonId(), ResourcePersonRelation.class, session);
		
		for(ResourcePersonRelation relation : allRelationsPerson2){
			//generate new person_change_id and log the old relation
			this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session);
			this.insert("logPubPersonUpdates", relation.getPersonRelChangeId(), session);
			//set change information and add new relation
			relation.setChangedBy(loginUser);
			relation.setChangedAt(new Date());
			relation.setPerson(match.getPerson1());
			this.update("updateResourcePersonRelation", relation, session);
		}
	}

	/**
	 * Person aliases will be merged
	 * 
	 * @param loginUser
	 * @param match
	 * @param session
	 */
	private void mergePersonAliases(String loginUser, PersonMatch match, DBSession session){
		List<PersonName> person1Names = this.queryForList("getNames", match.getPerson1().getPersonId(), PersonName.class, session);
		List<PersonName> person2Names = this.queryForList("getNames", match.getPerson2().getPersonId(), PersonName.class, session);
		for(PersonName name2 : person2Names){
			//check if person1 already has the name alias
			boolean contains = false;
			for(PersonName name1 : person1Names){
				if(name2.getFirstName().equals(name1.getFirstName()) && name2.getLastName().equals(name1.getLastName())){
					contains = true;
				}
			}
			//add new alias, if person1 does not have it
			if(!contains){
				int newId = this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session);
				name2.setPersonNameChangeId(newId);
				name2.setPersonId(match.getPerson1().getPersonId());
				name2.setChangedAt(new Date());
				name2.setChangedBy(loginUser);
			}
		}
	}
	
	
	/**
	 * This method will merge two persons, if there are no conflicts
	 * The person resource relation will be changed
	 * Name aliases will be added
	 * 
	 * @param match
	 * @param loginUser
	 * @param session
	 * 
	 * @return true if the merge was successful
	 */
	public boolean mergeSimilarPersons(PersonMatch match, String loginUser, DBSession session) {
		//merge two persons, if there is no conflict
		if(mergeable(match, session)){
			//redirect resourcePersonRelation to person1 and log the changes
			//Note that persons can have multiple related posts with same simhash and that they are will be grouped by their simhash1
			mergeAllPubs(match, loginUser, session);
			//add new further alias 
			mergePersonAliases(loginUser, match, session);
			
			//TODO merge person attributes
			
			//sets match state to 2
			this.update("acceptMerge", match.getMatchID(), session);
			//Substitutes person2's id with person1's for all unresolved matches
			this.update("updatePersonMatchAfterMerge", match, session);
			this.delete("removeReflexivMatcheReasons", match.getMatchID(), session);
			this.delete("removeReflexivPersonMatches", match.getMatchID(), session);
			this.delete("removeMatchReasons", match.getMatchID(), session);
			
			return true;
		}
		return false;
	}

	/**
	 *sets state for the match to 1
	 *
	 * @param matchID
	 * @param session
	 */
	public void denieMatch(PersonMatch match, DBSession session, String userName) {
		if(!match.getUserDenies().contains(userName)) {
			DenieMatchParam param = new DenieMatchParam(match.getMatchID(), userName);
			if (match.getUserDenies().size() == PersonMatch.denieThreshold-1) {
				this.delete("denieMatchByID", param, session);
			} 
			this.delete("denieMatchByIDForUser", param, session);
		}
	}

	/**
	 * @param dbSession
	 * @return match with matchID
	 */
	public PersonMatch getMatch(int matchID, DBSession dbSession) {
		return this.queryForObject("getMatchbyID", matchID, PersonMatch.class, dbSession);
	}
	
	/**
	 * @param session
	 * @param personID
	 * @return
	 */
	public List<PersonMatch> getMatchesFor(DBSession session, String personID) {
		return this.queryForList("getMatchesFor", personID, PersonMatch.class,session);
	}
	
	/**
	 * filters the matches such that matches the user denied wont be displayed
	 * @param session
	 * @param personID
	 * @return
	 */
	public List<PersonMatch> getMatchesForFilterWithUserName(DBSession session, String personID, String userName) {
		List<PersonMatch> matches = this.getMatchesFor(session, personID);
		List<PersonMatch> toRemove = new LinkedList<PersonMatch>();
		for (PersonMatch match : matches) {
			if(match.getUserDenies().contains(userName)) {
				toRemove.add(match);
			}
		}
		matches.removeAll(toRemove);
		return matches;
	}
	
	/**
	 * returns a map that contains for each match in matches a list
	 * @param matches
	 * @return
	 */
	public Map<Integer, List<PersonMergeFieldConflict>> getMergeConflicts(List<PersonMatch> matches){
		Map<Integer, List<PersonMergeFieldConflict>> map = new HashMap<Integer, List<PersonMergeFieldConflict>>();
		for(PersonMatch match : matches){
			List<PersonMergeFieldConflict> conflictFields = new LinkedList<PersonMergeFieldConflict>();
			try {
				for (String fieldName : Person.fieldsWithResolvableMergeConflicts) {
					Object person1Value = new PropertyDescriptor(fieldName, Person.class).getReadMethod()
							.invoke(match.getPerson1());
					Object person2Value = new PropertyDescriptor(fieldName, Person.class).getReadMethod()
							.invoke(match.getPerson2());
					if (person1Value != null && person2Value != null) {
						if (person1Value.getClass().equals(String.class)) {
							if (!((String) person1Value).equals((String) person2Value)) {
								conflictFields.add(new PersonMergeFieldConflict(fieldName, (String)person1Value, (String)person2Value));
							}
						} else if (person1Value.getClass().equals(PersonName.class)) {
							String person1Name = ((PersonName) person1Value).getLastName() + ", " +((PersonName) person1Value).getFirstName();
							String person2Name = ((PersonName) person2Value).getLastName() + ", " +((PersonName) person2Value).getFirstName();
							if (!person1Name.equals(person2Name)) {
								conflictFields.add(new PersonMergeFieldConflict(fieldName, person1Name, person2Name));
							}
						} else if (person1Value.getClass().equals(Gender.class)) {
							if (!((Gender) person1Value).equals((Enum) person2Value)) {
								conflictFields.add(new PersonMergeFieldConflict(fieldName, ((Gender) person1Value).name(), ((Gender) person2Value).name()));
							}
						} else {
							System.err.println(
									"Missing " + person1Value.getClass() + " class case for merge conflict detection");
						}
					}
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
					| IntrospectionException e) {
				// TODO Auto-generated catch block
				System.err.println(e);
			}
			map.put(new Integer(match.getMatchID()), conflictFields);
		}
		return map;
	}
}
