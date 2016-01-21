/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.model.logic.exception;

import java.util.List;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class ResourcePersonAlreadyAssignedException extends LogicException {
	private static final long serialVersionUID = 1526222655037790865L;
	
	private final ResourcePersonRelation existingRelation;
	
	public ResourcePersonAlreadyAssignedException(final ResourcePersonRelation existingRelation) {
		this.existingRelation = existingRelation;
	}

	public ResourcePersonRelation getExistingRelation() {
		return this.existingRelation;
	}

	public PersonName getPubPersonName() {
		List<PersonName> names = this.existingRelation.getPost().getResource().getPersonNamesByRole(this.existingRelation.getRelationType());
		if (names == null) {
			return null;
		}
		return names.get(this.existingRelation.getPersonIndex());
	}
}
