package org.bibsonomy.events.services;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.events.database.IbatisDBSessionFactory;
import org.bibsonomy.events.database.RegistrationParam;
import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;

public class DatabaseEventManager extends AbstractDatabaseManager implements EventManager {

	private final DBSessionFactory dbSessionFactory = new IbatisDBSessionFactory();
	
	@Override
	public Event getEvent(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerUser(final User user, final Event event, final String subEvent, final String address) {
		final DBSession session = dbSessionFactory.getDatabaseSession();
		try {
			final RegistrationParam registrationParam = new RegistrationParam();
			registrationParam.setEvent(event);
			registrationParam.setUser(user);
			registrationParam.setSubEvent(subEvent);
			registrationParam.setAddress(address);
			this.insert("registerUser_" + event.getName(), registrationParam, session);
		} finally {
			session.close();
		}
		
	}

}
