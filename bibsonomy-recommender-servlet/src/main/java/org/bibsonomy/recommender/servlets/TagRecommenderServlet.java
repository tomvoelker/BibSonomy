package org.bibsonomy.recommender.servlets;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

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
	
	/**
	 * the tag recommender
	 */
	TagRecommender recommender;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TagRecommenderServlet() {
        super();
    }
    
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
		// query data: the (xml)-encoded post - this fake recommender ignores post data  
		final String dataString = request.getParameter("data");

		// generate list of recommended tags
		dispatchQuery(response, dataString);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// query data: the (xml)-encoded post - this fake recommender ignores post data  
		final String dataString = request.getParameter("data");

		// generate list of recommended tags
		dispatchQuery(response, dataString);
	}
	
	/**
	 * dispatches recommender query
	 * 
	 * @param response where to write the serialized tags
	 * @param dataString the xml post model
	 * @throws IOException 
	 */
	protected void dispatchQuery(HttpServletResponse response, String dataString) throws IOException {
		// parse the post model
		Renderer renderer = XMLRenderer.getInstance();
		Reader doc = new StringReader(dataString);
		final Post<?> post = renderer.parsePost(doc);
		
		// query recommender
		Collection<RecommendedTag> tags = null;
		if( recommender!=null )
			tags = recommender.getRecommendedTags(post);
		
		// encode them into xml
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		renderer.serializeRecommendedTags(response.getWriter(), tags);
	}
}
