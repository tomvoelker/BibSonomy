/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class ResourcePersonRelation extends ResourcePersonRelationBase {
	private Person person;
	private Post<? extends BibTex> post;

	/**
	 * @return the personName
	 */
	public Person getPerson() {
		return this.person;
	}
	/**
	 * @param person the {@link Person} to set
	 */
	public void setPerson(Person person) {
		this.person = person;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append("[");
		sb.append(getPersonRelChangeId());
		sb.append("-");
		if (person != null) {
			sb.append(person.getPersonId());
		} else {
			sb.append("null");
		}
		sb.append("-");
		sb.append(this.getRelationType());
		sb.append("-");
		if ((post != null) && (post.getResource() != null)) {
			sb.append(post.getResource().getInterHash());
		} else {
			sb.append("null");
		}
		sb.append("]");
		return sb.toString();
	}
}
