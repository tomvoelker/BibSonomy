package org.bibsonomy.webapp.command.ajax;

/**
 * Command for sending information about a clicked recommended
 * post to the server.
 * 
 * @author lukas
  */
public class AjaxItemRecommenderFeedbackCommand extends AjaxCommand {

	private String intraHash;
	private String userName;
	
	/**
	 * @return the intrahash
	 */
	public String getIntraHash() {
		return this.intraHash;
	}
	/**
	 * @param intraHash the intrahash
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}
	/**
	 * @return the username
	 */
	public String getUserName() {
		return this.userName;
	}
	/**
	 * @param userName the username
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
