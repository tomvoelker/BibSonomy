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
import java.util.Set;

import org.bibsonomy.common.exceptions.DuplicateEntryException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.DNBAliasParam;
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
import org.bibsonomy.util.ValidationUtils;

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
	 * Updates all fields of a given Person in the database
	 * @param person
	 * @param session
	 */
	public void updatePersonOnAll(Person person, DBSession session) {
		session.beginTransaction();
		try {
			this.plugins.onPersonUpdate(person.getPersonId(), session);
			person.setPersonChangeId(generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("updatePersonOnAll", person, session);
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
	
	/**
	 * 
	 * @param personID
	 * @return a list of all matches
	 */
	public List<PersonMatch> getMatches(DBSession session) {
		return this.queryForList("getMatches", null, PersonMatch.class, session);
	}
	
	/**
	 * 
	 * @param personID
	 * @return a list of all matches for a person
	 */
	public List<PersonMatch> getMatchesFor(String personid, DBSession session) {
		return this.queryForList("getMatchesFor", personid, PersonMatch.class, session);
	}
	
	/**
	 * checks if a match can be merged
	 * 
	 * @param match
	 * @return true if no field is different
	 */
	private boolean mergeable(PersonMatch match, DBSession session, String loginUser){	 	
		if (!this.testMergeOnClaims(match, loginUser)) {
			//loginUser is not permitted
			return false;
		}
		
		//check if a phd/habil conflict raises
		Post habil1 = this.queryForObject("getHabilForPerson", match.getPerson1().getPersonId(), Post.class, session);
		Post habil2 = this.queryForObject("getHabilForPerson", match.getPerson2().getPersonId(), Post.class, session);
		//compare habils via hash
		if(habil1 != null && habil2 != null && habil1.getResource().getInterHash() != habil2.getResource().getInterHash()){
			return false;
		}
		Post phd1 = this.queryForObject("getPHDForPerson", match.getPerson1().getPersonId(), Post.class, session);
		Post phd2 = this.queryForObject("getPHDForPerson", match.getPerson2().getPersonId(), Post.class, session);
		//compare phd via hash
		if(phd1 != null && phd2 != null && phd1.getResource().getInterHash() != phd2.getResource().getInterHash()) {
			return false;
		}
		
		//checks if the persons have two different main names
		if(!match.getPerson1().getMainName().equals(match.getPerson2().getMainName())){
			return false;
		}
		
		//check on all other attributes
		if(!match.getPerson1().equalsTo(match.getPerson2())) {
			return false;
		}

		return true;
	}
	
	/**
	 * Person pubs will be redirected to person 1 and the change is logged
	 * 
	 * @param loginUser
	 * @param match
	 * @param session
	 */
	private void mergeAllPubs(PersonMatch match, String loginUser, DBSession session){
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
		if(mergeable(match, session, loginUser) && testMergeOnClaims(match, loginUser)){
			performMerge(match, loginUser, session);
			
			return true;
		}
		return false;
	}
	
	/**
	 * This method will merge two persons, if there are no conflicts
	 * The person resource relation will be changed
	 * Name aliases will be added
	 * 
	 * @param match
	 * @param loginUser
	 * @param session
	 */
	private void performMerge(PersonMatch match, String loginUser, DBSession session) {
		//redirect resourcePersonRelation to person1 and log the changes
		//Note that persons can have multiple related posts with same simhash and that they are will be grouped by their simhash1
		mergeAllPubs(match, loginUser, session);
		//add new further alias 
		mergePersonAliases(loginUser, match, session);
		
		boolean edit = this.combinePersonsAttributes(match.getPerson1(), match.getPerson2());
		if (edit) {
			this.updatePersonOnAll(match.getPerson1(), session);
		}
		this.mergePersonAttributes(match, session);
		//sets match state to 2
		this.update("acceptMerge", match.getMatchID(), session);
		//Substitutes person2's id with person1's for all unresolved matches
		this.update("updatePersonMatchAfterMerge", match, session);
		this.delete("removeReflexivPersonMatches", match.getMatchID(), session);
		this.mergeMerges(match.getPerson1().getPersonId(), session, loginUser);
	}

	/**
	 * updates all attributes for person1 for a conflict merge
	 * updates both persons because the need to be compared later with mergeable
	 * @param match
	 * @param session
	 */
	private void mergePersonAttributes(PersonMatch match, DBSession session) {
		try {
			boolean updated1 = false;
			boolean updated2 = false;
			//fields that can be merged
			for (String fieldName : Person.fieldsWithResolvableMergeConflicts) {
				//get person values of an attribute
				PropertyDescriptor desc = new PropertyDescriptor(fieldName, Person.class);
				Object person1Value = desc.getReadMethod().invoke(match.getPerson1());
				Object person2Value = desc.getReadMethod().invoke(match.getPerson2());
				if (person1Value == null && person2Value != null ){
					//update person 1
					desc.getWriteMethod().invoke(match.getPerson1(), person2Value);
					updated1 = true;
				} else if(person2Value == null && person1Value != null){ 
					//update person 2
					desc.getWriteMethod().invoke(match.getPerson2(), person1Value);
					updated2 = true;
				}
				//dnb personid has a table that will link ids for the same person
				if (fieldName.equals("dnbPersonId") && person1Value != null && person2Value != null) {
					this.insert("addOtherDNBID", new DNBAliasParam((String)person1Value, (String)person2Value), session);
					this.update("updateTransitivDNBID", new DNBAliasParam((String)person1Value, (String)person2Value), session);
				}
			}
			//write changes if a person was updated
			if (updated1){
				this.updatePerson(match.getPerson1(), session);
			}
			if(updated2) {
				this.updatePerson(match.getPerson2(), session);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| IntrospectionException e) {
			System.err.println(e);
		}
	}

	/**
	 * duplicate matches can occur after a merge was performed, because the match list is a transitive closure
	 * duplicates will be combined
	 * @param personId
	 * @param session
	 */
	private void mergeMerges(String personId, DBSession session, String userName) {
		List<PersonMatch> matches = this.getMatchesFor(session, personId);
		List<PersonMatch> dupes = new LinkedList<PersonMatch>();
		//get all duplicate matches
		//one copy remains because j is always bigger than i
		for (int i = 0; i < matches.size()-1; i++) {
			for (int j = i+1; j<matches.size(); j++) {
				if (matches.get(i).compareTo(matches.get(j)) == 0) {
					// both personId's are the same
					dupes.add(matches.get(i));
				}
			}
		}
		//merge duplicate matches
		for (PersonMatch dupe : dupes) {
			//get the same matches to redirect denies
			List<PersonMatch> toMerge = this.queryForList("getSimilarMatchesForMatch", dupe, PersonMatch.class, session);
			PersonMatch combinedMerge = toMerge.get(0);
			// only other matches will be removed
			for (int i = 1; i < toMerge.size(); i++) {
				this.update("redirectUserDenies", new DenieMatchParam(toMerge.get(i).getMatchID(), combinedMerge.getMatchID()), session);
				combinedMerge.getUserDenies().addAll(toMerge.get(i).getUserDenies());
				this.delete("removePersonMatch", toMerge.get(i).getMatchID(), session);
			}
			//get userDenies without duplicates
			combinedMerge.setUserDenies(this.queryForList("getDeniesForMatch", combinedMerge.getMatchID(), String.class, session));
			if (combinedMerge.getUserDenies().size() >= PersonMatch.denieThreshold) {
				//deny merge for all if the total user deny count is bigger than the deny threshold
				this.delete("denieMatchByID", new DenieMatchParam(combinedMerge.getMatchID(), userName), session);
			}
		}
	}

	/**
	 * updates all attributes for person1 for a non conflict merge 
	 * 
	 * @param person1
	 * @param person2
	 * @return true if an attributes was updated
	 * 
	 */
	private boolean combinePersonsAttributes(Person person1, Person person2) {
		boolean edit = false;
		try {
		for (String fieldName : Person.fieldsWithResolvableMergeConflicts) {
			PropertyDescriptor desc = new PropertyDescriptor(fieldName, Person.class);
			Object person1Value = desc.getReadMethod().invoke(person1);
			Object person2Value = desc.getReadMethod().invoke(person2);
			//get infos only person 2 has
			if (person1Value == null && person2Value != null) {
				edit = true;
				desc.getWriteMethod().invoke(person1, person2Value);
			}
		}
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e ) {
			System.err.print(e);
		}
		return edit;
		
		
		
	}

	/**
	 * add user to the deny list of a match 
	 * denies a match for all if a threshold is reached
	 *
	 * @param matchID
	 * @param session
	 */
	public void denieMatch(PersonMatch match, DBSession session, String userName) {
		if(!match.getUserDenies().contains(userName)) {
			DenieMatchParam param = new DenieMatchParam(match.getMatchID(), userName);
			if (match.getUserDenies().size() == PersonMatch.denieThreshold-1) {
				//deny match for all 
				this.delete("denieMatchByID", param, session);
			} 
			//deny match for user
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
	 * 
	 * @param session
	 * @param personID
	 * @return all matches for a person
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
		matches.removeIf(match -> match.getUserDenies().contains(userName));
		
		return matches;
	}

	/**
	 * performs a merge and resolves its conflicts
	 * 
	 * @param session
	 * @param formMatchId
	 * @param map conflicts
	 * @return true if merge could be performed
	 */
	public Boolean conflictMerge(DBSession session, int formMatchId, Map<String, String> map, String loginUser){
		PersonMatch match = this.getMatch(formMatchId, session);
		//check match in claim and field name conflicts
		if (!this.testMergeOnClaims(match, loginUser) || !onlyValidFields(map.keySet())) {
			return false;
		}
		try {
			Person person1 = match.getPerson1();
			Person person2 = match.getPerson2();
			for (String fieldName : map.keySet()) {
				
				//PersonNames are at a separate table
				if(fieldName.equals("mainName")) {
					//set new main names
					this.updateMainName(person1, map.get(fieldName), session, loginUser);
					this.updateMainName(person2, map.get(fieldName), session, loginUser);
				}else if (fieldName.equals("gender")) {
					//genders is an enum
					new PropertyDescriptor(fieldName, Person.class).getWriteMethod().invoke(person1, Gender.valueOf(map.get(fieldName)));
					new PropertyDescriptor(fieldName, Person.class).getWriteMethod().invoke(person2, Gender.valueOf(map.get(fieldName)));
				} else {
					//set all other person values
					new PropertyDescriptor(fieldName, Person.class).getWriteMethod().invoke(person1, map.get(fieldName));
					new PropertyDescriptor(fieldName, Person.class).getWriteMethod().invoke(person2, map.get(fieldName));
				}
			}
			// add changes to both so they can be compared with mergable()
			this.updatePersonOnAll(person1, session);
			this.updatePersonOnAll(person2, session);
			this.performMerge(match, loginUser, session);
			
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.err.println(e);
		}

		return true;
	}
	
	/**
	 * 
	 * @param fields
	 * @return true if only valid fieldNames are in fields
	 */
	private Boolean onlyValidFields(Set<String> fields) {
		fieldLoop:
		for (String string : fields) {
			for (String validField : Person.fieldsWithResolvableMergeConflicts) {
				if (string.equals(validField)) {
					//field found
					continue fieldLoop;
				}
			}
			//invalid field name was inserted
			return false;
		}
		return true;
	}
	
	/**
	 * set new mainName to person due to a conflict merge
	 * @param person
	 * @param newName
	 * @param session
	 */
	private Boolean updateMainName(Person person, String newName, DBSession session, String loginUser) {
		//old mainName
		PersonName mainName = person.getMainName();
		
		if (mainName == null) {
			//failed because no main name was found
			return false;
		}
		mainName.setMain(false);
		this.updatePersonName(mainName, session);
		
		
		//the name was inserted "lastName, firstName"
		String[] nameParts = newName.split(", ", 2);
		if (nameParts.length == 1){
			//failed because invalid input was inserted
			return false;
		}
		
		//check if name already exists as alias
		for (PersonName personName : person.getNames()) {
			String name = personName.toString();
			if(name.equals(newName)) {
				// found name in alias and set it to main name
				personName.setMain(true);
				personName.setChangedBy(loginUser);
				personName.setChangedAt(new Date());
				this.updatePersonName(personName, session);
				return true;
			}
		}
		//new PersonName needs to be added
		PersonName newMainName = new PersonName(nameParts[1], nameParts[0]);
		newMainName.setChangedBy(loginUser);
		newMainName.setChangedAt(new Date());
		newMainName.setMain(true);
		newMainName.setPersonId(person.getPersonId());
		newMainName.setPerson(person);
		this.createPersonName(newMainName, session);
		return true;
	}

	/**
	 * tests if the merge can be performed without a conflict on user claims
	 * 
	 * @param match
	 * @param loginUser
	 */
	private Boolean testMergeOnClaims(PersonMatch match, String loginUser) {
		return match.testMergeOnClaims(loginUser);
	}

	/**
	 * @param personId
	 * @return returns the updated personId, if the person was merged to an other person
	 */
	public String getForwardId(String personId, DBSession session) {
		return this.queryForObject("getPersonForward",personId, String.class, session);
	}
}
