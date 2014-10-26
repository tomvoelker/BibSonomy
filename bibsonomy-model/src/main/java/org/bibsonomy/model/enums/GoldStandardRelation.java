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

package org.bibsonomy.model.enums;


/**
 * 
 * all possible relations between two gold standards
 *
 * @author lka
 */
public enum GoldStandardRelation {
	/** gold standard is referenced by another gold standard */
	REFERENCE(0),
	
	/** gold standard is part of another gold standard */
	PART_OF(1);
	
	/**
	 * @param id
	 * @return the relation to the provided id
	 */
	public static GoldStandardRelation getGoldStandardRelation(final int id) {
		for (GoldStandardRelation goldStandardRelation : GoldStandardRelation.values()) {
			if (goldStandardRelation.getValue() == id) {
				return goldStandardRelation;
			}
		}
		
		throw new IllegalArgumentException("no relation with id " + id);
	}
	
	private final int value;
	
	private GoldStandardRelation(int value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return this.value;
	}
}
