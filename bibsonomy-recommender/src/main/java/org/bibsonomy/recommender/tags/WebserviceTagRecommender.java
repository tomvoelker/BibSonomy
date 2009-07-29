package org.bibsonomy.recommender.tags;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread; 
import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.recommender.tags.database.IdleClosingConnectionManager;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender.FeedbackDispatcher;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * Class for encapsulating webservice queries to recommenders
 * @author fei
 * @version $Id$
 */
public class WebserviceTagRecommender implements TagRecommenderConnector {
	private HttpClient client;
	// service's address
	private URI address;
	// serializes post
	Renderer renderer;
	final Logger log = Logger.getLogger(WebserviceTagRecommender.class);
	
	// FIXME: These values are also used in TagRecommenderServlet and should
	//        be defined in a class commonly accessible
	/** post parameter for the feedback (xml-)post model */
	public final String ID_FEEDBACK = "feedback";
	/** post parameter for the recommendation (xml-)post model */
	public final String ID_RECQUERY = "data";
	/** post parameter for the post id */
	public final String ID_POSTID   = "postID";

	/** url map for the getRecommendation method */
	private static final String METHOD_GETRECOMMENDEDTAGS = "getRecommendedTags";
	/** url map for the setFeedback method */
	private static final String METHOD_SETFEEDBACK = "setFeedback";
	
	private static final int SOCKET_TIMEOUT_MS = 10000;
	private static final int HTTP_CONNECTION_TIMEOUT_MS = 1000;
	private static final long IDLE_TIMEOUT_MS = 3000;

