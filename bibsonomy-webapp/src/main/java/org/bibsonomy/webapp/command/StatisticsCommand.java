package org.bibsonomy.webapp.command;


/**
 * command for statistic informations
 *
 * @author dzo
 */
public class StatisticsCommand extends BaseCommand {
	public enum StatisticType {
		POSTS,
		USERS;
	}
	
	private boolean spammers;
	private boolean all;
	private StatisticType type = StatisticType.POSTS;
	
	private String responseString;
	
	/**
	 * @return the spammers
	 */
	public boolean isSpammers() {
		return this.spammers;
	}

	/**
	 * @param spammer the spammers to set
	 */
	public void setSpammers(boolean spammers) {
		this.spammers = spammers;
	}

	/**
	 * @return the all
	 */
	public boolean isAll() {
		return this.all;
	}

	/**
	 * @param all the all to set
	 */
	public void setAll(boolean all) {
		this.all = all;
	}

	/**
	 * @return the type
	 */
	public StatisticType getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(StatisticType type) {
		this.type = type;
	}

	/**
	 * @return the responseString
	 */
	public String getResponseString() {
		return this.responseString;
	}

	/**
	 * @param responseString the responseString to set
	 */
	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}
}
