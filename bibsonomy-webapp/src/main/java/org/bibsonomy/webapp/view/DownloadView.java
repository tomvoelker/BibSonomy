package org.bibsonomy.webapp.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.webapp.command.actions.DownloadFileCommand;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;

/**
 * View class for the download of a document attached to a bibtex entry
 * @author cvo
 * @version $Id$
 */
public class DownloadView extends AbstractView{

	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		
		if(object instanceof DownloadFileCommand) {
			
			/**
			 * internal path of the requested document
			 */
			String pathToFile = null;
			
			/**
			 * filename of the requested document
			 */
			String filename = null;
			
			/**
			 * content type of the requested document
			 */
			String contentTyp = null;
			
			/**
			 * command object
			 */
			DownloadFileCommand command = (DownloadFileCommand)object;
			
			pathToFile = command.getPathToFile();
			filename = command.getFilename();
			contentTyp = command.getContentType();
			File document = new File(pathToFile);
			
			response.setHeader("Content-Disposition","inline; filename*='utf-8'" + URLEncoder.encode(filename, "UTF-8"));
			response.setContentType(contentTyp);
			response.setContentLength((int) document.length());
			
			/**
			 * streaming of the requested document to the user
			 */
			ServletOutputStream output = response.getOutputStream();
			BufferedInputStream buf = null;	
			try {
				buf = new BufferedInputStream(new FileInputStream(pathToFile));
				int readBytes = 0;
				// read from the file; write to the ServletOutputStream
				while ((readBytes = buf.read()) != -1) output.write(readBytes);
			} catch (IOException ioe) {
				throw new ServletException(ioe.getMessage());
			} finally {
				if (output != null) output.close();
				if (buf    != null)	buf.close();
			}
		}		
	}
}
