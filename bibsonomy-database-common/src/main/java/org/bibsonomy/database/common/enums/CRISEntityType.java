/**
 * BibSonomy-Database-Common - Helper classes for database interaction
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
package org.bibsonomy.database.common.enums;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * CRIS entity type
 *
 * @author dzo
 */
public enum CRISEntityType {

	PUBLICATION(1),

	PERSON(2),

	PROJECT(3),

	GROUP(4);

	private final int id;

	CRISEntityType(final int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	private static final Map<Class<? extends Linkable>, CRISEntityType> CLASS_CRIS_ENTITY_TYPE_MAP = new HashMap<>();

	static {
		CLASS_CRIS_ENTITY_TYPE_MAP.put(Project.class, PROJECT);
		CLASS_CRIS_ENTITY_TYPE_MAP.put(Post.class, PUBLICATION);
		CLASS_CRIS_ENTITY_TYPE_MAP.put(Person.class, PERSON);
		CLASS_CRIS_ENTITY_TYPE_MAP.put(Group.class, GROUP);
	}

	/**
	 * returns the cris entry type based on the class of the linkable
	 * @param clazz
	 * @return
	 */
	public static CRISEntityType getCRISEntityType(final Class<? extends Linkable> clazz) {
		if (CLASS_CRIS_ENTITY_TYPE_MAP.containsKey(clazz)) {
			return CLASS_CRIS_ENTITY_TYPE_MAP.get(clazz);
		}

		// second try
		// FIXME: only for the lacy loading classes in the person mapping :(
		for (Map.Entry<Class<? extends Linkable>, CRISEntityType> entry : CLASS_CRIS_ENTITY_TYPE_MAP.entrySet()) {
			if (entry.getKey().isAssignableFrom(clazz)) {
				return entry.getValue();
			}
		}

		throw new IllegalArgumentException("cris type not supported");
	}
}
