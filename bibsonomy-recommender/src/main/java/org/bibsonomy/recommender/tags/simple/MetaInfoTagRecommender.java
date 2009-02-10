package org.bibsonomy.recommender.tags.simple;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.RecommendedTagComparator;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.recommender.tags.TagRecommender;

/**
 * @author fei
 * @version $Id$
 */
public class MetaInfoTagRecommender implements TagRecommenderConnector {
	private static final Logger log = Logger.getLogger(MetaInfoTagRecommender.class);
	private String server = "";   // 'http://www.bibsonomy.org' 
	// FIXME: How can we automatically get bibsonomy's servername?
	
	public void addRecommendedTags(SortedSet<RecommendedTag> recommendedTags,
			Post<? extends Resource> post) {
		recommendedTags.addAll(getRecommendedTags(post));
	}

	public String getInfo() {
		return "Recommender using html <meta> informations.";
	}

	public SortedSet<RecommendedTag> getRecommendedTags(
			Post<? extends Resource> post) {

		SortedSet<RecommendedTag> result = 
			new TreeSet<RecommendedTag>(new RecommendedTagComparator());

		if( Bookmark.class.isAssignableFrom(post.getResource().getClass()) ) {
			HttpClient client = new HttpClient();
			// Create a method instance.
			String uri = getServer()+"/generalAjax?action=getTitleForUrl&pageURL="
				+ ((Bookmark)post.getResource()).getUrl();
			log.info("Querying: "+uri);
			
			GetMethod request = new GetMethod(uri);
			try {
				// Execute the method.
				int statusCode = client.executeMethod(request);

				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Method failed: " + request.getStatusLine());
				} else {
					// Read the response body.
					byte[] responseBody = request.getResponseBody();
					// clean up keywords
					String[] responseStr = new String(responseBody, "UTF8").split("pageKeywords\\s*\"\\s*:\\s*\"");
					if( responseStr.length>0 ) {
						String[] keywords = responseStr[1].split(",");
						if( keywords.length>0 ) {
							keywords[keywords.length-1]=keywords[keywords.length-1].replaceAll("\\s*\"\\s*}\\s*$", "");//
							for( int i=0; i<keywords.length; i++ ){
								if(keywords[i].length()>0)
									result.add(new RecommendedTag(keywords[i].toLowerCase(),0.5,0));
							}
						}
					}					
				}
			} catch (Exception e) {
				log.error("Fatal protocol violation.", e);
			} finally {
				// Release the connection.
				request.releaseConnection();
			}
		}

		return result;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getServer() {
		return server;
	}

	public boolean connect() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean disconnect() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] getMeta() {
		// TODO Auto-generated method stub
		return getServer().getBytes();
	}

	public boolean initialize(Properties props) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
