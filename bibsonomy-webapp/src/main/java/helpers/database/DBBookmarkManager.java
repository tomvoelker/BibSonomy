package helpers.database;

import helpers.ModifyGroupId;
import helpers.constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.Bookmark;

/**
 * This class allows to insert, update or delete several bookmarks at once.
 */
public class DBBookmarkManager {
	
	private static final Log log = LogFactory.getLog(DBBookmarkManager.class);
	
	private static final String SQL_INSERT_BOOKMARK = "INSERT INTO bookmark " 
		+ "(content_id,book_url_hash,book_description,book_extended,`group`,date,user_name, rating) "
		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_LOG_BOOKMARK = "INSERT INTO log_bookmark "
		+ "(rating, content_id,book_url_hash,book_description,book_extended,`group`,date,user_name) "
		+ "SELECT rating, content_id,book_url_hash,book_description,book_extended,`group`,date,user_name FROM bookmark where content_id = ?";
	private static final String SQL_LOG_BOOKMARK_UPDATE = "UPDATE log_bookmark SET new_content_id = ? "
		+ "WHERE content_id = ?";
	
	private PreparedStatement stmtP_insert_url          = null;
	private PreparedStatement stmtP_insert_bookmark     = null;
	private PreparedStatement stmtP_update_url_dec      = null;
	private PreparedStatement stmtP_update_url_inc      = null;
	private PreparedStatement stmtP_delete_bookmark     = null;
	private PreparedStatement stmtP_log_bookmark 	    = null;
	private PreparedStatement stmtP_log_bookmark_update = null;
	private PreparedStatement stmtP_select_group        = null;
	private PreparedStatement stmtP_check_spammer       = null;
	private PreparedStatement stmtP_insert_document     = null;
	private PreparedStatement stmtP_update_document     = null;
	private PreparedStatement stmtP_delete_document     = null;
	private ResultSet         rst                       = null;
	private DBTagManager      tagManager                = null;
	private DBRelationManager relationManager           = null;
	public  DBContentManager  contentIdManager          = null; // TODO: making this public is a dirty hack for BookmarkHandler         
	
	/**
	 * Maximal time to wait between retrying to insert a bookmark
	 * which could not be inserted. 
	 */
	private static final int MAX_WAIT_TIMEOUT = 5000; // milliseconds! 
	private static Random generator = new Random();
	
	/**
	 * Prepares all statements which are needed to update/insert/delete bookmarks. Must be called before
	 * calling @link #updateBookmarks(Collection, String, Connection, boolean, boolean).
	 *   
	 * @param conn The database connection to use.
	 * @throws SQLException
	 */
	public void prepareStatements (Connection conn) throws SQLException {
		stmtP_insert_url          = conn.prepareStatement("INSERT INTO urls (book_url,book_url_hash) VALUES (?, ?)");
		stmtP_update_url_dec      = conn.prepareStatement("UPDATE urls SET book_url_ctr=book_url_ctr-1 WHERE book_url_hash = ?");
		stmtP_update_url_inc      = conn.prepareStatement("UPDATE urls SET book_url_ctr=book_url_ctr+1 WHERE book_url_hash = ?");
		stmtP_delete_bookmark     = conn.prepareStatement("DELETE FROM bookmark WHERE content_id = ?");
		stmtP_insert_bookmark     = conn.prepareStatement(SQL_INSERT_BOOKMARK);
		stmtP_log_bookmark        = conn.prepareStatement(SQL_LOG_BOOKMARK);
		stmtP_log_bookmark_update = conn.prepareStatement(SQL_LOG_BOOKMARK_UPDATE);
		stmtP_check_spammer       = conn.prepareStatement("SELECT spammer FROM user WHERE user_name = ?");
		stmtP_insert_document     = conn.prepareStatement("INSERT INTO document (hash, content_id, name, user_name, date) VALUES (?, ?, ?, ?, ?)");
		stmtP_delete_document     = conn.prepareStatement("DELETE FROM document WHERE content_id = ? AND user_name = ?");
		stmtP_update_document     = conn.prepareStatement("UPDATE document SET content_id = ? WHERE content_id = ?");
		
		tagManager      = new DBTagManager();
		relationManager = new DBRelationManager();
		
		tagManager.prepareStatements(conn);
		relationManager.prepareStatements(conn);

		/* 
		 * TODO: this is a dirty hack for BookmarkHandler so that we can use 
		 * the ContentManager already before preparing all the statements here
		 */ 
		if (contentIdManager == null) {
			contentIdManager = new DBContentManager();
			contentIdManager.prepareStatementsForBookmark(conn);
		}
	}
	
