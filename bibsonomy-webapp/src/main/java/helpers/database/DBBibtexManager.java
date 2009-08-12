package helpers.database;

import helpers.ModifyGroupId;
import helpers.constants;

import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.Bibtex;
import servlets.BibtexHandler.BibtexException;


public class DBBibtexManager {

	private static final Log log = LogFactory.getLog(DBBibtexManager.class);
	
	/*
	 * SQL Statements
	 */
	
	private static final String SQL_INSERT_BIBTEX = "INSERT INTO bibtex "
		+ "(title,entrytype,author,editor,year,content_id,journal,volume,chapter,edition,month,bookTitle, "
		+ "howpublished,institution,organization,publisher,address,school,series,bibtexKey,`group`, "
		+ "date,user_name,url,description,annote,note,pages,bKey,number,crossref,misc,bibtexAbstract,type,day,scraperid,rating,privnote,simhash"+Bibtex.SIM_HASH_0 
		+																						           ", simhash"+Bibtex.SIM_HASH_1 
	    +																						           ", simhash"+Bibtex.SIM_HASH_2 
	    +																						           ", simhash"+Bibtex.SIM_HASH_3 + ")"  		                                                              
		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_LOG_BIBTEX = "INSERT INTO log_bibtex "
		+ "(rating, title,entrytype,author,editor,year,content_id,journal,volume,chapter,edition,month,bookTitle, "
		+ "howpublished,institution,organization,publisher,address,school,series,bibtexKey,`group`, "
		+ "date,user_name,url,description,annote,note,pages,bKey,number,crossref,misc,bibtexAbstract,type,day,scraperid,simhash"+Bibtex.SIM_HASH_0 
		+																						           ", simhash"+Bibtex.SIM_HASH_1 
	    +																						           ", simhash"+Bibtex.SIM_HASH_2 
	    +																						           ", simhash"+Bibtex.SIM_HASH_3 + ")"
	    + "SELECT rating, title,entrytype,author,editor,year,content_id,journal,volume,chapter,edition,month,bookTitle, "
		+ "howpublished,institution,organization,publisher,address,school,series,bibtexKey,`group`, "
		+ "date,user_name,url,description,annote,note,pages,bKey,number,crossref,misc,bibtexAbstract,type,day,scraperid,simhash"+Bibtex.SIM_HASH_0 
		+																						           ", simhash"+Bibtex.SIM_HASH_1 
	    +																						           ", simhash"+Bibtex.SIM_HASH_2 
	    +																						           ", simhash"+Bibtex.SIM_HASH_3
	    + " FROM bibtex WHERE content_id = ?";
	private static final String SQL_LOG_BIBTEX_UPDATE = "UPDATE log_bibtex SET new_content_id = ? WHERE content_id = ?";

	
	private static final String SQL_INSERT_HASH     = "INSERT INTO bibhash (hash, type) VALUES (?, ?) ON DUPLICATE KEY UPDATE ctr=ctr+1";
	private static final String SQL_UPDATE_HASH_DEC = "UPDATE bibhash SET ctr=ctr-1 WHERE hash = ? AND type = ?";
	private static final String SQL_SELECT_HASH     = "SELECT simhash"+Bibtex.SIM_HASH_0 + 
	                                                       ", simhash"+Bibtex.SIM_HASH_1 +
	                                                       ", simhash"+Bibtex.SIM_HASH_2 + 
	                                                       ", simhash"+Bibtex.SIM_HASH_3 + " FROM bibtex WHERE content_id = ?"; 
	
