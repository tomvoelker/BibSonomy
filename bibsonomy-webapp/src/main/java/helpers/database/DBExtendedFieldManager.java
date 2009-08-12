package helpers.database;

import helpers.constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.Bibtex;
import resources.ExtendedFieldMap;
import resources.ExtendedFieldMapComparator;
import resources.Resource;

public class DBExtendedFieldManager extends DBManager {

	private static final Log log = LogFactory.getLog(DBExtendedFieldManager.class);

	/**
	 * Gets all extended fields which exist for this group. The method only gets the metadata 
	 * for the fields, not the data itself. 
	 *  
	 * @param group group name as string for which to get extended fields
	 * @param user user name to check, if this user is in the given group
	 * @return
	 */
	public SortedSet<ExtendedFieldMap> getExtendedFieldsMap (String group, String user) {
		TreeSet<ExtendedFieldMap> set = new TreeSet<ExtendedFieldMap>(new ExtendedFieldMapComparator());
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				c.stmt = c.conn.prepareStatement("SELECT * FROM extended_fields_map m, groupids gid, groups g" +
						                         "  WHERE gid.group = m.group " +
						                         "    AND gid.group = g.group " +
						                         "    AND gid.group_name = ?" +
						                         "    AND g.user_name = ?");
				c.stmt.setString(1, group);
				c.stmt.setString(2, user);
				c.rst = c.stmt.executeQuery();
				while (c.rst.next()) {
					ExtendedFieldMap map = new ExtendedFieldMap();
					map.setGroup(c.rst.getInt("group"));
					map.setKey_id(c.rst.getInt("key_id"));
					map.setKey(c.rst.getString("key"));
					map.setDescription(c.rst.getString("description"));
					map.setOrder(c.rst.getInt("order"));
					set.add(map);
				}
			}
		} catch (SQLException e) {
			log.fatal("Could not get extended fields for group " + group + " and user " + user + ": "+ e.getMessage());
		} finally {
			c.close(); // close database connection
		}
		return set;
	}
	
	/** Inserts descriptions of extended fields into extended_fields_map table.
	 * @param group The group for which these extended fields should be inserted
	 * @param extended_fields_maps a set of extended_fields_maps which describe extended fields.
	 * @return
	 */
	public boolean createExtendedFieldsMap (String group, Set<ExtendedFieldMap> extended_fields_maps) {
		DBContext c = new DBContext();
		DBIdManager idmanager = new DBIdManager();		
		try {
			if (c.init()) { // initialize database
				idmanager.prepareStatements(c.conn, constants.SQL_IDS_EXTENDED_FIELDS);
				
				// get id for group
				c.stmt = c.conn.prepareStatement("SELECT `group` FROM groupids WHERE group_name = ?");
				c.stmt.setString(1, group);
				c.rst = c.stmt.executeQuery();
				if (c.rst.next()) {
					int groupid = c.rst.getInt("group");
					// prepare Statement
					c.stmt = c.conn.prepareStatement("INSERT INTO extended_fields_map (`group`, key_id, `key`, description, `order`) VALUES (?,?,?,?,?)");
					
					c.conn.setAutoCommit(false);
					for (ExtendedFieldMap map:extended_fields_maps) {
						c.stmt.setInt(1, groupid);
						c.stmt.setInt(2, idmanager.getNewId());
						c.stmt.setString(3, map.getKey());
						c.stmt.setString(4, map.getDescription());
						c.stmt.setInt(5, map.getOrder());
						c.stmt.executeUpdate();
					}
					c.conn.commit();
					return true;
				}
			}
		} catch (SQLException e) {
			try {
				c.conn.rollback();
			} catch (SQLException ex) {
				log.fatal("Could not roll back transaction for group " + group + ": " + ex.getMessage());
			}
			log.fatal("Could not set extended fields for group " + group + ": "+ e.getMessage());
		} finally {
			c.close(); // close database connection
			idmanager.closeStatements();
		}
		return false;
	}
	
	/** Gets the users own posts from the basket page together with the data for the extended fields of the given group.
	 * @param group
	 * @param user
	 * @return
	 */
	public LinkedList<Bibtex> getExtendedFields (String group, String user) {
		LinkedList<Bibtex> list = new LinkedList<Bibtex>();
		DBContext c = new DBContext();
		try {
			if (c.init()) { 
				/*
				 * TODO: FORCE INDEX (key_id) added because of buggy mysql which gives empty results, 
				 * although query should return results (which it does, when some columns are removed 
				 * in column selection). with FORCE INDEX this does not (so often?) happen ...
				 * MySQL sucks!
				 */
				String query = "SELECT " +  getBibtexSelect ("b") + ", m.key, e.value " +
								                                 "  FROM (" +
								                                 "    collector c JOIN bibtex b    ON c.content_id = b.content_id  " +
								                                 "                                   AND c.user_name = ? " +
								                                 "                                   AND b.user_name = ? " +
								                                 "                JOIN groups g    ON b.user_name = g.user_name " +
								                                 "                JOIN groupids gi ON g.group = gi.group " +
								                                 "                                   AND gi.group_name = ?" +
								                                 "        )" +
								                                 "  LEFT JOIN (" +
								                                 "    extended_fields_data e  FORCE INDEX (key_id) JOIN extended_fields_map m ON e.key_id = m.key_id " +
								                                 "                           JOIN groupids gid          ON m.group = gid.group " +
								                                 "                                                        AND gid.group_name = ?" +
								                                 "        ) " +
								                                 "  ON b.content_id = e.content_id" +
								                                 "  ORDER BY c.date DESC";
				/*
				 * get users own publication entries from collector table
				 * 
				 * this is done with a LEFT JOIN of which both tables are a JOIN of other tables
				 * 
				 * LEFT side of the join:
				 *   get users own publications from collector table ONLY if user is in given group!
				 * RIGHT side of the join:
				 *   get all metadata for this group
				 * 
				 */			

				c.stmt = c.conn.prepareStatement(query);
				c.stmt.setString(1, user);
				c.stmt.setString(2, user);
				c.stmt.setString(3, group);
				c.stmt.setString(4, group);
				c.rst = c.stmt.executeQuery();
				/* 
				 * build bibtex objects
				 */
				int contentid = Resource.UNDEFINED_CONTENT_ID;
				Bibtex bibtex = null;
				while (c.rst.next()) {
					if (contentid != c.rst.getInt("content_id")) {
						/* 
						 * content_id changed --> new resource!
						 */
						contentid = c.rst.getInt("content_id");
						bibtex = fillBibtex(c.rst);
						list.add(bibtex);
					}
					/*
					 * add extended fields
					 */
					bibtex.addExtended_fields(c.rst.getString("key"), c.rst.getString("value"));
				}
			}
		} catch (SQLException e) {
			log.fatal("Could not get resources with extended fields for group " + group + " and user " + user + ": "+ e.getMessage());
		} finally {
			c.close(); // close database connection
		}
		return list;
	}
	
	/** Returns only the picked entries (i.e., which are shown on the /basket page) of the 
	 * user which ARE NOT OWNED by the user.
	 * 
	 * @param user the user name for which to get the entries
	 * @return a list of bibtex entries
	 */
	public LinkedList<Bibtex> getPickedEntries (String user) {
		LinkedList<Bibtex> list = new LinkedList<Bibtex>();
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				/*
				 * get users own publication entries from collector table
				 */
				c.stmt = c.conn.prepareStatement("SELECT " + getBibtexSelect ("b") + 
						                         "  FROM collector c " +
						                         "    JOIN bibtex b ON c.content_id = b.content_id " +
						                         "  WHERE c.user_name = ? " +
						                         "    AND b.user_name != ?" +
						                         "  ORDER BY c.date DESC");
				c.stmt.setString(1, user);
				c.stmt.setString(2, user);
				c.rst = c.stmt.executeQuery();
				/* 
				 * build bibtex objects
				 */
				while (c.rst.next()) {
 					list.add(fillBibtex(c.rst));
				}
			}
		} catch (SQLException e) {
			log.fatal("Could not get users non-own picked entries for user " + user + ": "+ e.getMessage());
		} finally {
			c.close(); // close database connection
		}
		return list;
	}

	
	/**
	 * return appropriate select query string for different tables
	 */
	private static String getBibtexSelect (String table) {	
		String[] columns = {"address","annote","booktitle","chapter","crossref","edition",
				"howpublished","institution","journal","bkey","month","note","number","organization",
				"pages","publisher","school","series","type","volume","day","url", 
				"content_id", "description", "bibtexKey", "misc", "bibtexAbstract", "user_name", "date",
				"title","author", "editor", "year", "entrytype", "rating"};
		StringBuffer select = new StringBuffer();
		for (String col:columns) {
			select.append(table + "." + col + ",");
		}
		select.deleteCharAt(select.length()-1);
		return select.toString();		
	}
	
	/** Fills a Bibtex object from a given result set
	 * @param rst ResultSet containing which points to a row describing a bibtex entry.
	 * @return the filled bibtex object
	 * 
	 * @throws SQLException
	 */
	private Bibtex fillBibtex(ResultSet rst) throws SQLException {
		/*
		 * fill bibtex object with mandatory fields to compute hash
		 * TODO: this has to be updated, when mandatory fields for hash are added!
		 */
		Bibtex bibtex = new Bibtex();
		bibtex.setUrl(rst.getString("url"));
		bibtex.setJournal(rst.getString("journal"));
		bibtex.setBooktitle(rst.getString("booktitle"));
		bibtex.setDescription(rst.getString("description"));
		bibtex.setTitle(rst.getString("title"));
		bibtex.setAuthor(rst.getString("author"));
		bibtex.setEditor(rst.getString("editor"));
		bibtex.setYear(rst.getString("year"));
		bibtex.setEntrytype(rst.getString("entrytype"));
		bibtex.setVolume(rst.getString("volume"));
		bibtex.setNumber(rst.getString("number"));
		
		bibtex.setContentID(rst.getInt("content_id"));
		bibtex.setUser(rst.getString("user_name"));
		bibtex.setDate(rst.getTimestamp("date"));
		return bibtex;
	}
	
	/** This methods inserts the metadata for the given resources into the extended_fields_data table.
	 * NOTE that this method does not explicitly check, that the user owns the resources or that he 
	 * belongs to the group. 
	 * This is implicitly done by checking for every resource, if it is contained in currentresources.
	 * currentresources should be gotten by the method @link #getExtendedFields(String, String) which 
	 * checks, that the user is in the given group.
	 *  
	 * @param extendedfields a set which contains extended_fields metadata, as given by the 
	 * method @link #getExtendedFieldsMap(String, String)
	 * @param newresources a map containing the bibtex objects with the metadata to add. It is important,
	 * that every objects holds its hash in the field oldhash. This allows to extract the correct content
	 * it from the database (or in this case from the param currentresources)
	 * @param currentresources a list of the users own bibtex objects together with their metadata for the 
	 * specific group. this should be given by a call to the method @link #getExtendedFields(String, String)
	 * @return <code>true</code> if everything went well
	 */
	public boolean setExtendedFields (SortedSet<ExtendedFieldMap> extendedfields, Map<Integer,Bibtex> newresources, LinkedList<Bibtex> currentresources) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				/*
				 * map resources by oldhash
				 */
				HashMap<String,Bibtex> newresources_oldhash = new HashMap<String,Bibtex>();
				for (Bibtex bib:newresources.values()) {
					newresources_oldhash.put(bib.getOldHash(), bib);
				}
				/*
				 * iterate over resources as they're in the database
				 */
				for (Bibtex currbib:currentresources) {
					if (newresources_oldhash.containsKey(currbib.getHash())) {
						/*
						 * exists --> set content id 
						 */
						Bibtex newbib = newresources_oldhash.get(currbib.getHash());
						newbib.setContentID(currbib.getContentID());
						/*
						 * --> compare extended fields by iterating over all possible extended fields
						 */
						for (ExtendedFieldMap mapping:extendedfields) {
							String key = mapping.getKey();
							HashMap<String, String> newext = newbib.getExtended_fields();
							if (newext.containsKey(key)) {
								/*
								 * key included in new resource
								 */
								HashMap<String, String> currext = currbib.getExtended_fields();
								String newvalue = newext.get(key);
								if (currext.containsKey(key)) {
									/*
									 * old entry containes key - did it change?
									 */
								
									if (newvalue.equals(currext.get(key))) {
										/*
										 * value did not change --> remove it 
										 */
										newext.remove(key);
									} else {
										currext.put(key, newvalue);
									}
								} else {
									/*
									 * old entry does not contain key - is new value NULL?
									 */
									if (newvalue == null || "".equals(newvalue.trim())) {
										/*
										 * new entry is empty --> ignore it
										 */
										newext.remove(key);
									} else {
										/*
										 * update old objects
										 */
										currext.put(key, newvalue);
									}
								}
							}
						}
					}
				}
				
				/*
				 * put set into map such that we can access keys by their name and get their key_id
				 */
				HashMap<String, Integer> extendedfields_map = new HashMap<String, Integer>();
				for (ExtendedFieldMap e:extendedfields) {
					extendedfields_map.put(e.getKey(), e.getKey_id());
				}
				Timestamp date = new Timestamp (new Date().getTime());
				/*
				 * update extended fields data
				 */
				c.stmt = c.conn.prepareStatement("INSERT INTO extended_fields_data (`key_id`, `value`, `content_id`, `date_of_create`) VALUES (?,?,?,?)" +
						                         " ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)");
				for (Bibtex bib:newresources.values()) {
					int contentid = bib.getContentID();
					if (contentid != Resource.UNDEFINED_CONTENT_ID) {
						HashMap<String, String> bib_ef = bib.getExtended_fields();
						for (String key:bib_ef.keySet()) {
							c.stmt.setInt(1, extendedfields_map.get(key));
							c.stmt.setString(2, bib_ef.get(key));
							c.stmt.setInt(3, contentid);
							c.stmt.setTimestamp(4, date);
							c.stmt.executeUpdate();					
						}
					}
				}
				return true;
			}
		} catch (SQLException e) {
			log.fatal("Could not set extended fields of resources : " + e.getMessage());
		} finally {
			c.close(); // close database connection
		}
		return false;
	}
	
}
