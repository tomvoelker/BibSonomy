package org.bibsonomy.webapp.controller;

import java.util.List;

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
		
		int max = didYouKnowMessages.size();
		int random = (int)(Math.random() * max);
		
		return this.didYouKnowMessages.get(random);
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
