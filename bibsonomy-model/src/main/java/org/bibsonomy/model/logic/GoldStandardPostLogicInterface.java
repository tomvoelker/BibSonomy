/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
	public static final String GOLD_STANDARD_USER_NAME = "";

	/**
	 * adds relations to a gold standard resource
	 * 
	 * @param postHash   the hash of the gold standard post
	 * @param references the references to add (interhashes)
	 * @param relation  the relation between a post and its reference
	 */
	public void createRelations(final String postHash, final Set<String> references, final GoldStandardRelation relation);

	/**
	 * deletes relations from a gold stanard resource
	 * 
	 * @param postHash	 the hash of the gold standard post
	 * @param references the references to delete (interhashes)
	 * @param relation  the relation between a post and its reference
	 */
	public void deleteRelations(final String postHash, final Set<String> references, final GoldStandardRelation relation);
}