	// MultiThreadedHttpConnectionManager 
	IdleClosingConnectionManager connectionManager;
	IdleConnectionTimeoutThread idleConnectionHandler;
	//------------------------------------------------------------------------
	// constructors
	//------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public WebserviceTagRecommender(URI address) {
		this();
		this.setAddress(address);
	}
	
	public WebserviceTagRecommender() {
		// Create an instance of HttpClient.
		connectionManager = new IdleClosingConnectionManager();// MultiThreadedHttpConnectionManager();
      	client = new HttpClient(connectionManager);
      	
      	// set default timeouts
      	HttpConnectionManagerParams connectionParams = connectionManager.getParams();
      	connectionParams.setSoTimeout(SOCKET_TIMEOUT_MS);
      	connectionParams.setConnectionTimeout(HTTP_CONNECTION_TIMEOUT_MS);
      	connectionManager.setParams(connectionParams);
      	log.debug("MAXCONNECTIONS: "+connectionParams.getMaxTotalConnections());
      	log.debug("MAXCONNECTIONSPERHOST: "+connectionParams.getDefaultMaxConnectionsPerHost());
      	
      	
      	
      	// handle idle connections
      	connectionManager.closeIdleConnections(IDLE_TIMEOUT_MS);
      	idleConnectionHandler = new IdleConnectionTimeoutThread();
      	idleConnectionHandler.addConnectionManager(connectionManager);
      	idleConnectionHandler.start();
      	
		this.renderer = XMLRenderer.getInstance();
	}
	//------------------------------------------------------------------------
	// WebserviceTagRecommender interface
	//------------------------------------------------------------------------
	public void setAddress(URI address) {
		this.address = address;
	}

	public URI getAddress() {
		return address;
	}
	
	//------------------------------------------------------------------------
	// TagRecommender interface
	//------------------------------------------------------------------------
	public void addRecommendedTags(
			Collection<RecommendedTag> recommendedTags,
			Post<? extends Resource> post) {
		// render post
		// FIXME: choose buffer size
		StringWriter sw = new StringWriter(100);
		renderPost(post, sw);
		
		// Create a method instance.
		NameValuePair[] data = {
				new NameValuePair(ID_RECQUERY, sw.toString()),
				new NameValuePair(ID_POSTID, post.getContentId().toString())
		};
		// Create a method instance.
		// send request
		// FIXME: THIS IS JUST FOR DOWNWARD COMPATIBILITY DURING THE DC09 RECOMMENDER CHALLENGE
		//        Replace the following three lines of code with:
		//        InputStreamReader input = sendRequest(data, "/"+METHOD_GETRECOMMENDEDTAGS);
		PostMethod cnct = new PostMethod(getAddress().toString());
		cnct.setRequestBody(data);
		InputStreamReader input = sendRequest(cnct);
		if( input==null ) {
			cnct.releaseConnection();
			cnct = new PostMethod(getAddress().toString()+"/"+METHOD_GETRECOMMENDEDTAGS);
			cnct.setRequestBody(data);
			input = sendRequest(cnct); 
		}
		
		// Deal with the response.
		SortedSet<RecommendedTag> result = null;
		if( input!=null ) {
			try {
				result = renderer.parseRecommendedTagList(input);
			} catch( Exception e ) {
				log.error("Error parsing recommender response ("+getAddress().toString()+").", e);
				result = null;
			}
		}
		if( result!=null )
			recommendedTags.addAll(result);
		
		cnct.releaseConnection();
	}


	public SortedSet<RecommendedTag> getRecommendedTags(
			Post<? extends Resource> post) {
		SortedSet<RecommendedTag> retVal = 
			new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		addRecommendedTags(retVal, post);
		return retVal;
	}


	@Override
	public void setFeedback(Post<? extends Resource> post) {
		// render post
		// FIXME: choose buffer size
		StringWriter sw = new StringWriter(100);
		renderPost(post, sw);
		
		// Create a method instance.
		NameValuePair[] data = {
				new NameValuePair(ID_FEEDBACK, sw.toString()),
				new NameValuePair(ID_RECQUERY, sw.toString()), // for downward compatibility
				new NameValuePair(ID_POSTID, post.getContentId().toString())
		};

		// send request
		PostMethod cnct = new PostMethod(getAddress().toString()+"/"+METHOD_SETFEEDBACK);
		cnct.setRequestBody(data);
		InputStreamReader input = sendRequest(cnct);

		// Deal with the response.
		if( input!=null ) {
			String status = renderer.parseStat(input);
			log.info("Feedback status: " + status);
		}
		
		cnct.releaseConnection();
	}

	public byte[] getMeta() {
		return getAddress().toString().getBytes();
	}

	public String getInfo() {
		return "Webservice";
	}

	@Override
	public String getId() {
		return getAddress().toString();
	}

	//------------------------------------------------------------------------
	// TagRecommenderConnector interface
	//------------------------------------------------------------------------
	public boolean connect() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean disconnect() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean initialize(Properties props) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------

	private void renderPost(Post<? extends Resource> post, StringWriter sw) {
		final ViewModel vm = new ViewModel();
		// we use rest-api's xml rederer which perfoms some validation tests which our
		// post model has to pass:
		// 1) set hashes
		// 2) append 'empty' tag
		// 3) set empty title
		if( post.getResource().getInterHash()==null || post.getResource().getInterHash().length()==0 )
			post.getResource().setInterHash("abc");
		if( post.getResource().getIntraHash()==null || post.getResource().getIntraHash().length()==0 )
			post.getResource().setIntraHash("abc");
		if( (post.getTags()==null) || (post.getTags().size()==0) ) {
			Set<Tag> tags = new HashSet<Tag>();
			tags.add(TagUtils.getEmptyTag());
			post.setTags(tags);
		}
		if( post.getResource().getTitle()==null ) {
			post.getResource().setTitle("");
		}
		renderer.serializePost(sw, post, vm);		
	}
	
	
	private InputStreamReader sendRequest(PostMethod cnct) {
		InputStreamReader input = null;
		// byte[] responseBody = null;
		
		try {
			// Execute the method.
			int statusCode = client.executeMethod(cnct);

			if (statusCode != HttpStatus.SC_OK) {
				log.error("Method at " + getAddress().toString() + " failed: " + cnct.getStatusLine());
			} else {
				// Read the response body.
				// responseBody = cnct.getResponseBody();
				input        = new InputStreamReader(cnct.getResponseBodyAsStream(), "UTF-8");
			}

		} catch (HttpException e) {
			log.fatal("Fatal protocol violation("+getAddress()+"): " + e.getMessage(), e);
		} catch (UnsupportedEncodingException ex) {
			// returns InputStream with default encoding if a exception
			// is thrown with utf-8 support
			log.fatal("Encoding error("+getAddress()+"): " + ex.getMessage(), ex);
		} catch (IOException e) {
			log.fatal("Fatal transport error("+getAddress()+"): " + e.getMessage(), e);
		} catch (Exception e) {
			log.fatal("Unknown error ("+getAddress()+")", e);
		} finally {
			// Release the connection.
			// cnct.releaseConnection();
		}  	
		
		// all done.
		// log.debug("Got response: " + new String(responseBody));
		return input;
	}
}
