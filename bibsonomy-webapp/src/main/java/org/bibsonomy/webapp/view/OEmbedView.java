package org.bibsonomy.webapp.view;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONSerializer;

import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.directwebremoting.util.SwallowingHttpServletResponse;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.JstlView;


/**
 * Implements oEmbed output according to http://oembed.com/
 * 
 * For JSP rendering within Java, see 
 * http://technologicaloddity.com/2011/10/04/render-and-capture-the-output-of-a-jsp-as-a-string/
 * 
 * @author rja
 */
public class OEmbedView extends AbstractView {

	private ViewResolver viewResolver; // FIXME: unused
	private String projectName;
	private String projectHome;

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		/*
		 * get command
		 */
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);

		if(object instanceof SimpleResourceViewCommand) {
			final SimpleResourceViewCommand command = (SimpleResourceViewCommand)object;

			// These String objects are used to capture the output
			// of SwallowingHttpServletResponse
			final StringWriter sout = new StringWriter();

			final HttpServletResponse swallowingResponse = new SwallowingHttpServletResponse(response, sout, "UTF-8");


			try {
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");

				// "include" the file (but not really an include) with the dispatcher
				// The resulting rendering will come out in swallowing response,
				// via sbuffer
//				final RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/oembed.jspx");
//				dispatcher.include(request, swallowingResponse);

				// alternative approach / try
				// FIXME: having WEB-INF and .jspx here is not nice - can't we use the viewResolver instead?
				final JstlView view = new JstlView("/WEB-INF/jsp/export/oembed.jspx");  
				view.setApplicationContext(getApplicationContext());
				view.render(model, request, swallowingResponse);

				final StringBuffer sbuffer = sout.getBuffer();

				/*
				 * output stream
				 */
				final ServletOutputStream outputStream = response.getOutputStream();
				final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

				final Map<String, String> map = new HashMap<String, String>();


				map.put("type", "rich");
				map.put("version", "1.0");
				if (present(command.getPageTitle())) {
					map.put("title", command.getPageTitle());
				}
				//map.put("author_name", "1000"); // TODO: could be used on /user pages
				//map.put("author_url", "1000"); // TODO: could be used on /user pages
				map.put("provider_name", projectName);
				map.put("provider_url", projectHome); 
				//map.put("cache_age", projectHome);
				map.put("width", "1000"); // TODO
				map.put("height", "800"); // TODO

				// payload
				map.put("html", sbuffer.toString());

				// FIXME: is there a cleaner way to provide JSONP?
				writer.write(command.getCallback() + "(");
				writer.write(JSONSerializer.toJSON(map).toString());
				writer.write(");");
				
				writer.close();

			} catch(Exception e) {
				throw new IOException(e);
			}
		} else {
			// TODO: not supported command
			response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		}

	}

	public void setViewResolver(final ViewResolver viewResolver) {
		this.viewResolver = viewResolver;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}

}
