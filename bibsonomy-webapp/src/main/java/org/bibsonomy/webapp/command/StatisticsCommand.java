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
	
	private boolean spammer;
	private boolean all;
	private StatisticType type = StatisticType.POSTS;
	
	private String responseString;
	
	/**
	 * @return the spammer
	 */
	public boolean isSpammer() {
		return this.spammer;
	}

	/**
	 * @param spammer the spammer to set
	 */
	public void setSpammer(boolean spammer) {
		this.spammer = spammer;
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
