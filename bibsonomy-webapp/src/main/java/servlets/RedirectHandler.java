package servlets;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import resources.Resource;


public class RedirectHandler extends HttpServlet {
	
	// bibsonomy URLs for several formats
	private static final String[][] FORMAT_URLS = new String[][] {
			{"html", 	null, 	null},
			{"rss", 	"rss", 	"publrss"},
			{"xml", 	"xml",	"layout/dblp"},
			{"rdf",		null,	"burst"},
			{"bibtex",	null,	"bib"}
	};	
	
	private static final long serialVersionUID = 3691036578076309554L;	
	
	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
	}	
	
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost (request, response);
	}

	/*
	 * if data is sent through a POST form, we cannot navigate with current prev/next style and so on - 
	 * we need the data as GET parameter, which is ugly. Therefore we redirect to a "pretty" url, i.e.
	 * 
	 * /url?url=http://www.slashdot.org  ---> /url/afa796f945f58897689a76d9876e
	 * or
	 * /search?search=web --> /search/web
	 *  
	 * 
	 */
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requPage = request.getParameter("page");
		/*
		 * redirect queries for /url?... to /url/
		 */
		if ("url".equals(requPage)) {
			request.getSession(true).setAttribute("url",request.getParameter("requUrl"));
			response.sendRedirect("/url/" + Resource.hash(request.getParameter("requUrl")));
		} else
		/*
		 * redirect queries for /search?... to /search/
		 * - if scope is some user (i.e. "user:*"), add it to search
		 */
		if ("search".equals(requPage)) {
			String search = request.getParameter("q");     /* what do we search */
			String scope  = request.getParameter("scope"); /* in which entries do we search */
			if (scope != null && !"all".equals(scope)) {   /* scope is not "all" --> add scope to query */
				search = search + " " + scope;
			}
			response.sendRedirect("/search/" + URLEncoder.encode(search, "UTF-8"));
		} else
		/*
		 * redirect either to /user/*, to /author/*, to /tag/* or to /concept/tag/* page 
		 */
		if (requPage.equals("specialsearch")) {
			String search = request.getParameter("q");     /* what do we search */
			String scope  = request.getParameter("scope"); /* in which entries do we search */
			if ("tag".equals(scope)) {
				response.sendRedirect("/tag/" + URLEncoder.encode(search, "UTF-8"));
			} else 
				if ("user".equals(scope)) {
					response.sendRedirect("/user/" + URLEncoder.encode(search, "UTF-8"));
				} else if("group".equals(scope)){
					response.sendRedirect("/group/" + URLEncoder.encode(search, "UTF-8"));
				} else if("author".equals(scope)) {
					String requUser = request.getParameter("requUser");
					if (requUser != null) {
						response.sendRedirect("/author/" + URLEncoder.encode(search,"UTF-8") + "?requUser=" + URLEncoder.encode(requUser, "UTF-8"));
					} else {
						response.sendRedirect("/author/" + URLEncoder.encode(search,"UTF-8"));
					}
				} else if ("concept".equals(scope)) {
					response.sendRedirect("/concept/tag/" + URLEncoder.encode(search,"UTF-8"));
				} else if ("all".equals(scope)) {					
					response.sendRedirect("/search/" + URLEncoder.encode(search, "UTF-8"));						
				} else if (scope.startsWith("user"))  {
					search = search + " " + scope;					
					response.sendRedirect("/search/" + URLEncoder.encode(search, "UTF-8"));
				}
		} else 
		/* ********************************************************************************
		 * CONTENT NEGOTIATION
		 * redirect to requested output format in dependence on http header field "accept"
		 */
		if ("uri".equals(requPage)) {
			String requResource = request.getParameter("requResource");
			int contentType 	= ("url".equals(requResource)) ? 1 : 2;
			String requContent	= request.getParameter("requContent");			
			String accept		= request.getHeader("accept").toLowerCase();	
			
			int i=0;			
			while (i < FORMAT_URLS.length) {
				if (accept.indexOf(FORMAT_URLS[i][0].toLowerCase()) > -1) 					
					break;				
				i++;
			}
			
			// no correct format found --> send to standard HTML page
			if (i >= FORMAT_URLS.length)
				i = 0;
			
			// build redirectURL
			StringBuffer redirectURL = new StringBuffer();
			if (FORMAT_URLS[i][contentType] != null) {
				redirectURL.append("/" + FORMAT_URLS[i][contentType]);
			}			
			redirectURL.append("/" + requResource + "/" + URLEncoder.encode(requContent,"UTF-8"));
					
			response.sendRedirect(redirectURL.toString());			
		} else {
			response.sendRedirect("/");
		}
	}	
}