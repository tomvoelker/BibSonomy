/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonMergeFieldConflict;
import org.bibsonomy.model.PhDRecommendation;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.extra.SearchFilterElement;
import org.bibsonomy.model.logic.exception.LogicException;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageCommand extends BaseCommand {

	/** describes the relation between a person and a resource */
	private ResourcePersonRelation resourcePersonRelation;
	
	/** personId of the person requested */
	private String requestedPersonId;

	/** if true only persons of the configured cris system are displayed */
	private boolean limitResultsToCRISCollege;

	private List<Project> projects;
	private boolean showProjects;
	
	private Person person;
	private Post<? extends Resource> post;

	private int personPostsPerPage;
	private PersonPostsStyle personPostsStyle;
	private String personPostsLayout;
	private List<Post<BibTex>> myownPosts;
	private Map<String, String> myownPostsRendered;
	private String alternativeNames;

	private List<SearchFilterElement> entrytypeFilters;

	private List<ResourcePersonRelation> thesis;
	private List<ResourcePersonRelation> advisedThesis;
	private List<ResourcePersonRelation> allPosts;
	
	private String responseString;
	private List<ResourcePersonRelation> otherPubs;
	private List<ResourcePersonRelation> otherAdvisedPubs;
	
	private List<ResourcePersonRelation> similarAuthorPubs;
	
	private List<PersonMatch> personMatchList;
	
	private Map<Integer, PersonMergeFieldConflict[]> mergeConflicts;
	private List<PhDRecommendation> phdAdvisorRecForPerson;

	private Integer start;
	private Integer end;

	@Deprecated // TODO use posts per page as a var or use the listcommand class to get pagination for free
	private Integer prevStart;

	@Deprecated // FIXME: remove use errors handling build into spring
	private final Collection<LogicException> logicExceptions = new ArrayList<>();

	private Map<String, String> renderedPosts;

	public Map<String, String> getRenderedPosts() {
		return renderedPosts;
	}

	public void setRenderedPosts(Map<String, String> renderedPosts) {
		this.renderedPosts = renderedPosts;
	}



	/**
	 * @return the logicExceptions
	 */
	@Deprecated
	public Collection<LogicException> getLogicExceptions() {
		return this.logicExceptions;
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
	 * @return personPostsStyle the person posts style setting
	 */
	public PersonPostsStyle getPersonPostsStyle() {
		return personPostsStyle;
	}

	/**
	 * @param personPostsStyle the person posts style setting
	 */
	public void setPersonPostsStyle(PersonPostsStyle personPostsStyle) {
		this.personPostsStyle = personPostsStyle;
	}

	/**
	 * @return personPostsLayout the selected CSL-layout for person posts
	 */
	public String getPersonPostsLayout() {
		return personPostsLayout;
	}

	/**
	 * @param personPostsLayout set the CSL-layout for person posts
	 */
	public void setPersonPostsLayout(String personPostsLayout) {
		this.personPostsLayout = personPostsLayout;
	}

	/**
	 * @return the list of 'myown'-tagged posts by linked user
	 */
	public List<Post<BibTex>> getMyownPosts() {
		return myownPosts;
	}

	/**
	 * @param myownPosts the list of 'myown'-tagged posts by linked user
	 */
	public void setMyownPosts(List<Post<BibTex>> myownPosts) {
		this.myownPosts = myownPosts;
	}

	/**
	 * @return the thesis
	 */
	public List<ResourcePersonRelation> getThesis() {
		return this.thesis;
	}

	/**
	 * @param thesis the thesis to set
	 */
	public void setThesis(List<ResourcePersonRelation> thesis) {
		this.thesis = thesis;
	}

	/**
	 * @return the advisedThesis
	 */
	public List<ResourcePersonRelation> getAdvisedThesis() {
		return this.advisedThesis;
	}

	/**
	 * @param advisedThesis the advisedThesis to set
	 */
	public void setAdvisedThesis(List<ResourcePersonRelation> advisedThesis) {
		this.advisedThesis = advisedThesis;
	}

	/**
	 * @return the allPosts
	 */
	public List<ResourcePersonRelation> getAllPosts() {
		return this.allPosts;
	}

	/**
	 * @param allPosts the allPosts to set
	 */
	public void setAllPosts(List<ResourcePersonRelation> allPosts) {
		this.allPosts = allPosts;
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
	 * @param otherAuthorPosts
	 */
	public void setOtherPubs(List<ResourcePersonRelation> otherAuthorPosts) {
		this.otherPubs = otherAuthorPosts;
	}

	public List<ResourcePersonRelation> getOtherPubs() {
		return this.otherPubs;
	}

	/**
	 * @param otherAdvisedPubs
	 */
	public void setOtherAdvisedPubs(List<ResourcePersonRelation> otherAdvisedPubs) {
		this.otherAdvisedPubs = otherAdvisedPubs;
	}

	public List<ResourcePersonRelation> getOtherAdvisedPubs() {
		return this.otherAdvisedPubs;
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
	public List<ResourcePersonRelation> getSimilarAuthorPubs() {
		return this.similarAuthorPubs;
	}

	/**
	 * @param similarAuthorPubs the similarAuthorPubs to set
	 */
	public void setSimilarAuthorPubs(List<ResourcePersonRelation> similarAuthorPubs) {
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
	public Map<Integer, PersonMergeFieldConflict[]> getMergeConflicts() {
		return this.mergeConflicts;
	}

	/**
	 * @param mergeConflicts the mergeConflicts to set
	 */
	public void setMergeConflicts(Map<Integer, PersonMergeFieldConflict[]> mergeConflicts) {
		this.mergeConflicts = mergeConflicts;
	}

	/**
	 * @return the phdAdvisorRecForPerson
	 */
	public List<PhDRecommendation> getPhdAdvisorRecForPerson() {
		return this.phdAdvisorRecForPerson;
	}

	/**
	 * @param phdAdvisorRecForPerson the phdAdvisorRecForPerson to set
	 */
	public void setPhdAdvisorRecForPerson(List<PhDRecommendation> phdAdvisorRecForPerson) {
		this.phdAdvisorRecForPerson = phdAdvisorRecForPerson;
	}

	/**
	 * @return
	 */
	public boolean isShowProjects() {
		return showProjects;
	}

	/**
	 * @param showProjects
	 */
	public void setShowProjects(boolean showProjects) {
		this.showProjects = showProjects;
	}

	/**
	 * @return
	 */
	public List<Project> getProjects() {
		return projects;
	}

	/**
	 * @param projects
	 */
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	/**
	 * @return the limitResultsToCRISCollege
	 */
	public boolean isLimitResultsToCRISCollege() {
		return limitResultsToCRISCollege;
	}

	/**
	 * @param limitResultsToCRISCollege the limitResultsToCRISCollege to set
	 */
	public void setLimitResultsToCRISCollege(boolean limitResultsToCRISCollege) {
		this.limitResultsToCRISCollege = limitResultsToCRISCollege;
	}

	/**
	 * Number of publications displayed per page on the person page
	 * @return
	 */
	public int getPersonPostsPerPage() {
		return personPostsPerPage;
	}

	public void setPersonPostsPerPage(int personPostsPerPage) {
		this.personPostsPerPage = personPostsPerPage;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public Integer getPrevStart() {
		return prevStart;
	}

	public void setPrevStart(Integer prevStart) {
		this.prevStart = prevStart;
	}

	public Map<String, String> getMyownPostsRendered() {
		return myownPostsRendered;
	}

	public void setMyownPostsRendered(Map<String, String> myownPostsRendered) {
		this.myownPostsRendered = myownPostsRendered;
	}

	public String getAlternativeNames() {
		return alternativeNames;
	}

	public void setAlternativeNames(String alternativeNames) {
		this.alternativeNames = alternativeNames;
	}

	public List<SearchFilterElement> getEntrytypeFilters() {
		return entrytypeFilters;
	}

	public void setEntrytypeFilters(List<SearchFilterElement> entrytypeFilters) {
		this.entrytypeFilters = entrytypeFilters;
	}
}
