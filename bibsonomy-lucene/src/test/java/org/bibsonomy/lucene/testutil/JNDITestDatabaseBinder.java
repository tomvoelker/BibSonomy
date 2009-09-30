package org.bibsonomy.lucene.testutil;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.mockejb.jndi.MockContextFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * Helper class for binding a test database resource via JNDI to enable database access
 * without a running application server, which usually provides the JNDI tree.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
/**
 * @author bkr
 *
 */
public final class JNDITestDatabaseBinder {
	/** logging */
	private static final Logger log = Logger.getLogger(JNDITestDatabaseBinder.class);
	
	/** context name for environment variables */
	public static final String CONTEXTNAME = "java:/comp/env/";
	
	/** name of the property file which configures lucene */
	private static final String LUCENEPROPERTYFILENAME = "lucene.properties";

	/** property key for database url*/
	private static final String PROPERTY_DB_URL = "db.url";

	/** property key for database username */
	private static final String PROPERTY_DB_USERNAME = "db.username";

	/** property key for database password */
	private static final String PROPERTY_DB_PASSWORD = "db.password";

	/**
	 * Don't create instances of this class - use the static methods instead.
	 */
	private JNDITestDatabaseBinder() {
	}

	/**
	 * Main method: read configuration file 'database.properties', create SQL Data Source and
	 * register it via JNDI
	 */
	public static final void bind() {
		bindDatabaseContext("bibsonomy_lucene", LUCENEPROPERTYFILENAME);
		bindVariablesContext(CONTEXTNAME, LUCENEPROPERTYFILENAME);
	}

	private static void bindDatabaseContext(final String contextName, final String fileName) {
		final InitialContext ctx;
		final DataSource ds = getBasicDataSource(fileName);
		try {			
			// create Mock JNDI context
			MockContextFactory.setAsInitial();
			ctx = new InitialContext();
			ctx.bind("java:/comp/env/jdbc/"+contextName, ds);
		}
		catch (NamingException ex) {
			log.error("Error when trying to bind test database connection '" + contextName + "' via JNDI");
			log.error(ex.getMessage());
		}
		
	}

	/**
	 * Reads all properties (key=value) from given properties file and stores them in given context
	 * 
	 * @param contextName
	 * @param fileName
	 */
	private static void bindVariablesContext(final String contextName, final String fileName) {
		Context ctx;
		
		final Properties props = new Properties();		
		try {
			// read properties
			props.load(JNDITestDatabaseBinder.class.getClassLoader().getResourceAsStream(fileName));		
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
		try {
			MockContextFactory.setAsInitial();
			ctx = new InitialContext();
			for( Object key : props.keySet() ) {
				try {			
					ctx.bind(contextName+key, props.getProperty((String)key));
				}
				catch (NamingException ex) {
					log.error("Error binding environment variable:'" + contextName + "' via JNDI");
					log.error(ex.getMessage());
				}
			}
		} catch (NamingException e) {
			log.error("Error setting up JNDI environment variables:" + e.getMessage() );
		}

	}
	
	/**
	 * factory for property instances
	 */
	public static Properties getLuceneProperties() {
		final Properties props = new Properties();		
		try {
			// read properties
			props.load(JNDITestDatabaseBinder.class.getClassLoader().getResourceAsStream(LUCENEPROPERTYFILENAME));		
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return props;
	}
	
	/**
	 * Go back to original state
	 */
	public static void unbind() {
		MockContextFactory.revertSetAsInitial();
	}
	

	/**
	 * Create sql data source according to configuration in given property file
	 * 
	 * @param configFile
	 * @return
	 */
	private static DataSource getBasicDataSource(final String configFile) {

		final Properties props = new Properties();		
		try {
			// read database properties
			props.load(JNDITestDatabaseBinder.class.getClassLoader().getResourceAsStream(configFile));		
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		final BasicDataSource dataSource = new BasicDataSource();

		dataSource.setUrl(props.getProperty(PROPERTY_DB_URL));
		dataSource.setUsername(props.getProperty(PROPERTY_DB_USERNAME));
		dataSource.setPassword(props.getProperty(PROPERTY_DB_PASSWORD));
		
		return dataSource;
	}	
}