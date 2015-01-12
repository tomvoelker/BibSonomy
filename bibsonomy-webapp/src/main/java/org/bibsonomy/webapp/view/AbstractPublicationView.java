/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.PublicationViewCommand;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.JstlView;

/**
 * @author jensi
 * @param <CMD> Type of the Command that is required for this view
 */
public abstract class AbstractPublicationView<CMD extends PublicationViewCommand > extends AbstractView {

	/**
	 * TODO: move to FileUtil (together with {@link #getCleanedFilename(String)}?
	 */
	private static final Pattern FILE_NAME_DISALLOWED_CHARACTERS = Pattern.compile("^[A-Za-z0-9_\\-\\.]");
	
	private static final Log log = LogFactory.getLog(BibTeXView.class);
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

		/*
		 * get command
		 */
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		final CMD command = castCmd(object);

		if (command != null) {
			try {
				response.setContentType("text/plain; charset=UTF-8");
				response.setCharacterEncoding(StringUtils.CHARSET_UTF_8);

				if (command.isDownload()) { // FIXME: add to command/allowed fields - do we want to use a string or is a boolean sufficient? 
					/*
					 * TODO: How to properly encode the file name? Or should we 
					 * just remove all characters except A-Za-z0-0 
					 */
					response.setHeader("Content-Disposition", "attachement; filename=" + createFileName(command.getBibtex().getList(), command.getFormat()));
				}
				/*
				 * output stream
				 */
				final ServletOutputStream outputStream = response.getOutputStream();
				final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StringUtils.CHARSET_UTF_8);

				render(command, writer);
				
				writer.close();
			} catch (IOException e) {
				throw new IOException(e);
			}
		} else {
			log.error("unknown publicationview command: " + object);
			response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
			final BindingResult errors = ViewUtils.getBindingResult(model);
			errors.reject("error.layout.rendering", new Object[]{}, "Could not render publication layout");
			/*
			 * do the rendering ... a bit tricky: we need to get an appropriate JSTL view and give it the 
			 * application context
			 */
			final JstlView view = new JstlView("/WEB-INF/jsp/error.jspx");
			view.setApplicationContext(getApplicationContext());
			view.render(model, request, response);
		}
	}

	protected abstract CMD castCmd(Object object);

	protected abstract void render(final CMD command, final OutputStreamWriter writer) throws IOException;

	protected String createFileName(List<Post<BibTex>> list, String format) {
		StringBuilder sb = new StringBuilder();
		if (ValidationUtils.present(list) == false) {
			sb.append("empty");
		} else {
			sb.append(list.get(0).getResource().getEntrytype());
			if (list.size() > 0) {
				sb.append("_etc");
			}
		}
		sb.append(getFileTypeSuffix(format));
		return getCleanedFilename(sb);
	}

	protected String getFileTypeSuffix(String format) {
		if ((Views.FORMAT_STRING_BIB.equals(format) || Views.FORMAT_STRING_BIBTEX.equals(format))) {
			return ".bib";
		}
		return "." + format;
	}

	private static String getCleanedFilename(final CharSequence filename) {
		return FILE_NAME_DISALLOWED_CHARACTERS.matcher(filename).replaceAll("");
	}
	

}
