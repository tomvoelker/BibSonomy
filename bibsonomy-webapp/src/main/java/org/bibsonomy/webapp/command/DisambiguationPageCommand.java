/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.services.person.PersonRoleRenderer;

/**
 * @author Christian Pfeiffer
 */
public class DisambiguationPageCommand extends BaseCommand {
	
	@Deprecated // Use a Java JSPTag
	private PersonRoleRenderer personRoleRenderer;
	private String requestedAction;
	private String requestedPersonId;

	private String requestedHash;
	private PersonResourceRelationType requestedRole;
	private Integer requestedIndex;

	private PersonName personName;
	private Post<? extends BibTex> post;
	private List<Post<BibTex>> suggestedPosts;
	private List<Person> personSuggestions;

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
	public Post<? extends BibTex> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<? extends BibTex> post) {
		this.post = post;
	}

	/**
	 * @return
	 */
	public PersonResourceRelationType getRequestedRole() {
		return this.requestedRole;
	}

	/**
	 * @param requestedRole the requestedRole to set
	 */
	public void setRequestedRole(PersonResourceRelationType requestedRole) {
		this.requestedRole = requestedRole;
	}

	/**
	 * @return the requestedPersonId
	 */
	public String getRequestedPersonId() {
		return this.requestedPersonId;
	}

	/**
	 * @param requestedPersonId the requestedPersonId to set
	 */
	public void setRequestedPersonId(String requestedPersonId) {
		this.requestedPersonId = requestedPersonId;
	}

	/**
	 * @return the personName
	 */
	public PersonName getPersonName() {
		return this.personName;
	}

	/**
	 * @param personName the personName to set
	 */
	public void setPersonName(PersonName personName) {
		this.personName = personName;
	}

	/**
	 * @return the requestedIndex
	 */
	public Integer getRequestedIndex() {
		return this.requestedIndex;
	}

	/**
	 * @param requestedIndex the requestedIndex to set
	 */
	public void setRequestedIndex(final Integer requestedIndex) {
		this.requestedIndex = requestedIndex;
	}

	public String getRequestedAction() {
		return this.requestedAction;
	}

	public void setRequestedAction(String requestedAction) {
		this.requestedAction = requestedAction;
	}

	/**
	 * @return the personSuggestions
	 */
	public List<Person> getPersonSuggestions() {
		return personSuggestions;
	}

	/**
	 * @param personSuggestions the personSuggestions to set
	 */
	public void setPersonSuggestions(List<Person> personSuggestions) {
		this.personSuggestions = personSuggestions;
	}

	/**
	 * @return the personRoleRenderer
	 */
	public PersonRoleRenderer getPersonRoleRenderer() {
		return personRoleRenderer;
	}

	/**
	 * @param personRoleRenderer the personRoleRenderer to set
	 */
	public void setPersonRoleRenderer(PersonRoleRenderer personRoleRenderer) {
		this.personRoleRenderer = personRoleRenderer;
	}

	/**
	 * @return the suggestedPosts
	 */
	public List<Post<BibTex>> getSuggestedPosts() {
		return this.suggestedPosts;
	}

	/**
	 * @param otherAdvisorPosts the suggestedPosts to set
	 */
	public void setSuggestedPosts(List<Post<BibTex>> otherAdvisorPosts) {
		this.suggestedPosts = otherAdvisorPosts;
	}
}
