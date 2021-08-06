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
package org.bibsonomy.webapp.command.person.relation;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author tok
 */
public class PersonResourceRelationCommand extends AjaxCommand<Void> {

	private Person person;

	private PersonResourceRelationType type;

	private String interhash;

	private int index = -1;

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person the personId to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return the type
	 */
	public PersonResourceRelationType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PersonResourceRelationType type) {
		this.type = type;
	}

	/**
	 * @return the interhash
	 */
	public String getInterhash() {
		return interhash;
	}

	/**
	 * @param interhash the interHash to set
	 */
	public void setInterhash(String interhash) {
		this.interhash = interhash;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
