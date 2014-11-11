package org.bibsonomy.webapp.command;

import org.bibsonomy.webapp.util.DidYouKnowMessage;

/**
 * 
 * @author Sebastian Böttger <boettger@cs.uni-kassel.de>
 */
public interface DidYouKnowMessageCommand {

	
	public DidYouKnowMessage getDidYouKnowMessage();
	
	public void setDidYouKnowMessage(DidYouKnowMessage didYouKnowMessage);
	
}