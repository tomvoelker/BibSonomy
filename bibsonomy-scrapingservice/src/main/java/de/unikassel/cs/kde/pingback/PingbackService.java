package de.unikassel.cs.kde.pingback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author rja
 * @version $Id$
 */
public class PingbackService {

	private final Log log = LogFactory.getLog(PingbackService.class);
	
	public String ping(final String sourceURI, final String targetURI) {
		log.info("PING (" + sourceURI + "  -->  " + targetURI + ")");
		return "ignored";
	}
	
}
