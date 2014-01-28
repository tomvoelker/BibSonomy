package org.bibsonomy.webapp.command.admin;

import java.util.HashMap;
import java.util.Map;

/**
 * Presents status information of spam framework, for example, how many 
 * spammers have been flagged recently
 * @author sts
 * @author bkr
 */
public class AdminStatisticsCommand {

	private final Map<Long, Integer> numAdminSpammer = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numAdminNoSpammer = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numClassifierSpammer = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numClassifierSpammerUnsure = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numClassifierNoSpammer = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numClassifierNoSpammerUnsure = new HashMap<Long, Integer>();
	
	public Map<Long, Integer> getNumAdminSpammer() {
		return this.numAdminSpammer;
	}

	public void setNumAdminSpammer(final Long interval, final int counts) {
		this.numAdminSpammer.put(interval, counts);
	}

	public Map<Long, Integer> getNumAdminNoSpammer() {
		return this.numAdminNoSpammer;
	}

	public void setNumAdminNoSpammer(final Long interval, final int counts) {
		this.numAdminNoSpammer.put(interval, counts);
	}

	public Map<Long, Integer> getNumClassifierSpammer() {
		return this.numClassifierSpammer;
	}

	public void setNumClassifierSpammer(final Long interval, final int counts) {
		this.numClassifierSpammer.put(interval, counts);
	}

	public Map<Long, Integer> getNumClassifierSpammerUnsure() {
		return this.numClassifierSpammerUnsure;
	}

	public void setNumClassifierSpammerUnsure(final Long interval, final int counts) {
		this.numClassifierSpammerUnsure.put(interval, counts);
	}

	public Map<Long, Integer> getNumClassifierNoSpammer() {
		return this.numClassifierNoSpammer;
	}

	public void setNumClassifierNoSpammer(final Long interval, final int counts) {
		this.numClassifierNoSpammer.put(interval, counts);
	}

	public Map<Long, Integer> getNumClassifierNoSpammerUnsure() {
		return this.numClassifierNoSpammerUnsure;
	}

	public void setNumClassifierNoSpammerUnsure(final Long interval, final int counts) {
		this.numClassifierNoSpammerUnsure.put(interval, counts);
	}	
}