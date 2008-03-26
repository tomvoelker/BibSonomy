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
		
	public AdminViewCommand() {				
		addTab(ADMIN_SPAMMER_INDEX, "Admin: Spammer");
		addTab(ADMIN_NOSPAMMER_INDEX, "Admin: No Spammer");
		addTab(CLASSIFIER_SPAMMER_INDEX, "Classifier: Spammer");
		addTab(CLASSIFIER_SPAMMER_UNSURE_INDEX, "Classifier: Spammer (U)");
		addTab(CLASSIFIER_NOSPAMMER_INDEX, "Classifier: No Spammer");
		addTab(CLASSIFIER_NOSPAMMER_UNSURE_INDEX, "Classifier: No Spammer (U)");
	}		
}