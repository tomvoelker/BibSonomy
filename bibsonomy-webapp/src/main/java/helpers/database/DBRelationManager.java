package helpers.database;

import helpers.constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;

import resources.Tag;
import resources.TagRelation;


/**
 * Inserts and deletes relatoins.
 *
 */
public class DBRelationManager extends DBManager {
	
	private static final String SQL_UPDATE_TAGREL_ID = "UPDATE ids SET value=value+1 WHERE name = " + constants.SQL_IDS_TAGREL_ID;
	private static final String SQL_SELECT_TAGREL_ID = "SELECT value FROM ids WHERE name = " + constants.SQL_IDS_TAGREL_ID;
	/* NOTE: the "IGNORE" means, that there is no Exception thrown, if the relation already exists. Nontheless
	 * the relation is NOT inserted! (this is the desired behaviour - it removes the need to first check, if 
	 * the relation already exists)
	 * see also: http://dev.mysql.com/doc/refman/5.0/en/insert.html
	 */
	private static final String SQL_INSERT_TAGREL    = "INSERT IGNORE INTO tagtagrelations (relationID,lower,upper,date_of_create,user_name,lower_lcase, upper_lcase) VALUES (?,?,?,?,?,?,?)";
	private static final String SQL_DELETE_TAGREL    = "DELETE FROM tagtagrelations WHERE user_name=? AND upper=? AND lower=?;";
	private static final String SQL_LOG_TAGREL       = "INSERT INTO log_tagtagrelations "
													 + "(relationID, lower, upper, date_of_create, user_name) "
													 + "SELECT relationID, lower, upper, date_of_create, user_name FROM tagtagrelations WHERE lower=? AND upper=? AND user_name=?";
	
	
	private PreparedStatement stmtP_insert_tagrel    = null;
	private PreparedStatement stmtP_update_tagrelid  = null;
	private PreparedStatement stmtP_select_tagrelid  = null;
	private PreparedStatement stmtP_delete_tagrel    = null;
	private PreparedStatement stmtP_log_tagrel	     = null;

	private ResultSet rst   = null;
	
	/**
	 * Prepares Statements with the help of the given connection.
	 * 
	 * @param conn database connection to use
	 * @throws SQLException
	 */
	public void prepareStatements(Connection conn) throws SQLException {
		stmtP_update_tagrelid = conn.prepareStatement(SQL_UPDATE_TAGREL_ID);
		stmtP_select_tagrelid = conn.prepareStatement(SQL_SELECT_TAGREL_ID);
		stmtP_insert_tagrel   = conn.prepareStatement(SQL_INSERT_TAGREL);
		stmtP_delete_tagrel   = conn.prepareStatement(SQL_DELETE_TAGREL);
		stmtP_log_tagrel	  = conn.prepareStatement(SQL_LOG_TAGREL);
		
	}
		
	/**
	 * Closes all statements and result sets.
	 * 
	 */
	public void closeStatements() {
		if(stmtP_insert_tagrel   !=null) {try {stmtP_insert_tagrel.close();  } catch(SQLException e) {} stmtP_insert_tagrel   = null;}
		if(stmtP_update_tagrelid !=null) {try {stmtP_update_tagrelid.close();} catch(SQLException e) {} stmtP_update_tagrelid = null;}
		if(stmtP_select_tagrelid !=null) {try {stmtP_select_tagrelid.close();} catch(SQLException e) {} stmtP_select_tagrelid = null;}
		if(stmtP_delete_tagrel   !=null) {try {stmtP_delete_tagrel.close();  } catch(SQLException e) {} stmtP_delete_tagrel   = null;}
		if(stmtP_log_tagrel      !=null) {try {stmtP_log_tagrel.close();     } catch(SQLException e) {} stmtP_log_tagrel      = null;}
		if(rst  				 !=null) {try {rst.close(); 				 } catch(SQLException e) {} rst 				  = null;}
	}

	
	/**
	 * Inserts the relations of the tag object into the database.
	 * 
	 * @param tags the tag object containing the relations
	 * @param user the user for whom we insert the tags
	 * @throws SQLException
	 */
	public void insertRelations (Tag tags, String user) throws SQLException {
		/*
		 * iterate over every relation we want to insert
		 */
		for (TagRelation relation : tags.getTagrelations()){
			/* update relation_id */
			if (stmtP_update_tagrelid.executeUpdate() == 1) {
				/* get new relation id */
				rst = stmtP_select_tagrelid.executeQuery();
				if (rst.next()) {
					/* insert relation */
					stmtP_insert_tagrel.setInt(1, rst.getInt("value"));
					stmtP_insert_tagrel.setString(2, relation.getLower());
					stmtP_insert_tagrel.setString(3, relation.getUpper());
					stmtP_insert_tagrel.setTimestamp(4, new Timestamp((new Date()).getTime()) );
					stmtP_insert_tagrel.setString(5, user);
					stmtP_insert_tagrel.setString(6, relation.getLower().toLowerCase());
					stmtP_insert_tagrel.setString(7, relation.getUpper().toLowerCase());
					stmtP_insert_tagrel.executeUpdate();
				}
			}
		}
	}

	
	/** 
	 * Deletes the relations of the tag object from the database
	 * 
	 * @param tags the tag object containing the relations
	 * @param currUser the user for whom we insert the tags
	 * @throws SQLException
	 */
	public void deleteRelations (Tag tags, String currUser) throws SQLException {
		/*
		 * iterate over every relation we want to delete 
		 */
		for (TagRelation relation : tags.getTagrelations()) {
			/* log relation */
			stmtP_log_tagrel.setString(1, relation.getLower());
			stmtP_log_tagrel.setString(2, relation.getUpper());
			stmtP_log_tagrel.setString(3, currUser);
			stmtP_log_tagrel.executeUpdate();
			
			/* delete relation */
			stmtP_delete_tagrel.setString(1, relation.getLower());
			stmtP_delete_tagrel.setString(2, relation.getUpper());
			stmtP_delete_tagrel.setString(3, currUser);
			stmtP_delete_tagrel.executeUpdate();
		}
	}
	
	
	/** Builds relations for tag object from strings of upper and lower tags. For every 
	 * tag l in lower and every tag u in upper, adds the relation l-&gt;u to the tag object.
	 * 
	 * @param lower a string of lower tags separated by whitespace 
	 * @param upper a string of upper tags separated by whitespace
	 *  
	 * @return a tag object containing the relations
	 * @throws SQLException
	 */
	public Tag buildRelations (String lower, String upper) throws SQLException {
		Tag tags = new Tag();
		/*
		 * return empty tag if lower or upper is null
		 */
		if (lower != null && upper != null) {
			/*
			 * initialize tokenizer and list for subtags
			 */
			StringTokenizer token        = new StringTokenizer(lower);
			LinkedList<String> lowerList = new LinkedList<String>();

			/*
			 * iterate over all lower tags and put them into a list
			 */
			while(token.hasMoreTokens()){
				lowerList.add(token.nextToken());
			}

			/*
			 * iterate over all upper tags and add the relations to the object
			 */
			token = new StringTokenizer(upper);
			while (token.hasMoreTokens()) {
				/*
				 * iterate over all lower tags
				 */
				String upperTag = token.nextToken();
				for(String lowerTag : lowerList) {
					/*
					 * add new relation to tag object
					 */
					tags.addTagRelation(lowerTag, upperTag);
				}
			}
		}
		return tags;
	}
	
}