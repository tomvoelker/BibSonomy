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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonMergeFieldConflict;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.exception.LogicException;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageCommand extends BaseCommand {

	/** Used during the generation of new names */
	private PersonName newName;
	
	/** describes the relation between a person and a resource */
	private ResourcePersonRelation resourcePersonRelation;
	
	/** personId of the person requested */
	private String requestedPersonId;
	
	private String formSelectedName;

	
	@Deprecated // TODO: bind person directly
	private String formResourceHash;
	@Deprecated // TODO: bind person directly
	private String formPersonId;
	@Deprecated // TODO: bind person directly
	private PersonResourceRelationType formPersonRole;

	@Deprecated // TODO: bind person dier rectly
	private String formThesisId;
	@Deprecated // TODO: bind person directly
	private String formPersonNameId;
	@Deprecated // TODO: bind person directly
	private List<String> formPersonRoles;
	@Deprecated // TODO: bind person directly
	private String formRequestType;
	@Deprecated // TODO: bind person directly
	private String formResourcePersonRelationId;
	@Deprecated // TODO: bind person directly
	private String formInterHash;
	@Deprecated // TODO: bind person directly
	private String formIntraHash;
	@Deprecated // TODO: bind person directly
	private boolean formThatsMe;
	@Deprecated // TODO: bind person directly
	private int formPersonIndex = -1;
	
	
	private String formAction;
	
	private Person person;
	private Post<? extends Resource> post;
	
	private List<Post<?>> thesis;
	private List<Post<?>> advisedThesis;
	private List<Post<?>> allPosts;
	
	@Deprecated // FIXME: access enum directly
	private List<PersonResourceRelationType> availableRoles = new ArrayList<>();
	
	private String responseString;
	private List<Post<?>> otherPubs;
	private List<Post<?>> otherAdvisedPubs;
	
	private List<Post<BibTex>> similarAuthorPubs;
	
	private List<PersonMatch> personMatchList;
	
	private Map<Integer, List<PersonMergeFieldConflict>> mergeConflicts;
	
	private String okHintKey;
	
	@Deprecated // FIXME: remove use errors handling build into spring
	private final Collection<LogicException> logicExceptions = new ArrayList<>();

	private PersonUpdateOperation updateOperation;
	
	/**
	 * @return the updateOperation
	 */
	public PersonUpdateOperation getUpdateOperation() {
		return this.updateOperation;
	}

	/**
	 * @param updateOperation the updateOperation to set
	 */
	public void setUpdateOperation(PersonUpdateOperation updateOperation) {
		this.updateOperation = updateOperation;
	}

	/**
	 * @return the logicExceptions
	 */
	@Deprecated
	public Collection<LogicException> getLogicExceptions() {
		return this.logicExceptions;
	}
	
	/**
	 * @return the formSelectedName
	 */
	public String getFormSelectedName() {
		return this.formSelectedName;
	}

	/**
	 * @param formSelectedName the formSelectedName to set
	 */
	public void setFormSelectedName(String formSelectedName) {
		this.formSelectedName = formSelectedName;
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
	 * @return String
	 */
	public String getRequestedPersonId() {
		return this.requestedPersonId;
	}
	
	/**
	 * @param personId String
	 */
	public void setRequestedPersonId(String personId) {
		this.requestedPersonId = personId;
	}

	/**
	 * @return the formPersonRole
	 */
	public List<String> getFormPersonRoles() {
		return this.formPersonRoles;
	}
	
	/**
	 * @param formPersonRoles the formPersonRoles to set
	 */
	public void setFormPersonRoles(List<String> formPersonRoles) {
		this.formPersonRoles = formPersonRoles;
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
	 * @return the thesis
	 */
	public List<Post<?>> getThesis() {
		return this.thesis;
	}

	/**
	 * @param thesis the thesis to set
	 */
	public void setThesis(List<Post<?>> thesis) {
		this.thesis = thesis;
	}

	/**
	 * @return the advisedThesis
	 */
	public List<Post<?>> getAdvisedThesis() {
		return this.advisedThesis;
	}

	/**
	 * @param advisedThesis the advisedThesis to set
	 */
	public void setAdvisedThesis(List<Post<?>> advisedThesis) {
		this.advisedThesis = advisedThesis;
	}

	/**
	 * @return the allPosts
	 */
	public List<Post<?>> getAllPosts() {
		return this.allPosts;
	}

	/**
	 * @param allPosts the allPosts to set
	 */
	public void setAllPosts(List<Post<?>> allPosts) {
		this.allPosts = allPosts;
	}

	/**
	 * @return the formAction
	 */
	public String getFormAction() {
		return this.formAction;
	}

	/**
	 * @param formAction the formAction to set
	 */
	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	/**
	 * @return String
	 */
	@Deprecated // TODO: bind person directly
	public String getFormResourceHash() {
		return this.formResourceHash;
	}

	/**
	 * @return the formPersonId
	 */
	@Deprecated // TODO: bind person directly
	public String getFormPersonId() {
		return this.formPersonId;
	}

	/**
	 * @param formPersonId the formPersonId to set
	 */
	@Deprecated // TODO: bind person directly
	public void setFormPersonId(String formPersonId) {
		this.formPersonId = formPersonId;
	}

	/**
	 * @return the formPersonRole
	 */
	@Deprecated // TODO: bind person directly
	public PersonResourceRelationType getFormPersonRole() {
		return this.formPersonRole;
	}

	/**
	 * @param formPersonRole the formPersonRole to set
	 */
	@Deprecated // TODO: bind person directly
	public void setFormPersonRole(PersonResourceRelationType formPersonRole) {
		this.formPersonRole = formPersonRole;
	}

	/**
	 * @return the formThesisId
	 */
	@Deprecated // TODO: bind person directly
	public String getFormThesisId() {
		return this.formThesisId;
	}

	/**
	 * @param formThesisId the formThesisId to set
	 */
	@Deprecated // TODO: bind person directly
	public void setFormThesisId(String formThesisId) {
		this.formThesisId = formThesisId;
	}

	/**
	 * @param formResourceHash the formResourceHash to set
	 */
	@Deprecated // TODO: bind person directly
	public void setFormResourceHash(String formResourceHash) {
		this.formResourceHash = formResourceHash;
	}

	/**
	 * @return String
	 */
	@Deprecated // TODO: bind person directly
	public String getFormPersonNameId() {
		return this.formPersonNameId;
	}

	/**
	 * @param personNameId2 the nameId to set
	 */
	@Deprecated // TODO: bind person directly
	public void setFormPersonNameId(String personNameId2) {
		this.formPersonNameId = personNameId2;
	}

	/**
	 * @param jsonString
	 */
	public void setResponseString(String jsonString) {
		this.responseString = jsonString;
	}

	/**
	 * @return the responseString
	 */
	public String getResponseString() {
		return this.responseString;
	}

	/**
	 * @return the formRequestType
	 */
	public String getFormRequestType() {
		return this.formRequestType;
	}

	/**
	 * @param formRequestType the formRequestType to set
	 */
	public void setFormRequestType(String formRequestType) {
		this.formRequestType = formRequestType;
	}

	/**
	 * @return String
	 */
	public String getFormResourcePersonRelationId() {
		return this.formResourcePersonRelationId;
	}

	/**
	 * @param formResourcePersonRelationId the formResourcePersonRelationId to set
	 */
	public void setFormResourcePersonRelationId(String formResourcePersonRelationId) {
		this.formResourcePersonRelationId = formResourcePersonRelationId;
	}

	/**
	 * @return the formInterHash
	 */
	public String getFormInterHash() {
		return this.formInterHash;
	}

	/**
	 * @param formInterHash the formInterHash to set
	 */
	public void setFormInterHash(String formInterHash) {
		this.formInterHash = formInterHash;
	}

	/**
	 * @return the formIntraHash
	 */
	public String getFormIntraHash() {
		return this.formIntraHash;
	}

	/**
	 * @param formIntraHash the formIntraHash to set
	 */
	public void setFormIntraHash(String formIntraHash) {
		this.formIntraHash = formIntraHash;
	}

	/**
	 * @return the availableRoles
	 */
	@Deprecated // FIXME: access enum directly
	public List<PersonResourceRelationType> getAvailableRoles() {
		return this.availableRoles;
	}

	/**
	 * @param availableRoles the availableRoles to set
	 */
	@Deprecated // FIXME: access enum directly
	public void setAvailableRoles(List<PersonResourceRelationType> availableRoles) {
		this.availableRoles = availableRoles;
	}

	/**
	 * @return true if the current login user is associated to this person
	 */
	@Deprecated // TODO: bind person directly
	public boolean isFormThatsMe() {
		return this.formThatsMe;
	}

	/**
	 * @param formThatsMe if the current login user is associated to this person
	 */
	@Deprecated // TODO: bind person directly
	public void setFormThatsMe(boolean formThatsMe) {
		this.formThatsMe = formThatsMe;
	}

	/**
	 * @return the formAuthorIndex
	 */
	@Deprecated // TODO: bind person directly
	public int getFormPersonIndex() {
		return this.formPersonIndex;
	}

	/**
	 * @param formAuthorIndex the formAuthorIndex to set
	 */
	@Deprecated // TODO: bind person directly
	public void setFormPersonIndex(int formAuthorIndex) {
		this.formPersonIndex = formAuthorIndex;
	}

	/**
	 * @param otherAuthorPosts
	 */
	public void setOtherPubs(List<Post<?>> otherAuthorPosts) {
		this.otherPubs = otherAuthorPosts;
	}

	public List<Post<?>> getOtherPubs() {
		return this.otherPubs;
	}

	/**
	 * @param otherAdvisorPosts
	 */
	public void setOtherAdvisedPubs(List<Post<?>> otherAdvisedPubs) {
		this.otherAdvisedPubs = otherAdvisedPubs;
	}

	public List<Post<?>> getOtherAdvisedPubs() {
		return this.otherAdvisedPubs;
	}

	/**
	 * @param actionKeyCreateAndLinkPerson
	 */
	public void setOkHintKey(String okHintKey) {
		this.okHintKey = okHintKey;
	}
	
	/**
	 * @return the okHintKey
	 */
	public String getOkHintKey() {
		return this.okHintKey;
	}

	/**
	 * @return the newName
	 */
	public PersonName getNewName() {
		return this.newName;
	}

	/**
	 * @param newName the newName to set
	 */
	public void setNewName(PersonName newName) {
		this.newName = newName;
	}

	/**
	 * @return the resourcePersonRelation
	 */
	public ResourcePersonRelation getResourcePersonRelation() {
		return this.resourcePersonRelation;
	}

	/**
	 * @param resourcePersonRelation the resourcePersonRelation to set
	 */
	public void setResourcePersonRelation(ResourcePersonRelation resourcePersonRelation) {
		this.resourcePersonRelation = resourcePersonRelation;
	}

	/**
	 * @return the similarAuthorPubs
	 */
	public List<Post<BibTex>> getSimilarAuthorPubs() {
		return this.similarAuthorPubs;
	}

	/**
	 * @param similarAuthorPubs the similarAuthorPubs to set
	 */
	public void setSimilarAuthorPubs(List<Post<BibTex>> similarAuthorPubs) {
		this.similarAuthorPubs = similarAuthorPubs;
	}

	/**
	 * @return the personMatchList
	 */
	public List<PersonMatch> getPersonMatchList() {
		return this.personMatchList;
	}

	/**
	 * @param personMatchList the personMatchList to set
	 */
	public void setPersonMatchList(List<PersonMatch> personMatchList) {
		this.personMatchList = personMatchList;
	}

	/**
	 * @return the mergeConflicts
	 */
	public Map<Integer, List<PersonMergeFieldConflict>> getMergeConflicts() {
		return this.mergeConflicts;
	}

	/**
	 * @param mergeConflicts the mergeConflicts to set
	 */
	public void setMergeConflicts(Map<Integer, List<PersonMergeFieldConflict>> mergeConflicts) {
		this.mergeConflicts = mergeConflicts;
	}

}
