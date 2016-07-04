/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.directwebremoting.util.SwallowingHttpServletResponse;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;


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
		if (object instanceof SimpleResourceViewCommand) {
			final SimpleResourceViewCommand command = (SimpleResourceViewCommand)object;

			// These String objects are used to capture the output
			// of SwallowingHttpServletResponse
			final StringWriter sout = new StringWriter();

			final HttpServletResponse swallowingResponse = new SwallowingHttpServletResponse(response, sout, StringUtils.CHARSET_UTF_8);


			try {
				response.setContentType("application/json");
				response.setCharacterEncoding(StringUtils.CHARSET_UTF_8);
				
				final View view = this.viewResolver.resolveViewName("export/oembed", request.getLocale());
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
				
				final String pageTitle = command.getPageTitle();
				if (present(pageTitle)) {
					map.put("title", pageTitle);
				}
				
				//map.put("author_name", "1000"); // TODO: could be used on /user pages
				//map.put("author_url", "1000"); // TODO: could be used on /user pages
				map.put("provider_name", this.projectName);
				map.put("provider_url", this.projectHome); 
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

	/**
	 * @param viewResolver the viewResolver to set
	 */
	public void setViewResolver(ViewResolver viewResolver) {
		this.viewResolver = viewResolver;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @param projectHome the projectHome to set
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}
}
