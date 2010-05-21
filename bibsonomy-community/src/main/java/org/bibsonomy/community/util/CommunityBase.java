package org.bibsonomy.community.util;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommunityBase {
	/** the naming context for community classes */
	public static final String CONTEXT_ENV_NAME    = "java:/comp/env";
	/** context variable containing community configuration */
	public static final String CONTEXT_CONFIG_BEAN = "communityConfig";
	
	private CommunityConfig configuration;
	private final static Log log = LogFactory.getLog(CommunityBase.class);
	
	protected void loadConfiguration() {
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context) initContext.lookup(CONTEXT_ENV_NAME);
			configuration = (CommunityConfig)envContext.lookup(CONTEXT_CONFIG_BEAN);
		} catch( Exception e ) {
			log.error("Error getting community base configuration", e);
			configuration = new CommunityConfig();
		}
	}
	
	public String getCommunityDBName() {
		return this.configuration.getCommunityDBName();
	}
}
