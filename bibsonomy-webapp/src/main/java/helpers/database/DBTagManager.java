package helpers.database;

import helpers.constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import resources.Resource;

@Deprecated
public class DBTagManager {
	
	/*
	 * TODO:
	 * how many tagtag-combinations should we try to insert at once into the DB? 
	 * Here you can set the maximal number of tags. Remember: tagtag-combinations
	 * grow squared to the number of tags!
	 * 
	 * if we have more than that many tags, we batch the insertion of tagtag-combis.
	 */
	private static final int MAX_TAGS_TO_INSERT = 10;
		
	private static final String SQL_INSERT_TAGTAG 	   = "INSERT INTO tagtag (t1, t2, ctr) VALUES (?,?,?)";
	private static final String SQL_UPDATE_TAGTAG_INC  = "UPDATE tagtag SET ctr=ctr+1 WHERE t1=? AND t2=?";
	private static final String SQL_UPDATE_TAGTAG_DEC  = "UPDATE tagtag SET ctr=ctr-1 WHERE t1=? AND t2=?";
	
	private static final String SQL_UPDATE_TAG_INC     = "INSERT INTO tags (tag_name) VALUES (?) ON DUPLICATE KEY UPDATE tag_ctr=tag_ctr+1";
	private static final String SQL_UPDATE_TAG_DEC     = "UPDATE tags SET tag_ctr=tag_ctr-1 WHERE tag_name = ?";
	
	private static final String SQL_GETNEW_TAS_ID      = "SELECT value FROM ids WHERE name = " + constants.SQL_IDS_TAS_ID;
	private static final String SQL_UPDATE_TAS_ID      = "UPDATE ids SET value=value+1 WHERE name = " + constants.SQL_IDS_TAS_ID;

	private static final String SQL_SELECT_TAS         = "SELECT tag_name FROM tas WHERE content_id = ?";
	private static final String SQL_INSERT_TAS 	 	   = "INSERT INTO tas (tas_id,tag_name,tag_lower,content_id,content_type,user_name,date,`group`) VALUES (?,?,?,?,?,?,?,?)";
	private static final String SQL_DELETE_TAS         = "DELETE FROM tas WHERE content_id = ?";	
	private static final String SQL_LOG_TAS            = "INSERT INTO log_tas"
		                                                 + "(tas_id,tag_name,content_id,content_type,date,change_date)"
		                                                 + "SELECT tas_id,tag_name,content_id,content_type,date,change_date FROM tas WHERE content_id = ?";
	
	private static final String SQL_INSERT_TAGTAGBATCH = "INSERT INTO tagtag_batch (content_id, tags, toinc) VALUES (?,?,?)";
		
	private PreparedStatement stmtP_insert_tagtag          = null;
	private PreparedStatement stmtP_update_tagtag_dec      = null;
	private PreparedStatement stmtP_update_tagtag_inc      = null;
	
	private PreparedStatement stmtP_update_tag_dec         = null;
	private PreparedStatement stmtP_update_tag_inc         = null;

	private PreparedStatement stmtP_update_tasid           = null;
	private PreparedStatement stmtP_getnew_tasid           = null;
	
	private PreparedStatement stmtP_select_tas             = null;
	private PreparedStatement stmtP_insert_tas             = null;
	private PreparedStatement stmtP_log_tas                = null;
	private PreparedStatement stmtP_delete_tas             = null;
	
	private PreparedStatement stmtP_insert_tagtagbatch     = null;
	
	private ResultSet rst = null;
	
	/* prepares statements */
	public void prepareStatements (Connection conn) throws SQLException {
		stmtP_insert_tagtag          = conn.prepareStatement(SQL_INSERT_TAGTAG);
		stmtP_update_tagtag_dec      = conn.prepareStatement(SQL_UPDATE_TAGTAG_DEC);
		stmtP_update_tagtag_inc      = conn.prepareStatement(SQL_UPDATE_TAGTAG_INC);
		
		stmtP_update_tag_dec         = conn.prepareStatement(SQL_UPDATE_TAG_DEC);
		stmtP_update_tag_inc         = conn.prepareStatement(SQL_UPDATE_TAG_INC);

		stmtP_update_tasid           = conn.prepareStatement(SQL_UPDATE_TAS_ID);
		stmtP_getnew_tasid           = conn.prepareStatement(SQL_GETNEW_TAS_ID);
		
		stmtP_select_tas             = conn.prepareStatement(SQL_SELECT_TAS);
		stmtP_insert_tas             = conn.prepareStatement(SQL_INSERT_TAS);
		stmtP_log_tas                = conn.prepareStatement(SQL_LOG_TAS);
		stmtP_delete_tas             = conn.prepareStatement(SQL_DELETE_TAS);
		
		stmtP_insert_tagtagbatch     = conn.prepareStatement(SQL_INSERT_TAGTAGBATCH);
	}
	
