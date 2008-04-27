package org.bibsonomy.testutil;

import java.io.IOException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.mockejb.jndi.MockContextFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * Helper class for binding a test database resource via JNDI to enable database access
 * without a running application server, which usually provides the JNDI tree.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public final class JNDITestDatabaseBinder {

	private static final Logger log = Logger.getLogger(JNDITestDatabaseBinder.class);

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
		final InitialContext ctx;
		final DataSource ds = getDataSource();
		try {			
			// create Mock JNDI context
			MockContextFactory.setAsInitial();
			ctx = new InitialContext();
			ctx.bind("java:comp/env/jdbc/bibsonomy", ds);
		}
		catch (NamingException ex) {
			log.error("Error when trying to bind test database connection via JNDI");
			log.error(ex.getMessage());
		}
	}

	/**
	 * Go back to original state
	 */
	public static void unbind() {
		MockContextFactory.revertSetAsInitial();
	}

	private static DataSource getDataSource() {
		final Properties props = new Properties();		
		try {
			// read database properties
			props.load(JNDITestDatabaseBinder.class.getClassLoader().getResourceAsStream("database.properties"));		
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		final MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUrl(props.getProperty("url"));
		dataSource.setUser(props.getProperty("username"));
		dataSource.setPassword(props.getProperty("password"));
		dataSource.setZeroDateTimeBehavior(props.getProperty("zeroDateTimeBehaviour"));
		return dataSource;
	}	
}