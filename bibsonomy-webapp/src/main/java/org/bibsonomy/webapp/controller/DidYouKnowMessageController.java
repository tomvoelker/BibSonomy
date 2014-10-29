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
