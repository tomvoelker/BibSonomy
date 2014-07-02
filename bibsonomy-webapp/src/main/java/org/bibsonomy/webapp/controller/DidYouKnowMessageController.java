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
	
	public void initializeDidYouKnowMessageCommand(DidYouKnowMessageCommand command) {
		command.setDidYouKnowMessage(this.getRandomDidYouKnowMessage());
	}
	
	public DidYouKnowMessage getRandomDidYouKnowMessage() {
		
		int max = didYouKnowMessages.size() - 1;
		int random = (int)(Math.random() * max);
		
		return this.didYouKnowMessages.get(random);
	}

	public List<DidYouKnowMessage> getDidYouKnowMessages() {
		return didYouKnowMessages;
	}

	public void setDidYouKnowMessages(List<DidYouKnowMessage> didYouKnowMessages) {
		this.didYouKnowMessages = didYouKnowMessages;
	}
}
