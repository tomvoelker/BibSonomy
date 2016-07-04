package org.bibsonomy.database.managers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.junit.Test;

/**
 * 
 * tests for {@link PersonDatabaseManager}
 *
 * @author dzo
 */
public class PersonDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static final PersonDatabaseManager PERSON_DATABASE_MANAGER = PersonDatabaseManager.getInstance();
	private static final BibTexDatabaseManager PUBLICATION_DATABASE_MANAGER = BibTexDatabaseManager.getInstance();
	
	private static final User loginUser = new User("testuser1");
	
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
	 * tests {@link PersonDatabaseManager#addResourceRelation(ResourcePersonRelation, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testAddResourceRelation() {
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		final Post<? extends BibTex> post = PUBLICATION_DATABASE_MANAGER.getPostDetails(loginUser.getName(), "b77ddd8087ad8856d77c740c8dc2864a", loginUser.getName(), Collections.singletonList(Integer.valueOf(PUBLIC_GROUP_ID)), this.dbSession);
		resourcePersonRelation.setPost(post);
		final Person person = PERSON_DATABASE_MANAGER.getPersonById("h.muller", this.dbSession);
		resourcePersonRelation.setPerson(person);
		resourcePersonRelation.setRelationType(PersonResourceRelationType.AUTHOR);
		assertTrue(PERSON_DATABASE_MANAGER.addResourceRelation(resourcePersonRelation, this.dbSession));
		
		// test inserting of a duplicate
		assertFalse(PERSON_DATABASE_MANAGER.addResourceRelation(resourcePersonRelation, this.dbSession));
	}
}
