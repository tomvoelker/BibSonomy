package org.bibsonomy.webapp.command;

import java.util.Set;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author Christian Pfeiffer
 */
public class DisambiguationPageCommand extends UserResourceViewCommand {

	private String requestedAuthorName;
	private String requestedHash;
	private String requestedAction;
	
	private Set<Person> suggestedPersons;
	private Person person;
	private Post<? extends Resource> post;
	private PersonName authorName;

	/**
	 * @return the suggestedPersons
	 */
	public Set<Person> getSuggestedPersons() {
		return this.suggestedPersons;
	}

	/**
	 * @param suggestedPersons the suggestedPersons to set
	 */
	public void setSuggestedPersons(Set<Person> suggestedPersons) {
		this.suggestedPersons = suggestedPersons;
	}

	/**
	 * @return the requestedAction
	 */
	public String getRequestedAction() {
		return this.requestedAction;
	}

	/**
	 * @param requestedAction the requestedAction to set
	 */
	public void setRequestedAction(String requestedAction) {
		this.requestedAction = requestedAction;
	}

	/**
	 * @return the requestedHash
	 */
	public String getRequestedHash() {
		return this.requestedHash;
	}

	/**
	 * @param requestedHash the requestedHash to set
	 */
	public void setRequestedHash(String requestedHash) {
		this.requestedHash = requestedHash;
	}

	/**
	 * @return the post
	 */
	public Post<? extends Resource> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<? extends Resource> post) {
		this.post = post;
	}

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return the requestedAuthorName
	 */
	public String getRequestedAuthorName() {
		return this.requestedAuthorName;
	}

	/**
	 * @param requestedAuthorName the requestedAuthorName to set
	 */
	public void setRequestedAuthorName(String requestedAuthorName) {
		this.requestedAuthorName = requestedAuthorName;
	}

	/**
	 * @param PersonName personName
	 */
	public void setAuthorName(PersonName personName) {
		this.authorName = personName;
		
	}

	/**
	 * @return the authorName
	 */
	public PersonName getAuthorName() {
		return this.authorName;
	}
}
