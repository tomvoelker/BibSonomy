package org.bibsonomy.events.services;

import org.bibsonomy.events.database.JNDIBinder;
import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DatabaseEventManagerTest {

	@Before
	public void bindDatabase() {
		JNDIBinder.bind("events_database.properties");
	}
	
	@Test
	@Ignore
	public void testRegisterUser() {
//		fail("Not yet implemented");
		
		final DatabaseEventManager manager = new DatabaseEventManager();
		final Event event = new Event();
		event.setId("lwa2010");
		
		manager.registerUser(new User("jaeschke"), event, "kdml", "here");
		
	}

}
