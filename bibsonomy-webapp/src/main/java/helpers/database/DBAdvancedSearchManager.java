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
	 * container für tags, authors and titles
	 */
	private static LinkedList<String> tagList;
	private static LinkedList<String> authorList;
	private static LinkedList<String> titleList;
	
	/**
	 * containers for relation tables
	 */
	private static SortedSet[] 	tagTitle;
	private static SortedSet[] 	authorTitle;	
	private static SortedSet[] 	tagAuthor;
	private static SortedSet[] 	titleAuthor;
	private static String[]		bibtexHashs;
	private static String[]		bibtexUrls;	
	
	/**
	 * gets all titles, authors and tags of given user from the database
	 * @param bean reference on AdvancedSearchBean
	 */
	public static void queryDB(AdvancedSearchBean bean) {
		DBContext c 	= new DBContext();
		ResultSet rst 	= null;
		
		SortedSet<String> titles 	= new TreeSet<String>();
		SortedSet<String> tags 		= new TreeSet<String>();
		SortedSet<String> authors 	= new TreeSet<String>();				
		
		try {
			c.init();
			
			String query = "SELECT s.content_id, s.author, t.tag_name, b.title, b.simhash2, b.url "
					+ "		FROM search s "
					+ "			JOIN tas t USING (content_id) "
					+ "			JOIN bibtex b USING (content_id) "
					+ "		WHERE user_name = ? AND content_type = 2 "
					+ "		ORDER BY title ASC";			
			c.stmt = c.conn.prepareStatement(query);	
			c.stmt.setString(1, bean.getUser());
			rst = c.stmt.executeQuery();
			
			int contentID = Bibtex.UNDEFINED_CONTENT_ID;			
			while(rst.next()) {
				String title 	= rst.getString("title").replace("\r\n", "");
				String tag 		= rst.getString("tag_name"); //lower case?
				String author 	= rst.getString("author");
				
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
			tagList 	= (LinkedList<String>) toList(tags);
			authorList 	= (LinkedList<String>) toList(authors);
			titleList 	= (LinkedList<String>) toList(titles);
			
			bean.setAuthors(authorList);
			bean.setTags(tagList);
			bean.setTitles(titleList);			
			
			// build relation arrays
			buildRelationTables(rst);						
			bean.setTagTitle(tagTitle);	
			bean.setAuthorTitle(authorTitle);
			bean.setTagAuthor(tagAuthor);
			bean.setTitleAuthor(titleAuthor);
			bean.setBibtexHash(bibtexHashs);
			bean.setBibtexUrls(bibtexUrls);
			
		} catch (Exception e) {
			log.fatal("got no results for advanced search " + e.getMessage());
		} finally {
			c.close();
		}
	}
	
	/**
	 * gets arrays of relations between title, author and tags 
	 * @param rst resultset of database query
	 * @throws SQLException
	 */
	private static void buildRelationTables(ResultSet rst) throws SQLException {
		tagTitle 	= new TreeSet[tagList.size()]; 
		authorTitle = new TreeSet[authorList.size()]; 
		tagAuthor 	= new TreeSet[tagList.size()];
		titleAuthor = new TreeSet[titleList.size()];
		bibtexHashs	= new String[titleList.size()];
		bibtexUrls	= new String[titleList.size()];
				
		while (rst.previous()) {
			// read values from resultset
			String title 	= rst.getString("title").replace("\r\n", "");;
			String tag 		= rst.getString("tag_name"); //lower case?
			String author 	= rst.getString("author");	
			String hash 	= rst.getString("simhash2");
			String url		= rst.getString("url");
						
			// tag --> title relation			 			
			if (tagTitle[tagList.indexOf(tag)] == null) {				
				SortedSet v = new TreeSet();				
				v.add(titleList.indexOf(title));				
				tagTitle[tagList.indexOf(tag)] = v;				
			} else {
				tagTitle[tagList.indexOf(tag)].add(titleList.indexOf(title));
			}
						
			// author --> title relation			 
			List<String> authorsLastNames = extractAuthorsLastNames(author);
			for (String name: authorsLastNames) {
				if (authorTitle[authorList.indexOf(name)] == null) {
					SortedSet v = new TreeSet();				
					v.add(titleList.indexOf(title));
					authorTitle[authorList.indexOf(name)] = v;
				} else {
					authorTitle[authorList.indexOf(name)].add(titleList.indexOf(title));
				}
			}
			
			// tag --> author relation
			if (tagAuthor[tagList.indexOf(tag)] == null) {
				SortedSet v = new TreeSet();
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
				SortedSet v = new TreeSet();
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
	}
	
	/**
	 * makes a list from given set
	 * @param set
	 * @return list 
	 */
	private static LinkedList<String> toList(final SortedSet<String> set) {
		final LinkedList<String> list = new LinkedList<String>();
		
		for (final String element: set) {
			list.add(element);
		}			
		return list;		
	}
	
	/**
	 * extracts lastnames from given authorstring (unfortunately redundant @see Bibtex.java)
	 * @param authors string
	 * @return list of last names
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
}