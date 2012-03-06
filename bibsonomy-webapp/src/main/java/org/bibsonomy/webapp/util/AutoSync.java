package org.bibsonomy.webapp.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * @author wla
 * @version $Id$
 */
public class AutoSync {
	private static final Log log = LogFactory.getLog(AutoSync.class);
	
	public AutoSync() {
		log.debug("constructor called");
	}

	private LogicInterface adminLogic;
	
	/**
	 * 
	 */
	public void performAutoSync() {
		log.debug("start automatic synchronization");
	}

	/**
	 * @return the adminLogic
	 */
	public LogicInterface getAdminLogic() {
		return adminLogic;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
}