	/* close all statements and the resultset */
	public void closeStatements () {
		if(stmtP_update_tasid     !=null){try{stmtP_update_tasid.close();	  }catch(SQLException e){}stmtP_update_tasid     =null;}
		if(stmtP_getnew_tasid     !=null){try{stmtP_getnew_tasid.close();	  }catch(SQLException e){}stmtP_getnew_tasid     =null;}
		if(stmtP_select_tas       !=null){try{stmtP_select_tas.close();	      }catch(SQLException e){}stmtP_select_tas       =null;}
		if(stmtP_insert_tas       !=null){try{stmtP_insert_tas.close();	      }catch(SQLException e){}stmtP_insert_tas       =null;}
		if(stmtP_insert_tagtagbatch !=null){try{stmtP_insert_tagtagbatch.close(); }catch(SQLException e){}stmtP_insert_tagtagbatch =null;}
		if(stmtP_insert_tagtag    !=null){try{stmtP_insert_tagtag.close();    }catch(SQLException e){}stmtP_insert_tagtag    =null;}
		if(stmtP_delete_tas       !=null){try{stmtP_delete_tas.close();	      }catch(SQLException e){}stmtP_delete_tas       =null;}
		if(stmtP_log_tas          !=null){try{stmtP_log_tas.close();	      }catch(SQLException e){}stmtP_log_tas          =null;}
		if(stmtP_update_tagtag_inc!=null){try{stmtP_update_tagtag_inc.close();}catch(SQLException e){}stmtP_update_tagtag_inc=null;}
		if(stmtP_update_tag_inc   !=null){try{stmtP_update_tag_inc.close();   }catch(SQLException e){}stmtP_update_tag_inc   =null;}
		if(stmtP_update_tagtag_dec!=null){try{stmtP_update_tagtag_dec.close();}catch(SQLException e){}stmtP_update_tagtag_dec=null;}
		if(stmtP_update_tag_dec   !=null){try{stmtP_update_tag_dec.close();   }catch(SQLException e){}stmtP_update_tag_dec   =null;}
		if(rst                    !=null){try{rst.close();                    }catch(SQLException e){}rst                    =null;}
	}
	
	public Set<String> deleteTags (final int oldcontentid) throws SQLException {
		// get tags for this content_id
		stmtP_select_tas.setInt(1, oldcontentid);
		rst = stmtP_select_tas.executeQuery();
		// add these tags to list and decrease counter in tag table
		HashSet<String> tagSet = new HashSet<String>();
		while (rst.next()) {
			String tag = rst.getString(1);
			tagSet.add(tag);
			// decrease counter in tag table
			stmtP_update_tag_dec.setString(1, tag);
			stmtP_update_tag_dec.executeUpdate();
		}							
		// decrease counter in tagtag table
		if (tagSet.size() > MAX_TAGS_TO_INSERT) {
			// too much tags: batch the job
			/* a note regarding tag batch processing:
			 * the batch table has four columns:
			 * content_id  tags  toinc  isactive  
			 * - the batch processor first sets the "isactive" column of a row to TRUE (1) 
			 *   and then inserts all tags into the tagtag table, afterwards it deletes the
			 *   row from the batch table
			 *   IMPORTANT: getting rows and then setting them to active has to be done in 
			 *   a transaction, otherwise they could get removed in between
			 *   IMPORTANT: read further to end of this note!
			 */
			// schedule job for decrement
			stmtP_insert_tagtagbatch.setInt(1, oldcontentid);
			stmtP_insert_tagtagbatch.setString(2, tagsToString(tagSet));
			stmtP_insert_tagtagbatch.setBoolean(3, false);
			stmtP_insert_tagtagbatch.executeUpdate();
		} else {
			// few tags: do it here ...
			Iterator<String> it1, it2;
			String tag1, tag2;
			it1 = tagSet.iterator();
			while (it1.hasNext()) {
				tag1 = it1.next();
				it2  = tagSet.iterator();
				while (it2.hasNext()) {
					tag2 = it2.next();
					if (! tag1.equals(tag2)) {
						// decrease counter in tagtag table
						stmtP_update_tagtag_dec.setString(1, tag1);
						stmtP_update_tagtag_dec.setString(2, tag2);
						stmtP_update_tagtag_dec.executeUpdate();
					}
				}
			}
		}
		// log all tas related to this bookmark 
		stmtP_log_tas.setInt(1, oldcontentid);							stmtP_log_tas.executeUpdate();	
		
		// delete all tas related to this bookmark
		stmtP_delete_tas.setInt(1, oldcontentid);						stmtP_delete_tas.executeUpdate();
		return tagSet;
	}
	
