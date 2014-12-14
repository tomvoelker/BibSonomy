package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageCommand extends UserResourceViewCommand {

	private String requestedPersonId;
	private String requestedPersonName;
	private String requestedHash;
	private String requestedAction;
	private String requestedRole;
	
	private String formSelectedName;
	private String formAcademicDegree;
	private String formFirstName;
	private String formMiddleName;
	private String formLastName;
	private String formResourceHash;
	private String formPersonId;
	private String formPersonRole;
	private String formOrcid;
	private String formThesisId;
	private String formUser;
	private int formNameId;
	private List<String> formPersonRoles;
	
	private String formAction;
	
	private Person person;
	private Post<? extends Resource> post;
	
	private List<Post<BibTex>> thesis;
	private List<Post<BibTex>> advisedThesis;
	private List<Post<BibTex>> allPosts;
	

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
	 * @return the formGraduation
	 */
	public String getFormAcademicDegree() {
		return this.formAcademicDegree;
	}

	/**
	 * @param formAcademicDegree the formAcademicDegree to set
	 */
	public void setFormAcademicDegree(String formAcademicDegree) {
		this.formAcademicDegree = formAcademicDegree;
	}
	
	/**
	 * @return the formFirstName
	 */
	public String getFormFirstName() {
		return this.formFirstName;
	}

	/**
	 * @param formFirstName the formFirstName to set
	 */
	public void setFormFirstName(String formFirstName) {
		this.formFirstName = formFirstName;
	}

	/**
	 * @return the formLastName
	 */
	public String getFormLastName() {
		return this.formLastName;
	}

	/**
	 * @param formLastName the formLastName to set
	 */
	public void setFormLastName(String formLastName) {
		this.formLastName = formLastName;
	}

	/**
	 * @return String
	 */
	public String getRequestedPersonId() {
		return this.requestedPersonId;
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
	 * @param personId String
	 */
	public void setRequestedPersonId(String personId) {
		this.requestedPersonId = personId;
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
	 * @return the formPersonRole
	 */
	public List<String> getFormPersonRoles() {
		return this.formPersonRoles;
	}

	/**
	 * @param formPersonRoles 
	 * @param formPersonRole the formPersonRole to set
	 */
	public void setformPersonRoles(List<String> formPersonRoles) {
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
	 * @return the requestedRole
	 */
	public String getRequestedRole() {
		return this.requestedRole;
	}

	/**
	 * @param requestedRole the requestedRole to set
	 */
	public void setRequestedRole(String requestedRole) {
		this.requestedRole = requestedRole;
	}

	/**
	 * @return the formMiddleName
	 */
	public String getFormMiddleName() {
		return this.formMiddleName;
	}

	/**
	 * @param formMiddleName the formMiddleName to set
	 */
	public void setFormMiddleName(String formMiddleName) {
		this.formMiddleName = formMiddleName;
	}

	/**
	 * @return the requestedPersonName
	 */
	public String getRequestedPersonName() {
		return this.requestedPersonName;
	}

	/**
	 * @param requestedPersonName the requestedPersonName to set
	 */
	public void setRequestedPersonName(String requestedPersonName) {
		this.requestedPersonName = requestedPersonName;
	}

	/**
	 * @return the thesis
	 */
	public List<Post<BibTex>> getThesis() {
		return this.thesis;
	}

	/**
	 * @param thesis the thesis to set
	 */
	public void setThesis(List<Post<BibTex>> thesis) {
		this.thesis = thesis;
	}

	/**
	 * @return the advisedThesis
	 */
	public List<Post<BibTex>> getAdvisedThesis() {
		return this.advisedThesis;
	}

	/**
	 * @param advisedThesis the advisedThesis to set
	 */
	public void setAdvisedThesis(List<Post<BibTex>> advisedThesis) {
		this.advisedThesis = advisedThesis;
	}

	/**
	 * @return the allPosts
	 */
	public List<Post<BibTex>> getAllPosts() {
		return this.allPosts;
	}

	/**
	 * @param allPosts the allPosts to set
	 */
	public void setAllPosts(List<Post<BibTex>> allPosts) {
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
	public String getFormResourceHash() {
		return this.formResourceHash;
	}

	/**
	 * @return the formPersonId
	 */
	public String getFormPersonId() {
		return this.formPersonId;
	}

	/**
	 * @param formPersonId the formPersonId to set
	 */
	public void setFormPersonId(String formPersonId) {
		this.formPersonId = formPersonId;
	}

	/**
	 * @return the formPersonRole
	 */
	public String getFormPersonRole() {
		return this.formPersonRole;
	}

	/**
	 * @param formPersonRole the formPersonRole to set
	 */
	public void setFormPersonRole(String formPersonRole) {
		this.formPersonRole = formPersonRole;
	}

	/**
	 * @return the formThesisId
	 */
	public String getFormThesisId() {
		return this.formThesisId;
	}

	/**
	 * @param formThesisId the formThesisId to set
	 */
	public void setFormThesisId(String formThesisId) {
		this.formThesisId = formThesisId;
	}

	/**
	 * @return the formUser
	 */
	public String getFormUser() {
		return this.formUser;
	}

	/**
	 * @param formUser the formUser to set
	 */
	public void setFormUser(String formUser) {
		this.formUser = formUser;
	}

	/**
	 * @param formResourceHash the formResourceHash to set
	 */
	public void setFormResourceHash(String formResourceHash) {
		this.formResourceHash = formResourceHash;
	}

	/**
	 * @param formPersonRoles the formPersonRoles to set
	 */
	public void setFormPersonRoles(List<String> formPersonRoles) {
		this.formPersonRoles = formPersonRoles;
	}

	/**
	 * @return int
	 */
	public int getFormNameId() {
		return this.formNameId;
	}

	/**
	 * @param nameId the nameId to set
	 */
	public void setFormNameId(int nameId) {
		this.formNameId = nameId;
	}

	/**
	 * @return the formOrcid
	 */
	public String getFormOrcid() {
		return this.formOrcid;
	}

	/**
	 * @param formOrcid the formOrcid to set
	 */
	public void setFormOrcid(String formOrcid) {
		this.formOrcid = formOrcid;
	}
}