	private static final String SQL_UPDATE_DOC       = "UPDATE document             SET content_id = ? WHERE content_id = ?";
	private static final String SQL_UPDATE_COLLECTED = "UPDATE collector            SET content_id = ? WHERE content_id = ?";
	private static final String SQL_UPDATE_EXTENDED  = "UPDATE extended_fields_data SET content_id = ? WHERE content_id = ?";
	private static final String SQL_UPDATE_BIBURL    = "UPDATE bibtexurls           SET content_id = ? WHERE content_id = ?";
	private static final String SQL_DELETE_BIBTEX    = "DELETE FROM bibtex WHERE content_id = ?";
	private static final String SQL_DELETE_DOC       = "DELETE FROM document             WHERE content_id = ?";
	private static final String SQL_DELETE_COLLECTED = "DELETE FROM collector            WHERE content_id = ?";
	private static final String SQL_DELETE_EXTENDED  = "DELETE FROM extended_fields_data WHERE content_id = ?";
	private static final String SQL_DELETE_BIBURL    = "DELETE FROM bibtexurls           WHERE content_id = ?";
	private static final String SQL_CHECK_SPAMMER    = "SELECT spammer FROM user WHERE user_name = ?";
	
	private static final int MAX_WAIT_TIMEOUT = 5000; // in milliseconds
	private static Random generator = new Random();

	private ResultSet rst                             = null;
	private PreparedStatement stmtP_select_bibtex     = null;
	private PreparedStatement stmtP_insert_bibtex     = null;
	private PreparedStatement stmtP_delete_bibtex     = null;
	private PreparedStatement stmtP_log_bibtex        = null;
	private PreparedStatement stmtP_log_bibtex_update = null;
	private PreparedStatement stmtP_update_doc        = null;
	private PreparedStatement stmtP_delete_doc        = null;
	private PreparedStatement stmtP_update_collected  = null;
	private PreparedStatement stmtP_delete_collected  = null;
	private PreparedStatement stmtP_update_extended   = null;
	private PreparedStatement stmtP_delete_extended   = null;
	private PreparedStatement stmtP_update_biburl     = null;
	private PreparedStatement stmtP_delete_biburl     = null;

	
	// statements for hashes
	private PreparedStatement stmtP_insert_hash       = null;
	private PreparedStatement stmtP_update_shash_dec  = null;
	private PreparedStatement stmtP_select_hashes     = null;
	private PreparedStatement stmtP_check_spammer     = null;
	
	// inserts, deletes and logs tags, tas, tagtag and tagtagrelations
	DBTagManager tagmanager = new DBTagManager();
	DBRelationManager relationman = new DBRelationManager();
	// gets content ids for resources
	DBContentManager contentman = new DBContentManager();
	
	public void prepareStatements (Connection conn) throws SQLException {
		/* prepare statements */
		stmtP_insert_bibtex     = conn.prepareStatement(SQL_INSERT_BIBTEX);
		stmtP_delete_bibtex     = conn.prepareStatement(SQL_DELETE_BIBTEX);
		stmtP_update_doc        = conn.prepareStatement(SQL_UPDATE_DOC);
		stmtP_delete_doc        = conn.prepareStatement(SQL_DELETE_DOC);
		stmtP_update_collected  = conn.prepareStatement(SQL_UPDATE_COLLECTED);
		stmtP_delete_collected  = conn.prepareStatement(SQL_DELETE_COLLECTED);
		stmtP_update_extended   = conn.prepareStatement(SQL_UPDATE_EXTENDED);
		stmtP_delete_extended   = conn.prepareStatement(SQL_DELETE_EXTENDED);
		stmtP_update_biburl     = conn.prepareStatement(SQL_UPDATE_BIBURL);
		stmtP_delete_biburl     = conn.prepareStatement(SQL_DELETE_BIBURL);
		stmtP_insert_hash       = conn.prepareStatement(SQL_INSERT_HASH);
		stmtP_update_shash_dec  = conn.prepareStatement(SQL_UPDATE_HASH_DEC);
		stmtP_select_hashes     = conn.prepareStatement(SQL_SELECT_HASH);
		stmtP_log_bibtex        = conn.prepareStatement(SQL_LOG_BIBTEX);
		stmtP_log_bibtex_update = conn.prepareStatement(SQL_LOG_BIBTEX_UPDATE);
		stmtP_check_spammer     = conn.prepareStatement(SQL_CHECK_SPAMMER);

		// prepare Statements for DB*Manager
		contentman.prepareStatementsForBibtex(conn);
		tagmanager.prepareStatements(conn); // prepare statements for tags
		relationman.prepareStatements(conn);
	}
	
