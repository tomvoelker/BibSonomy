package org.bibsonomy.events.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.LinkedList;

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
		assertEquals("mygroup", eventManagerProxy.getEvent("mygroup").getId());
	}

}
