package org.bibsonomy.importer.DBLP;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.importer.DBLP.configuration.Configuration;
import org.bibsonomy.importer.DBLP.configuration.ConfigurationReader;
import org.bibsonomy.importer.DBLP.db.DBHandler;
import org.bibsonomy.importer.DBLP.parser.DBLPParseResult;
import org.bibsonomy.importer.DBLP.parser.DBLPParserReader;
import org.bibsonomy.importer.DBLP.update.BibtexUpdate;
import org.bibsonomy.importer.DBLP.update.BookmarkUpdate;
import org.bibsonomy.importer.DBLP.update.CheckDeletedEntries;
import org.bibsonomy.importer.DBLP.update.HTTPBibtexUpdate;
import org.bibsonomy.importer.DBLP.update.HTTPBookmarkUpdate;


public class DBLPUpdater {


	static {
		System.setProperty("log4j.configuration", "dblp.log4j.properties");
	}
	

	private final static Log log = LogFactory.getLog(DBLPUpdater.class);

	
	public static void main(String[] args) throws MalformedURLException {

		// get application constants
		ConfigurationReader conReader = null;
		DBHandler dbhandler = null;

		try {
			conReader = new ConfigurationReader();

			if (args.length == 0) {
				System.err.println("Please specify a configuration file!");
				System.err.println("usage:");
				System.err.println("java -Xmx2000M " + DBLPUpdater.class.getName() + " DBLPConstants.xml");
				System.exit(1);
			} else
				conReader.readConfiguration(new File(args[0]));// user input

			log.info("DBLP UPDATE started");

			final Configuration configuration = conReader.getConResult();
			conReader = null;

			dbhandler = new DBHandler(configuration);
			dbhandler.open();
			String passHash = dbhandler.getUserhash();
			dbhandler.close();
			if (passHash == null)
				throw new Exception("user is not in database");

			/*
			 * read XML file from DBLP
			 */
			final DBLPParserReader dblpReader = new DBLPParserReader(configuration.getUrl());
			
			log.info("start reading XML");
			dbhandler.open();
			dblpReader.readDBLP(dbhandler.getDBLPDate());
			dbhandler.close();
			log.info("finished reading XML");

			log.info("getting parseresult");
			final DBLPParseResult presult = dblpReader.getResult();

			/*
			 * update date in DB
			 * FIXME: shouldn't we do this only on success?
			 */
			dbhandler.open();
			final Date newDBLPdate = presult.getNewDBLPdate();
			log.info("setting dblp date to " + newDBLPdate);
			dbhandler.setDBLPDate(newDBLPdate);
			dbhandler.close();

			
			log.info("initializing updaters");
			final HTTPBookmarkUpdate httpBookUpdate = new HTTPBookmarkUpdate(configuration.getHome(), configuration.getUser(), passHash);
			final HTTPBibtexUpdate httpBibUpdate = new HTTPBibtexUpdate(configuration.getHome(), configuration.getUser(), passHash);

			final BookmarkUpdate bookupdate = new BookmarkUpdate(httpBookUpdate, presult, dbhandler);
			final BibtexUpdate bibupdate = new BibtexUpdate(httpBibUpdate, presult, dbhandler);
			/*
			 * checking entries which must be deleted
			 */
			final CheckDeletedEntries cde = new CheckDeletedEntries(httpBookUpdate, httpBibUpdate);
			log.info("deleting old posts");
			cde.deleteOldPosts(presult.getAllKeys(), dbhandler);

			/*
			 * do updates
			 */
			log.info("updating bibtex");    int pcount = bibupdate.update(cde.getBibtexKeysInDatabase());
			log.info("updated " + pcount + " posts");
			log.info("updating bookmarks");	int bcount = bookupdate.update(cde.getBookmarkKeysInDatabase());
			log.info("updated " + bcount + " posts");
			
			// delete and insert DBLP home bookmark, because ist must stay in
			// the first place

			log.info("updating dblp home page");
			httpBookUpdate.updateDBLPHome();

			log.info("DBLP Update finished");
			log.info(presult.getEval().eval());

		} catch (Exception e) {
			log.fatal("DBLPUpdater: ", e);
			dbhandler.close();
		}

	}

}