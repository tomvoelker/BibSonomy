package servlets;

import helpers.MultiPartRequestParser;
import helpers.database.DBLayoutManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.layout.jabref.JabrefLayoutUtils;
import org.bibsonomy.layout.jabref.LayoutPart;
import org.bibsonomy.util.StringUtils;

import servlets.listeners.InitialConfigListener;
import beans.UploadBean;
import filters.SessionSettingsFilter;

public class LayoutHandler extends HttpServlet { 

	private static final Log log = LogFactory.getLog(LayoutHandler.class);
	private static final long serialVersionUID = 3839748679655351876L;

	private static String documentPath = null;
	private static String rootPath = null;

	/**
	 * An instance of the (new!) layout renderer. We need it here to unload custom
	 * user layouts.
	 */
	private JabrefLayoutRenderer jabrefLayoutRenderer = JabrefLayoutRenderer.getInstance(); 

	public void init(ServletConfig config) throws ServletException {	
		super.init(config); 
		rootPath = InitialConfigListener.getInitParam("rootPath");
		documentPath = rootPath + "bibsonomy_docs/";
	}

	/** 
	 * Deletes the specified layout.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		final String currUser = SessionSettingsFilter.getUser(request).getName(); 
		final String action = request.getParameter("action");

		if ("delete".equals(action) && currUser != null) {
			/*
			 * FIXME: not needed any longer (at least for the renderer, because
			 * it unloads ALL layouts of the user).
			 */
			final String hash = request.getParameter("hash");
			/* 
			 * delete entry in database
			 */
			if (DBLayoutManager.deleteLayout(currUser, hash)) {
				/*
				 * delete file from filesystem
				 */
				new File(documentPath + hash.substring(0,2) + "/" + hash).delete();
				/*
				 * delete layout object from exporter
				 */
				jabrefLayoutRenderer.unloadUserLayout(currUser);
			}
			response.sendRedirect("/settings#layout");
		}
	}


	/**
	 * Handles upload of custom layout filters.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String currUser = SessionSettingsFilter.getUser(request).getName(); 

		if (currUser == null) {
			response.sendRedirect("/login");
			return;
		}

		/*
		 * Use MultiPartRequestParser to retrieve all fields 
		 * from uploaded form. You'll get Map of 
		 * Hier den MultiPartRequestParser(request) aufrufen, damit 
		 * man eine Map zurück erhält, die FileItems enthält!
		 * Map<String, FileItem> fieldMap = MultiPartRequestParser.getFields(request,rootPath);
		 */
		try {
			final MultiPartRequestParser parser = new MultiPartRequestParser(); 
			final Map<String, FileItem> fieldMap = parser.getFields(request, rootPath);

			for (final String layoutType:LayoutPart.getLayoutTypes()) {

				// retrieve form field "file"
				final FileItem file = fieldMap.get("file." + layoutType);

				/*
				 * check for null (user might choose to upload only one of the three layouts
				 * if filename is empty, user has not choosen a file for that type: don't try 
				 * to check file ending
				 */
				if (file != null) {
					final String fileName = file.getName();

					if (fileName != null && !fileName.trim().equals("")) {

						// check file extension
						if (!StringUtils.matchExtension(fileName, "layout")) {
							throw new FileUploadException ("Please check your file. Only layout files are accepted. ");	
						}

						// get hashed name
						final String hashedName = JabrefLayoutUtils.userLayoutHash(currUser, LayoutPart.getLayoutType(layoutType));

						// build path from first two letters of file name hash
						final String docPath = documentPath + hashedName.substring(0, 2).toLowerCase();

						/* *************************************************
						 * save file and insert data into database
						 * *************************************************/
						final File inputFile = new File(docPath, hashedName);
						log.debug("writing user layout file to " + inputFile);

						if (DBLayoutManager.insertLayout(currUser, hashedName, fileName)) {
							file.write(inputFile);	
						} 
						// writing into file was ok -> delete fileitem upfile
						file.delete();
					}
				}
			}
			// redirect to bibtex entry
			response.sendRedirect("/settings#layout");
		} catch (Exception e) {
			log.fatal("layout upload for user " + currUser + " failed.", e);
			// if it failed, send user to error page
			final UploadBean bean = new UploadBean();
			bean.setErrors("file", "Your upload failed: " + e);
			request.setAttribute("upBean", bean);
			getServletConfig().getServletContext().getRequestDispatcher("/upload_error").forward(request, response);		
		}
	}


}