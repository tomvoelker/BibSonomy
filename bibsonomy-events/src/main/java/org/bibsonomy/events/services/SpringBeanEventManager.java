package org.bibsonomy.events.services;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Map;

import org.bibsonomy.events.model.Event;
import org.bibsonomy.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Reads the events from a Spring configured bean in form of a String->Event map.
 * 
 * @author rja
 *
 */
public class SpringBeanEventManager implements EventManager {

	private static final String DEFAULT_BEAN_NAME = "events";
	private static final String DEFAULT_BEAN_CONFIG_LOCATION = "events.xml";
	private String beanConfigLocation = DEFAULT_BEAN_CONFIG_LOCATION;
	private String beanName = DEFAULT_BEAN_NAME;
	
	private final Map<String, Event> events;
	
	
	public SpringBeanEventManager() {
		this.events = this.getEvents();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Event> getEvents() {
		final ApplicationContext springBeanFactory = new ClassPathXmlApplicationContext(beanConfigLocation);
		
		final Object bean = springBeanFactory.getBean(beanName);
		if (!present(bean)) throw new RuntimeException("Could not get event definitions from " + beanConfigLocation);
		
		if (bean instanceof Map) {
			return (Map) bean;
		} else {
			throw new RuntimeException("no <String,Event> map found as bean " + beanName);
		}
	}
	
	@Override
	public Event getEvent(String name) {
		return events.get(name);
	}

	/**
	 * @return The name of the Spring bean configuration file.
	 */
	public String getBeanConfigLocation() {
		return beanConfigLocation;
	}

	/**
	 * @return The name of the bean used to define events.
	 */
	public String getBeanName() {
		return beanName;
	}

	/**
	 * The name of the Spring bean configuration file.
	 * 
	 * @param beanConfigLocation
	 */
	public void setBeanConfigLocation(String beanConfigLocation) {
		this.beanConfigLocation = beanConfigLocation;
	}

	/**
	 * The name of the bean (must be a <String,Event> map) defining events.
	 * 
	 * @param beanName
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void registerUser(User user, Event event, String subEvent, String address) {
		throw new UnsupportedOperationException();
	}

}
