package org.bibsonomy.webapp.command.admin;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.webapp.command.BaseCommand;


/**
 * Command bean for admin page 
 * 
 * @author Beate Krause
 * @version $Id$
 */
public class AdminCommand extends BaseCommand{

	/*
	 * Admin Actions to be integrated
	 */
	private final Map<String,String> actionTitles;
	
	public AdminCommand(){
	
		actionTitles = new HashMap<String, String>();
		actionTitles.put("spam", "Flag / unflag spammers");
		actionTitles.put("lucene", "Manage lucene");
	}

	public Map<String, String> getActionTitles() {
		return actionTitles;
	}

}