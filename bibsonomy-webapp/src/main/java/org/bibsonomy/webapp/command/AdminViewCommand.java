package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

/**
 * Command bean for admin page 
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminViewCommand extends TabsCommand<User> {
	
	/** Indexes of definded tabs */
	public final static int ADMIN_SPAMMER_INDEX = 1;
	public final static int ADMIN_NOSPAMMER_INDEX = 2;
	public final static int CLASSIFIER_SPAMMER_INDEX = 3;
	public final static int CLASSIFIER_SPAMMER_UNSURE_INDEX = 4;
	public final static int CLASSIFIER_NOSPAMMER_INDEX	= 5;
	public final static int CLASSIFIER_NOSPAMMER_UNSURE_INDEX = 6;
	
	/** whether the page contains spammers or nonspammers */
	public boolean isSpammerPage = true;
		
	public AdminViewCommand() {				
		addTab(ADMIN_SPAMMER_INDEX, "Admin: Spammer");
		addTab(ADMIN_NOSPAMMER_INDEX, "Admin: No Spammer");
		addTab(CLASSIFIER_SPAMMER_INDEX, "Classifier: Spammer");
		addTab(CLASSIFIER_SPAMMER_UNSURE_INDEX, "Classifier: Spammer (U)");
		addTab(CLASSIFIER_NOSPAMMER_INDEX, "Classifier: No Spammer");
		addTab(CLASSIFIER_NOSPAMMER_UNSURE_INDEX, "Classifier: No Spammer (U)");
		
		System.out.println("TAB: " + selTab);
		
	}
	
	@Override
	public void setSelTab(Integer selectedTab) {
		super.setSelTab(selectedTab);
		if (selTab == ADMIN_NOSPAMMER_INDEX ||
				selTab == CLASSIFIER_NOSPAMMER_INDEX ||
				selTab == CLASSIFIER_NOSPAMMER_UNSURE_INDEX) {
				isSpammerPage = false;
		}
	}



	public boolean isSpammerPage() {
		return this.isSpammerPage;
	}	
}