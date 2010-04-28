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

import org.bibsonomy.webapp.command.actions.DownloadFileCommand;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;

/**
 * View class for the download of a document attached to a bibtex entry
 * 
 * @author cvo
 * @version $Id$
 */
public class DocumentDownloadView extends AbstractView{

	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		
		if (object instanceof DownloadFileCommand) {
			
			/*
			 * command object
			 */
			final DownloadFileCommand command = (DownloadFileCommand)object;
			
			/*
			 * file to stream
			 */
			final File document = new File(command.getPathToFile());
			
			/*
			 * set HTTP headers
			 */
			response.setHeader("Content-Disposition","inline; filename*='utf-8'" + URLEncoder.encode(command.getFilename(), "UTF-8"));
			setContentType(command.getContentType());
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
		}		
	}
}
