package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Steffen Kress
 * @version $Id$
 */
//TODO
public class SettingsViewCommand extends TabsCommand<Object> {
	private static final Log log = LogFactory.getLog(SettingsViewCommand.class);
	
	/** Indexes of definded tabs */
	
	public final static int MY_PROFILE_IDX = 0;
	public final static int SETTINGS_IDX = 1;
	public final static int IMPORTS_IDX = 2;
	
	/**
	 * Constructor.
	 */
	public SettingsViewCommand() {
		addTab(MY_PROFILE_IDX, "navi.myprofile");
		addTab(SETTINGS_IDX, "navi.settings");
		addTab(IMPORTS_IDX, "navi.imports");
		setSelTab(MY_PROFILE_IDX);
	}
}