package org.bibsonomy.lucene.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class QueryDB {

	private SqlMapClient sqlMapClient = null;

	private static QueryDB instance = null;
	
	
	/**
	 * Private constructor = singleton
	 * @throws IOException 
	 */
	private QueryDB() throws IOException {
		Reader reader;
//		reader = new InputStreamReader(QueryDB.class.getClassLoader().getResourceAsStream("SqlMapConfig.xml"));
//		reader = new InputStreamReader(QueryDB.class.getResourceAsStream("org.bibsonomy.logging.SqlMapConfig.xml"));
		reader = new InputStreamReader(QueryDB.class.getClassLoader().getResourceAsStream("SqlMapConfigLogger.xml"));
//		Properties prop = new Properties();
//		prop.load(QueryDB.class.getResourceAsStream("database.properties"));
		sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(reader);
	}
	
	
	public static QueryDB getInstance() throws IOException {
		if (instance == null) {
			instance = new QueryDB();
		}
		return instance;
	}
	
	
	public void insertLogdata(Log logdata) throws SQLException {
		sqlMapClient.insert("BibLog.insertLogdata", logdata);
	}
	
	
	public static void main(String[] args) throws IOException {

		final QueryDB db = QueryDB.getInstance();

//		try {
			/*
			 * Lesen
			 */
/*
			List<Post> list = new LinkedList<Post>();
			list.addAll((List<Post>) db.getSqlMapClient().queryForList("getBookmarkPosts", null));
			list.addAll((List<Post>) db.getSqlMapClient().queryForList("getBibtexPosts", null));

			for (Post p:list) {
				System.out.println(p + "  " + p.whoAmI());
			}
*/
			/*
			 * Schreiben
			 */
/*			Bookmark b = Bookmark.getExample();
			Post p = new Post();
			p.setContentId(999999999);
			p.setRes(b);
*/


//		} catch (SQLException e) {
//			e.printStackTrace();
//		}



	}


	public SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}


}