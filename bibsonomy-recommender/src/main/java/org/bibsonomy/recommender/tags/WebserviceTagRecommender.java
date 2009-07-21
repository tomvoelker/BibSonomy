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
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.model.util.TagUtils;
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
	
	/**
	 * Constructor
	 */
	public WebserviceTagRecommender(URI address) {
		// Create an instance of HttpClient.
		client = new HttpClient();
		this.setAddress(address);
		this.renderer = XMLRenderer.getInstance();
	}
	
	public WebserviceTagRecommender() {
		client = new HttpClient();
		this.renderer = XMLRenderer.getInstance();
	}
	
	public void addRecommendedTags(
			Collection<RecommendedTag> recommendedTags,
			Post<? extends Resource> post) {
	}

	public String getInfo() {
		return "Webservice";
	}

	public SortedSet<RecommendedTag> getRecommendedTags(
			Post<? extends Resource> post) {
		// serialize post
		// FIXME choose buffer size
		StringWriter sw = new StringWriter(100);
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
			Set tags = new HashSet<Tag>();
			tags.add(TagUtils.getEmptyTag());
			post.setTags(tags);
		}
		if( post.getResource().getTitle()==null ) {
			post.getResource().setTitle("");
		}
		renderer.serializePost(sw, post, vm);		

		// Create a method instance.
		PostMethod cnct = new PostMethod(getAddress().toString());
		NameValuePair[] data = {
				new NameValuePair("data", sw.toString())
		};

		cnct.setRequestBody(data);
		SortedSet<RecommendedTag> result = null;
		try {
			// Execute the method.
			int statusCode = client.executeMethod(cnct);

			if (statusCode != HttpStatus.SC_OK) {
				log.error("Method failed: " + cnct.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = cnct.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			log.info("Got response: " + new String(responseBody));

			InputStreamReader input = null;
			// returns InputStream with correct encoding
			input = new InputStreamReader(cnct.getResponseBodyAsStream(), "UTF-8");
			result = renderer.parseRecommendedTagList(input);

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
			cnct.releaseConnection();
		}  		
		if( result!=null )
			return result;
		else 
			return new TreeSet<RecommendedTag>(new RecommendedTagComparator());
	}

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

	public byte[] getMeta() {
		return getAddress().toString().getBytes();
	}

	public void setAddress(URI address) {
		this.address = address;
	}

	public URI getAddress() {
		return address;
	}

	@Override
	public String getId() {
		return getAddress().toString();
	}

	@Override
	public void setFeedback(Post<? extends Resource> post) {
		// TODO
		throw new RuntimeException("not implemented");
	}

	
}
