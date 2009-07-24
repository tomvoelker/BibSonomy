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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
	/** usage string */
	private static final String MSG_USAGE = "usage: ServletClient <recommender' s base url> <user name>";
	
	/** post parameter for the feedback (xml-)post model */
	public final static String ID_FEEDBACK = "feedback";
	/** post parameter for the recommendation (xml-)post model */
	public final static String ID_RECQUERY = "data";
	/** post parameter for the post id */
	public final static String ID_POSTID   = "postID";

	/** url map for the getRecommendation method */
	private static final String METHOD_GETRECOMMENDEDTAGS = "getRecommendedTags";
	/** url map for the setFeedback method */
	private static final String METHOD_SETFEEDBACK = "setFeedback";
	
	public static void main( String[] args ) throws IOException {
		//--------------------------------------------------------------------
		// read parameters
		//--------------------------------------------------------------------
		if( args.length<2 ) {
			usage();
			return;
		}
		String recommenderURL = args[0];
		String userName       = args[1];
		
		System.out.println("URL: "+recommenderURL+"\t User Name: "+userName);
		
		ServletClient client = new ServletClient();
		
		client.queryRecommender(recommenderURL, createBibTeXPost(userName));
		client.sendFeedback(recommenderURL, createBibTeXPost(userName));
	}

	private void queryRecommender(String recommenderURL, Post<?> post) throws IOException {
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
		renderer.serializePost(sw, post, vm);

	
		//--------------------------------------------------------------------
		// query recommender
		//--------------------------------------------------------------------
		System.out.println("Querying recommender for post: " + sw.toString());
		// Create a method instance.
		NameValuePair[] data = {
				new NameValuePair(ID_RECQUERY, sw.toString()), 
				new NameValuePair(ID_POSTID, "0")
		};
		PostMethod   cnct  = new PostMethod(recommenderURL+"/"+METHOD_GETRECOMMENDEDTAGS);
		cnct.setRequestBody(data);

		InputStreamReader response = sendRequest(cnct);

		//--------------------------------------------------------------------
		// handle response
		//--------------------------------------------------------------------
		SortedSet<RecommendedTag> result = renderer.parseRecommendedTagList(response);
			
		// write out recommended tags
		for( RecommendedTag tag : result ) {
			System.out.println("Got tag: " + tag.toString());
		}
		cnct.releaseConnection();
		

	}
	
	private void sendFeedback(String recommenderURL, Post<?> post) throws IOException {
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
		renderer.serializePost(sw, post, vm);
		
		//--------------------------------------------------------------------
		// send feedback
		//--------------------------------------------------------------------
		System.out.println("Setting feedback...");
		// Create a method instance.
		NameValuePair[] feedback = {
				new NameValuePair(ID_FEEDBACK, sw.toString()), 
				new NameValuePair(ID_POSTID, "0")
		};
		PostMethod cnct = new PostMethod(recommenderURL+"/"+METHOD_SETFEEDBACK);
		cnct.setRequestBody(feedback);

		InputStreamReader response = sendRequest(cnct);

		//--------------------------------------------------------------------
		// handle response
		//--------------------------------------------------------------------
		// parse xml data
		renderer = XMLRenderer.getInstance();
		if( response!=null )
			System.out.println("Status: " + renderer.parseStat(response));
		cnct.releaseConnection();
	}
	
	/**
	 * send an http request
	 * @param post
	 * @return
	 */
	private InputStreamReader sendRequest(PostMethod   post) {
		InputStreamReader result = null;
		HttpClient client  = new HttpClient();
		
		try {
		      // Execute the method.
		      int statusCode = client.executeMethod(post);

		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + post.getStatusLine());
		      }
		      // Read the response body.
		      result = new InputStreamReader(post.getResponseBodyAsStream(),"UTF-8");
		} catch (HttpException e) {
		      System.err.println("Fatal protocol violation." + e.toString());
		} catch (UnsupportedEncodingException e) {
		   	  System.err.println("Unsupported encoding" + e.toString());
		} catch (IOException e) {
			  System.err.println("Fatal transport error: " + e.toString());
		}
		
		return result;
	}
	
	/**
	 * prints usage information to stdout
	 */
	private static void usage() {
		System.out.println(MSG_USAGE);
	}

	/**
	 * helper function for creating an example post
	 * 
	 * @return a mockup post
	 */
	@SuppressWarnings("unused")
	private static Post<? extends Resource> createBookmarkPost(String userName) {
		final Post<Resource> post = new Post<Resource>();
		final User user = new User();
		user.setName(userName);
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
	 * @param userName 
	 * 
	 * @return a mockup post
	 */
	@SuppressWarnings("unused")
	private static Post<? extends Resource> createBibTeXPost(String userName) {
		final Post<Resource> post = new Post<Resource>();
		final User user = new User();
		user.setName(userName);
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
