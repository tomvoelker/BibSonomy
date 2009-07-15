package org.bibsonomy.importer.bookmark.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.services.importer.RelationImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * Imports bookmarks and relations from Delicious. To get an instance of this 
 * class, use the {@link DeliciousImporterFactory}.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class DeliciousImporter implements RemoteServiceBookmarkImporter, RelationImporter {

	private static final Log log = LogFactory.getLog(DeliciousImporter.class);

	/**
	 * The URL to contact Delicious.
	 */
	private final URL apiURL;
	private final String userAgent;


	private String password;
	private String userName;
	
	public static final String HEADER_USER_AGENT = "User-Agent";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_AUTH_BASIC = "Basic ";
	public static final String UTF8 = "UTF-8";


	/**
	 * Constructor which allows to give a specific {@link #apiURL}.
	 * @param apiUrl - the URL to contact delicious
	 * @param userAgent - the userAgent this importer shall use to identify 
	 * itself in the corresponding HTTP header
	 */
	protected DeliciousImporter(final URL apiUrl, final String userAgent) {
		this.apiURL = apiUrl;
		this.userAgent = userAgent;
	}

	/**
	 * This Method retrieves a list of Posts for a given user.
	 */
	public List<Post<Bookmark>> getPosts() throws IOException{
		
		final List<Post<Bookmark>> posts = new LinkedList<Post<Bookmark>>();
				
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				
		//open a connection to delicious and retrieve a document
		Document document = getDocument();
		
		// traverse document and put everything into Post<Bookmark> Objects
		NodeList postList = document.getElementsByTagName("post");
		for (int i = 0; i < postList.getLength(); i++) {
			Element resource = (Element)postList.item(i);
								
			Post<Bookmark> post = new Post<Bookmark>();
			Bookmark bookmark = new Bookmark();
			bookmark.setTitle(resource.getAttribute("description"));
			bookmark.setUrl(resource.getAttribute("href"));
			try {
				post.getTags().addAll(TagUtils.parse(resource.getAttribute("tag")));
			} catch (Exception e) {
				throw new IOException(e);
			}
			
			//no tags available? -> add one tag to the resource and mark it as "imported"
			if(post.getTags().isEmpty()){
				post.getTags().add(new Tag("imported"));
			}
			
			post.setDescription(resource.getAttribute("extended"));
			try {
				post.setDate(df.parse(resource.getAttribute("time")));
			} catch (ParseException e) {
				throw new IOException(e);
			}
			
			//set the visibility of the imported resource
			if(resource.hasAttribute("shared")){
				if(resource.getAttribute("shared").equals("no")){
					post.getGroups().add(new Group(GroupID.PRIVATE.getId()));
				}else{
					post.getGroups().add(new Group(GroupID.PUBLIC.getId()));
				}
			}
			post.setResource(bookmark);
			posts.add(post);
			
		}
		
		return posts;
	}

	/**
	 * This method retrieves a list of tags with subTags from Delicious.
	 */
	public List<Tag> getRelations() throws IOException {
		final List<Tag> relations = new LinkedList<Tag>();
		//open a connection to delicious and retrieve a document
		Document document = getDocument();
		NodeList bundles = document.getElementsByTagName("bundle");
		for(int i = 0; i < bundles.getLength(); i++){
			Element resource = (Element)bundles.item(i);
			try {
				Tag tag = new Tag(resource.getAttribute("name"));
				tag.getSubTags().addAll(TagUtils.parse(resource.getAttribute("tags")));
				relations.add(tag);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		return relations;
	}

	@Override
	public void setCredentials(final String userName, final String password) {
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * Method opens a connection and parses the retrieved InputStream with a JAXP parser.
	 * @return The from the parse call returned Document Object
	 * @throws IOException
	 */
	private Document getDocument() throws IOException{
				
		URLConnection connection = apiURL.openConnection();
		connection.setRequestProperty(HEADER_USER_AGENT, userAgent);
		connection.setRequestProperty(HEADER_AUTHORIZATION, encodeForAuthorization());
		InputStream inputStream = connection.getInputStream();
		
		// Get a JAXP parser factory object
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// Tell the factory what kind of parser we want 
		dbf.setValidating(false);
		// Use the factory to get a JAXP parser object
		
		DocumentBuilder parser;
		try {
			parser = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
		
		// Tell the parser how to handle errors.  Note that in the JAXP API,
		// DOM parsers rely on the SAX API for error handling
		parser.setErrorHandler(new ErrorHandler() {
			public void warning(SAXParseException e) {
				log.warn(e);
			}
			public void error(SAXParseException e) {
				log.error(e);
			}
			public void fatalError(SAXParseException e)
			throws SAXException {
				log.fatal(e);
				throw e;   // re-throw the error
			}
		});
		
		// Finally, use the JAXP parser to parse the file.  
		// This call returns a Document object. 
		
		Document document;
		try {
			document = parser.parse(inputStream);
		} catch (SAXException e) {
			throw new IOException(e);
		}
		
		inputStream.close();
		
		return document;
			
	}	
	
	/**
	 * Encode the username and password for BASIC authentication
	 * 
	 * @return Basic + Base64 encoded(username + ':' + password)
	 */
	protected String encodeForAuthorization() {
		String retVal = HEADER_AUTH_BASIC;
		try {
			retVal += new String(Base64.encodeBase64((this.userName + ":" + this.password).getBytes()), UTF8 );
		} catch (UnsupportedEncodingException e1) {
			retVal += new String(Base64.encodeBase64((this.userName + ":" + this.password).getBytes()));
		}
		return retVal;
	}
	
}

