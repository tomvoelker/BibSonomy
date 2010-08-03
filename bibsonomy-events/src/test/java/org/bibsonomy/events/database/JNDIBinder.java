package org.bibsonomy.events.database;

import java.io.IOException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.ConnectionPoolDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockejb.jndi.MockContextFactory;

/**
 * Helper class for binding a test database resource via JNDI to enable database access
 * without a running application server, which usually provides the JNDI tree.
 * 
 * @author Dominik Benz
 * @author bkr
 * @author dzo
 * @version $Id$
 */
public final class JNDIBinder {
	private static final Log log = LogFactory.getLog(JNDIBinder.class);
	
	/**
	 * Go back to original state
	 */
	public static void unbind() {
		MockContextFactory.revertSetAsInitial();
	}	

	/**
	 * binds all database property files (ending with _database.properties and in the root class path) to the context before _
	 * 
	 * e.g. foobar_database.properties is bind to the context "foobar"
	 */
	public static void bind(final String propFile) {
		try {			
			// create Mock JNDI context
			MockContextFactory.setAsInitial();
			bindDataSource(propFile, new InitialContext());
		} catch (Exception ex) {
			log.error("Error when trying to bind test database connection via JNDI");
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
	}
	
	private static void bindDataSource(String propFile, InitialContext ctx) throws IOException, NamingException {
		log.debug("loading properties " + propFile);
		final Properties properties = getPropertiesFromFile(propFile);
		
		ConnectionPoolDataSource cs;
		
//		final PGSimpleDataSource dataSource = new PGSimpleDataSource();
//
//		dataSource.setServerName(properties.getProperty("host"));
//		dataSource.setPortNumber(Integer.parseInt(properties.getProperty("port")));
//		dataSource.setDatabaseName(properties.getProperty("database"));
//		dataSource.setUser(properties.getProperty("username"));
//		dataSource.setPassword(properties.getProperty("password"));
//		
//		final String jdbcKey = propFile.substring(0, propFile.lastIndexOf("_"));
//		
//		ctx.bind("java:comp/env/jdbc/" + jdbcKey, dataSource);
	}

	private static Properties getPropertiesFromFile(String filename) throws IOException {
		final Properties props = new Properties();		

		// read database properties
		props.load(JNDIBinder.class.getClassLoader().getResourceAsStream(filename));
		
		return props;
	}
}