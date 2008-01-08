package org.bibsonomy.database.managers;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.AdminParam;
import org.bibsonomy.database.util.DBSession;

/** Provides functionalities which are typically only available to admins.
 * This might include flagging a user as spammer, setting the status of an 
 * InetAddress (IP), and other things.
 * 
 * @author rja
 * @version $Id$
 */
public class AdminDatabaseManager extends AbstractDatabaseManager {

	
	private static final Logger LOGGER = Logger.getLogger(AdminDatabaseManager.class);
	private final static AdminDatabaseManager singleton = new AdminDatabaseManager();

	private AdminDatabaseManager() {
	}

	/**
	 * @return a singleton instance of this AdminDatabaseManager
	 */
	public static AdminDatabaseManager getInstance() {
		return singleton;
	}
	
	public void addInetAddressStatus(InetAddress address, InetAddressStatus status, DBSession session) {
		final AdminParam param = new AdminParam();
		param.setInetAddress(address);
		param.setInetAddressStatus(status);
		this.insert("addInetAddressStatus", param, session);
	}

	public void deleteInetAdressStatus(InetAddress address, DBSession session) {
		this.delete("deleteInetAddressStatus", address, session);
	}

	public InetAddressStatus getInetAddressStatus(InetAddress address, DBSession session) {
		InetAddressStatus status = (InetAddressStatus) this.queryForObject("getInetAddressStatus", address, session);
		if (status == null) return InetAddressStatus.UNKNOWN;
		return status;
	}
	
}
