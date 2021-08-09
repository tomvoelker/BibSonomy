/**
 * BibSonomy-Model - Java- and JAXB-Model.
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

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A PersonMatch object contains the id's of two persons which might be equal and a flag if they are equal
 *
 * @author jhi
 */
public class PersonMatch implements Serializable {
	private static final long serialVersionUID = -470932185819510145L;

	public static final int MAX_NUMBER_OF_DENIES = 5;
	
	private Person person1;
	private Person person2;
	private int state; //0 open, 1 denied, 2 already merged
	private int matchID;
	private List<String> userDenies;
	private List<Post> person1Posts;
	private List<Post> person2Posts;
	
	/**
	 * @return the matchID
	 */
	public int getMatchID() {
		return this.matchID;
	}
	/**
	 * @param matchID the matchID to set
	 */
	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}
	/**
	 * @return the person1
	 */
	public Person getPerson1() {
		return this.person1;
	}
	/**
	 * @param person1 the person1 to set
	 */
	public void setPerson1(Person person1) {
		this.person1 = person1;
	}
	/**
	 * @return the person2
	 */
	public Person getPerson2() {
		return this.person2;
	}
	/**
	 * @param person2 the person2 to set
	 */
	public void setPerson2(Person person2) {
		this.person2 = person2;
	}
	/**
	 * @return the deleted
	 */
	public int getState() {
		return this.state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * @return the userDenies
	 */
	public List<String> getUserDenies() {
		return this.userDenies;
	}
	/**
	 * @param userDenies the userDenies to set
	 */
	public void setUserDenies(List<String> userDenies) {
		this.userDenies = userDenies;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PersonMatch that = (PersonMatch) o;
		return Objects.equals(person1, that.person1) && Objects.equals(person2, that.person2) ||
				Objects.equals(person1, that.person2) && Objects.equals(person2, that.person1);
	}

	@Override
	public int hashCode() {
		return Objects.hash(person1, person2);
	}

	/**
	 * @return the person1Posts
	 */
	public List<Post> getPerson1Posts() {
		return this.person1Posts;
	}

	/**
	 * @param person1Posts the person1Posts to set
	 */
	public void setPerson1Posts(List<Post> person1Posts) {
		this.person1Posts = person1Posts;
	}
	/**
	 * @return the person2Posts
	 */
	public List<Post> getPerson2Posts() {
		return this.person2Posts;
	}

	/**
	 * @param person2Posts the person2Posts to set
	 */
	public void setPerson2Posts(List<Post> person2Posts) {
		this.person2Posts = person2Posts;
	}

}
