package helpers.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import resources.Bibtex;
import resources.Resource;
import beans.AdvancedSearchBean;

public class DBAdvancedSearchManager extends DBManager {

	/**
	 * the logger
	 */
	private static final Log log = LogFactory.getLog(DBAdvancedSearchManager.class);	


	/**
	 * gets all titles, authors and tags of given user from the database
	 * @param bean reference on AdvancedSearchBean
	 */
	public static void queryDB(AdvancedSearchBean bean) {
		DBContext c 	= new DBContext();
		ResultSet rst 	= null;


		try {
			if (c.init()) {

				SortedSet<String> titles  = new TreeSet<String>();
				SortedSet<String> tags 	  = new TreeSet<String>();
				SortedSet<String> authors = new TreeSet<String>();				

				String query = "SELECT s.content_id, b.author, b.editor, t.tag_name, b.title, b.simhash2, b.url "
					+ "		FROM search s "
					+ "			JOIN tas t USING (content_id) "
					+ "			JOIN bibtex b USING (content_id) "
					+ "		WHERE s.user_name = ? AND s.content_type = 2 "
					+ "		ORDER BY s.content_id";			
				c.stmt = c.conn.prepareStatement(query);	
				c.stmt.setString(1, bean.getUser());
				rst = c.stmt.executeQuery();

				int contentID = Bibtex.UNDEFINED_CONTENT_ID;			
				while(rst.next()) {
					String title 	= rst.getString("title").replaceAll("\\n|\\r", "");
					String tag 		= rst.getString("tag_name"); //lower case?
					String author	= buildAuthorsAndEditors(rst.getString("author"), rst.getString("editor"));
					
					if (rst.getInt("content_id") == contentID) {
						// just add tag					
						tags.add(tag);						
					} else {
						contentID = rst.getInt("content_id");

						// add title					
						titles.add(title);

						// add authors
						List<String> authorsLastNames = extractAuthorsLastNames(author);
						for (String name: authorsLastNames) {						
							authors.add(name);							
						}

						// add tag
						tags.add(tag);								
					}			
				}		

				// convert to list because we need indexOf function
				/*
				 * TODO: using indexOf is expensive because it needs to scan the whole list! 
				 */
				bean.setAuthors(new LinkedList<String>(authors));
				bean.setTags(new LinkedList<String>(tags));
				bean.setTitles(new LinkedList<String>(titles));			

				// build relation arrays
				buildRelationTables(rst, bean);

			}
			
		} catch (SQLException e) {
			log.fatal("got no results for advanced search: " + e);
		} finally {
			c.close();
		}
	}

