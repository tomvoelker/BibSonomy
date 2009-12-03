/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.common.exceptions.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author nosebrain
 * @version $Id$
 */
public class DatabaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final Map<String, List<AbstractDatabaseException>> exceptions;
	
	/**
	 * initiate map
	 */
	public DatabaseException() {
		this.exceptions = new HashMap<String, List<AbstractDatabaseException>>();
	}
	
	/**
	 * @param hash the hash of the post
	 * @return the exceptions for the post (represented by its hash)
	 */
	public List<AbstractDatabaseException> getExceptions(final String hash) {
		return this.exceptions.get(hash);
	}
	
	/**
	 * adds an exception to the list of exceptions for the specified post (hash)
	 * @param hash the hash of the post
	 * @param e the exception to add
	 */
	public void addToExceptions(final String hash, final AbstractDatabaseException e) {
		List<AbstractDatabaseException> list = this.exceptions.get(hash);
		
		if (!present(list)) {
			list = new LinkedList<AbstractDatabaseException>();
		}
		
		list.add(e);
	}
	
	/**
	 * @param hash	the hash of the post
	 * @return true if post has exceptions
	 */
	public boolean hasExceptions(final String hash) {
		return present(this.exceptions.get(hash));
	}
}
