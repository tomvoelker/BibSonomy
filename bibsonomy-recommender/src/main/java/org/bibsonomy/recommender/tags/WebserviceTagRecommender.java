package org.bibsonomy.recommender.tags;

import static org.bibsonomy.util.ValidationUtils.present;

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
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.recommender.tags.database.IdleClosingConnectionManager;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.springframework.beans.factory.DisposableBean;

/**
 * Class for encapsulating webservice queries to recommenders
 * 
 * @author fei
 */
public class WebserviceTagRecommender implements TagRecommenderConnector, DisposableBean {
	private static final Log log = LogFactory.getLog(WebserviceTagRecommender.class);
	
	/** url map for the getRecommendation method */
	private static final String METHOD_GETRECOMMENDEDTAGS = "getRecommendedTags";
	/** url map for the setFeedback method */
	private static final String METHOD_SETFEEDBACK = "setFeedback";
	
	// FIXME: These values are also used in TagRecommenderServlet and should
	//        be defined in a class commonly accessible
	/** post parameter for the feedback (xml-)post model */
	private static final String ID_FEEDBACK = "feedback";
	/** post parameter for the recommendation (xml-)post model */
	private static final String ID_RECQUERY = "data";
	/** post parameter for the post id */
	private static final String ID_POSTID   = "postID";
	
	private static final int SOCKET_TIMEOUT_MS = 10000;
	private static final int HTTP_CONNECTION_TIMEOUT_MS = 1000;
	private static final long IDLE_TIMEOUT_MS = 3000;
	
	private final HttpClient client;
	// service's address
	private URI address;
	// serializes post
	private final Renderer renderer;

	private final IdleClosingConnectionManager connectionManager;
	private final IdleConnectionTimeoutThread idleConnectionHandler;
	
	/**
	 * inits the recommender
	 */
	public WebserviceTagRecommender() {
		// create an instance of HttpClient.
		this.connectionManager = new IdleClosingConnectionManager();
      	this.client = new HttpClient(this.connectionManager);
      	
      	// set default timeouts
      	final HttpConnectionManagerParams connectionParams = this.connectionManager.getParams();
      	connectionParams.setSoTimeout(SOCKET_TIMEOUT_MS);
      	connectionParams.setConnectionTimeout(HTTP_CONNECTION_TIMEOUT_MS);
      	this.connectionManager.setParams(connectionParams);
      	log.debug("MAXCONNECTIONS: "+connectionParams.getMaxTotalConnections());
      	log.debug("MAXCONNECTIONSPERHOST: "+connectionParams.getDefaultMaxConnectionsPerHost());
      	
      	// handle idle connections
      	this.connectionManager.closeIdleConnections(IDLE_TIMEOUT_MS);
      	this.idleConnectionHandler = new IdleConnectionTimeoutThread();
      	this.idleConnectionHandler.addConnectionManager(this.connectionManager);
      	this.idleConnectionHandler.start();
      	
		this.renderer = new RendererFactory(new UrlRenderer("/api/")).getRenderer(RenderingFormat.XML);
	}
	
	/**
	 * Constructor
	 * @param address 
	 */
	public WebserviceTagRecommender(final URI address) {
		this();
		this.setAddress(address);
	}
	
