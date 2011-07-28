package org.bibsonomy.util.log4j.filter;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * 
 * @author dzo
 * @version $Id$
 */
public class ExceptionFilter extends Filter {

	private final Set<String> ignoredExceptions = new HashSet<String>();

	@Override
	public int decide(final LoggingEvent event) {
		final ThrowableInformation throwInfo = event.getThrowableInformation();
		
		if (throwInfo != null) {
			final Class<? extends Throwable> throwableClass = throwInfo.getThrowable().getClass();
			
			if (ignoredExceptions.contains(throwableClass.getName())) {
				return DENY;
			}
		}
		
		return NEUTRAL;
	}
	
	/**
	 * XXX: we use a setter to add objects to the ignored exception list
	 * because this is the only way to config this filter by log4j
	 * Example
	 * <code>
	 * 	&lt;param name="IgnoredException" value="java.lang.Exception"/&gt;
	 * </code>	
	 * @param ignoredException the ignoredExceptions to set
	 */
	public void setIgnoredException(final String ignoredException) {
		this.ignoredExceptions.add(ignoredException);
	}

}
