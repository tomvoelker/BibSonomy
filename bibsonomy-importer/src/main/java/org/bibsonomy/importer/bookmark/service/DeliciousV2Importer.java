package org.bibsonomy.importer.bookmark.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.services.importer.RelationImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DeliciousV2Importer implements RemoteServiceBookmarkImporter, RelationImporter {

    private static final Log log = LogFactory.getLog(DeliciousImporter.class);

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    private static String BUNDLES_PATH = "http://api.del.icio.us/v2/tags/bundles/all";
    private static String POST_PATH   = "http://api.del.icio.us/v2/posts/all";
    
    private DeliciousSignPost oAuth;

    public DeliciousV2Importer(DeliciousSignPost oAuth) {
	this.oAuth = oAuth;
    }

    @Override
    public void setCredentials(String userName, String password) {
	/* no use */
    }


	/**
	 * This Method retrieves a list of Posts for a given user.
	 */
	@Override
	public List<Post<Bookmark>> getPosts() throws IOException{
		
		final List<Post<Bookmark>> posts = new LinkedList<Post<Bookmark>>();
				
		//open a connection to delicious and retrieve a document
		final Document document = getDocument(POST_PATH);
		
		// traverse document and put everything into Post<Bookmark> Objects
		final NodeList postList = document.getElementsByTagName("post");
		for (int i = 0; i < postList.getLength(); i++) {
			final Element resource = (Element)postList.item(i);
								
			final Post<Bookmark> post = new Post<Bookmark>();
			final Bookmark bookmark = new Bookmark();
			bookmark.setTitle(resource.getAttribute("description"));
			bookmark.setUrl(resource.getAttribute("href"));
			try {
				post.getTags().addAll(TagUtils.parse(resource.getAttribute("tag")));
			} catch (Exception e) {
				throw new IOException("Could not parse tags. ", e);
			}
			
			//no tags available? -> add one tag to the resource and mark it as "imported"
			if (post.getTags().isEmpty()) {
				post.setTags(Collections.singleton(TagUtils.getEmptyTag()));
			}
			
			post.setDescription(resource.getAttribute("extended"));
			try {
				post.setDate(df.parse(resource.getAttribute("time")));
			} catch (ParseException e) {
				log.warn("Could not parse date.", e);
				post.setDate(new Date());
			}
			
			//set the visibility of the imported resource
			if (resource.hasAttribute("shared")) {
				if ("no".equals(resource.getAttribute("shared"))) {
					post.getGroups().add(GroupUtils.getPrivateGroup());
				} else {
					post.getGroups().add(GroupUtils.getPublicGroup());
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
	@Override
	public List<Tag> getRelations() throws IOException {
		final List<Tag> relations = new LinkedList<Tag>();
		//open a connection to delicious and retrieve a document
		final Document document = getDocument(BUNDLES_PATH);
		final NodeList bundles = document.getElementsByTagName("bundle");
		for(int i = 0; i < bundles.getLength(); i++){
			final Element resource = (Element)bundles.item(i);
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
	
	/**
	 * Method opens a connection and parses the retrieved InputStream with a JAXP parser.
	 * @return The from the parse call returned Document Object
	 * @throws IOException
	 */
	private Document getDocument(String url) throws IOException{
				
		final InputStream inputStream = oAuth.sign(new URL(url)).getInputStream();
		
		// Get a JAXP parser factory object
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// Tell the factory what kind of parser we want 
		dbf.setValidating(false);
		// Use the factory to get a JAXP parser object
		
		final DocumentBuilder parser;
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
		
		final Document document;
		try {
			document = parser.parse(inputStream);
		} catch (SAXException e) {
			throw new IOException(e);
		}
		
		inputStream.close();
		
		return document;
			
	}	
}
