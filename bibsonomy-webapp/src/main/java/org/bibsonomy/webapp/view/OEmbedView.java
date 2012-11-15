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
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.JstlView;


/**
 * For JSP rendering within Java, see 
 * http://technologicaloddity.com/2011/10/04/render-and-capture-the-output-of-a-jsp-as-a-string/
 * 
 * @author rja
 * @version $Id$
 */
public class OEmbedView extends AbstractView implements ServletContextAware {

	private ViewResolver viewResolver; // FIXME: unused

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
				final JstlView view = new JstlView("/WEB-INF/jsp/export/oembed.jspx");
				view.setApplicationContext(getApplicationContext());
				view.render(model, request, swallowingResponse);
				

				final StringBuffer sbuffer = sout.getBuffer();

				System.out.println("wrote " + sbuffer.length() + " characters");


				/*
				 * output stream
				 */
				final ServletOutputStream outputStream = response.getOutputStream();
				final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

				final Map<String, String> map = new HashMap<String, String>();


				map.put("version", "1.0");
				map.put("type", "rich");
				map.put("width", "1000"); // TODO
				map.put("height", "800"); // TODO
				if (present(command.getPageTitle())) {
					map.put("title", command.getPageTitle());
				}
				map.put("url", "TODO: how to get the URL?"); // TODO
				map.put("provider_name", "TODO: use project.name"); // TODO
				map.put("provider_url", "TODO: use project.home"); // TODO

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

}