	/**
	 * gets arrays of relations between title, author and tags 
	 * @param rst resultset of database query
	 * @throws SQLException
	 */
	private static void buildRelationTables(ResultSet rst, AdvancedSearchBean bean) throws SQLException {

		/**
		 * containers for relation tables
		 */
		final LinkedList<String> titleList  = bean.getTitles();
		final LinkedList<String> tagList    = bean.getTags();
		final LinkedList<String> authorList = bean.getAuthors();
		
		SortedSet<Integer>[] 	tagTitle 	= new TreeSet[tagList.size()]; 
		SortedSet<Integer>[] 	authorTitle = new TreeSet[authorList.size()]; 
		SortedSet<Integer>[] 	tagAuthor 	= new TreeSet[tagList.size()];
		
		SortedSet<Integer>[] 	titleAuthor = new TreeSet[titleList.size()];
		String[]		bibtexHashs	= new String[titleList.size()];
		String[]		bibtexUrls	= new String[titleList.size()];

		
		while (rst.previous()) {
			// read values from resultset
			String title 	= rst.getString("title").replaceAll("\\n|\\r", "");
			String tag 		= rst.getString("tag_name"); //lower case?
			String hash 	= rst.getString("simhash2");
			String url		= rst.getString("url");
			String author	= buildAuthorsAndEditors(rst.getString("author"), rst.getString("editor"));
						
			// tag --> title relation			 			
			if (tagTitle[tagList.indexOf(tag)] == null) {				
				SortedSet<Integer> v = new TreeSet<Integer>();				
				v.add(titleList.indexOf(title));				
				tagTitle[tagList.indexOf(tag)] = v;				
			} else {
				tagTitle[tagList.indexOf(tag)].add(titleList.indexOf(title));
			}

			// author --> title relation			 
			List<String> authorsLastNames = extractAuthorsLastNames(author);
			for (String name: authorsLastNames) {
				if (authorTitle[authorList.indexOf(name)] == null) {
					SortedSet<Integer> v = new TreeSet<Integer>();
					v.add(titleList.indexOf(title));
					authorTitle[authorList.indexOf(name)] = v;
				} else {
					authorTitle[authorList.indexOf(name)].add(titleList.indexOf(title));
				}
			}

			// tag --> author relation
			if (tagAuthor[tagList.indexOf(tag)] == null) {
				SortedSet<Integer> v = new TreeSet<Integer>();
				for (String name: authorsLastNames) {
					v.add(authorList.indexOf(name));
				}
				tagAuthor[tagList.indexOf(tag)] = v;
			} else {
				for (String name: authorsLastNames) {
					tagAuthor[tagList.indexOf(tag)].add(authorList.indexOf(name));
				}
			}

			// title --> author relation			 
			if (titleAuthor[titleList.indexOf(title)] == null) {
				SortedSet<Integer> v = new TreeSet<Integer>();
				for (String name: authorsLastNames) {
					v.add(authorList.indexOf(name));
				}
				titleAuthor[titleList.indexOf(title)] = v;
			} else {
				for (String name: authorsLastNames) {
					titleAuthor[titleList.indexOf(title)].add(authorList.indexOf(name));
				}
			}

			// BibTeX-Hashtable			 
			bibtexHashs[titleList.indexOf(title)] = hash;	

			// Urls
			bibtexUrls[titleList.indexOf(title)] = Resource.cleanUrl(url);
		}	


		bean.setTagTitle(tagTitle);	
		bean.setAuthorTitle(authorTitle);
		bean.setTagAuthor(tagAuthor);
		bean.setTitleAuthor(titleAuthor);
		bean.setBibtexHash(bibtexHashs);
		bean.setBibtexUrls(bibtexUrls);
	}


	/**
	 * extracts lastnames from given authorstring (unfortunately redundant @see Bibtex.java)
	 * @param authors string
	 * @return list of last names
	 * 
	 * TODO: this function is for sure a duplicate ... it is probably needed elsewhere (Bibtex.java?),
	 * at least to enable author search.
	 * 
	 */
	private static List<String> extractAuthorsLastNames(String authors) {
		List<String> authorsList = new LinkedList<String>();		
		List<String> names = new LinkedList<String>();
		Pattern pattern = Pattern.compile("[0-9]+"); // only numbers

		Scanner s = new Scanner(authors);
		s.useDelimiter(" and ");
		while(s.hasNext())
			names.add(s.next());		

		for (String person: names) {
			/*
			 * extract all parts of a name
			 */
			List<String> nameList = new LinkedList<String>();
			StringTokenizer token = new StringTokenizer(person);
			while (token.hasMoreTokens()) {
				/*
				 * ignore numbers (from DBLP author names) 
				 */
				final String part = token.nextToken();
				if (!pattern.matcher(part).matches()) {
					nameList.add(part);	
				}
			}

			/*
			 * detect lastname
			 */
			int i = 0;
			while (i < nameList.size() - 1) { // iterate up to the last but one part
				final String part = nameList.get(i++);
				/*
				 * stop, if this is the last abbreviated forename 
				 */
				if (part.contains(".") && !nameList.get(i).contains(".")) {
					break;
				}
			}				

			StringBuffer lastName = new StringBuffer();
			while (i < nameList.size()) {
				lastName.append(nameList.get(i++) + " ");
			}

			// add name to list
			authorsList.add(lastName.toString().trim());
		}			
		return authorsList;
	}
	
	/**
	 * builds a string of author- and editornames separated by "and"
	 * @param author authornames
	 * @param editor editornames
	 * @return 
	 */
	private static String buildAuthorsAndEditors(String author, String editor) {
		StringBuffer authors = new StringBuffer();
		
		if (author != null) 
			authors.append(author);
		
		if (editor != null) {
			if (author != null)
				authors.append(" and ");
			authors.append(editor);
		}		
		
		return authors.toString();
	}
}