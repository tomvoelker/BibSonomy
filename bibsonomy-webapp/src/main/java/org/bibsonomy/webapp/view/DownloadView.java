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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.DownloadCommand;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;

/**
 * View class for the download of a document attached to a bibtex entry
 * 
 * @author cvo
 */
@SuppressWarnings("deprecation")
public class DownloadView extends AbstractView {
	private static final Log log = LogFactory.getLog(DownloadView.class);
	
	
	@Override
	protected void renderMergedOutputModel(final Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		
		if (object instanceof DownloadCommand) {
			
			/*
			 * command object
			 */
			final DownloadCommand command = (DownloadCommand)object;
			
			/*
			 * file to stream
			 */
			final File document = new File(command.getPathToFile());
			
			/*
			 * set HTTP headers
			 */
			response.setHeader("Content-Disposition","inline; filename*='" + StringUtils.CHARSET_UTF_8.toLowerCase() + "'" + URLEncoder.encode(command.getFilename(), StringUtils.CHARSET_UTF_8));
			response.setContentType(command.getContentType());
			response.setContentLength((int) document.length());
			
			/*
			 * streaming of the requested document to the user
			 */
			final BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
			BufferedInputStream buf = null;	
			try {
				buf = new BufferedInputStream(new FileInputStream(document.getAbsolutePath()));
				int readBytes = 0;
				// read from the file; write to the ServletOutputStream
				while ((readBytes = buf.read()) != -1) output.write(readBytes);
			} catch (IOException ioe) {
				throw new ServletException(ioe.getMessage());
			} finally {
				output.close();
				if (buf != null) buf.close();
			}
		} else {
			log.warn("Command is not instance of DownloadCommand, doing nothing");
		}
	}
}
