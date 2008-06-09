package servlets;

import filters.SessionSettingsFilter;
import helpers.MultiPartRequestParser;
import helpers.database.DBLayoutManager;
import helpers.export.bibtex.ExportBibtex;
import helpers.export.bibtex.ExportBibtex.LayoutType;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;

import resources.Bibtex;
import servlets.listeners.InitialConfigListener;
import tags.Functions;
import beans.ResourceBean;
import beans.UploadBean;
import beans.UserBean;

public class LayoutHandler extends HttpServlet { 

	private static final Logger log = Logger.getLogger(LayoutHandler.class);
	private static final long serialVersionUID = 3839748679655351876L;

	private static String documentPath = null;
	private static String rootPath = null;
	private static final ExportBibtex export = ExportBibtex.getInstance();
	private static HashMap<String,OutputType> typemap = new HashMap<String,OutputType>();


	public void init(ServletConfig config) throws ServletException {	
		super.init(config); 
		rootPath = InitialConfigListener.getInitParam("rootPath");
		documentPath = rootPath + "bibsonomy_docs/";
		initTypeMap(); // initialize mapping of type names to content-type/file-ending 
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		/* Get the session attribute of current user  */
		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 

		String action = request.getParameter("action");

		if ("delete".equals(action) && currUser != null) {
			String hash = request.getParameter("hash");
			/* 
			 * delete entry in database
			 */
			if (DBLayoutManager.deleteLayout(currUser, hash)) {
				/*
				 * delete file from filesystem
				 */
				new File(documentPath + hash.substring(0,2) + "/" + hash).delete();
				//delete layout object from exporter, if necessary
				export.unloadCustomFilter(hash);
			}
			response.sendRedirect("/settings#layout");
		} else {
			/*
			 * format bibtex list for chosen layout
			 */

			String layout = (String) request.getAttribute("layout");
			OutputType t = typemap.get(layout.toLowerCase());
			if (t != null) {
				StringBuffer exportStringBuffer = null;
				try {
					/*
					 * get bibtex list - either with or without duplicates, depending on users choice
					 */
					final ResourceBean resourceBean = ((ResourceBean) request.getAttribute("ResourceBean"));
					final Collection<Bibtex> bibtexList;
					if ("no".equals(request.getParameter("duplicates"))) {
						bibtexList = resourceBean.getBibtexSortedByYear();
					} else {
						bibtexList = resourceBean.getBibtex();
					}
					exportStringBuffer = export.exportBibtex(bibtexList, currUser, layout);
				} catch (Exception e) {
					/*
					 * loading of layout failed: send error message to user
					 */
					log.warn("could not load layout for user " + currUser + ": " + e);
					request.setAttribute("error", "Sorry, I was not able to load the layout: " + e.getMessage());
					getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
					return;
				}
				/*
				 * set header
				 */
				response.setContentType(t.contentType);
				response.setCharacterEncoding("UTF-8");
				if (t.ending != null) {
					response.setHeader("Content-Disposition", "attachement; filename=" + Functions.makeCleanFileName((String)request.getAttribute("requPath")) + "." + t.ending);
				}
				/*
				 * send output to user
				 */
				response.getOutputStream().write(exportStringBuffer.toString().getBytes("UTF-8"));
				response.getOutputStream().close();

			} else {
				/*
				 * could not find layout in map for content types
				 */
				request.setAttribute("error", "Sorry, I was not able to load the layout " + layout + ".");
				getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
			}
		}
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 

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
			MultiPartRequestParser parser = new MultiPartRequestParser(); 
			Map<String, FileItem> fieldMap = parser.getFields(request, rootPath);

			for (String layoutType:LayoutType.getLayoutTypes()) {

				// retrieve form field "file"
				FileItem file = (FileItem) fieldMap.get("file." + layoutType);
				String fileName = file.getName();
				
				/*
				 * if filename is empty, user has not choosen a file for that type: don't try 
				 * to check file ending
				 */
				if (file != null && fileName != null && !fileName.trim().equals("")) {

					// check file extension
					if (!DocumentUploadHandler.matchExtension(fileName, "layout")) {
						throw new FileUploadException ("Please check your file. Only layout files are accepted. ");	
					}

					// get hashed name
					String hashedName = ExportBibtex.userLayoutHash(currUser, LayoutType.getLayoutType(layoutType));

					// build path from first two letters of file name hash
					String docPath = documentPath + hashedName.substring(0, 2).toLowerCase();

					/* *************************************************
					 * save file and insert data into database
					 * *************************************************/
					File inputFile = new File(docPath, hashedName);			

					if (DBLayoutManager.insertLayout(currUser, hashedName, fileName)) {
						file.write(inputFile);	
					} 
					// writing into file was ok -> delete fileitem upfile
					file.delete();
				}
			}
			// redirect to bibtex entry
			response.sendRedirect("/settings#layout");
		} catch (Exception e) {
			log.fatal("layout upload for user " + currUser + " failed: " + e);
			// if it failed, send user to error page
			UploadBean bean = new UploadBean();
			bean.setErrors("file", "Your upload failed: " + e);
			request.setAttribute("upBean", bean);
			getServletConfig().getServletContext().getRequestDispatcher("/upload_error").forward(request, response);		
		}
	}

	private void initTypeMap() {
		/*
		 * add default layout types (from JabRef 2.2) TODO: this needs to be updated,
		 * when layout directory is updated! 
		 * 
		 * TODO: put this into a properties file or something like that
		 * 
		 * TODO: if you add/change something here, you have to also add/change this on exports.jsp! 
		 * 
		 * TODO: please rewrite this using a configuration file! changing sourcecode is awkward
		 * 
		 *  NOTE: case is ignored
		 */
		typemap.put("html",                new OutputType(null, "text/html"));
		typemap.put("tablerefs",           new OutputType(null, "text/html"));
		typemap.put("tablerefsabsbib",     new OutputType(null, "text/html"));
		typemap.put("tablerefsabsbibsort", new OutputType(null, "text/html"));
		typemap.put("simplehtml",          new OutputType(null, "text/html"));
		typemap.put("custom",              new OutputType(null, "text/html"));

		typemap.put("bibtexml",            new OutputType(null, "text/xml"));
		typemap.put("docbook",             new OutputType(null, "text/xml"));
		typemap.put("dblp",                new OutputType(null, "text/xml"));

		typemap.put("harvard",             new OutputType("rtf", "text/rtf"));

		typemap.put("endnote",             new OutputType(null, "text/plain"));
		typemap.put("text",                new OutputType(null, "text/plain"));
		
		typemap.put("openoffice-csv",      new OutputType("csv", "text/comma-separated-values"));
		
		// additional layouts, installed by us
		typemap.put("se",          	       new OutputType(null, "text/html"));
		typemap.put("jucs", 		       new OutputType("rtf", "text/rtf"));
	}

	private static class OutputType {
		public String ending;
		public String contentType;
		public OutputType(String ending, String contentType) {
			super();
			this.ending = ending;
			this.contentType = contentType;
		}
	}


}