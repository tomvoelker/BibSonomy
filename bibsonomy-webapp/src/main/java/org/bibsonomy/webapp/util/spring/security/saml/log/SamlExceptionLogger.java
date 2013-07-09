package org.bibsonomy.webapp.util.spring.security.saml.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLConstants;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.log.SAMLLogger;

/**
 * Unfortunately the provided SAMLLogger does not log exceptions, so we need this class.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class SamlExceptionLogger implements SAMLLogger {
	private static final Log log = LogFactory.getLog(SamlExceptionLogger.class);
	private SAMLLogger regularSamlLogger;
	
	
	@Override
	public void log(String operation, String result, SAMLMessageContext context) {
		regularSamlLogger.log(operation, result, context);
	}

	@Override
	public void log(String operation, String result, SAMLMessageContext context, Exception e) {
		if ((e != null) && (SAMLConstants.SUCCESS.equals(result) == false)) {
			log.error("Exception during SAML operation '" + operation + "'", e);
		}
		regularSamlLogger.log(operation, result, context, e);
	}

	@Override
	public void log(String operation, String result, SAMLMessageContext context, Authentication a, Exception e) {
		if ((e != null) && (SAMLConstants.SUCCESS.equals(result) == false)) {
			log.error("Exception during SAML operation '" + operation + "'", e);
		}
		regularSamlLogger.log(operation, result, context, a, e);
		
	}

	/**
	 * @param regularSamlLogger the regularSamlLogger to set
	 */
	public void setRegularSamlLogger(SAMLLogger regularSamlLogger) {
		this.regularSamlLogger = regularSamlLogger;
	}
}
