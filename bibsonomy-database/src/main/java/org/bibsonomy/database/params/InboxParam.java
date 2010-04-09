package org.bibsonomy.database.params;

/**
 * @author Stephan Doerfel
 * @version $Id$
 */
public class InboxParam extends GenericParam {
	private int messageId;
	private int contentId;
	private String intraHash;
	private String receiver;
	private String sender;
	
	/**
	 * @return int
	 */
	public int getContentId() {
		return this.contentId;
	}

	/**
	 * @param contentId
	 */
	public void setContentId(final int contentId) {
		this.contentId = contentId;
	}

	/**
	 * @return int
	 */
	public int getMessageId() {
		return this.messageId;
	}

	/**
	 * @param messageId
	 */
	public void setMessageId(final int messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return intraHash
	 */
	public String getIntraHash() {
		return this.intraHash;
	}

	/**
	 * @param intraHash
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}

	/**
	 * @return String
	 */
	public String getReceiver() {
		return this.receiver;
	}

	/**
	 * @param receiver
	 */
	public void setReceiver(final String receiver) {
		this.receiver = receiver;
	}
	/**
	 * @return String
	 */
	public String getSender() {
		return this.sender;
	}

	/**
	 * @param sender
	 */
	public void setSender(final String sender) {
		this.sender= sender;
	}
}