	public void closeStatements () {
		// Always make sure result sets and statements are closed,
		// and the connection is returned to the pool
		if (rst                    != null) {try {rst.close();	                 } catch (SQLException e) {	} rst                    = null;}
		if (stmtP_delete_bibtex    != null) {try {stmtP_delete_bibtex.close();	 } catch (SQLException e) {	} stmtP_delete_bibtex    = null;}
		if (stmtP_select_bibtex    != null) {try {stmtP_select_bibtex.close();	 } catch (SQLException e) {	} stmtP_select_bibtex    = null;}
		if (stmtP_insert_bibtex    != null) {try {stmtP_insert_bibtex.close();   } catch (SQLException e) {	} stmtP_insert_bibtex    = null;}
		if (stmtP_update_doc       != null) {try {stmtP_update_doc.close();      } catch (SQLException e) {	} stmtP_update_doc       = null;}
		if (stmtP_delete_collected != null) {try {stmtP_delete_collected.close();} catch (SQLException e) { } stmtP_delete_collected = null;}
		if (stmtP_update_collected != null) {try {stmtP_update_collected.close();} catch (SQLException e) {	} stmtP_update_collected = null;}
		if (stmtP_delete_extended  != null) {try {stmtP_delete_extended.close(); } catch (SQLException e) { } stmtP_delete_extended  = null;}
		if (stmtP_update_extended  != null) {try {stmtP_update_extended.close(); } catch (SQLException e) {	} stmtP_update_extended  = null;}
		if (stmtP_delete_biburl    != null) {try {stmtP_delete_biburl.close();   } catch (SQLException e) { } stmtP_delete_biburl    = null;}
		if (stmtP_update_biburl    != null) {try {stmtP_update_biburl.close();   } catch (SQLException e) {	} stmtP_update_biburl    = null;}
		if (stmtP_delete_doc       != null) {try {stmtP_delete_doc.close();	     } catch (SQLException e) { } stmtP_delete_doc       = null;}
		if (stmtP_select_hashes    != null) {try {stmtP_select_hashes.close();	 } catch (SQLException e) {	} stmtP_select_hashes    = null;}
		if (stmtP_update_shash_dec != null) {try {stmtP_update_shash_dec.close();} catch (SQLException e) {	} stmtP_update_shash_dec = null;}
		if (stmtP_insert_hash      != null) {try {stmtP_insert_hash.close();	 } catch (SQLException e) {	} stmtP_insert_hash      = null;}
		if (stmtP_check_spammer    != null) {try {stmtP_check_spammer.close();   } catch (SQLException e) { } stmtP_check_spammer    = null;}
		tagmanager.closeStatements();
		contentman.closeStatements();
		relationman.closeStatements();
	}
	
