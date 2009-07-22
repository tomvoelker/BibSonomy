/**
 *  
 *  BibSonomy Recommender Webapp - Example remote recommender implementation
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.recommender;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.SortedSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.log4j.Logger;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;

/**
 * A simple http client class for querying a recommender servlet.
 * 
 * Execution: <code>mvn exec:java -Dexec.mainClass=org.bibsonomy.recommender.ServletClient</code>
 * @author fei
 */
public class ServletClient {
	private final static Logger log = Logger.getLogger(ServletClient.class);
	//private final static String REC_URL = "http://localhost:8080/bibsonomy-recommender-servlet/SimpleContentBasedTagRecommenderServlet";
	private final static String REC_URL = "http://localhost:8080/bibsonomy-recommender-servlet/DummyTagRecommenderServlet";
	
	public static void main( String[] args ) {
		//--------------------------------------------------------------------
		// serialize a post
		//--------------------------------------------------------------------
		Renderer renderer;
		renderer = XMLRenderer.getInstance();
		StringWriter sw = new StringWriter(100);
		final ViewModel vm = new ViewModel();
		vm.setStartValue(0);
		vm.setEndValue(10);
		vm.setUrlToNextResources("www.bibsonomy.org/foo/bar");
		renderer.serializePost(sw, createBibTeXPost(), vm);

		log.debug("Querying recommender for post: " + sw.toString());
	
		//--------------------------------------------------------------------
		// query recommender
		//--------------------------------------------------------------------
		// setup http post request
		HttpClient client = new HttpClient();
		PostMethod   post = new PostMethod(REC_URL);

		NameValuePair[] data = {
				new NameValuePair("data", sw.toString())
		};
		post.setRequestBody(data);

		try {
		      // Execute the method.
		      int statusCode = client.executeMethod(post);

		      if (statusCode != HttpStatus.SC_OK) {
		        log.error("Method failed: " + post.getStatusLine());
		      }
		      // Read the response body.
		      byte[] responseBody = post.getResponseBody();

		      log.debug(new String(responseBody));
		} catch (HttpException e) {
		      log.error("Fatal protocol violation.", e);
		      return;
		} catch (IOException e) {
		      log.error("Fatal transport error: ", e);
		      return;
		} finally {
		      // Release the connection.
		      post.releaseConnection();
		}
		
		//--------------------------------------------------------------------
		// handle response
		//--------------------------------------------------------------------
		// parse xml data
		InputStreamReader input = null;
		try {
			// returns InputStream with correct encoding
			input = new InputStreamReader(post.getResponseBodyAsStream(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		   	  log.error("Unsupported encoding", e);
		   	  return;
		} catch (IOException e) {
		      log.error("Fatal transport error: ", e);
		      return;
		};
		SortedSet<RecommendedTag> result = renderer.parseRecommendedTagList(input);
			
		// write out recommended tags
		for( RecommendedTag tag : result ) {
			System.out.println("Got tag: " + tag.toString());
		}
	}
	
	/**
	 * helper function for creating an example post
	 * 
	 * @return a mockup post
	 */
	@SuppressWarnings("unused")
	private static Post<? extends Resource> createBookmarkPost() {
		final Post<Resource> post = new Post<Resource>();
		final User user = new User();
		user.setName("foo");
		final Group group = new Group();
		group.setName("bar");
		final Tag tag = new Tag();
		tag.setName("foobar");
		post.setUser(user);
		post.getGroups().add(group);
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));
		
		// create bookmark object
		final Bookmark bookmark = new Bookmark();
		bookmark.setInterHash("12345678");
		bookmark.setIntraHash("12345678");
		bookmark.setUrl("www.foobar.de");
		bookmark.setTitle("bookmarktitle");
		post.setResource(bookmark);

		// all done.
		return post;
	}
	
	/**
	 * helper function for creating an example post
	 * 
	 * @return a mockup post
	 */
	@SuppressWarnings("unused")
	private static Post<? extends Resource> createBibTeXPost() {
		final Post<Resource> post = new Post<Resource>();
		final User user = new User();
		user.setName("foo");
		final Group group = new Group();
		group.setName("bar");
		final Tag tag = new Tag();
		tag.setName("foobar");
		post.setUser(user);
		post.getGroups().add(group);
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));

		// create bibtex object
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("foo and bar");
		bibtex.setIntraHash("abc");
		bibtex.setInterHash("abc");
		bibtex.setYear("2009");
		bibtex.setBibtexKey("test");
		bibtex.setEntrytype("twse");
		post.setResource(bibtex);
		
		// all done.
		return post;
	}

}
