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

package org.bibsonomy.recommender.servlets;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.impl.XMLRenderer;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * A tag recommender dispatching servlet for providing a remote tag recommender to BibSonomy.
 * The recommender's class name is given via the context parameter 'TagRecommender' and loaded 
 * during servlet's initialization.
 * 
 *  The context parameter can either be given in the servlets web.xml:
 *    <context-param>
 * 		  <param-name>TagRecommender</param-name>
 *	      <param-value>org.bibsonomy.recommender.tags.simple.SimpleContentBasedTagRecommender</param-value>
 *	  </context-param>
 *
 *  or in the servlet container's context configuration, for example in tomcat's 'conf/context.xml':
 *    <Parameter name="TagRecommender" value="org.bibsonomy.recommender.tags.simple.DummyTagRecommender" override="false"/>
 */
public class TagRecommenderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/** indicates that post identifier was not given */
	public static int UNKNOWN_POSTID = -1;
	
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

	
	/** the tag recommender */
	TagRecommender recommender;
	
	//------------------------------------------------------------------------
	// constructors
	//------------------------------------------------------------------------
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TagRecommenderServlet() {
        super();
    }
    
    
	//------------------------------------------------------------------------
	// HttpServlet interface
	//------------------------------------------------------------------------
	/**
	 * Initialize the tag recommender when the servlet is loaded.
	 * The recommender's classname is provided in the context parameter 'TagRecommender'.
	 *  
	 * @see GenericServlet
	 */

    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	
    	// instantiate tag recommender
    	String recName = getServletContext().getInitParameter("TagRecommender");
    	try {
			Class<?> recClass = Class.forName(recName);
			recommender       = (TagRecommender) recClass.newInstance();
		} catch (ClassNotFoundException e) {
			throw new ServletException("Could not load recommender class", e);
		} catch (InstantiationException e) {
			throw new ServletException("Could not instantiate recommender", e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
    	
    }

    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	private void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		// query postID: recommendation's post id
		Integer postID = null;
		try {
			postID = Integer.parseInt(request.getParameter(ID_POSTID));
		} catch( NumberFormatException e ) {
			postID = UNKNOWN_POSTID;
		}
		if( postID==null )
			postID = UNKNOWN_POSTID;

		// determine whether we should recommend tags or set recommender's feedback
		String methodName = request.getPathInfo();
		if( methodName!=null )
			methodName = methodName.replace("/", "");
		if( (methodName==null)||(METHOD_GETRECOMMENDEDTAGS.equals(methodName)) ) {
			// query data: the (xml)-encoded post for tag recommendation  
			final String dataString = request.getParameter(ID_RECQUERY);
			// generate list of recommended tags
			dispatchQuery(response, dataString, postID);
		} else if( METHOD_SETFEEDBACK.equals(methodName) ) {
			// query feedback: the (xml)-encoded post for feedback
			final String feedbackString = request.getParameter(ID_FEEDBACK);
			// forward feedback to recommender
			dispatchFeedback(response, feedbackString, postID);
		} else {
			// unknown method requested
			throw new ServletException("Method " + methodName + " not implemented." ); 
		}
	}

	/**
	 * dispatches recommender query
	 * 
	 * @param response where to write the serialized tags
	 * @param dataString the xml post model
	 * @param postID recommendtaion task's post id
	 * @throws IOException 
	 */
	protected void dispatchQuery(HttpServletResponse response, String dataString, Integer postID) throws IOException {
		// parse the post model
		Renderer renderer = XMLRenderer.getInstance();
		Reader doc = new StringReader(dataString);
		final Post<?> post = renderer.parsePost(doc);
		
		Collection<RecommendedTag> tags = new TreeSet<RecommendedTag>();
		if( (post!=null)&&(recommender!=null) ) {
			// query recommender
			post.setContentId(postID);
			tags = recommender.getRecommendedTags(post);
		}
		
		
		// encode them into xml
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		renderer.serializeRecommendedTags(response.getWriter(), tags);
	}

	/**
	 * forwards final post as stored in bibsonomy to recommender (identified by its postID)
	 * 
	 * @param response
	 * @param feedbackString
	 * @param postID
	 * @throws IOException 
	 */
	private void dispatchFeedback(HttpServletResponse response,
			String feedbackString, Integer postID) throws IOException {
		// parse the post model
		final Post<?> post = parsePost(feedbackString);
		
		// query recommender
		if( (recommender!=null)&&(post!=null) ) {
			post.setContentId(postID);
			recommender.setFeedback(post);
		}
		
		// encode them into xml
		Renderer renderer = XMLRenderer.getInstance();
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		renderer.serializeOK(response.getWriter());
	}
	
	/**
	 * parses xml-rendered post in given string 
	 *  
	 * @param input xml-rendered post 
	 * @return
	 */
	private Post<?> parsePost(String input) {
		// parse the post model
		Renderer renderer = XMLRenderer.getInstance();
		Reader doc = new StringReader(input);
		final Post<?> post = renderer.parsePost(doc);
		
		return post;
	}
}


