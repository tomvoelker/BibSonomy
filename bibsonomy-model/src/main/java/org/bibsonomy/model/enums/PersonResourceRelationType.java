/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.Resource;

/**
 * relations that may hold between a {@link Person} and a {@link Resource}
 *
 * @author jil
 */
public enum PersonResourceRelationType {
	/**
	 * doctor vater
	 */
	DOCTOR_VATER("Bdtv"),
	/**
	 * first reviewer of thesis
	 */
	FIRST_REVIEWER("B1st"),
	/**
	 * reviewer of a thesis
	 */
	REVIEWER("Mrev"),
	
	/**
	 * thesis advisor
	 */
	ADVISOR("Mths"),
	/**
	 * Author
	 */
	AUTHOR("Maut"),
	/** editor */
	EDITOR("Medt"),
	/**
	 * some non-specific relation influence
	 */
	OTHER("Moth");
	
	private final String relatorCode;
	private static final Map<String, PersonResourceRelationType> byRelatorCode = new HashMap<String, PersonResourceRelationType>();
	
	static {
		for (PersonResourceRelationType value : PersonResourceRelationType.values()) {
			byRelatorCode.put(value.getRelatorCode(), value);
		}
	}

	private PersonResourceRelationType(String relatorCode) {
		this.relatorCode = relatorCode;
	}
	
	/**
	 * @return the relatorCode
	 */
	public String getRelatorCode() {
		return this.relatorCode;
	}
	
	public static PersonResourceRelationType getByRelatorCode(String relatorCode) {
		final PersonResourceRelationType rVal = byRelatorCode.get(relatorCode);
		if (rVal == null) {
			throw new NoSuchElementException(relatorCode);
		}
		return rVal;
	}

}
