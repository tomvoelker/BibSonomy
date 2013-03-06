/**
 *
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.common.enums;

/**
 * Enum for the different tag relations.
 * 
 * TODO: Think about combining it with TagSimilarity.
 * 
 * @author niebler
 * @version $Id$
 */
public enum TagRelation {
	/** Co-Occurring tags. */
	RELATED,
	/** Cosine-similar tags. */
	SIMILAR,
	/** Subtags. */
	SUBTAGS,
	/** Supertags. */
	SUPERTAGS;
	
	/**
	 * Tries to match a string onto a relation.
	 * @param string a string
	 * @return the requested relation or null if no match was found.
	 */
	public static TagRelation getRelationByString(String string) {
		if (string.equalsIgnoreCase("related")) return TagRelation.RELATED;
		else if (string.equalsIgnoreCase("similar")) return TagRelation.SIMILAR;
		else if (string.equalsIgnoreCase("subtags")) return TagRelation.SUBTAGS;
		else if (string.equalsIgnoreCase("supertags")) return TagRelation.SUPERTAGS;
		else return null;
	}
}
