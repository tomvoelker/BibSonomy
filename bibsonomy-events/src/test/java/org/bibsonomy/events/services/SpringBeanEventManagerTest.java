package org.bibsonomy.events.services;

import static org.junit.Assert.assertNotNull;

import org.bibsonomy.events.model.Event;
import org.bibsonomy.events.services.EventManager;
import org.bibsonomy.events.services.SpringBeanEventManager;
import org.junit.Test;

public class SpringBeanEventManagerTest {

	@Test
	public void testGetEvent() {
		final EventManager eventManager = new SpringBeanEventManager();
		final Event event = eventManager.getEvent("lwa2010");
		assertNotNull(event);
		
	}

}
