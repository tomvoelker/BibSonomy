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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonMergeFieldConflict;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.util.PersonMatchUtils;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * tests for {@link PersonDatabaseManager}
 *
 * @author dzo
 * @author mho
 */
public class PersonDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static final PersonDatabaseManager PERSON_DATABASE_MANAGER = PersonDatabaseManager.getInstance();
	private static final BibTexDatabaseManager PUBLICATION_DATABASE_MANAGER = BibTexDatabaseManager.getInstance();
	private static final GoldStandardPublicationDatabaseManager COMMUNITY_DATABASE_MANAGER = GoldStandardPublicationDatabaseManager.getInstance();

	private static final User loginUser = new User("jaeschke");
	private static final String intraHash = "15a1bdcbff44431651957f45097dc4f4";
	private static final String PERSON_ID = "h.muller";

	private Person testPerson;

	/**
	 * Initializes the test environment for this class
	 * NOTE: we have to use @Before because we need the DB session to access the database
	 */
	@Before
	public void init() {
		this.testPerson = new Person();
		this.testPerson.setMainName(new PersonName("Max", "Mustermann"));
		PERSON_DATABASE_MANAGER.createPerson(this.testPerson, this.dbSession);
	}	
	
	/**
	 * tests {@link PersonDatabaseManager#createPerson(Person, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testCreatePerson() {
		final Person person = new Person();
		person.setMainName(new PersonName("John", "Doe"));
		PERSON_DATABASE_MANAGER.createPerson(person, this.dbSession);
		
		PERSON_DATABASE_MANAGER.getPersonById(person.getPersonId(), this.dbSession);
	}
	
	/**
	 * tests {@link PersonDatabaseManager#addResourceRelation(ResourcePersonRelation, User, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testAddResourceRelation() {
		final Post<? extends BibTex> post = PUBLICATION_DATABASE_MANAGER.getPostDetails(loginUser.getName(), intraHash, loginUser.getName(), Collections.singletonList(Integer.valueOf(PUBLIC_GROUP_ID)), this.dbSession);
		this.addTestRelationForPost(post);
	}

	private void addTestRelationForPost(Post<? extends BibTex> post) {
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		resourcePersonRelation.setPost(post);
		final Person person = PERSON_DATABASE_MANAGER.getPersonById(PERSON_ID, this.dbSession);
		resourcePersonRelation.setPerson(person);
		resourcePersonRelation.setRelationType(PersonResourceRelationType.AUTHOR);
		assertThat(PERSON_DATABASE_MANAGER.addResourceRelation(resourcePersonRelation, loginUser, this.dbSession), is(true));

		// check if the community post was created
		final Post<GoldStandardPublication> communityPost = COMMUNITY_DATABASE_MANAGER.getPostDetails(loginUser.getName(), post.getResource().getInterHash(), "", Collections.emptyList(), this.dbSession);
		// inserting a publication resource relation should have created a new community post
		assertNotNull(communityPost);

		// test inserting of a duplicate
		assertThat(PERSON_DATABASE_MANAGER.addResourceRelation(resourcePersonRelation, loginUser, this.dbSession), is(false));
	}

	@Test
	public void testAddResourceRelationWithLookupPost() {
		final Post<BibTex> publicationPost = new Post<>();
		final BibTex publication = new BibTex();
		publication.setIntraHash(intraHash);
		publication.setInterHash("a5936835f9eeab91eb09d84948306178");
		publicationPost.setResource(publication);
		this.addTestRelationForPost(publicationPost);
	}

	@Test
	public void testGetPersonById() {
		final Person person = PERSON_DATABASE_MANAGER.getPersonById(PERSON_ID, this.dbSession);
		assertThat(person.getPersonId(), is(PERSON_ID));
	}

	/**
	 * tests {@link PersonDatabaseManager#removeResourceRelation(String, String, int, PersonResourceRelationType, User, DBSession)}
	 */
	@Test
	public void testRemoveResourceRelation() {
		final List<ResourcePersonRelation> resourcePersonRelationsWithPosts = PERSON_DATABASE_MANAGER.getResourcePersonRelationsWithPosts(PERSON_ID, loginUser, GoldStandardPublication.class, this.dbSession);

		final ResourcePersonRelation firstRelation = resourcePersonRelationsWithPosts.get(0);
		PERSON_DATABASE_MANAGER.removeResourceRelation(PERSON_ID, firstRelation.getPost().getResource().getInterHash(), firstRelation.getPersonIndex(), firstRelation.getRelationType(), loginUser, this.dbSession);

		final List<ResourcePersonRelation> afterDeletion = PERSON_DATABASE_MANAGER.getResourcePersonRelationsWithPosts(PERSON_ID, loginUser, GoldStandardPublication.class, this.dbSession);

		assertThat(afterDeletion.size(), is(resourcePersonRelationsWithPosts.size() - 1));
	}

	/**
	 * tests {@link PersonDatabaseManager#getResourcePersonRelations(String, Integer, PersonResourceRelationType, DBSession)}
	 */
	@Test
	public void testGetResourcePersonRelations() {
		final List<ResourcePersonRelation> resourcePersonRelations = PERSON_DATABASE_MANAGER.getResourcePersonRelations("0b539e248a02e3edcfe591c64346c7a0", 0, PersonResourceRelationType.AUTHOR, this.dbSession);

		assertThat(resourcePersonRelations.size(), is(1));
	}

	/**
	 * tests {@link PersonDatabaseManager#updateAcademicDegree(Person, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testUpdateAcademicDegree() {
		final String scientificDegree = "Prof. Dr.";
		this.testPerson.setAcademicDegree(scientificDegree);
		PERSON_DATABASE_MANAGER.updateAcademicDegree(this.testPerson, this.dbSession);
		final Person person = PERSON_DATABASE_MANAGER.getPersonById(this.testPerson.getPersonId(), this.dbSession);
		assertThat(person.getAcademicDegree(), is(scientificDegree));
	}
	
	/**
	 * tests {@link PersonDatabaseManager#updateOrcid(Person, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testUpdateOrcid() {
		final String orcid = "1234567891011121";
		this.testPerson.setOrcid(orcid);
		PERSON_DATABASE_MANAGER.updateOrcid(this.testPerson, this.dbSession);
		final Person person = PERSON_DATABASE_MANAGER.getPersonById(this.testPerson.getPersonId(), this.dbSession);
		assertThat(person.getOrcid(), is(orcid));
	}

	/**
	 * tests {@link PersonDatabaseManager#updateCollege(Person, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testUpdateCollege() {
		final String college = "Universität Kassel";
		this.testPerson.setCollege(college);
		PERSON_DATABASE_MANAGER.updateCollege(this.testPerson, this.dbSession);
		final Person person = PERSON_DATABASE_MANAGER.getPersonById(this.testPerson.getPersonId(), this.dbSession);
		assertThat(person.getCollege(), is(college));
	}
	
	/**
	 * tests {@link PersonDatabaseManager#updateEmail(Person, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testUpdateEmail() {
		final String email = "test@test.de";
		this.testPerson.setEmail(email);
		PERSON_DATABASE_MANAGER.updateEmail(this.testPerson, this.dbSession);
		final Person person = PERSON_DATABASE_MANAGER.getPersonById(this.testPerson.getPersonId(), this.dbSession);
		assertThat(person.getEmail(), is(email));
	}
	
	/**
	 * tests {@link PersonDatabaseManager#updateHomepage(Person, org.bibsonomy.database.common.DBSession)}
	 * @throws MalformedURLException 
	 */
	@Test
	public void testUpdateHomepage(){
		final URL homepage = TestUtils.createURL("http://www.bibsonomy.org");
		this.testPerson.setHomepage(homepage);
		PERSON_DATABASE_MANAGER.updateHomepage(this.testPerson, this.dbSession);
		final Person person = PERSON_DATABASE_MANAGER.getPersonById(this.testPerson.getPersonId(), this.dbSession);
		assertThat(person.getHomepage(), is(homepage));
	}


	@Test
	public void testUpdateUserLink() {
		final String usernameToLink = loginUser.getName();
		this.testPerson.setUser(usernameToLink);
		PERSON_DATABASE_MANAGER.updateUserLink(this.testPerson, this.dbSession);

		final Person person = PERSON_DATABASE_MANAGER.getPersonById(this.testPerson.getPersonId(), this.dbSession);
		assertThat(person.getUser(), is(usernameToLink));
	}

	@Test
	public void testRemovePersonName() {
		PERSON_DATABASE_MANAGER.removePersonName(7, loginUser, this.dbSession);

		final Person personById = PERSON_DATABASE_MANAGER.getPersonById(PERSON_ID, this.dbSession);

		assertThat(personById.getNames().size(), is(1));
	}

	@Test
	public void testMergePersons() {
		List<PersonMatch> matches = PERSON_DATABASE_MANAGER.getMatches(this.dbSession);
		assertThat(matches.size(), greaterThan(0));
		
		// conflict for merge with id 4
		final Map<Integer, PersonMergeFieldConflict[]> mergeConflicts = PersonMatchUtils.getMergeConflicts(matches);
		final PersonMergeFieldConflict[] mergeConflictsToTest = mergeConflicts.get(4);

		assertThat(mergeConflictsToTest.length, is(3));

		final PersonMatch personMatch4 = getMatchById(matches, 4);
		// conflict for merge remains
		final boolean mergeResult = PERSON_DATABASE_MANAGER.mergePersons(personMatch4, loginUser, this.dbSession);
		assertThat(mergeResult, is(false)); // not mergeable

		final Map<String, String> map = new HashMap<>();
		for (final PersonMergeFieldConflict conflict : mergeConflictsToTest) {
			map.put(conflict.getFieldName(), conflict.getPerson2Value());
		}

		assertThat(PERSON_DATABASE_MANAGER.mergePersonsWithConflicts(personMatch4.getMatchID(), map, loginUser, this.dbSession), is(true));
		final Person personMergeTarget = personMatch4.getPerson1();
		// second person should be merged into the first person
		final Person personToMerge = personMatch4.getPerson2();
		try {
			PERSON_DATABASE_MANAGER.getPersonById(personToMerge.getPersonId(), this.dbSession);
			fail("object moved exception expected");
		} catch (final ObjectMovedException e) {
			assertThat(e.getNewId(), is(personMergeTarget.getPersonId()));
		}

		final List<ResourcePersonRelation> relations = PERSON_DATABASE_MANAGER.getResourcePersonRelationsWithPosts(personMergeTarget.getPersonId(), loginUser, GoldStandardPublication.class, this.dbSession);

		for (final ResourcePersonRelation relation : relations) {
			assertThat(relation.getRelationType(), is(notNullValue()));
		}

		// the person should have a new name and a the gender should be updated

		final Person updatedPerson = PERSON_DATABASE_MANAGER.getPersonById(personMergeTarget.getPersonId(), this.dbSession);
		assertThat(updatedPerson.getMainName(), is(personToMerge.getMainName()));
		assertThat(updatedPerson.getGender(), is(personToMerge.getGender()));

		// the new main name is identical but both persons got a different second name (so there must be 3 new names)
		assertThat(updatedPerson.getNames().size(), is(3));

		final PersonMatch personMatch1 = getMatchById(matches, 1);
		assertThat(PERSON_DATABASE_MANAGER.mergePersons(personMatch1, loginUser, this.dbSession), is(true));

		List<PersonMatch> newMatches = PERSON_DATABASE_MANAGER.getMatches(this.dbSession);
		// two merges are performed and one is removed because it is redundant due to transitive dependencies
		assertThat(matches.size() - 3, is(newMatches.size()));
		PersonMatch deniedMatch = newMatches.get(0);
		PERSON_DATABASE_MANAGER.denyMatch(deniedMatch, loginUser.getName(), this.dbSession);

		newMatches = PERSON_DATABASE_MANAGER.getMatchesForFilterWithUserName(deniedMatch.getPerson1().getPersonId(), loginUser.getName(), this.dbSession);
		assertThat(newMatches.size(), is(0));
		for (int i = 2; i < PersonMatch.MAX_NUMBER_OF_DENIES; i++) {
			PERSON_DATABASE_MANAGER.denyMatch(deniedMatch, "testuser" + i, this.dbSession);
		}

		// deny match after threshold is reached
		matches = PERSON_DATABASE_MANAGER.getMatches(this.dbSession);
		assertThat(matches.size(), is(1));

		deniedMatch = PERSON_DATABASE_MANAGER.getMatch(deniedMatch.getMatchID(), this.dbSession);
		PERSON_DATABASE_MANAGER.denyMatch(deniedMatch, "testuser" + PersonMatch.MAX_NUMBER_OF_DENIES, this.dbSession);
		matches = PERSON_DATABASE_MANAGER.getMatches(this.dbSession);
		assertThat(matches.size(), is(0));
	}

	private static PersonMatch getMatchById(List<PersonMatch> matches, int id) {
		for (PersonMatch match : matches) {
			if (match.getMatchID() == id) {
				return match;
			}
		}

		return null;
	}

}
