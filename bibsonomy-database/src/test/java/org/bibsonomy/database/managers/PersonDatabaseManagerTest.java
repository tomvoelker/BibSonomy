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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.bibsonomy.testutil.TestUtils;
import org.bibsonomy.util.ValidationUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * tests for {@link PersonDatabaseManager}
 *
 * @author dzo
 * @author mho
 */
public class PersonDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static final PersonDatabaseManager PERSON_DATABASE_MANAGER = PersonDatabaseManager.getInstance();
	private static final BibTexDatabaseManager PUBLICATION_DATABASE_MANAGER = BibTexDatabaseManager.getInstance();
	private static final GoldStandardPublicationDatabaseManager COMMUNITY_DATABASE_MANAGER = GoldStandardPublicationDatabaseManager.getInstance();

	private static final User loginUser = new User("testuser1");
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
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		final String intraHash = "b77ddd8087ad8856d77c740c8dc2864a";
		final Post<? extends BibTex> post = PUBLICATION_DATABASE_MANAGER.getPostDetails(loginUser.getName(), intraHash, loginUser.getName(), Collections.singletonList(Integer.valueOf(PUBLIC_GROUP_ID)), this.dbSession);
		resourcePersonRelation.setPost(post);
		final Person person = PERSON_DATABASE_MANAGER.getPersonById(PERSON_ID, this.dbSession);
		resourcePersonRelation.setPerson(person);
		resourcePersonRelation.setRelationType(PersonResourceRelationType.AUTHOR);
		assertTrue(PERSON_DATABASE_MANAGER.addResourceRelation(resourcePersonRelation, loginUser, this.dbSession));

		// check if the community post was created
		final Post<GoldStandardPublication> communityPost = COMMUNITY_DATABASE_MANAGER.getPostDetails(loginUser.getName(), post.getResource().getInterHash(), "", Collections.emptyList(), this.dbSession);
		// inserting a publication resource relation should have created a new community post
		assertNotNull(communityPost);

		// test inserting of a duplicate
		assertFalse(PERSON_DATABASE_MANAGER.addResourceRelation(resourcePersonRelation, loginUser, this.dbSession));
	}

	/**
	 * tests {@link PersonDatabaseManager#removeResourceRelation(String, int, PersonResourceRelationType, User, DBSession)}
	 */
	@Test
	public void testRemoveResourceRelation() {
		final List<ResourcePersonRelation> resourcePersonRelationsWithPosts = PERSON_DATABASE_MANAGER.getResourcePersonRelationsWithPosts(PERSON_ID, loginUser, GoldStandardPublication.class, this.dbSession);

		final ResourcePersonRelation firstRelation = resourcePersonRelationsWithPosts.get(0);
		PERSON_DATABASE_MANAGER.removeResourceRelation(firstRelation.getPost().getResource().getInterHash(), firstRelation.getPersonIndex(), firstRelation.getRelationType(), loginUser, this.dbSession);

		final List<ResourcePersonRelation> afterDeletion = PERSON_DATABASE_MANAGER.getResourcePersonRelationsWithPosts(PERSON_ID, loginUser, GoldStandardPublication.class, this.dbSession);

		assertThat(afterDeletion.size(), is(resourcePersonRelationsWithPosts.size() - 1));
	}

	/**
	 * tests {@link PersonDatabaseManager#removeResourceRelation(int, User, DBSession)}
	 */
	@Test
	public void testRemoveResourceRelation() {
		PERSON_DATABASE_MANAGER.removeResourceRelation(30, loginUser, this.dbSession);
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
		assertEquals(scientificDegree, person.getAcademicDegree());
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
		assertEquals(orcid, person.getOrcid());
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
		assertEquals(college, person.getCollege());
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
		assertEquals(email, person.getEmail());
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
		assertEquals(homepage, person.getHomepage());
	}

	@Test
	public void testGetPublicationPersonRelations() {
		final List<ResourcePersonRelation> resourcePersonRelationsWithPosts = PERSON_DATABASE_MANAGER.getResourcePersonRelationsWithPosts(PERSON_ID, loginUser, GoldStandardPublication.class, this.dbSession);

		assertEquals(1, resourcePersonRelationsWithPosts.size());

		final ResourcePersonRelation resourcePersonRelation = resourcePersonRelationsWithPosts.get(0);
		assertEquals("Wurst aufs Brot", resourcePersonRelation.getPost().getResource().getTitle());
	}

	@Test
	public void testRemovePersonName() {
		PERSON_DATABASE_MANAGER.removePersonName(7, loginUser, this.dbSession);

		final Person personById = PERSON_DATABASE_MANAGER.getPersonById(PERSON_ID, this.dbSession);

		assertThat(personById.getNames().size(), is(1));
	}

	@Test
	public void testSimilarPerson(){
		List<PersonMatch> matches = this.PERSON_DATABASE_MANAGER.getMatches(this.dbSession);
		assertTrue(matches.size() > 0);
		
		// conflict for merge with id 4
		Map<Integer, PersonMergeFieldConflict[]> mergeConflicts = PersonMatch.getMergeConflicts(matches);
		assertTrue(mergeConflicts.get(4).length >0);
		for(PersonMatch match: matches) {
			if (match.getMatchID() == 4) {
				// conflict for merge remains
				assertTrue(!this.PERSON_DATABASE_MANAGER.mergeSimilarPersons(match, loginUser, this.dbSession));
				Map<String, String> map = new HashMap<>();
				String newPage = null;
				for(PersonMergeFieldConflict conflict : mergeConflicts.get(4)) {
					map.put(conflict.getFieldName(), conflict.getPerson2Value());
					if (conflict.getFieldName().equals("homepage")) {
						newPage = conflict.getPerson2Value();
					}
				}
				assertTrue(this.PERSON_DATABASE_MANAGER.conflictMerge(match.getMatchID(), map, loginUser, this.dbSession));
				Person updatedPerson = this.PERSON_DATABASE_MANAGER.getPersonById(match.getPerson1().getPersonId(), this.dbSession);
				assertTrue(ValidationUtils.equalsWithNull(updatedPerson.getHomepage(), newPage));
			} else if (match.getMatchID() == 1) {
				assertTrue(this.PERSON_DATABASE_MANAGER.mergeSimilarPersons(match, loginUser, this.dbSession));
			}
		}
		List<PersonMatch> newMatches = this.PERSON_DATABASE_MANAGER.getMatches(this.dbSession);
		//two merges are performed and one is removed because it is redundant due to transitive dependencies 
		assertTrue(matches.size() == newMatches.size() + 3);
		this.PERSON_DATABASE_MANAGER.denyMatch(newMatches.get(0), loginUser.getName(), this.dbSession);
		PersonMatch deniedMatch = newMatches.get(0);
		newMatches = this.PERSON_DATABASE_MANAGER.getMatchesForFilterWithUserName(deniedMatch.getPerson1().getPersonId(), loginUser.getName(), this.dbSession);
		assertTrue(newMatches.size() == 0);
		for (int i = 2; i < PersonMatch.denieThreshold; i++){
			this.PERSON_DATABASE_MANAGER.denyMatch(deniedMatch, "testuser" + i, this.dbSession);
		}
		//deny match after threshold is reached
		matches = this.PERSON_DATABASE_MANAGER.getMatches(this.dbSession);
		assertTrue(matches.size() >0);
		deniedMatch = this.PERSON_DATABASE_MANAGER.getMatch(deniedMatch.getMatchID(), this.dbSession);
		this.PERSON_DATABASE_MANAGER.denyMatch(deniedMatch, "testuser" + PersonMatch.denieThreshold, this.dbSession);
		matches = this.PERSON_DATABASE_MANAGER.getMatches(this.dbSession);
		assertTrue(matches.size() == 0);
	}
	
}
