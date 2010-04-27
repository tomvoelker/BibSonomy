/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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
 * Defines possible search entities. These are used to distinguish 
 * where to search for a given search term.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public enum SearchEntity {

	/**
	 * search over all available fields 
	 */	 
	ALL,	
	/**
	 * search only in author field
	 */
	AUTHOR,
	/**
	 * search by bibtexkey
	 */
	BIBTEXKEY,
	/**
	 * fulltext search via lucene
	 */
	LUCENE;
}