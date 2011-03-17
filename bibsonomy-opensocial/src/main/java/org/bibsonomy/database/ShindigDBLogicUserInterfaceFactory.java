package org.bibsonomy.database;

import org.apache.shindig.auth.SecurityToken;
import org.bibsonomy.database.DBLogic;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import static org.bibsonomy.util.ValidationUtils.present;

public class ShindigDBLogicUserInterfaceFactory  implements ShindigLogicInterfaceFactory {
	private DBSessionFactory dbSessionFactory;
	
	private LogicInterfaceFactory noAuthLogicFactory;

	public LogicInterface getLogicAccess(SecurityToken st) {
		if (present(st)&&!st.isAnonymous()) {
			return new DBLogic(new User(st.getViewerId()), this.dbSessionFactory);
		} else {
			return this.noAuthLogicFactory.getLogicAccess(null, null);
		}
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setNoAuthLogicFactory(LogicInterfaceFactory noAuthLogicFactory) {
		this.noAuthLogicFactory = noAuthLogicFactory;
	}

	public LogicInterfaceFactory getNoAuthLogicFactory() {
		return noAuthLogicFactory;
	}

	public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}

	public DBSessionFactory getDbSessionFactory() {
		return dbSessionFactory;
	}
}
