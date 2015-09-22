/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.services.person.PersonRoleRenderer;

/**
 * @author Christian Pfeiffer
 */
public class DisambiguationPageCommand extends UserResourceViewCommand {

	private PersonRoleRenderer personRoleRenderer;
	private String requestedAction;
	private String requestedHash;
	private PersonResourceRelationType requestedRole;
	private int requestedIndex;
	
	private String requestedPersonId;
	
	private Person person;
	private PersonName personName;
	private Post<BibTex> post;
	private List<ResourcePersonRelation> personSuggestions;

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
	public Post<BibTex> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<BibTex> post) {
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
	 * @return the formAddPersonId
	 */
	public String getRequestedPersonId() {
		return this.requestedPersonId;
	}

	/**
	 * @param formAddPersonId the formAddPersonId to set
	 */
	public void setRequestedPersonId(String formPersonId) {
		this.requestedPersonId = formPersonId;
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
	public int getRequestedIndex() {
		return this.requestedIndex;
	}

	/**
	 * @param requestedIndex the requestedIndex to set
	 */
	public void setRequestedIndex(int requestedIndex) {
		this.requestedIndex = requestedIndex;
	}

	public String getRequestedAction() {
		return this.requestedAction;
	}

	public void setRequestedAction(String requestedAction) {
		this.requestedAction = requestedAction;
	}

	/**
	 * @param personSuggestions
	 */
	public void setPersonSuggestions(List<ResourcePersonRelation> personSuggestions) {
		this.personSuggestions = personSuggestions;
	}

	public List<ResourcePersonRelation> getPersonSuggestions() {
		return this.personSuggestions;
	}

	public PersonRoleRenderer getPersonRoleRenderer() {
		return this.personRoleRenderer;
	}

	public void setPersonRoleRenderer(PersonRoleRenderer personRoleRenderer) {
		this.personRoleRenderer = personRoleRenderer;
	}
}
