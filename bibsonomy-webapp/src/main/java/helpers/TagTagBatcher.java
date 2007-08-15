package helpers;

import java.sql.*;

/**
 * Does the batch jobs from the tagtag batch table. 
 * 
 * @author rja
 *
 */
public class TagTagBatcher {
	
	public static int MAX_ROWS = 1000; // how many content_ids to work per batch?
	public static int TO_INC = 1;  // row is to increment
	public static int TO_DEC = 0;  // row is to decement
	public static int DO_INC = 1;  // increment (also start value for counter)
	public static int DO_DEC = -1; // decrement (also start value for counter)
	
	/* give database name and password as environment variables DB and DB_PASS, DB_USER, DB_HOST */
	public static void main (String [] args){
		/* Establish all connections, result sets and statements which we will need */
		Connection conn                     = null;
		ResultSet rst                       = null;
		PreparedStatement stm_set_active    = null;
		PreparedStatement stm_get_active    = null;
		PreparedStatement stm_del_active    = null;
		PreparedStatement stm_tagtag_insert = null;
		PreparedStatement stm_tagtag_update = null;
		PreparedStatement stm_insert_temp   = null;
		PreparedStatement stm_delete_temp   = null;
		PreparedStatement stm_select_temp   = null;
		
		
		try {
			/* connect to database */
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			conn = DriverManager.getConnection ("jdbc:mysql://" + 
										        System.getenv("DB_HOST") + ":" + 
										        System.getenv("DB_PORT") + "/" + 
										        System.getenv("DB") + 
					                            "?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8", 
					                            System.getenv("DB_USER"), 
					                            System.getenv("DB_PASS"));
			conn.setAutoCommit(false); // turn autocommit off
			System.out.println ("Database connection to " + System.getenv("DB_HOST") + " established.");
			
			/* prepare statements */
			stm_set_active    = conn.prepareStatement("UPDATE tagtag_batch SET isactive = 1 LIMIT " + MAX_ROWS);                           // set batch job to active
			stm_get_active    = conn.prepareStatement("SELECT tags, toinc, id FROM tagtag_batch WHERE isactive = 1 ORDER BY id LIMIT 1");  // get active batch job
			stm_del_active    = conn.prepareStatement("DELETE FROM tagtag_batch WHERE id = ?");                                            // remove batch job
			stm_insert_temp   = conn.prepareStatement("INSERT INTO tagtag_temp (t1,t2,incdec) VALUES (?,?,?)");                            // insert values into tagtag_temp table
			stm_delete_temp   = conn.prepareStatement("DELETE FROM tagtag_temp WHERE id = ?");                                             // delete from tagtag_temp
			stm_select_temp   = conn.prepareStatement("SELECT t1,t2,incdec,id FROM tagtag_temp LIMIT 1");                                  // get one row from tagtag_temp
			stm_tagtag_insert = conn.prepareStatement("INSERT INTO tagtag (t1, t2, ctr) VALUES (?, ?, ?)");                                // insert tagtag
			stm_tagtag_update = conn.prepareStatement("UPDATE tagtag SET ctr = ctr + ? WHERE t1 = ? AND t2 = ?");                          // update tagtag
			
			try {
				// set all rows which are now in the batch table to active
				System.out.println("Marking " + MAX_ROWS + " for update.");
				stm_set_active.executeUpdate();   
				conn.commit();
				boolean moreActiveRows = true;
				
				while (moreActiveRows) {
					moreActiveRows = false;
					
					// get one job to batch 
					rst = stm_get_active.executeQuery();
					
					if (rst.next()) {
						moreActiveRows = true; // we got one row ... so try to get one more the next time
						
						// get data we need from result set
						int id        = rst.getInt("id");
						String tags   = rst.getString("tags");
						boolean toInc = rst.getBoolean("toinc");
						/*
						 * TODO: removed this
						 * This should not be neccessary, since InnoDB has row level locking and BibSonomy 
						 * only ADDS rows to the table, never has to access existing rows. Therefore deadlocks
						 * should be impossible
						 *
						 * conn.commit();  // we got all information we need --> release batch table
						 */
						
						System.out.println("Batching " + toInc + " : " + tags + ".");
						
						// decide, if to inrecement or to decrement
						int increment = DO_INC;  // increment counter
						if (!toInc) {
							increment = DO_DEC; // decrement counter
						}
						
						// extract tags
					    String[] tag = tags.split("\\s");
					    for (int t1=0; t1<tag.length; t1++) {
						    for (int t2=0; t2<tag.length; t2++) {
								if (!tag[t1].equals(tag[t2])) {
									// insert into tagtag_temp table
									stm_insert_temp.setString(1, tag[t1]);
									stm_insert_temp.setString(2, tag[t2]);
									stm_insert_temp.setInt(3, increment);
									stm_insert_temp.executeUpdate();
								}
							}
						}
						
						// delete job from batch table
						stm_del_active.setInt(1, id);
						if (stm_del_active.executeUpdate() != 1) {
							throw new SQLException ("deleted more or less than one job from batch table!");
						}
						/*
						 * this commit is NOT neccessary, since we write to the tagtag_TEMP table which 
						 * is NOT accessed by BibSonomy 
						 */
						//conn.commit();
						
						// copy tagtag from tagtag_temp to tagtag
						boolean moreTempRows = true;
						while (moreTempRows) {
							moreTempRows = false;
							rst = stm_select_temp.executeQuery();
							while (rst.next()) {
								moreTempRows = true;
								// get a row from the tagtag_temp table
								String tag1 = rst.getString("t1");
								String tag2 = rst.getString("t2");
								increment   = rst.getInt("incdec");
								id          = rst.getInt("id");
								// do update in tagtag table
								stm_tagtag_update.setInt(1, increment);
								stm_tagtag_update.setString(2, tag1);
								stm_tagtag_update.setString(3, tag2);
								if (stm_tagtag_update.executeUpdate() == 0) {
									// no rows updated --> insert new
									stm_tagtag_insert.setString(1, tag1);
									stm_tagtag_insert.setString(2, tag2);
									stm_tagtag_insert.setInt(3, increment);
									if (stm_tagtag_insert.executeUpdate() == 0) {
										throw new SQLException ("could not insert row into tagtag table");
									}
								}
								// remove row from temp table
								stm_delete_temp.setInt(1, id);
								stm_delete_temp.executeUpdate();
								// finally: commit transaction
								conn.commit();
								Thread.sleep(0);
							}
						}
					}
				}
				

			} catch (SQLException e) {
				conn.rollback();
				System.out.println("Rolling back transaction because of " + e);
			}
			System.out.println("Finished.");
		} catch (SQLException e) {
			System.out.println (e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println (e);
			e.printStackTrace();			
		} catch (InstantiationException e) {
			System.out.println (e);
			e.printStackTrace();					
		} catch (ClassNotFoundException e) {
			System.out.println (e);
			e.printStackTrace();		
		} catch (InterruptedException e) {
			System.out.println (e);
			e.printStackTrace();
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rst != null)  {try {rst.close();} catch (SQLException e) {} rst = null;}
			if (conn != null) {try {conn.close();} catch (SQLException e) {}conn = null;}
			if (stm_set_active    != null) {try {stm_set_active.close();}    catch (SQLException e) {} stm_set_active    = null;}
			if (stm_get_active    != null) {try {stm_get_active.close();}    catch (SQLException e) {} stm_get_active    = null;}
			if (stm_del_active    != null) {try {stm_del_active.close();}    catch (SQLException e) {} stm_del_active    = null;}
			if (stm_tagtag_insert != null) {try {stm_tagtag_insert.close();} catch (SQLException e) {} stm_tagtag_insert = null;}
			if (stm_tagtag_update != null) {try {stm_tagtag_update.close();} catch (SQLException e) {} stm_tagtag_update = null;}
			if (stm_select_temp   != null) {try {stm_select_temp.close();}   catch (SQLException e) {} stm_select_temp   = null;}
			if (stm_delete_temp   != null) {try {stm_delete_temp.close();}   catch (SQLException e) {} stm_delete_temp   = null;}
			if (stm_insert_temp   != null) {try {stm_insert_temp.close();}   catch (SQLException e) {} stm_insert_temp   = null;}
		}
	}
}
