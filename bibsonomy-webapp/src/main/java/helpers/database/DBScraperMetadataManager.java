package helpers.database;

import helpers.constants;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.ScrapingContext;


public class DBScraperMetadataManager extends DBManager {

	private static final Log log = LogFactory.getLog(DBScraperMetadataManager.class);
	
	/** Saves the metadata a scraper produced in the database.
	 *  
	 * @param sc ScrapingContext which contains metadata, URL, Scraper, etc.
	 * @return the id the metadata row got assigned in the scraper metadata table
	 */
	public int insertMetadata (ScrapingContext sc) {
		int newId = -1;
		DBContext c = new DBContext();
		/*
		 * to generate a new id for every row of metadata we use the DBIdManager
		 */
		DBIdManager idmanager = new DBIdManager();
		try {
			if (c.init()) {
				idmanager.prepareStatements(c.conn, constants.SQL_IDS_SCRAPER_METADATA);
				/*
				 * get new ID and insert data in a transaction
				 */
				c.conn.setAutoCommit(false);
				/*
				 * get new ID for scraper metadata
				 */
				newId = idmanager.getNewId();
				/*
				 * insert row into scraper metadata table
				 */
				c.stmt = c.conn.prepareStatement("INSERT INTO scraperMetaData (`id`,`metaResult`,`scraper`,`url`) VALUES (?,?,?,?)");
				c.stmt.setInt(1, newId);
				c.stmt.setString(2, sc.getMetaResult());
				c.stmt.setString(3, sc.getScraper().getClass().getName());
				if (sc.getUrl() != null) {
					c.stmt.setString(4, sc.getUrl().toString());
				} else {
					c.stmt.setString(4, null);
				}
				c.stmt.executeUpdate();
				c.conn.commit();
			}
		} catch (SQLException e) {
			try {
				c.conn.rollback();
			} catch (SQLException ex) {
				log.fatal("Could not roll back transaction for scraper " + sc.getScraper().getClass().getName() + ": "+ ex.getMessage());
			}
			log.fatal("Could not save metadata for scraper " + sc.getScraper().getClass().getName() + ": "+ e.getMessage());
		} finally {
			c.close(); // close database connection
			idmanager.closeStatements();
		}
		return newId;
	}
}
