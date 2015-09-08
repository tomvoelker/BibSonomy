package org.bibsonomy.webapp;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class DebugDataSource extends BasicDataSource {
	private static final Log log = LogFactory.getLog(DebugDataSource.class);
	/* (non-Javadoc)
	 * @see org.apache.commons.dbcp.BasicDataSource#close()
	 */
	@Override
	public synchronized void close() throws SQLException {
		try {
			throw new RuntimeException("this should not be called");
		} catch ( RuntimeException e) {
			log.fatal(e,e);
		}
		super.close();
	}

}
