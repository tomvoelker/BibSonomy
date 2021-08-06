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
package org.bibsonomy.model.logic;

import java.util.Set;

import org.bibsonomy.model.enums.GoldStandardRelation;


/**
 * @author dzo
 */
public interface GoldStandardPostLogicInterface extends PostLogicInterface {
	
	/**
	 * the user name of the gold standard
	 */
	String GOLD_STANDARD_USER_NAME = "";

	/**
	 * adds relations to a gold standard resource
	 * 
	 * @param postHash   the hash of the gold standard post
	 * @param references the references to add (interhashes)
	 * @param relation  the relation between a post and its reference
	 */
	void createResourceRelations(final String postHash, final Set<String> references, final GoldStandardRelation relation);

	/**
	 * deletes relations from a gold stanard resource
	 * 
	 * @param postHash	 the hash of the gold standard post
	 * @param references the references to delete (interhashes)
	 * @param relation  the relation between a post and its reference
	 */
	void deleteResourceRelations(final String postHash, final Set<String> references, final GoldStandardRelation relation);
}
