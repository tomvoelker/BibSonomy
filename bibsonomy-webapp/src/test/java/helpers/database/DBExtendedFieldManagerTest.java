package helpers.database;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockejb.jndi.MockContextFactory;

import resources.ExtendedFieldMap;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class DBExtendedFieldManagerTest {
	private static final Logger log = Logger.getLogger(DBExtendedFieldManagerTest.class);
	
	@BeforeClass
	public static void setup() {
		final InitialContext ctx;
		final DataSource ds = getDataSource();
		try {
			MockContextFactory.setAsInitial();
			ctx = new InitialContext();
			ctx.bind("java:/comp/env/jdbc/bibsonomy", ds);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static DataSource getDataSource() {
		final Properties props = new Properties();
		try {
			log.debug(DBExtendedFieldManagerTest.class.getClassLoader().getResource("database.properties").toString()); // FIXME: maven2 surefire plugin has massive classloader problems
			log.debug(DBExtendedFieldManagerTest.class.getClassLoader().getResource("log4j.properties").toString());
			props.load(DBExtendedFieldManagerTest.class.getClassLoader().getResourceAsStream("database.properties"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		final MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUrl(props.getProperty("url"));
		dataSource.setUser(props.getProperty("username"));
		dataSource.setPassword(props.getProperty("password"));
		return dataSource;
	}

	@Test
	public void isEmpty() {
		DBExtendedFieldManager eman = new DBExtendedFieldManager();
		
		//BasicConfigurator.configure();
		
		HashSet<ExtendedFieldMap> list = new HashSet<ExtendedFieldMap>();
		ExtendedFieldMap map = new ExtendedFieldMap();
		
		map.setDescription("Type of Audience, maybe \"international\", \"research\", \"industrial\", etc.");
		map.setKey("audience");
		map.setOrder(1);
		list.add(map);
		
		map = new ExtendedFieldMap();
		map.setDescription("Size of audience - how many persons did attend?");
		map.setKey("audience_size");
		map.setOrder(2);
		list.add(map);
		
		Assert.assertTrue( eman.createExtendedFieldsMap("nepomuk", list) );
	}
}
