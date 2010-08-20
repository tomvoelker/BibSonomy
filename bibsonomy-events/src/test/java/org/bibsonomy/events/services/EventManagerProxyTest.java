package org.bibsonomy.events.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.LinkedList;

import org.bibsonomy.events.services.EventManager;
import org.bibsonomy.events.services.EventManagerProxy;
import org.bibsonomy.events.services.SpringBeanEventManager;
import org.junit.Test;

public class EventManagerProxyTest {

	@Test
	public void testGetEvent() {
		/*
		 * no events set -> NPE
		 */
		final EventManagerProxy eventManagerProxy = new EventManagerProxy();
		
		try {
			eventManagerProxy.getEvent("foo");
			fail("NPE expected");
		} catch (NullPointerException e) {
			
		}
		eventManagerProxy.setEventManagers(new LinkedList<EventManager>());
		try {
			eventManagerProxy.getEvent("foo");
			fail("UnsupportedOperationException expected");
		} catch (UnsupportedOperationException e) {
			
		}
		eventManagerProxy.getEventManagers().add(new SpringBeanEventManager());
		assertEquals("lwa2010", eventManagerProxy.getEvent("lwa2010").getId());
	}

}