	/**
	 * Closes all statements and resultsets. This method has to be called in the 
	 * <code>finally</code> block of the exception catching block which surrounds 
	 * @link #updateBookmarks(Collection, String, Connection, boolean, boolean). 
	 * 
	 */
	public void closeStatements () {
		if (stmtP_insert_bookmark     != null) {try {stmtP_insert_bookmark.close();    } catch(SQLException e){}stmtP_insert_bookmark    =null;}
		if (stmtP_insert_url          != null) {try {stmtP_insert_url.close();	     } catch(SQLException e){}stmtP_insert_url         =null;}
		if (stmtP_delete_bookmark     != null) {try {stmtP_delete_bookmark.close();    } catch(SQLException e){}stmtP_delete_bookmark    =null;}
		if (stmtP_log_bookmark        != null) {try {stmtP_log_bookmark.close();       } catch(SQLException e){}stmtP_log_bookmark       =null;}
		if (stmtP_log_bookmark_update != null) {try {stmtP_log_bookmark_update.close();} catch(SQLException e){}stmtP_log_bookmark_update=null;}
		if (stmtP_select_group        != null) {try {stmtP_select_group.close();	     } catch(SQLException e){}stmtP_select_group       =null;}
		if (stmtP_update_url_inc      != null) {try {stmtP_update_url_inc.close();	 } catch(SQLException e){}stmtP_update_url_inc     =null;}
		if (stmtP_update_url_dec      != null) {try {stmtP_update_url_dec.close();	 } catch(SQLException e){}stmtP_update_url_dec     =null;}
		if (stmtP_check_spammer       != null) {try {stmtP_check_spammer.close();      } catch(SQLException e){}stmtP_check_spammer      =null;}
		if (stmtP_insert_document     != null) {try {stmtP_insert_document.close();    } catch(SQLException e){}stmtP_insert_document    =null;}
		if (stmtP_update_document     != null) {try {stmtP_update_document.close();    } catch(SQLException e){}stmtP_update_document    =null;}
		if (stmtP_delete_document     != null) {try {stmtP_delete_document.close();    } catch(SQLException e){}stmtP_delete_document    =null;}
		if (rst                       != null) {try {rst.close();                      } catch(SQLException e){}rst                      =null;}
		if (tagManager                != null) tagManager.closeStatements();
		if (relationManager           != null) relationManager.closeStatements();
		if (contentIdManager          != null) contentIdManager.closeStatements();
	}
	
