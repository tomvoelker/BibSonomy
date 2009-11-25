package filters;


import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DebuggingFilter implements Filter {
	
	public static final String STATIC_RESOURCES = "/resources";
	
	private static final Log log = LogFactory.getLog(DebuggingFilter.class);
	
	private int counter = 0;
	
	private String position; 
	
	public void destroy() {
	}
	
	public void init(FilterConfig filterConfig) throws ServletException {
		position = filterConfig.getInitParameter("position");
	}	
	
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		final HttpServletRequest req = (HttpServletRequest) request;
		final String requPath = req.getServletPath();
		/*
		 * ignore resource files (CSS, JPEG/PNG, JavaScript) ... 
		 */
		if (requPath.startsWith(STATIC_RESOURCES)) {
			chain.doFilter(request, response);
			return;
		} 

		
		counter++;
		
		/*
		 * print request parameters
		 */
		
		log(req.getRequestURI() + "  (" + req.getQueryString() + ")");
		final Map parameterMap = req.getParameterMap();
		
		for (Object key : parameterMap.keySet()) {
			final Object value = parameterMap.get(key);
			try {
				log(key + " = " + Arrays.toString((Object[]) value));
			} catch (Exception e) {
				log(key + " = " + value);
			}
			
		}
		
		
		// Pass control on to the next filter
		chain.doFilter(request, response);
	}
	
	private void log (final String s) {
		log.error(position + "(" + counter + "): " + s);
	}
	
}
