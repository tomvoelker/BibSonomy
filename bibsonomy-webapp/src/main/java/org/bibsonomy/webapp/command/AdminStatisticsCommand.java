package org.bibsonomy.webapp.command;

import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.SpamStatus;

/**
 * @author sts
 * @version $Id$
 */
public class AdminStatisticsCommand {

	private int numAdminSpammers;
	
	private int numAdminNoSpammer;
	
	private int numClassifierSpammer;
	
	private int numClassifierSpammerUnsure;
	
	private int numClassifierNoSpammer;
	
	private int numClassifierNoSpammerUnsure;

	public int getNumAdminSpammers() {
		return this.numAdminSpammers;
	}

	public void setNumAdminSpammers(int numAdminSpammers) {
		this.numAdminSpammers = numAdminSpammers;
	}

	public int getNumAdminNoSpammer() {
		return this.numAdminNoSpammer;
	}

	public void setNumAdminNoSpammer(int numAdminNoSpammer) {
		this.numAdminNoSpammer = numAdminNoSpammer;
	}

	public int getNumClassifierSpammer() {
		return this.numClassifierSpammer;
	}

	public void setNumClassifierSpammer(int numClassifierSpammer) {
		this.numClassifierSpammer = numClassifierSpammer;
	}

	public int getNumClassifierSpammerUnsure() {
		return this.numClassifierSpammerUnsure;
	}

	public void setNumClassifierSpammerUnsure(int numClassifierSpammerUnsure) {
		this.numClassifierSpammerUnsure = numClassifierSpammerUnsure;
	}

	public int getNumClassifierNoSpammer() {
		return this.numClassifierNoSpammer;
	}

	public void setNumClassifierNoSpammer(int numClassifierNoSpammer) {
		this.numClassifierNoSpammer = numClassifierNoSpammer;
	}

	public int getNumClassifierNoSpammerUnsure() {
		return this.numClassifierNoSpammerUnsure;
	}

	public void setNumClassifierNoSpammerUnsure(int numClassifierNoSpammerUnsure) {
		this.numClassifierNoSpammerUnsure = numClassifierNoSpammerUnsure;
	}	
}