	/** Inserts a collection of Bibtex objects into the database.
	 * 
	 * @param publications A list of bibtex objects which may be inserted, depending on parameter overwrite 
	 * and if they already exist. Entries which are not inserted are removed from the collection.
	 * @param duplicatePublications If a collection is given (i.e., this parameter is not null) all entries 
	 * which are not inserted because they already exist (and overwrite = false) are returned in this collection.
	 * @param currUser the user for which we want to insert the entries
	 * @param conn a database connection
	 * @param overwrite if set to <code>true</code> existing entries are overwritten by the given entries. Otherwise not.
	 * @param oldhash if an entry should be moved instead of overwritten, this can be achieved by giving the oldhash for that
	 * entry. this works only with one entry. TODO: it's just a workaround, do this for multiple entries.
	 * @param errors TODO
	 * @return
	 * @throws SQLException
	 * @throws BibtexException
	 */
	public int updateBibtex (Collection<Bibtex> publications, Collection<Bibtex> duplicatePublications, String currUser, Connection conn, boolean overwrite, String oldhash, Map<Bibtex, String> errors) throws SQLException, BibtexException {
		
		
		conn.setAutoCommit(false); // deactivate auto-commit to enable transaction
		
		/* check if current user is a spammer */
		stmtP_check_spammer.setString(1, currUser);
		rst = stmtP_check_spammer.executeQuery();
		boolean isSpammer = rst.next() && rst.getInt("spammer") == constants.SQL_CONST_SPAMMER_TRUE;
	
		
		int bibSuccessCounter = 0; // counter for succesfull bibtex inserts
		int oldcontentid = Bibtex.UNDEFINED_CONTENT_ID;
		boolean success;
		/* *********************************************************************************
		 * INSERT / DELETE
		 * *********************************************************************************/
		
		/* iterate over all complete bibtex objects */
		for (Bibtex bib: publications) {
			
			int wait = 10; // milliseconds!
			success = false;
			
			while (wait < MAX_WAIT_TIMEOUT && !success) {
				
				try {
					/* TODO:
					 * rja, 2006-01-16, I changed user name from currUser to bib.getUser(), because
					 * otherwise group-copy does not work. On the other hand this means, that I can
					 * overwrite existing articles in the group (under which circumstances??)
					 */
					/* *************************************
					 * duplicate checks
					 * *************************************/
					int contentid = contentman.getContentID(bib.getUser(), bib.getHash());
					if (contentid == Bibtex.UNDEFINED_CONTENT_ID) {
						/*
						 * the bibtex entry does NOT exist for that user ---> set toIns
						 */
						bib.setToIns(true);

						// this is for doing a "move" operation, which is only done, if target does not exist
						// check, if old hash is available and if we treat bibtex of currUser
						if (!"".equals(oldhash) && currUser.equals(bib.getUser())) {
							// yes --> do a "move" operation
							bib.setContentID(contentman.getContentID(currUser, oldhash));
							bib.setToDel(true);
						}

					} else { 
						/*
						 * the bibtex entry EXISTS for that user
						 */
						if (overwrite) {
							// overwrite it --> set content id for delete
							bib.setContentID(contentid);
							bib.setToDel(true);
							bib.setToIns(true);
						} else {
							/* put duplicates into warning list */
							if (duplicatePublications != null) duplicatePublications.add(bib);
							/* "remove" bibtex entry so that it is not inserted */
							bib.setToDel(false);
							bib.setToIns(false);
						}
					}
					
					if (bib.isToDel()) {
						/* *************************************
						 * DELETE
						 * *************************************/
						
						oldcontentid = bib.getContentID();

						// get hashes
						stmtP_select_hashes.setInt (1, oldcontentid);
						rst = stmtP_select_hashes.executeQuery();
						if (!rst.next()) {
							/*
							 * TODO: this is not good, since we should immediately proceed to the user with an error message!
							 * The error message doesn't help, too, since the user gets back to the broken entry and tries to
							 * re-enter it again (which will not work, since oldhash just does not exists any longer)
							 * Solution would be: 
							 * - find the entry, the user wants to edit (difficult)
							 * - send him to the home page, together with an error message
							 * - something else
							 * 
							 */
							throw new BibtexException ("Entry not found in table!");
						}

						
						// log Bibtex
						stmtP_log_bibtex.setInt(1, oldcontentid);						
						stmtP_log_bibtex.executeUpdate();
						
						/* decrement hash counter */
						stmtP_update_shash_dec.setString(1, rst.getString("simhash" + Bibtex.SIM_HASH_0));
						stmtP_update_shash_dec.setInt(2, Bibtex.SIM_HASH_0);
						stmtP_update_shash_dec.executeUpdate();
						/* decrement sim hash counter 1 */
						stmtP_update_shash_dec.setString(1, rst.getString("simhash" + Bibtex.SIM_HASH_1));
						stmtP_update_shash_dec.setInt(2, Bibtex.SIM_HASH_1);
						stmtP_update_shash_dec.executeUpdate();
						/* decrement sim hash counter 2 */
						stmtP_update_shash_dec.setString(1, rst.getString("simhash" + Bibtex.SIM_HASH_2));
						stmtP_update_shash_dec.setInt(2, Bibtex.SIM_HASH_2);
						stmtP_update_shash_dec.executeUpdate();
						/* decrement sim hash counter 3 */
						stmtP_update_shash_dec.setString(1, rst.getString("simhash" + Bibtex.SIM_HASH_3));
						stmtP_update_shash_dec.setInt(2, Bibtex.SIM_HASH_3);
						stmtP_update_shash_dec.executeUpdate();
						
						// delete tags for this item
						tagmanager.deleteTags(oldcontentid);
						/* delete bibtex */
						stmtP_delete_bibtex.setInt(1, oldcontentid);
						stmtP_delete_bibtex.executeUpdate();
						
					} // delete
					if (bib.isToIns()) {
						/* *************************************
						 * INSERT
						 * *************************************/
						
						/* create unique content_id */
						bib.setContentID(contentman.getNewContentID());
						
						/* insert into bibtex table */
						insertBibIntoDB(bib, isSpammer);
						// insert tags, tas, tagtag, tagtagrelations into database
						tagmanager.insertTags(bib);
						relationman.insertRelations(bib.getTag(), bib.getUser());
						
						// count successful inserted bibtex entries and add them to a list
						bibSuccessCounter++;
															
						/* update documents and collector table, if item has been moved */
						if (bib.isToDel()) {

							/* save content_id to log_bibtex */
							stmtP_log_bibtex_update.setInt(1, bib.getContentID());
							stmtP_log_bibtex_update.setInt(2, oldcontentid);
							stmtP_log_bibtex_update.executeUpdate();
							
							// Update content_id to linked document, if id has changed
							stmtP_update_doc.setInt(1, bib.getContentID());// set new contentid
							stmtP_update_doc.setInt(2, oldcontentid);
							stmtP_update_doc.executeUpdate();
							
							// Update content_id in collector table, if id has changed
							stmtP_update_collected.setInt(1, bib.getContentID()); // set new contentid
							stmtP_update_collected.setInt(2, oldcontentid);
							stmtP_update_collected.executeUpdate();
							
							// update content_id in extended_fields table, if id has changed
							stmtP_update_extended.setInt(1, bib.getContentID()); // set new contentid
							stmtP_update_extended.setInt(2, oldcontentid);
							stmtP_update_extended.executeUpdate();

							// update content_id in bibtexurl, if id has changed
							stmtP_update_biburl.setInt(1, bib.getContentID()); // set new contentid
							stmtP_update_biburl.setInt(2, oldcontentid);
							stmtP_update_biburl.executeUpdate();
						}

						
					} // insert
					
					conn.commit(); // commit transaction
					success = true;
				} catch (DataTruncation e) {
					conn.rollback();
					success = true;
					if (errors != null) {
						errors.put(bib, e.getMessage());
					}
				} catch (SQLException e) {
					conn.rollback(); // rollback all queries, if transaction fails
					wait = wait * 2;
					log.fatal("Could not insert bibtex objects, will wait at most " + wait + " milliseconds. Error was: " + e);
					try {
						Thread.sleep(generator.nextInt(wait));
					} catch (InterruptedException i) {
					}
				} // catch SQLException (wait ...)
				
			} // while loop wait
			if (!success && wait >= MAX_WAIT_TIMEOUT) {
				log.fatal("Could not insert bibtex objects, waiting too long! bibtex is: " + bib);
				throw new SQLException("retry/wait timeout");
			}
			
		} // while loop bibtex
		return bibSuccessCounter;
	}
	
	
	