	/**
	 * @return the address
	 */
	public URI getAddress() {
		return this.address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(final URI address) {
		this.address = address;
	}

	//------------------------------------------------------------------------
	// TagRecommender interface
	//------------------------------------------------------------------------
	@Override
	public void addRecommendedTags(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> post) {
		// render post
		// FIXME: choose buffer size
		final StringWriter sw = new StringWriter(100);
		this.renderPost(post, sw);
		
		// Create a method instance.
		final NameValuePair[] data = {
				new NameValuePair(ID_RECQUERY, sw.toString()),
				new NameValuePair(ID_POSTID, "" + post.getContentId())
		};
		// Create a method instance.
		// send request
		// FIXME: THIS IS JUST FOR DOWNWARD COMPATIBILITY DURING THE DC09 RECOMMENDER CHALLENGE
		//        Replace the following three lines of code with:
		//        InputStreamReader input = sendRequest(data, "/"+METHOD_GETRECOMMENDEDTAGS);
		PostMethod cnct = new PostMethod(this.getAddress().toString());
		cnct.setRequestBody(data);
		InputStreamReader input = this.sendRequest(cnct);
		if (input == null) {
			cnct.releaseConnection();
			cnct = new PostMethod(this.getAddress().toString()+"/"+METHOD_GETRECOMMENDEDTAGS);
			cnct.setRequestBody(data);
			input = this.sendRequest(cnct); 
		}
		
		// Deal with the response.
		SortedSet<RecommendedTag> result = null;
		if( input!=null ) {
			try {
				result = this.renderer.parseRecommendedTagList(input);
			} catch( final Exception e ) {
				log.error("Error parsing recommender response ("+this.getAddress().toString()+").", e);
				result = null;
			}
		}
		if( result!=null ) {
			recommendedTags.addAll(result);
		}
		
		cnct.releaseConnection();
	}

	@Override
	public SortedSet<RecommendedTag> getRecommendedTags(final Post<? extends Resource> post) {
		final SortedSet<RecommendedTag> retVal = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		this.addRecommendedTags(retVal, post);
		return retVal;
	}


	@Override
	public void setFeedback(final Post<? extends Resource> post) {
		// render post
		// FIXME: choose buffer size
		final StringWriter sw = new StringWriter(100);
		this.renderPost(post, sw);
		
		// Create a method instance.
		final NameValuePair[] data = {
				new NameValuePair(ID_FEEDBACK, sw.toString()),
				new NameValuePair(ID_RECQUERY, sw.toString()), // for downward compatibility
				new NameValuePair(ID_POSTID, "" + post.getContentId())
		};

		// send request
		final PostMethod cnct = new PostMethod(this.getAddress().toString()+"/"+METHOD_SETFEEDBACK);
		cnct.setRequestBody(data);
		final InputStreamReader input = this.sendRequest(cnct);

		// Deal with the response.
		if (input != null) {
			final String status = this.renderer.parseStat(input);
			log.info("Feedback status: " + status);
		}
		
		cnct.releaseConnection();
	}

	@Override
	public byte[] getMeta() {
		return this.getAddress().toString().getBytes();
	}

	@Override
	public String getInfo() {
		return "Webservice";
	}

	@Override
	public String getId() {
		return this.getAddress().toString();
	}

	//------------------------------------------------------------------------
	// TagRecommenderConnector interface
	//------------------------------------------------------------------------
	@Override
	public boolean connect() throws Exception {
		return false;
	}

	@Override
	public boolean disconnect() throws Exception {
		return false;
	}

	@Override
	public boolean initialize(final Properties props) throws Exception {
		return false;
	}
	
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------

	private void renderPost(final Post<? extends Resource> post, final StringWriter sw) {
		final ViewModel vm = new ViewModel();
		// we use rest-api's xml rederer which perfoms some validation tests which our
		// post model has to pass:
		// 1) set hashes
		// 2) append 'empty' tag
		// 3) set empty title
		if (!present(post.getResource().getInterHash())) {
			post.getResource().setInterHash("abc");
		}
		if (!present(post.getResource().getIntraHash())) {
			post.getResource().setIntraHash("abc");
		}
		if (!present(post.getTags())) {
			final Set<Tag> tags = new HashSet<Tag>();
			tags.add(TagUtils.getEmptyTag());
			post.setTags(tags);
		}
		if (post.getResource().getTitle() == null) {
			post.getResource().setTitle("");
		}
		this.renderer.serializePost(sw, post, vm);		
	}
	
	private InputStreamReader sendRequest(final PostMethod cnct) {
		InputStreamReader input = null;
		// byte[] responseBody = null;
		
		try {
			// Execute the method.
			final int statusCode = this.client.executeMethod(cnct);

			if (statusCode != HttpStatus.SC_OK) {
				log.error("Method at " + this.getAddress().toString() + " failed: " + cnct.getStatusLine());
			} else {
				// Read the response body.
				// responseBody = cnct.getResponseBody();
				input = new InputStreamReader(cnct.getResponseBodyAsStream(), "UTF-8");
			}

		} catch (final HttpException e) {
			log.fatal("Fatal protocol violation("+this.getAddress()+"): " + e.getMessage(), e);
		} catch (final UnsupportedEncodingException ex) {
			// returns InputStream with default encoding if a exception
			// is thrown with utf-8 support
			log.fatal("Encoding error("+this.getAddress()+"): " + ex.getMessage(), ex);
		} catch (final IOException e) {
			log.fatal("Fatal transport error("+this.getAddress()+"): " + e.getMessage(), e);
		} catch (final Exception e) {
			log.fatal("Unknown error ("+this.getAddress()+")", e);
		} finally {
			// Release the connection.
			// cnct.releaseConnection();
		}  	
		
		// all done.
		// log.debug("Got response: " + new String(responseBody));
		return input;
	}

	@Override
	public void destroy() throws Exception {
		// needed to prevent a failing spring-context to start more and more threads
		if (this.idleConnectionHandler != null) {
			this.idleConnectionHandler.shutdown();
		}
	}
}
