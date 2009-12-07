/**
 *  
 *  BibSonomy-Common - Common things (e.g., error Messages, enums, utils, etc.)
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

import org.bibsonomy.common.errors.ErrorMessage;

/**
 * @author dzo
 * @version $Id$
 */
public class DatabaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final Map<String, List<ErrorMessage>> errorMessages;
	
	/**
	 * initiate map
	 */
	public DatabaseException() {
		this.errorMessages = new HashMap<String, List<ErrorMessage>>();
	}
	
	
	/**
	 * @return the errorMessages
	 */
	public Map<String, List<ErrorMessage>> getErrorMessages() {
		return this.errorMessages;
	}

	/**
	 * @param hash the hash of the post
	 * @return the error Messages for the post (represented by its hash)
	 */
	public List<ErrorMessage> getErrorMessages(final String hash) {
		return this.errorMessages.get(hash);
	}
	
	/**
	 * adds an error Message to the list of error Messages for the specified post (hash)
	 * @param hash the hash of the post
	 * @param errorMessage the error Message to add
	 */
	public void addToErrorMessages(final String hash, final ErrorMessage errorMessage) {
		List<ErrorMessage> list = this.errorMessages.get(hash);
		
		if (!present(list)) {
			list = new LinkedList<ErrorMessage>();
			this.errorMessages.put(hash, list);
		}
		
		list.add(errorMessage);
	}
	
	/**
	 * @param hash	the hash of the post
	 * @return true if post has error Messages
	 */
	public boolean hasErrorMessages(final String hash) {
		return present(this.errorMessages.get(hash));
	}
	
	/**
	 * @return true if any post has error Messages
	 */
	public boolean hasErrorMessages() {
		return (!this.errorMessages.isEmpty());
	}
}