	private void insertBibIntoDB(Bibtex bib, Boolean isSpammer) throws SQLException {
		
		/*
		 * take care of spammers
		 */
		bib.setGroupid(ModifyGroupId.getGroupId(bib.getGroupid(), isSpammer));
		
		stmtP_insert_bibtex.setString(1, bib.getTitle());
		stmtP_insert_bibtex.setString(2, bib.getEntrytype());
		stmtP_insert_bibtex.setString(3, bib.getAuthor());
		stmtP_insert_bibtex.setString(4, bib.getEditor());
		stmtP_insert_bibtex.setString(5, bib.getYear());
		
		stmtP_insert_bibtex.setInt(6, bib.getContentID());
		stmtP_insert_bibtex.setString(7, bib.getJournal());
		stmtP_insert_bibtex.setString(8, bib.getVolume());
		stmtP_insert_bibtex.setString(9, bib.getChapter());
		stmtP_insert_bibtex.setString(10, bib.getEdition());
		stmtP_insert_bibtex.setString(11, bib.getMonth());
		stmtP_insert_bibtex.setString(12, bib.getBooktitle());
		stmtP_insert_bibtex.setString(13, bib.getHowpublished());
		stmtP_insert_bibtex.setString(14, bib.getInstitution());
		stmtP_insert_bibtex.setString(15, bib.getOrganization());
		stmtP_insert_bibtex.setString(16, bib.getPublisher());
		stmtP_insert_bibtex.setString(17, bib.getAddress());
		stmtP_insert_bibtex.setString(18, bib.getSchool());
		stmtP_insert_bibtex.setString(19, bib.getSeries());
		stmtP_insert_bibtex.setString(20, bib.getBibtexKey());
		stmtP_insert_bibtex.setInt(21, bib.getGroupid());
		stmtP_insert_bibtex.setTimestamp(22, new Timestamp(bib.getDate().getTime()));
		stmtP_insert_bibtex.setString(23, bib.getUser());
		stmtP_insert_bibtex.setString(24, bib.getUrl());
		stmtP_insert_bibtex.setString(25, bib.getDescription());
		stmtP_insert_bibtex.setString(26, bib.getAnnote());
		stmtP_insert_bibtex.setString(27, bib.getNote());
		stmtP_insert_bibtex.setString(28, bib.getPages());
		stmtP_insert_bibtex.setString(29, bib.getKey());
		stmtP_insert_bibtex.setString(30, bib.getNumber());
		stmtP_insert_bibtex.setString(31, bib.getCrossref());
		stmtP_insert_bibtex.setString(32, bib.getMisc());
		stmtP_insert_bibtex.setString(33, bib.getBibtexAbstract());
		stmtP_insert_bibtex.setString(34, bib.getType());
		stmtP_insert_bibtex.setString(35, bib.getDay());
		stmtP_insert_bibtex.setInt(36, bib.getScraperid());
		stmtP_insert_bibtex.setInt(37, bib.getRating());
		stmtP_insert_bibtex.setString(38, bib.getPrivnote());
		stmtP_insert_bibtex.setString(39, bib.getSimHash(Bibtex.SIM_HASH_0));
		stmtP_insert_bibtex.setString(40, bib.getSimHash(Bibtex.SIM_HASH_1));
		stmtP_insert_bibtex.setString(41, bib.getSimHash(Bibtex.SIM_HASH_2));
		stmtP_insert_bibtex.setString(42, bib.getSimHash(Bibtex.SIM_HASH_3));
		stmtP_insert_bibtex.executeUpdate();
		
		/* increment similarity hash counter 0 */
		stmtP_insert_hash.setString(1, bib.getSimHash(Bibtex.SIM_HASH_0));
		stmtP_insert_hash.setInt(2, Bibtex.SIM_HASH_0);
		stmtP_insert_hash.executeUpdate();

		/* increment similarity hash counter 1 */
		stmtP_insert_hash.setString(1, bib.getSimHash(Bibtex.SIM_HASH_1));
		stmtP_insert_hash.setInt(2, Bibtex.SIM_HASH_1);
		stmtP_insert_hash.executeUpdate();

		/* increment similarity hash counter 2 */
		stmtP_insert_hash.setString(1, bib.getSimHash(Bibtex.SIM_HASH_2));
		stmtP_insert_hash.setInt(2, Bibtex.SIM_HASH_2);
		stmtP_insert_hash.executeUpdate();

		/* increment similarity hash counter 3 */
		stmtP_insert_hash.setString(1, bib.getSimHash(Bibtex.SIM_HASH_3));
		stmtP_insert_hash.setInt(2, Bibtex.SIM_HASH_3);
		stmtP_insert_hash.executeUpdate();

	}
	

