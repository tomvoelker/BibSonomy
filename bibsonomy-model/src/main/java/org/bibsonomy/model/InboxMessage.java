/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
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

package org.bibsonomy.model;

/**
 * A Message is uses to send posts to friends
 * 
 * @author sdo
 * @version $Id$
 */
public class InboxMessage {
	private int contentId;
	private String sender;
	private String receiver;
	
	public InboxMessage(){
	}
	
	/**
	 * set message parameters as specified
	 * 
	 * @param contentId
	 * @param sender
	 * @param receiver
	 */
	public InboxMessage(final int contentId, final String sender, final String receiver) {
		this.contentId = contentId;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	/**
	 * @param contentId the contentId to set
	 */
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Override
	public String toString(){
		return ("MessageContent: " + contentId + " sent by: " + sender + " received by: " + receiver);
	}
}