	public void insertTags (Resource r) throws SQLException{
		Iterator<String> it1, it2;
		String lower, upper;
		int tasId;
		HashMap<String,Integer> tasIDs = new HashMap<String,Integer>();
		Set<String>allTags = r.getTags();
		if (allTags.size() > MAX_TAGS_TO_INSERT) {
			/*
			 * do it in a batch job
			 */
			stmtP_insert_tagtagbatch.setInt(1, r.getContentID());
			stmtP_insert_tagtagbatch.setString(2, tagsToString(allTags)); // build tagstring from tag set - there should be also "for:tags" included
			stmtP_insert_tagtagbatch.setBoolean(3, true);                     // true = to increment
			stmtP_insert_tagtagbatch.executeUpdate();
			it1 = allTags.iterator();
			while (it1.hasNext()) {                                           // insert tas and increment tags table
				lower = it1.next();
				tasId = insertTas(lower, r);
				insertTag(lower);
				// remember tasid for tagtagrelations
				tasIDs.put(lower, new Integer(tasId));
			}
		} else {
			/*
			 * do it here
			 */
			it1 = allTags.iterator();
			while (it1.hasNext()) {
				lower = it1.next();
				tasId = insertTas(lower, r); 
				insertTag(lower);
				// remember tasid for tagtagrelations
				tasIDs.put(lower, new Integer(tasId));
				/* update tagtag table */
				it2 = allTags.iterator();
				while (it2.hasNext()) {
					upper = it2.next();
					/* different tags */
					if (! lower.equals(upper)) {
						insertTagTag(lower, upper);
					}
				}
			}
		}
	}
	
	
	/**
	 * Increases the tagtag counter for the given tag combination. If this combination does not exist,
	 * inserts the tagtag combination into the tagtag table.
	 * 
	 * @param tag1 the first tag
	 * @param tag2 the second tag
	 * @throws SQLException
	 */
	private void insertTagTag(String tag1, String tag2) throws SQLException {
		stmtP_update_tagtag_inc.setString(1, tag1);
		stmtP_update_tagtag_inc.setString(2, tag2);
		if (stmtP_update_tagtag_inc.executeUpdate() == 0) {
			/* nothing updated, so insert new tagtag combination into table */
			stmtP_insert_tagtag.setString(1, tag1);
			stmtP_insert_tagtag.setString(2, tag2);
			stmtP_insert_tagtag.setInt(3, 1);
			stmtP_insert_tagtag.executeUpdate();
		}
	}
	
	/**
	 * Increases the tag counter in the tag table for the given tag. If this tag does not exist
	 * inside the tag table, inserts it with count 1.
	 * @param tag the tag to be updated/inserted
	 * @throws SQLException
	 */
	private void insertTag(String tag) throws SQLException {
		stmtP_update_tag_inc.setString(1, tag);
		stmtP_update_tag_inc.executeUpdate();
	}
	

	/**
	 * Inserts a tas (tag assignment) into the tas table. Before inserting the tas it
	 * gets a new tas id from the id table.
	 * 
	 * @param tag the tag which should be inserted for the given resource
	 * @param resource the resource which contains user name, date, ...
	 * @return the tas_id
	 * @throws SQLException
	 */
	private int insertTas (String tag, Resource resource) throws SQLException {
		ResultSet rst;
		/* get tas_id for this tas */
		stmtP_update_tasid.executeUpdate();
		rst = stmtP_getnew_tasid.executeQuery();
		/* check, if we got an id */
		if (rst.next()) {
			int tas_id = rst.getInt("value");
			stmtP_insert_tas.setInt(1, tas_id);
			stmtP_insert_tas.setString(2, tag);
			stmtP_insert_tas.setString(3, tag.toLowerCase()); // TODO: does Java toLowerCase() do the same as SQL LOWER()?
			stmtP_insert_tas.setInt(4, resource.getContentID());
			stmtP_insert_tas.setInt(5, resource.getContentType());
			stmtP_insert_tas.setString(6, resource.getUser());
			stmtP_insert_tas.setTimestamp(7, new Timestamp(resource.getDate().getTime()));
			stmtP_insert_tas.setInt(8, resource.getGroupid());
			stmtP_insert_tas.executeUpdate();
			return tas_id;
		} else {
			throw new SQLException ("Could not get an tas id from id table");
		}
	}
	
	/**
	 * Builds a string from a list of tags. The tags are separated by space in the string.
	 * 
	 * @param tags a collection of tags
	 * @return the string of white space separated tags
	 */
	private String tagsToString (Collection<String> tags) {
		StringBuffer s = new StringBuffer();
		Iterator<String> it = tags.iterator();
		while (it.hasNext()) {
			s.append(it.next()).append(" ");
		}
		return s.toString().trim();
	}
	
}