	public void deleteBibtex(Connection conn, String currUser, String hash) throws SQLException {
		conn.setAutoCommit(false);
		
		// get content_id and delete it 
		deleteBibtex(conn, contentman.getContentID(currUser, hash));
	}

	public void deleteBibtex(Connection conn, int contentId) throws SQLException {
		if (contentId != Bibtex.UNDEFINED_CONTENT_ID) {
			conn.setAutoCommit(false);
			
			// log Bibtex
			stmtP_log_bibtex.setInt(1, contentId);						
			stmtP_log_bibtex.executeUpdate();
			
			// delete tags for this item
			tagmanager.deleteTags(contentId);
			
			// get hashes
			stmtP_select_hashes.setInt (1, contentId);
			rst = stmtP_select_hashes.executeQuery();
			
			if (!rst.next()) {
				throw new SQLException ("could not find hash in bibtex table");
			}
			
			/* decrement hash counter */
			stmtP_update_shash_dec.setString(1, rst.getString("simhash" + Bibtex.SIM_HASH_0));
			stmtP_update_shash_dec.setInt(2, Bibtex.SIM_HASH_0);
			stmtP_update_shash_dec.executeUpdate();
			/* decrement sim hash counter 1 */
			stmtP_update_shash_dec.setString(1, rst.getString("simhash" + Bibtex.SIM_HASH_1));
			stmtP_update_shash_dec.setInt(2, Bibtex.SIM_HASH_1);
			stmtP_update_shash_dec.executeUpdate();
			/* decrement sim hash counter 2 */
			stmtP_update_shash_dec.setString(1, rst.getString("simhash" + Bibtex.SIM_HASH_2));
			stmtP_update_shash_dec.setInt(2, Bibtex.SIM_HASH_2);
			stmtP_update_shash_dec.executeUpdate();
			/* decrement sim hash counter 3 */
			stmtP_update_shash_dec.setString(1, rst.getString("simhash" + Bibtex.SIM_HASH_3));
			stmtP_update_shash_dec.setInt(2, Bibtex.SIM_HASH_3);
			stmtP_update_shash_dec.executeUpdate();
			
			// delete bibtex
			stmtP_delete_bibtex.setInt(1, contentId);
			stmtP_delete_bibtex.executeUpdate();
			
			// delete link to related document
			stmtP_delete_doc.setInt(1, contentId);
			stmtP_delete_doc.executeUpdate();
			
			// delete id in collector table
			stmtP_delete_collected.setInt(1, contentId);
			stmtP_delete_collected.executeUpdate();
			
			// delete id in extended fields table
			stmtP_delete_extended.setInt(1, contentId);
			stmtP_delete_extended.executeUpdate();

			// delete id in bibtexturl table
			stmtP_delete_biburl.setInt(1, contentId);
			stmtP_delete_biburl.executeUpdate();

			conn.commit();
		}
	}
	
}
