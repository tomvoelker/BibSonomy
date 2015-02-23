/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller;

import java.util.List;
import java.util.Random;

import org.bibsonomy.webapp.command.DidYouKnowMessageCommand;
import org.bibsonomy.webapp.util.DidYouKnowMessage;

/**
 * This class enables controllers to handle with random 
 * did you know messages. 
 * 
 * @author Sebastian Böttger <boettger@cs.uni-kassel.de>
 */
public abstract class DidYouKnowMessageController {

	/**
	 * this list contains all didyouknow messages of the controller
	 * this will be filled via bibsonomy2-servlet*.xml
	 */
	private List<DidYouKnowMessage> didYouKnowMessages;
	
	/**
	 * sets random didYouKnowMessage
	 * @param command
	 */
	public void initializeDidYouKnowMessageCommand(DidYouKnowMessageCommand command) {
		command.setDidYouKnowMessage(this.getRandomDidYouKnowMessage());
	}
	
	/**
	 * returns a random didYouKnowMessage
	 * @return didYouKnowMessage
	 */
	public DidYouKnowMessage getRandomDidYouKnowMessage() {
		if (this.didYouKnowMessages == null) {
			return null;
		}
		final Random random = new Random();
		return this.didYouKnowMessages.get(random.nextInt(this.didYouKnowMessages.size()));
	}

	/**
	 * returns a list of didYouKnowMessages
	 * @return didYouKnowMessage
	 */
	public List<DidYouKnowMessage> getDidYouKnowMessages() {
		return didYouKnowMessages;
	}

	/**
	 * 
	 * @param didYouKnowMessages
	 */
	public void setDidYouKnowMessages(List<DidYouKnowMessage> didYouKnowMessages) {
		this.didYouKnowMessages = didYouKnowMessages;
	}
}
