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

	/**
	 * @param contentId
	 * @param sender
	 * @param receiver
	 */
	
	public InboxMessage(){
		
	}
	public InboxMessage(final int contentId, final String sender, final String receiver) {
		setInboxMessage(contentId, sender, receiver);
	}
	
	public void setContentId(int contentId) {
		this.contentId=contentId;
	}
	
	public void setSender(String sender) {
		this.sender=sender;
	}
	
	public void setReceiver(String receiver) {
		this.receiver=receiver;
	}
	/**
	 * set message parameters as specified
	 * @param contentId
	 * @param sender
	 * @param receiver
	 */
	public void setInboxMessage(final int contentId, final String sender, final String receiver) {
		this.contentId=contentId;
		this.sender=sender;
		this.receiver=receiver;
	}
	
	@Override
	public String toString(){
		return ("MessageContent: "+contentId+" sent by: "+sender+" received by: "+receiver);
	}
}
