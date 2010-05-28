package org.bibsonomy.webapp.command.actions;

/**
 * @author philipp
 * @version $Id$
 */
public class RelationsEditCommand {
	
	private String upper = "";
	
	private String lower = "";
	
	/**
	 * which action is requested
	 * 0 = add relation
	 * 1 = del relation
	 */
	private int forcedAction;

	/**
	 * @param upper the upper to set
	 */
	public void setUpper(String upper) {
		this.upper = upper;
	}

	/**
	 * @return the upper
	 */
	public String getUpper() {
		return upper;
	}

	/**
	 * @param lower the lower to set
	 */
	public void setLower(String lower) {
		this.lower = lower;
	}

	/**
	 * @return the lower
	 */
	public String getLower() {
		return lower;
	}

	/**
	 * @param forcedAction the forcedAction to set
	 */
	public void setForcedAction(int forcedAction) {
		this.forcedAction = forcedAction;
	}

	/**
	 * @return the forcedAction
	 */
	public int getForcedAction() {
		return forcedAction;
	}

}
