package helpers.database;

import java.sql.SQLException;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.FolkrankItem;
import resources.SplittedTags;

/**
 * Gets the folkrank ranking results for every dimension of items
 */

public class DBFolkrankManager extends DBManager {	

	private static final Log log = LogFactory.getLog(DBFolkrankManager.class);
	
	/**
	 * calculates ranking results with preference for the requested item for tags, users or resources using folkrank algorithm 
	 * @param requItem The requested item or itemlist
	 * @param srcDim 
	 * 		the dimension of the requested item
	 * 		dimension <code>0</code>: tag
	 * 		dimension <code>1</code>: user
	 * 		dimension <code>2</code>: resource	  			
	 * @param destDim
	 * 		the dimension of resultset items which will be ranked
	 * 		dimension <code>0</code>: tag
	 * 		dimension <code>1</code>: user
	 * 		dimension <code>2</code>: resource
	 * @param destItemType
	 * 		type of resource in order the resultset items are resources (dimesnion 2)
	 * 		<code>0</code>: no resource
	 * 		<code>1</code>: Bookmark resource
	 * 		<code>2</code>: Bibtex resource	  
	 * @param limit max size of resultset
	 * @return a list of ranked items 
	 */
	public static LinkedList<FolkrankItem> getRankingSet(String requItem, int srcDim, int destDim, int destItemType, int limit) {		
		DBContext c = new DBContext();		
		LinkedList<FolkrankItem> items = new LinkedList<FolkrankItem>();			

		try {
			if (c.init()) {

				SplittedTags requItemList = new SplittedTags(requItem,"",true);
				String subquery = requItemList.getFolkrankQuery();

				String query = "SELECT w.item, ROUND(SUM(weight),5) AS weight "
					+ "		  	FROM rankings r "
					+ "		  		JOIN weights w USING (id) "
					+ "			WHERE (" +  subquery + ") AND r.dim = ? "
					+ "				AND w.dim = ? AND w.itemtype = ? "
					+ "			GROUP BY w.item "
					+ "			ORDER BY 2 DESC "
					+ "			LIMIT ?";

				c.stmt = c.conn.prepareStatement(query);

				int paramPos = 1;
				for (String item: requItemList) {
					c.stmt.setString(paramPos++, item);				
				}
				c.stmt.setInt(paramPos++, srcDim);
				c.stmt.setInt(paramPos++, destDim);
				c.stmt.setInt(paramPos++, destItemType);
				c.stmt.setInt(paramPos++, limit);	

				c.rst = c.stmt.executeQuery();

				while(c.rst.next()) {				
					String item 	= c.rst.getString("item");
					float weight 	= c.rst.getFloat("weight");

					if (!requItemList.contains(item)) {
						items.add(new FolkrankItem(item,weight));
					}								
				}
			}
		} catch (SQLException e) {
			log.fatal("Could not get ranking for item " + requItem + ": " + e);
		} finally {
			c.close();
		}			
		return items;
	}
}