package servlets;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

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
				} else if ("bibtexkey".equals(scope)) {
					response.sendRedirect("/bibtexkey/" + URLEncoder.encode(search,"UTF-8"));
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
			String requContent2	= request.getParameter("requContent2");
			String accept		= request.getHeader("accept");	
			
			// get response format
			int index = getResponseFormat(accept, contentType);
					
			// build redirectURL
			StringBuffer redirectURL = new StringBuffer();
			if (FORMAT_URLS[index][contentType] != null) 
				redirectURL.append("/" + FORMAT_URLS[index][contentType]);							
						
			if (requContent2 == null || requContent2.equals("")) 
				redirectURL.append("/" + requResource + "/" + URLEncoder.encode(requContent,"UTF-8"));
			else 
				redirectURL.append("/" + requResource + "/" + URLEncoder.encode(requContent,"UTF-8") + "/" + URLEncoder.encode(requContent2,"UTF-8"));
								
			// send HTTP 303 redirect
			response.setStatus(HttpServletResponse.SC_SEE_OTHER);
			response.setHeader("Location", redirectURL.toString());			
		} else {
			response.sendRedirect("/");
		}
	}	
	
	/**
	 * gets the preferred response format which is supported in 
	 * dependence of the 'q-Value' (similar to a priority)
	 *
	 * @param acceptHeader 
	 * 			the HTML ACCEPT Header
	 * 			(example: 
	 * 				<code>ACCEPT: text/xml,text/html;q=0.9,text/plain;q=0.8,image/png</code>
	 * 				would be interpreted in the following precedence:
	 * 				1) text/xml
	 * 				2) image/png
	 * 				3) text/html
	 * 				4) text/plain)
	 * 			) 	
	 * @param contentType
	 * 			the contentType of the requested resource 
	 * 			<code>0</code> for bookmarks
	 * 			<code>1</code> for BibTeX
	 * @return 
	 * 			an index for access to the FORMAT_URLS array with the 
	 * 			url for redirect
	 */
	private int getResponseFormat(final String acceptHeader, int contentType) {
		
		int responseFormat = 0;

		// if no acceptHeader is set, return default (= 0);
		if (acceptHeader == null) return responseFormat;
		
		// maps the q-value to output format
		SortedMap<Double,Vector<String>> preferredTypes = new TreeMap<Double,Vector<String>>(new Comparator<Double>() {
			public int compare(Double o1, Double o2) {
				if (o1.doubleValue() > o2.doubleValue())
					return -1;
				else if (o1.doubleValue() < o2.doubleValue())
					return 1;
				else
					return o1.hashCode() - o2.hashCode();
			}				
		});		
		
		// fill map with q-values an formats
		Scanner scanner = new Scanner(acceptHeader.toLowerCase());
		scanner.useDelimiter(",");
			
		while(scanner.hasNext()) {
			String[] types = scanner.next().split(";");
			String type = types[0];
			double qValue = 1;
			
			if (types.length != 1) 
				qValue = Double.parseDouble(types[1].split("=")[1]);
			
			if (!preferredTypes.containsKey(qValue)) {
				Vector<String> v = new Vector<String>();
				v.add(type);				
				preferredTypes.put(qValue, v);
			} else {
				preferredTypes.get(qValue).add(type);					
			}
		}
					
		List<String> formatOrder = new ArrayList<String>();			
		for (Entry<Double, Vector<String>> entry: preferredTypes.entrySet()) {								
			for (String type: entry.getValue()) {				
				formatOrder.add(type);					
			}
		}
		
		// check for supported formats
		boolean found = false;
		for (String type: formatOrder) {
			if (found) break;
			
			for (int j=0; j<FORMAT_URLS.length; j++) {					
				String checkType = FORMAT_URLS[j][0];				
				if (type.indexOf(checkType) != -1) {						
					if (FORMAT_URLS[j][contentType] != null || checkType == "html") {
						responseFormat = j;
						found = true;
						break;	
					}
				}
			}
		}		
		return responseFormat;
	}
}