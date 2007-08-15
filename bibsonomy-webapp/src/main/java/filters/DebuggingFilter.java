package filters;


import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class DebuggingFilter implements Filter {
	
	private static int counter = 0;
	
	public void destroy() {
	}
	
	public void init(FilterConfig filterConfig) throws ServletException {
	}	
	
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		counter++;
		
		/*
		 * print request parameters
		 */
		HttpServletRequest req = (HttpServletRequest) request;
		
		log(req.getRequestURI() + "  (" + req.getQueryString() + ")");
		
		
		// Pass control on to the next filter
		chain.doFilter(request, response);
	}
	
	private void log (String s) {
		System.out.println("DebbuggingFilter(" + counter + "): " + s);
	}
	
}
