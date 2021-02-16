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

import static org.bibsonomy.util.ValidationUtils.present;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.DuplicateEntryException;
import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.DNBAliasParam;
import org.bibsonomy.database.params.DenyMatchParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.PhDRecommendation;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Gender;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.util.PersonUtils;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.database.services.PersonSearch;
import org.bibsonomy.util.ObjectUtils;

/**
 * database manger for handling {@link Person} related actions
 *
 * @author jensi
 * @author Christian Pfeiffer / eisfair
 */
public class PersonDatabaseManager extends AbstractDatabaseManager {
	private static final Log log = LogFactory.getLog(PersonDatabaseManager.class);
	private static final PersonDatabaseManager singleton = new PersonDatabaseManager();

	private final GeneralDatabaseManager generalManager;
	private final DatabasePluginRegistry plugins;
	private GoldStandardPublicationDatabaseManager goldStandardPublicationDatabaseManager;
	private BibTexDatabaseManager publicationDatabaseManager;
	private PersonSearch personSearch;

	@Deprecated // config via spring
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
	public String createPerson(final Person person, final DBSession session) {
		session.beginTransaction();
		final String generatedPersonId = this.generatePersonId(person, session);
		person.setPersonId(generatedPersonId);
		try {
			// get a new id from the database for the new person
			person.setPersonChangeId(this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("insertPerson", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return generatedPersonId;
	}

	/**
	 * Generates a unique person ID (used for speaking URL) Concatinates the
	 * name and a counter variable
	 *
	 * @param person
	 * @param session
	 * @return
	 */
	private String generatePersonId(final Person person, final DBSession session) {
		int counter = 1;
		final String newPersonId = PersonUtils.generatePersonIdBase(person);
		String tempPersonId = newPersonId;
		// increment id until we find the first that is not used (for the
		// current name)
		do {
			boolean idTaken;
			try {
				final Person tempPerson = this.getPersonById(tempPersonId, session);
				idTaken = present(tempPerson);
			} catch (final ObjectMovedException e) {
				// ignore; but id is taken
				idTaken = true;
			}
			if (idTaken) {
				if (counter < 1000000) {
					tempPersonId = newPersonId + "." + counter;
				} else {
					throw new RuntimeException("Too many person id occurences");
				}
			} else {
				break;
			}
			counter++;
		} while (true);
		return tempPersonId;
	}

	/**
	 * Returns a Person identified by it's linked username or null if the given
	 * User has not claimed a Person so far
	 *
	 * @param user
	 * @param session
	 * @return Person
	 */
	public Person getPersonByUser(String user, final DBSession session) {
		return this.queryForObject("getPersonByUser", user, Person.class, session);
	}

	/**
	 * Returns a Person identified by it's unique ID
	 *
	 * @param id
	 * @param session
	 * @return Person
	 */
	public Person getPersonById(String id, DBSession session) {
		final Person person = this.queryForObject("getPersonById", id, Person.class, session);
		if (!present(person)) {
			final String forwardId = this.getForwardId(id, session);
			if (present(forwardId)) {
				throw new ObjectMovedException(id, Person.class, forwardId, null, null);
			}
		}
		return person;
	}

	/**
	 * Returns a Person identified by it's unique DNB ID
	 *
	 * @param dnbId
	 * @param session
	 * @return Person
	 */
	public Person getPersonByDnbId(String dnbId, DBSession session) {
		return this.queryForObject("getPersonByDnbId", dnbId, Person.class, session);
	}

	/**
	 * Creates a new name and adds it to the specified Person
	 *
	 * @param name
	 * @param session
	 */
	public void createPersonName(PersonName name, DBSession session) {
		session.beginTransaction();
		try {
			name.setPersonNameChangeId(this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			this.insert("insertName", name, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Updates all fields of the given Person
	 *
	 * @param person
	 * @param session
	 */
	public void updatePerson(final Person person, final DBSession session) {
		session.beginTransaction();
		try {
			// always set a new person change id
			person.setPersonChangeId(this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));

			// inform others (to e.g. log the old person)
			this.plugins.onPersonUpdate(person.getPersonId(), session);
			this.update("updatePerson", person, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	private void updatePersonField(final Person person, final String fieldName, final DBSession session) {
		session.beginTransaction();
		try {
			// first check if the person is in the database
			final String personId = person.getPersonId();

			final Person personInDB = this.getPersonById(personId, session);
			if (!present(personInDB)) {
				// FIXME: error message
				return;
			}
			// prepare person
			person.setPersonChangeId(this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));

			// inform the plugins about the update
			this.plugins.onPersonUpdate(personId, session);

			// update (specific field)
			this.update("updatePerson" + fieldName, person, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Update the OrcID of a Person
	 *
	 * @param person
	 * @param session
	 */
	public void updateOrcid(Person person, DBSession session) {
		this.updatePersonField(person, "Orcid", session);
	}

	/**
	 * Update the academic degree of a Person
	 *
	 * @param person
	 * @param session
	 */
	public void updateAcademicDegree(Person person, DBSession session) {
		this.updatePersonField(person, "AcademicDegree", session);
	}

	/**
	 * Update the College of a Person
	 *
	 * @param person
	 * @param session
	 */
	public void updateCollege(Person person, DBSession session) {
		this.updatePersonField(person, "College", session);
	}

	/**
	 * Update the Email of a Person
	 *
	 * @param person
	 * @param session
	 */
	public void updateEmail(Person person, DBSession session) {
		this.updatePersonField(person, "Email", session);
	}

	/**
	 * Update the Homepage of a Person
	 *
	 * @param person
	 * @param session
	 */
	public void updateHomepage(Person person, DBSession session) {
		this.updatePersonField(person,"Homepage", session);
	}

	/**
	 *
	 * @param person
	 * @param session
	 */
	public void updateUserLink(Person person, DBSession session) {
		this.updatePersonField(person, "User", session);
	}

	/**
	 * @param resourcePersonRelation
	 * @param loggedinUser the loggedin user
	 * @param session
	 * @return <code>true</code> iff the relation was added
	 */
	public boolean addResourceRelation(final ResourcePersonRelation resourcePersonRelation, final User loggedinUser, final DBSession session) {
		return this.addResourceRelation(resourcePersonRelation, true, loggedinUser, session);
	}

	private boolean addResourceRelation(final ResourcePersonRelation resourcePersonRelation, boolean generateId, final User loggedinUser, final DBSession session) {
		// FIXME: add validator (index)
		session.beginTransaction();
		/*
		 * to ensure that the resource is always available even when the user deletes a post
		 * we create here a community post of the provided post
		 * FIXME: it is very inefficient to post the complete post e.g. via api
		 */
		final Post<? extends BibTex> post = resourcePersonRelation.getPost();
		final BibTex publication = post.getResource();

		final String intraHash = publication.getIntraHash();
		final String interHash = publication.getInterHash();
		final Post<GoldStandardPublication> communityPostInDB = this.goldStandardPublicationDatabaseManager.getPostDetails(loggedinUser.getName(), interHash, "", Collections.emptyList(), session);
		if (!present(communityPostInDB)) {
			final BibTex resourceToCopy;
			// FIXME: use a better way to test whether a dummy post was provided or a real post FIXME_CRIS
			if (!present(publication.getTitle())) {
				final List<Post<BibTex>> postsByHash = this.publicationDatabaseManager.getPostsByHash(loggedinUser.getName(), intraHash, HashID.SIM_HASH2, GroupID.INVALID.getId(), UserUtils.getListOfGroupIDs(loggedinUser), 1, 0, session);
				if (present(postsByHash)) {
					resourceToCopy = postsByHash.get(0).getResource();
				} else {
					throw new RuntimeException("can't create community post");
				}
			} else {
				resourceToCopy = publication;
			}

			/*
			 * create a new post and setup it with user and date information
			 */
			final Post<GoldStandardPublication> communityPost = new Post<>();
			final Date postingDate = new Date();
			communityPost.setDate(postingDate);
			communityPost.setChangeDate(postingDate);
			communityPost.setUser(loggedinUser);

			final GoldStandardPublication goldPublication = new GoldStandardPublication();
			ObjectUtils.copyPropertyValues(resourceToCopy, goldPublication);
			communityPost.setResource(goldPublication);
			goldPublication.recalculateHashes();
			this.goldStandardPublicationDatabaseManager.createPost(communityPost, loggedinUser, session);
		}

		try {
			/*
			 * only if the flag generateId is set, generate an id
			 * else the id was already generated by another call
			 */
			if (generateId) {
				resourcePersonRelation.setPersonRelChangeId(this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session));
			} else if (resourcePersonRelation.getPersonRelChangeId() == 0) {
				throw new IllegalStateException("person resource relation id not set");
			}

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
	 * @param personId
	 * @param interHash
	 * @param index
	 * @param type
	 * @param loginUser
	 * @param session
	 */
	public void removeResourceRelation(String personId, final String interHash, final int index, final PersonResourceRelationType type, final User loginUser, final DBSession session) {
		this.removeResourceRelation(personId, interHash, index, type, loginUser, false, session);
	}

	/**
	 * @param personId
	 * @param interHash
	 * @param index
	 * @param type
	 * @param loginUser
	 * @param session
	 */
	protected void removeResourceRelation(String personId, final String interHash, final int index, final PersonResourceRelationType type, final User loginUser, final boolean update, final DBSession session) {
		session.beginTransaction();

		try {
			final ResourcePersonRelation resourcePersonRelation = this.getResourcePersonRelation(personId, interHash, index, type, session);
			if (!present(resourcePersonRelation)) {
				// TODO: notify someone
				return;
			}

			// inform the plugins (e.g. to log the deleted relation)
			if (!update) {
				this.plugins.onPubPersonDelete(resourcePersonRelation, loginUser, session);
			}

			this.delete("removeResourceRelation", resourcePersonRelation.getPersonRelChangeId(), session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * @param personNameChangeId
	 * @param loggedInUser
	 * @param session
	 */
	public void removePersonName(int personNameChangeId, final User loggedInUser, DBSession session) {
		session.beginTransaction();
		try {
			final PersonName oldPersonName = this.getPersonNameById(personNameChangeId, session);

			if (!present(oldPersonName)) {
				session.commitTransaction();
				return;
			}
			// inform the plugins (e.g. log the deleted relation)
			this.plugins.onPersonNameDelete(oldPersonName, loggedInUser, session);
			this.delete("removePersonName", Integer.valueOf(personNameChangeId), session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	private PersonName getPersonNameById(int personNameChangeId, final DBSession session) {
		return this.queryForObject("getPersonNameById", personNameChangeId, PersonName.class, session);
	}

	// TODO: write testcase for this method and test whether groupBy of
	// OR-mapping works as expected
	public List<ResourcePersonRelation> getResourcePersonRelationsByPublication(String interHash, DBSession databaseSession) {
		return this.queryForList("getResourcePersonRelationsByPublication", interHash, ResourcePersonRelation.class, databaseSession);
	}

	/**
	 * @param username
	 */
	public void unlinkUser(final String username, final DBSession session) {
		session.beginTransaction();
		try {
			// FIXME: why not getting the person by username and calling onPersonUpdate
			this.plugins.onPersonUpdateByUserName(username, session);
			this.update("unlinkUser", username, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * @param interhash
	 * @param authorIndex
	 * @param role
	 * @param session
	 * @return list of ResourcePersonRelation
	 */
	public List<ResourcePersonRelation> getResourcePersonRelations(final String interhash, final Integer authorIndex, final PersonResourceRelationType role, final DBSession session) {
		final ResourcePersonRelation rpr = new ResourcePersonRelation();
		final Post<BibTex> post = new Post<>();
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

	private ResourcePersonRelation getResourcePersonRelation(final String personId, final String interhash, final int index, final PersonResourceRelationType type, final DBSession session) {
		final ResourcePersonRelation param = new ResourcePersonRelation();
		param.setPersonIndex(index);
		param.setRelationType(type);

		final Person person = new Person();
		person.setPersonId(personId);
		param.setPerson(person);
		final Post<BibTex> post = new Post<>();
		final BibTex bibTex = new BibTex();
		bibTex.setInterHash(interhash);
		post.setResource(bibTex);
		param.setPost(post);

		return this.queryForObject("getResourcePersonRelationByResourcePersonRelation", param, ResourcePersonRelation.class, session);
	}

	/**
	 * @param personId
	 * @param loginUser
	 * @param publicationType
	 * @param session
	 * @return List<ResourcePersonRelation>
	 */
	public List<ResourcePersonRelation> getResourcePersonRelationsWithPosts(String personId, User loginUser, Class<? extends BibTex> publicationType, DBSession session) {

		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, BibTex.class, null, null, null, null, null, 0, Integer.MAX_VALUE, null, null, null, null, loginUser);
		final ResourcePersonRelation personRelation = new ResourcePersonRelation();
		personRelation.setPerson(new Person());
		personRelation.getPerson().setPersonId(personId);
		param.setPersonRelation(personRelation);

		if (publicationType == GoldStandardPublication.class) {
			return this.queryForList("getCommunityBibTexRelationsForPerson", param, ResourcePersonRelation.class, session);
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
		return this.queryForList("getResourcePersonRelationsWithPersonsByInterhash", interhash, ResourcePersonRelation.class, session);
	}

	/**
	 * @param options
	 * @return
	 */
	public List<ResourcePersonRelation> getPersonSuggestion(PersonSuggestionQueryBuilder options) {
		return this.personSearch.getPersonSuggestion(options);
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
	 * @param newNameWithOldId
	 * @param session
	 */
	public void updatePersonName(final PersonName newNameWithOldId, final User loggedinUser, final DBSession session) {
		session.beginTransaction();
		try {
			this.plugins.onPersonNameUpdate(newNameWithOldId, loggedinUser, session);
			this.delete("removePersonName", newNameWithOldId.getPersonNameChangeId(), session);
			this.createPersonName(newNameWithOldId, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * @return a list of all matches
	 */
	public List<PersonMatch> getMatches(final DBSession session) {
		return this.queryForList("getMatches", null, PersonMatch.class, session);
	}

	/**
	 * 
	 * @param personID
	 * @return a list of all matches for a person
	 */
	public List<PersonMatch> getMatchesFor(String personID, DBSession session) {
		return this.queryForList("getMatchesFor", personID, PersonMatch.class, session);
	}

	/**
	 * checks if a two persons can be merged on different attributes and their
	 * phd/habil
	 *
	 * @param match
	 * @param loggedInUser
	 * @param session
	 * @return true if no field is different
	 */
	private boolean mergeable(PersonMatch match, User loggedInUser, DBSession session) {
		if (!this.testMergeOnClaims(match, loggedInUser)) {
			// loginUser is not permitted
			return false;
		}

		final Person person1 = match.getPerson1();
		final Person person2 = match.getPerson2();
		final String personId1 = person1.getPersonId();
		final String personId2 = person2.getPersonId();

		// check if a phd/habil conflict raises
		final Post habil1 = this.queryForObject("getHabilForPerson", personId1, Post.class, session);
		final Post habil2 = this.queryForObject("getHabilForPerson", personId2, Post.class, session);

		// compare habils via hash
		if (habil1 != null && habil2 != null && !habil1.getResource().getInterHash().equals(habil2.getResource().getInterHash())) {
			return false;
		}

		final Post<?> phd1 = this.queryForObject("getPHDForPerson", personId1, Post.class, session);
		final Post<?> phd2 = this.queryForObject("getPHDForPerson", personId2, Post.class, session);
		// compare phd via hash
		if (phd1 != null && phd2 != null && !phd1.getResource().getInterHash().equals(phd2.getResource().getInterHash())) {
			return false;
		}

		// checks if the persons have two different main names
		if (!person1.getMainName().equals(person2.getMainName())) {
			return false;
		}

		// check on all other attributes
		if (!person1.equalsTo(person2)) {
			return false;
		}

		return true;
	}

	/**
	 * Person pubs will be redirected to person 1 and the change is logged
	 *
	 * @param loggedinUser
	 * @param match
	 * @param session
	 */
	private void mergeAllPubs(final PersonMatch match, final User loggedinUser, final DBSession session) {
		final List<ResourcePersonRelation> allRelationsPerson2 = this.getResourcePersonRelationsWithPosts(match.getPerson2().getPersonId(), loggedinUser, GoldStandardPublication.class, session);
		try {
			session.beginTransaction();
			for (final ResourcePersonRelation relation : allRelationsPerson2) {
				this.moveRelationToPerson(relation, match.getPerson1(), loggedinUser, session);
			}
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * moves a relation to a new person
	 *
	 * @param relation
	 * @param person
	 * @param loggedinUser
	 * @param session
	 */
	private void moveRelationToPerson(final ResourcePersonRelation relation, final Person person, final User loggedinUser, final DBSession session) {
		try {
			session.beginTransaction();

			final Integer newId = this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session);

			final ResourcePersonRelation newRelation = new ResourcePersonRelation();
			newRelation.setPost(relation.getPost());
			newRelation.setPerson(person);
			newRelation.setPersonRelChangeId(newId);
			newRelation.setRelationType(relation.getRelationType());
			newRelation.setPersonIndex(relation.getPersonIndex());
			newRelation.setChangedBy(loggedinUser.getName());
			newRelation.setChangedAt(new Date());

			this.plugins.onPersonResourceRelationUpdate(relation, newRelation, loggedinUser, session);

			// remove it from the person
			this.removeResourceRelation(relation.getPerson().getPersonId(), relation.getPost().getResource().getInterHash(), relation.getPersonIndex(), relation.getRelationType(), loggedinUser, true, session);
			this.addResourceRelation(newRelation, false, loggedinUser, session);

			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * Person aliases will be merged
	 *@param match
	 * @param loggedinUser
	 * @param session
	 */
	private void mergePersonNameAliases(final PersonMatch match, final User loggedinUser, final DBSession session) {
		final Person personMergeTarget = match.getPerson1();
		final String personMergeTargetId = personMergeTarget.getPersonId();
		final List<PersonName> person1Names = this.queryForList("getNames", personMergeTargetId, PersonName.class, session);
		final List<PersonName> person2Names = this.queryForList("getNames", match.getPerson2().getPersonId(), PersonName.class, session);
		for (final PersonName personName : person2Names) {
			// add new alias, if merge target does not have it
			if (!person1Names.contains(personName)) {
				final int newId = this.generalManager.getNewId(ConstantID.PERSON_CHANGE_ID, session);
				personName.setPersonNameChangeId(newId);
				personName.setPersonId(personMergeTargetId);
				personName.setChangedAt(new Date());
				personName.setChangedBy(loggedinUser.getName());

				this.createPersonName(personName, session);
			}
		}
	}

	/**
	 * This method will merge two persons, if there are no conflicts The person
	 * resource relation will be changed Name aliases will be added
	 *
	 * @param match
	 * @param loggedinUser
	 * @param session
	 *
	 * @return true if the merge was successful
	 */
	public boolean mergePersons(PersonMatch match, User loggedinUser, DBSession session) {
		// merge two persons, if there is no conflict
		final boolean personsCanBeMerged = mergeable(match, loggedinUser, session) && testMergeOnClaims(match, loggedinUser);
		if (!personsCanBeMerged) {
			return false;
		}

		session.beginTransaction();
		try {
			this.performMerge(match, loggedinUser, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
			return true;
		}
	}

	/**
	 * This method will merge two persons, if there are no conflicts The person
	 * resource relation will be changed Name aliases will be added
	 *
	 * @param match
	 * @param loggedinUser
	 * @param session
	 */
	private void performMerge(final PersonMatch match, final User loggedinUser, final DBSession session) {
		/*
		 * move resourcePersonRelations from person2 to person1 and log the changes
		 */
		this.mergeAllPubs(match, loggedinUser, session);

		// add new further person names
		this.mergePersonNameAliases(match, loggedinUser, session);

		/*
		 * update person attributes
		 */
		final Person personMergeTarget = match.getPerson1();
		final Person personToMerge = match.getPerson2();
		final boolean edit = this.combinePersonsAttributes(personMergeTarget, personToMerge);
		if (edit) {
			this.updatePerson(personMergeTarget, session);
		}

		this.mergePersonAttributes(match, session);

		// sets match state to 2
		this.update("acceptMerge", match.getMatchID(), session);
		// Substitutes person2's id with 's for all unresolved matches
		this.update("updatePersonMatchAfterMerge", match, session);
		this.delete("removeReflexivPersonMatches", match.getMatchID(), session);
		this.mergeMerges(personMergeTarget.getPersonId(), loggedinUser, session);

		/*
		 * at the end delete the person that should be merged with the merge target
		 */
		final String personId = personToMerge.getPersonId();
		this.plugins.onPersonDelete(personToMerge, loggedinUser, session);

		// delete person names first, TODO: remove constraint
		this.delete("deletePersonNamesByPersonId", personId, session);
		this.delete("deletePersonById", personId, session);
	}

	/**
	 * updates all attributes for person1 for a conflict merge updates both
	 * persons because the need to be compared later with mergeable
	 *
	 * @param match
	 * @param session
	 */
	private void mergePersonAttributes(PersonMatch match, DBSession session) {
		try {
			boolean updated1 = false;
			boolean updated2 = false;
			// fields that can be merged
			final Person personMergeTarget = match.getPerson1();
			final Person personToMerge = match.getPerson2();
			for (String fieldName : Person.fieldsWithResolvableMergeConflicts) {
				// get person values of an attribute
				PropertyDescriptor desc = new PropertyDescriptor(fieldName, Person.class);
				Object person1Value = desc.getReadMethod().invoke(personMergeTarget);
				Object person2Value = desc.getReadMethod().invoke(personToMerge);
				if (person1Value == null && person2Value != null) {
					// update person 1
					desc.getWriteMethod().invoke(personMergeTarget, person2Value);
					updated1 = true;
				} else if (person2Value == null && person1Value != null) {
					// update person 2
					desc.getWriteMethod().invoke(personToMerge, person1Value);
					updated2 = true;
				}
				// dnb personid has a table that will link ids for the same
				// person
				if (fieldName.equals("dnbPersonId") && person1Value != null && person2Value != null) {
					this.insert("addOtherDNBID", new DNBAliasParam((String) person1Value, (String) person2Value), session);
					this.update("updateTransitivDNBID", new DNBAliasParam((String) person1Value, (String) person2Value), session);
				}
			}
			// write changes if a person was updated
			if (updated1) {
				this.updatePerson(personMergeTarget, session);
			}

			// FIXME: why do we have to update the person?
			if (updated2) {
				this.updatePerson(personToMerge, session);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			log.error(e);
		}
	}

	/**
	 * duplicate matches can occur after a merge was performed, because the
	 * match list is a transitive closure duplicates will be combined
	 *
	 * @param personId
	 * @param loggedInUser
	 * @param session
	 */
	private void mergeMerges(final String personId, final User loggedInUser, final DBSession session) {
		List<PersonMatch> matches = this.getMatchesFor(personId, session);
		List<PersonMatch> dupes = new LinkedList<>();
		// get all duplicate matches
		// one copy remains because j is always bigger than i
		for (int i = 0; i < matches.size() - 1; i++) {
			for (int j = i + 1; j < matches.size(); j++) {
				if (matches.get(i).equals(matches.get(j))) {
					// both personId's are the same
					dupes.add(matches.get(i));
				}
			}
		}
		// merge duplicate matches
		for (PersonMatch dupe : dupes) {
			// get the same matches to redirect denys
			List<PersonMatch> toMerge = this.queryForList("getSimilarMatchesForMatch", dupe, PersonMatch.class, session);
			PersonMatch combinedMerge = toMerge.get(0);
			// only other matches will be removed
			for (int i = 1; i < toMerge.size(); i++) {
				this.update("redirectUserDenies", new DenyMatchParam(toMerge.get(i).getMatchID(), combinedMerge.getMatchID()), session);
				combinedMerge.getUserDenies().addAll(toMerge.get(i).getUserDenies());
				this.delete("removePersonMatch", toMerge.get(i).getMatchID(), session);
			}
			// get userDenies without duplicates
			combinedMerge.setUserDenies(this.queryForList("getDeniesForMatch", combinedMerge.getMatchID(), String.class, session));
			if (combinedMerge.getUserDenies().size() >= PersonMatch.MAX_NUMBER_OF_DENIES) {
				// deny merge for all if the total user deny count is bigger
				// than the deny threshold
				this.delete("denyMatchByID", new DenyMatchParam(combinedMerge.getMatchID(), loggedInUser.getName()), session);
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
				// get infos only person 2 has
				if (person1Value == null && person2Value != null) {
					edit = true;
					desc.getWriteMethod().invoke(person1, person2Value);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			log.error(e);
		}
		return edit;
	}

	/**
	 * add user to the deny list of a match denys a match for all if a threshold
	 * is reached
	 *
	 * @param match
	 * @param userName
	 * @param session
	 */
	public void denyMatch(PersonMatch match, String userName, DBSession session) {
		if (!match.getUserDenies().contains(userName)) {
			DenyMatchParam param = new DenyMatchParam(match.getMatchID(), userName);
			if (match.getUserDenies().size() == PersonMatch.MAX_NUMBER_OF_DENIES - 1) {
				// deny match for all
				this.delete("denyMatchByID", param, session);
			}
			// deny match for user
			this.delete("denyMatchByIDForUser", param, session);
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
	 *FIXME: why are we not filtering this using a query?
	 *
	 * filters the matches such that matches the user denied wont be displayed
	 *
	 * @param personID
	 * @param userName
	 * @param session
	 * @return
	 */
	public List<PersonMatch> getMatchesForFilterWithUserName(String personID, String userName, DBSession session) {
		final List<PersonMatch> matches = this.getMatchesFor(personID, session);
		matches.removeIf(match -> match.getUserDenies().contains(userName));

		return matches;
	}

	/**
	 * performs a merge and resolves its conflicts
	 *

	 * @param formMatchId
	 * @param map
	 *            conflicts
	 * @param loggedInUser
	 * @param session
	 * @return true if merge could be performed
	 */
	public boolean mergePersonsWithConflicts(int formMatchId, Map<String, String> map, User loggedInUser, DBSession session) {
		final PersonMatch match = this.getMatch(formMatchId, session);
		// check match in claim and field name conflicts
		if (!this.testMergeOnClaims(match, loggedInUser) || !onlyValidFields(map.keySet())) {
			return false;
		}

		try {
			session.beginTransaction();

			final Person person1 = match.getPerson1();
			final Person person2 = match.getPerson2();

			 for (final String fieldName : map.keySet()) {
				// PersonNames are at a separate table
				if (fieldName.equals("mainName")) {
					// set new main names
					this.updateMainName(person1, map.get(fieldName), loggedInUser, session);
					this.updateMainName(person2, map.get(fieldName), loggedInUser, session);
				} else if (fieldName.equals("gender")) {
					// genders is an enum
					new PropertyDescriptor(fieldName, Person.class).getWriteMethod().invoke(person1, Gender.valueOf(map.get(fieldName)));
					new PropertyDescriptor(fieldName, Person.class).getWriteMethod().invoke(person2, Gender.valueOf(map.get(fieldName)));
				} else {
					// set all other person values
					new PropertyDescriptor(fieldName, Person.class).getWriteMethod().invoke(person1, map.get(fieldName));
					new PropertyDescriptor(fieldName, Person.class).getWriteMethod().invoke(person2, map.get(fieldName));
				}
			}

			// add changes to both so they can be compared with mergable
			this.updatePerson(person1, session);
			this.updatePerson(person2, session);

			try {
				this.performMerge(match, loggedInUser, session);
				session.commitTransaction();
			} finally {
				session.endTransaction();
			}

		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error(e);
		}

		return true;
	}

	/**
	 *
	 * @param fields
	 * @return true if only valid fieldNames are in fields
	 */
	private boolean onlyValidFields(Set<String> fields) {
		fieldLoop: for (String string : fields) {
			for (String validField : Person.fieldsWithResolvableMergeConflicts) {
				if (string.equals(validField)) {
					// field found
					continue fieldLoop;
				}
			}
			// invalid field name was inserted
			return false;
		}
		return true;
	}

	/**
	 * set new mainName to person due to a conflict merge
	 *
	  @param person
	 * @param newName FIXME: why is newName not of type PersonName
	 * @param session
	 */
	private boolean updateMainName(Person person, String newName, User loggedinUser, DBSession session) {
		final String loggedinUserName = loggedinUser.getName();
		// old mainName
		final PersonName mainName = person.getMainName();

		if (mainName == null) {
			// failed because no main name was found
			return false;
		}
		mainName.setMain(false);
		this.updatePersonName(mainName, loggedinUser, session);
		// FIXME: why not using PersonNameUtils.discoverPersonName???
		// the name was inserted "lastName, firstName"
		final String[] nameParts = newName.split(", ", 2);
		if (nameParts.length == 1) {
			// failed because invalid input was inserted
			// TODO: check if the main flag is not removed from the name
			return false;
		}

		// check if name already exists as alias
		final Date changeDate = new Date();
		for (final PersonName personName : person.getNames()) {
			String name = personName.toString();
			if (name.equals(newName)) {
				// found name in alias and set it to main name
				personName.setMain(true);
				personName.setChangedBy(loggedinUserName);
				personName.setChangedAt(changeDate);
				this.updatePersonName(personName, loggedinUser, session);
				return true;
			}
		}
		// new PersonName needs to be added
		final PersonName newMainName = new PersonName(nameParts[1], nameParts[0]);
		newMainName.setChangedBy(loggedinUserName);
		newMainName.setChangedAt(changeDate);
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
	private boolean testMergeOnClaims(final PersonMatch match, final User loginUser) {
		final String loggedinUserName = loginUser.getName();
		final String person1User = match.getPerson1().getUser();
		final String person2User = match.getPerson2().getUser();

		final boolean p1Claim = present(person1User);
		final boolean p2Claim = present(person2User);
		if (p1Claim && p2Claim) {
			return false;
		} else if (!p1Claim && !p2Claim) {
			return true;
		} else if (p1Claim) {
			//TODO notify user1 that their is a merge
			return person1User.equals(loggedinUserName);
		} else {
			//TODO notify user2 that their is a merge
			return person2User.equals(loggedinUserName);
		}
	}

	/**
	 * @param personId
	 * @return returns the updated personId, if the person was merged to an
	 *         other person
	 */
	private String getForwardId(String personId, DBSession session) {
		return this.queryForObject("getPersonForward", personId, String.class, session);
	}

	/**
	 * @param personSearch
	 *            the personSearch to set
	 */
	public void setPersonSearch(PersonSearch personSearch) {
		this.personSearch = personSearch;
	}

	/**
	 * @param personID
	 * @param session
	 * @return
	 */
	public List<PhDRecommendation> getPhdAdvisorRecForPerson(String personID, DBSession session) {
		return this.queryForList("getPhdAdvisorRec", personID, PhDRecommendation.class, session);
	}

	/**
	 * @param goldStandardPublicationDatabaseManager the goldStandardPublicationDatabaseManager to set
	 */
	public void setGoldStandardPublicationDatabaseManager(GoldStandardPublicationDatabaseManager goldStandardPublicationDatabaseManager) {
		this.goldStandardPublicationDatabaseManager = goldStandardPublicationDatabaseManager;
	}

	/**
	 * @param publicationDatabaseManager the publicationDatabaseManager to set
	 */
	public void setPublicationDatabaseManager(BibTexDatabaseManager publicationDatabaseManager) {
		this.publicationDatabaseManager = publicationDatabaseManager;
	}
}
