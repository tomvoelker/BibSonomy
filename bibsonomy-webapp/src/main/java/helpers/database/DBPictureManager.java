package helpers.database;

import java.sql.*;
import beans.PictureBean;

public class DBPictureManager extends DBManager {

	/**
	 * Extracts all requiered information to be shown be geotagging_entry.jsp
	 * and stores them into the provided bean
	 * 
	 * @param bean to be filled
	 */
	public static void getBookmarkContent (PictureBean bean) {
		
		boolean firstRow = true;
		StringBuffer tags = new StringBuffer();
		String tag;
		
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
				// prepare Statement
				c.stmt = c.conn.prepareStatement("SELECT * FROM bookmark b, document d, urls u, tas t WHERE b.content_id = d.content_id AND b.book_url_hash = u.book_url_hash AND b.content_id = t.content_id AND d.hash = ? AND d.user_name = ?");
				c.stmt.setString(1, bean.getBookmark().getDocHash());
				c.stmt.setString(2, bean.getBookmark().getUser());
				c.rst = c.stmt.executeQuery();
				while (c.rst.next()) {
					// fill bean
					//first row fills all values
					if (firstRow){
						bean.getBookmark().setTitle(c.rst.getString("book_description"));
						bean.getBookmark().setExtended(c.rst.getString("book_extended"));
						bean.getBookmark().setUrl(c.rst.getString("book_url"));
						//add tags to buffer and check if its lat or lon
						tag = c.rst.getString("tag_name");
						tags.append(tag);
						if (tag.startsWith("lat:")){
							bean.setLat(tag.split(":")[1]);
							bean.setLatD(tag.substring(tag.length()-1));
						}
						else if (tag.startsWith("lon:")){
							bean.setLon(tag.split(":")[1]);
							bean.setLonD(tag.substring(tag.length()-1));
						}
						
						firstRow = false;
					}
					//following rows add tags but no values
					else{
						//add tags to buffer and check if its lat or lon
						tag = c.rst.getString("tag_name");
						tags.append(" " + tag);
						if (tag.startsWith("lat:")){
							bean.setLat(tag.split(":")[1]);
							bean.setLatD(tag.substring(tag.length()-1));
						}
						else if (tag.startsWith("lon:")){
							bean.setLon(tag.split(":")[1]);
							bean.setLonD(tag.substring(tag.length()-1));
						}
					}
					
				}
				//all tags written to buffer - save tags string
				bean.getBookmark().setTags(tags.toString());
			}
		} catch (SQLException e) {
			System.out.println("DBSM: " + e);
		} finally {
			c.close(); // close database connection
		}
		
	}
}
