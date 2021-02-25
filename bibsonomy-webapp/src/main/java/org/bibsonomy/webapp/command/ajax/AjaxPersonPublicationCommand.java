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
package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author wla
 */
public class AjaxPersonPublicationCommand extends AjaxCommand<String> {

	private Person person;
	private int page;
	private int size;

	private int personPostsStyleSettings;
	private int personPostsPerPage;

	private List<Post<BibTex>> myownPosts;
	private List<ResourcePersonRelation> otherPubs;

	/** personId of the person requested */
	private String requestedPersonId;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPersonPostsStyleSettings() {
		return personPostsStyleSettings;
	}

	public void setPersonPostsStyleSettings(int personPostsStyleSettings) {
		this.personPostsStyleSettings = personPostsStyleSettings;
	}

	public String getRequestedPersonId() {
		return requestedPersonId;
	}

	public void setRequestedPersonId(String requestedPersonId) {
		this.requestedPersonId = requestedPersonId;
	}

	public List<Post<BibTex>> getMyownPosts() {
		return myownPosts;
	}

	public void setMyownPosts(List<Post<BibTex>> myownPosts) {
		this.myownPosts = myownPosts;
	}

	public List<ResourcePersonRelation> getOtherPubs() {
		return otherPubs;
	}

	public void setOtherPubs(List<ResourcePersonRelation> otherPubs) {
		this.otherPubs = otherPubs;
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
}
