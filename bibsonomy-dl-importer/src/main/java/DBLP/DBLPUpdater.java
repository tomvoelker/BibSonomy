package DBLP;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import DBLP.constants.DBLPConstantsReader;
import DBLP.constants.DBLPConstantsResult;
import DBLP.db.DBHandler;
import DBLP.parser.DBLPParseResult;
import DBLP.parser.DBLPParserReader;
import DBLP.update.BibtexUpdate;
import DBLP.update.BookmarkUpdate;
import DBLP.update.CheckDeletedEntries;
import DBLP.update.HTTPBibtexUpdate;
import DBLP.update.HTTPBookmarkUpdate;

public class DBLPUpdater {


	static {
		System.setProperty("log4j.configuration", "dblp.log4j.properties");
	}
	

	private final static Log log = LogFactory.getLog(DBLPUpdater.class);

	
	public static void main(String[] args) throws MalformedURLException {

		// get application constants
		DBLPConstantsReader conReader = null;
		DBHandler dbhandler = null;

		try {
			conReader = new DBLPConstantsReader();

			if (args.length == 0) {
				System.err.println("Please specify a configuration file!");
				System.err.println("usage:");
				System.err.println("java -Xmx2000M " + DBLPUpdater.class.getName() + " DBLPConstants.xml");
				System.exit(1);
			} else
				conReader.readConstants(new File(args[0]));// user input

			log.info("DBLP UPDATE started");

			DBLPConstantsResult cresult = conReader.getConResult();
			conReader = null;

			dbhandler = new DBHandler(cresult);
			dbhandler.open();
			String passHash = dbhandler.getUserhash();
			dbhandler.close();
			if (passHash == null)
				throw new Exception("user is not in database");

			DBLPParserReader dblpReader = new DBLPParserReader(cresult.getUrl());
			
			log.info("start reading XML");
			dbhandler.open();
			dblpReader.readDBLP(dbhandler.getDBLPDate());
			dbhandler.close();
			log.info("finished reading XML");

			log.info("getting parseresult");
			DBLPParseResult presult = dblpReader.getResult();
			dblpReader = null;

			
			
			dbhandler.open();
			Date newDBLPdate = presult.getNewDBLPdate();
			log.info("setting dblp date to " + newDBLPdate);
			dbhandler.setDBLPDate(newDBLPdate);
			dbhandler.close();

			log.info("initializing updaters");
			HTTPBookmarkUpdate httpBookUpdate = new HTTPBookmarkUpdate(cresult.getHome(), cresult.getUser(), passHash);
			HTTPBibtexUpdate httpBibUpdate = new HTTPBibtexUpdate(cresult.getHome(), cresult.getUser(), passHash);

			BookmarkUpdate bookupdate = new BookmarkUpdate(httpBookUpdate, presult, dbhandler);
			BibtexUpdate bibupdate = new BibtexUpdate(httpBibUpdate, presult, dbhandler);
			CheckDeletedEntries cde = new CheckDeletedEntries(httpBookUpdate, httpBibUpdate);

			log.info("deleting old posts");
			cde.deleteOldPosts(presult.getAllKeys(), dbhandler);
			presult.setAllKeys(null); // free memory
			
			/*
			 * do updates
			 */
			int error_count = 0;
			log.info("updating bibtex");
			error_count += bibupdate.update(cde.getDb_bibtex());

			log.info("updating bookmarks");
			error_count += bookupdate.update(cde.getDb_bookmark());

			/*
			 * dbhandler.open(); dbhandler.countEntrytypes(presult.getEval());
			 * dbhandler.countBookmarks(presult.getEval()); dbhandler.close();
			 */

			log.info("saving evaluation results");
			dbhandler.open();
			dbhandler.saveAuthorEditorFailure(presult.getInsert_incomplete_author_editor());
			dbhandler.saveDuplicateFailure(presult.getInsert_duplicate());
			dbhandler.saveIncompleteFailure(presult.getInsert_incomplete());
			dbhandler.saveWarningFailure(presult.getInsert_warning());
			dbhandler.saveBookmarkEmptyUrlFailure(presult.getInsert_bookmark_empty_url());
			dbhandler.saveExceptions(presult.getException());
			dbhandler.saveUploadError(presult.getUpload_error());
			dbhandler.close();

			// delete and insert DBLP home bookmark, because ist must stay in
			// the first place

			log.info("updating dblp home page");
			httpBookUpdate.updateDBLPHome();

			log.info("DBLP Update finished");
			log.info(presult.getEval().eval());

		} catch (Exception e) {
			log.fatal("DBLPUpdater: " + e);
			e.printStackTrace();
			dbhandler.close();
		}

	}

}