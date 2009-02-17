package org.bibsonomy.webapp.command;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.webapp.controller.AdminPageController;

/**
 * @author sts
 * @version $Id$
 */
public class AdminStatisticsCommand {

	private HashMap<Long, Integer> numAdminSpammer = new HashMap<Long, Integer>();
	
	private HashMap<Long, Integer> numAdminNoSpammer = new HashMap<Long, Integer>();
	
	private HashMap<Long, Integer> numClassifierSpammer = new HashMap<Long, Integer>();
	
	private HashMap<Long, Integer> numClassifierSpammerUnsure = new HashMap<Long, Integer>();
	
	private HashMap<Long, Integer> numClassifierNoSpammer = new HashMap<Long, Integer>();
	
	private HashMap<Long, Integer> numClassifierNoSpammerUnsure = new HashMap<Long, Integer>();
	
	public HashMap<Long, Integer> getNumAdminSpammer() {
		return this.numAdminSpammer;
	}

	public void setNumAdminSpammer(Long interval, int counts) {
		this.numAdminSpammer.put(interval, counts);
	}

	public HashMap<Long, Integer> getNumAdminNoSpammer() {
		return this.numAdminNoSpammer;
	}

	public void setNumAdminNoSpammer(Long interval, int counts) {
		this.numAdminNoSpammer.put(interval, counts);
	}

	public HashMap<Long, Integer> getNumClassifierSpammer() {
		return this.numClassifierSpammer;
	}

	public void setNumClassifierSpammer(Long interval, int counts) {
		this.numClassifierSpammer.put(interval, counts);
	}

	public HashMap<Long, Integer> getNumClassifierSpammerUnsure() {
		return this.numClassifierSpammerUnsure;
	}

	public void setNumClassifierSpammerUnsure(Long interval, int counts) {
		this.numClassifierSpammerUnsure.put(interval, counts);
	}

	public HashMap<Long, Integer> getNumClassifierNoSpammer() {
		return this.numClassifierNoSpammer;
	}

	public void setNumClassifierNoSpammer(Long interval, int counts) {
		this.numClassifierNoSpammer.put(interval, counts);
	}

	public HashMap<Long, Integer> getNumClassifierNoSpammerUnsure() {
		return this.numClassifierNoSpammerUnsure;
	}

	public void setNumClassifierNoSpammerUnsure(Long interval, int counts) {
		this.numClassifierNoSpammerUnsure.put(interval, counts);
	}	
}