	/**
	 * This methods inserts, updates and deletes bookmarks.
	 * 
	 * TODO: it might be useful, to return the collection of bookmarks together with the
	 * content ids they got assigned.
	 * 
	 * To delete a bookmark (given its hash): set the hash of the bookmark and overwrite to <code>true</code>.
	 * 
	 * @param bookmarks a collection of bookmark objects, to insert or update. 
	 *        Depending on the value of the toIns and toDel attributes of each 
	 *        bookmark the bookmark is deleted, inserted or both. 
	 * @param currUser the current user - to check for existing bookmarks of 
	 *        that user which we may have to alter 
	 * @param conn a database connection
	 * @param overwrite if <code>true</code> existing bookmarks are overwritten
	 * @param change if <code>true</code> a bookmark with an old url hash may be 
	 *        changed instead of copied
	 *        
	 * @return <code>true</code> if user is a spammer, otherwise false (TODO: this 
	 *         is a hack to not repeat the spam checking query in BookmarkHandler)
	 * 
	 * @throws SQLException If it was not possible, to insert/delete one of the bookmarks.
	 * 
	 * 
	 */
	public boolean updateBookmarks (Collection<Bookmark> bookmarks, String currUser, Connection conn, boolean overwrite, boolean change) throws SQLException {

		/*
		 * deactivate auto-commit to enable transaction
		 */
		conn.setAutoCommit(false);    

		/* *************** check if current user is a spammer ******************* */
		stmtP_check_spammer.setString(1, currUser);
		rst = stmtP_check_spammer.executeQuery();
		boolean spammer = rst.next() && rst.getInt("spammer") == constants.SQL_CONST_SPAMMER_TRUE;
	
		
		boolean success;
		int wait;        // waiting time between several tries when trying to insert one bookmark 

		/* *************** iterate over all bookmark objects ******************** */
		for (Bookmark bookmark:bookmarks) {
			wait     = 10; // milliseconds!
			success  = false;
			
			while (!success && wait < MAX_WAIT_TIMEOUT) {
				try {
					
					/*
					 * first we check, if the bookmark URL already exists for this user
					 */
					/* get old content_id, if it exists */
					int oldcontentid = contentIdManager.getContentID(bookmark.getUser(), bookmark.getHash());
					
				
						/*wenn bookmark noch nicht drinne*/
					if (oldcontentid == Bookmark.UNDEFINED_CONTENT_ID && !bookmark.isToDel()) {
						bookmark.setToIns(true);
						if (change && bookmark.getUser().equals(currUser)) {
							
							
							
							/*
							 * (do this only, if currUser = book.user, otherwise overwriting group entries is possible)
							 * 
							 * it may be the case, that the user wants to change a bookmarks URL, this can be done 
							 * in two ways: making a copy of it (this is done, when coming from bookmarklet with
							 * existing bookmark and then changing the URL) or moving it (when user presses "edit"
							 * button and then changes the URL). 
							 * To delete the old bookmark, we have to extract its content id
							 */
							// the new bookmark URL does NOT exist --> check, if the old one exists
							String oldurlhash = Bookmark.hash(bookmark.getOldurl());
							oldcontentid = contentIdManager.getContentID (bookmark.getUser(), oldurlhash);
							if (oldcontentid != Bookmark.UNDEFINED_CONTENT_ID) {
								/* old bookmark exists, but NOT called by bookmarklet --> change old bookmark */
								/**
								 * für urls table
								 */
								bookmark.setOldHash(oldurlhash);
								bookmark.setContentID(oldcontentid);
								bookmark.setToDel(true);
							}
						}
					} else {
						/* the bookmarks URL already exists for this user */
						if (overwrite) {
							/* we shall overwrite it */
							bookmark.setOldHash(bookmark.getHash());
							bookmark.setContentID(oldcontentid);
							bookmark.setToDel(true);
							bookmark.setToIns(true);
						} else { 
							/* we do nothing and ignore it */
							bookmark.setContentID(oldcontentid);
							bookmark.setToIns(false);
						}

					}
					
					Collection<String> oldTags = null;
					if (bookmark.isToDel()) {
						/* ****************************************************************
						 *  DELETE 
						 * **************************************************************** */

						oldcontentid = bookmark.getContentID();
							
						if (oldcontentid != Bookmark.UNDEFINED_CONTENT_ID) {
							// delete tags for this bookmark
							oldTags = tagManager.deleteTags(oldcontentid);
							/* decrement URL counter */
							stmtP_update_url_dec.setString(1, bookmark.getOldHash());	stmtP_update_url_dec.executeUpdate();
							// log Bookmark
							stmtP_log_bookmark.setInt(1, oldcontentid);					stmtP_log_bookmark.executeUpdate();
							// delete Bookmark
							stmtP_delete_bookmark.setInt(1, oldcontentid);				stmtP_delete_bookmark.executeUpdate();
						}
					} // DELETE
					
					if (bookmark.isToIns()) {
						/* ****************************************************************
						 *  INSERT 
						 * **************************************************************** */
						
						/*
						 * spammer: modify group id 
						 */
						if (spammer) {
							bookmark.setGroupid(ModifyGroupId.getGroupId(bookmark.getGroupid(), true));
						}
						
						/* create unique content_id from table id_generator */
						bookmark.setContentID(contentIdManager.getNewContentID());
						
						if(bookmark.isToDel()){
							/* save content_id to logged_bookmark */
							stmtP_log_bookmark_update.setInt(1, bookmark.getContentID());
							stmtP_log_bookmark_update.setInt(2, oldcontentid);
							stmtP_log_bookmark_update.executeUpdate();
						}
						/* insert into bookmark table */		
						stmtP_insert_bookmark.setInt(1, bookmark.getContentID()); 
						stmtP_insert_bookmark.setString(2, bookmark.getHash());
						stmtP_insert_bookmark.setString(3, bookmark.getTitle());
						stmtP_insert_bookmark.setString(4, bookmark.getExtended());
						stmtP_insert_bookmark.setInt(5, bookmark.getGroupid());
						stmtP_insert_bookmark.setTimestamp(6, new Timestamp(bookmark.getDate().getTime()));
						stmtP_insert_bookmark.setString(7, bookmark.getUser());
						stmtP_insert_bookmark.setInt(8, bookmark.getRating());
						stmtP_insert_bookmark.executeUpdate();
						
						/* increment URL counter */
						/* 
						 * TODO: this could be improved with MySQL feature INSERT INTO ... ON DUPLICATE UPDATE ...
						 * wenn der hash doppelt dann zähle hoch 
						 */ 
						stmtP_update_url_inc.setString(1, bookmark.getHash());
						if (stmtP_update_url_inc.executeUpdate() == 0) {
							/* nothing updated, so insert new url into table */
							stmtP_insert_url.setString(1, bookmark.getUrl());
							stmtP_insert_url.setString(2, bookmark.getHash());
							stmtP_insert_url.executeUpdate();
						}
						// insert tags, tas, tagtag, tagtagrelations
						tagManager.insertTags(bookmark);
						relationManager.insertRelations(bookmark.getTag(), bookmark.getUser());
					} // INSERT
					
					/* ************************************************
					 * update document table, if neccessary
					 * ************************************************/
					if (bookmark.isToIns()) {
						if (bookmark.isToDel()) {
							/*
							 * insert + delete: update
							 */
							stmtP_update_document.setInt(1, bookmark.getContentID());
							stmtP_update_document.setInt(2, oldcontentid);
							stmtP_update_document.executeUpdate();
							/**
							 * geotagging
							 */
						} else if (bookmark.getDocHash() != null) {
							/*
							 * only insert: insert (hash, content_id, name, user_name, date) 
							 */
							stmtP_insert_document.setString(1, bookmark.getDocHash());
							stmtP_insert_document.setInt(2, bookmark.getContentID());
							stmtP_insert_document.setString(3, bookmark.getDocName());
							stmtP_insert_document.setString(4, currUser);
							stmtP_insert_document.setTimestamp(5, new Timestamp(bookmark.getDate().getTime()));
							stmtP_insert_document.executeUpdate();
						}
					} else if (bookmark.isToDel()) {
						/*
						 * only delete: delete
						 */
						stmtP_delete_document.setInt(1, oldcontentid);
						stmtP_delete_document.setString(2, currUser);
						stmtP_delete_document.executeUpdate();
					}
					
					
					/* 
					 * Commit successful transaction 
					 */
					conn.commit();
//					try {
//						TagVectorUpdater.doUpdate(oldTags,bookmark);
//					} catch (Exception e) {
//						log.fatal("TagVectorUpdater had problems: " + e);
//					}
					success = true;
					
				} catch(SQLException e) {
					/*
					 * roll back transaction and wait for retry
					 */
					conn.rollback();
					wait = wait * 2;
					log.fatal("Could not insert bookmark objects, will wait at most " + wait + " milliseconds. Error was: " + e);
					try {
						Thread.sleep(generator.nextInt(wait));
					} catch (InterruptedException i) {
					}
				} // catch SQLException (wait ...)    
			} // while loop
			
			if (!success && wait >= MAX_WAIT_TIMEOUT) {
				throw new SQLException("retry/wait timeout");
			}
			
		} // get every bookmark
		return spammer;
	}	
}