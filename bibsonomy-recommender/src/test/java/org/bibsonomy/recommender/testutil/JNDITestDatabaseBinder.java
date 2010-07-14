package org.bibsonomy.recommender.testutil;

import java.io.IOException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockejb.jndi.MockContextFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * Helper class for binding a test database resource via JNDI to enable database access
 * without a running application server, which usually provides the JNDI tree.
 * @author bkr
 * @author Dominik Benz
 * @version $Id$
 */
@Deprecated
public final class JNDITestDatabaseBinder {

	private static final Log log = LogFactory.getLog(JNDITestDatabaseBinder.class);

	/**
	 * Don't create instances of this class - use the static methods instead.
	 */
	private JNDITestDatabaseBinder() {
		//
	}

	/**
	 * Main method: read configuration file 'database.properties', create SQL Data Source and
	 * register it via JNDI
	 */
	public static final void bind() {
		bindDatabaseContext("bibsonomy_recommender", "database_recommender.properties");
		bindDatabaseContext("bibsonomy", "database_bibsonomy.properties");
	}

	private static void bindDatabaseContext(final String contextName, final String fileName) {
		final InitialContext ctx;
		final DataSource ds = getDataSource(fileName);
		try {			
			// create Mock JNDI context
			MockContextFactory.setAsInitial();
			ctx = new InitialContext();
			ctx.bind("java:comp/env/jdbc/" + contextName, ds);
		}
		catch (NamingException ex) {
			log.error("Error when trying to bind test database connection '" + contextName + "' via JNDI");
			log.error(ex.getMessage());
		}
	}

	
	
	/**
	 * Go back to original state
	 */
	public static void unbind() {
		MockContextFactory.revertSetAsInitial();
	}

	private static DataSource getDataSource(final String configFile) {

		final Properties props = new Properties();		
		try {
			// read database properties
			props.load(JNDITestDatabaseBinder.class.getClassLoader().getResourceAsStream(configFile));		
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		final MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();

		dataSource.setUrl(props.getProperty("url"));
		dataSource.setDatabaseName(props.getProperty("database"));
		dataSource.setUser(props.getProperty("username"));
		dataSource.setPassword(props.getProperty("password"));
		
//		dataSource.setZeroDateTimeBehavior(props.getProperty("zeroDateTimeBehaviour"));
		return dataSource;
	}	
}