package org.bibsonomy.testutil;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.log4j.Logger;

/**
 * Helper class for binding a test database resource via JNDI to enable database access
 * without a running application server, which usually provides the JNDI tree.
 * 
 * @author dbenz
 */
public class JNDITestDatabaseBinder {

	// log4j
	private static final Logger log = Logger.getLogger(JNDITestDatabaseBinder.class);
		
	/**
	 * Main method: read configuration file 'database.properties', create SQL Data Source and
	 * register it via JNDI
	 * 
	 * @author dbenz
	 */
	public static final void bind() {		
		try {
			// read database properties
			Properties prop = new Properties();
			prop.load(JNDITestDatabaseBinder.class.getClassLoader().getResourceAsStream("database.properties"));
	
			// Construct Reference to Database Resource
			Reference ref = new Reference("javax.sql.DataSource", "org.apache.commons.dbcp.BasicDataSourceFactory", null);
			ref.add(new StringRefAddr("com.mysql.jdbc.Driver", "org.apache.commons.dbcp.TesterDriver"));
			ref.add(new StringRefAddr("url", prop.getProperty("url")));
			ref.add(new StringRefAddr("username", prop.getProperty("username")));
			ref.add(new StringRefAddr("password", prop.getProperty("password")));
			ref.add(new StringRefAddr("driverClassName", prop.getProperty("driverClassName")));
	
			// register database resource via JNDI
			Context ctx = new InitialContext();
			ctx.rebind(prop.getProperty("JNDIName"), ref);		
		}
		catch (IOException ex) {
			log.error("I/O-Error when reading test database connection properties file");
			log.error(ex.getMessage());
		}
		catch (NamingException ex) {
			log.error("Error when trying to bind test database connection via JNDI");
			log.error(ex.getMessage());
		}
	}
}
