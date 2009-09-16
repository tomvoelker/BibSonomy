package servlets;

import helpers.database.DBBibtexURLManager;
import helpers.database.DBExtendedFieldManager;
import helpers.database.DBPrivnoteManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.SortedSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.extra.BibTexExtra;

import resources.Bibtex;
import resources.ExtendedFieldMap;
import beans.UserBean;
import filters.ActionValidationFilter;
import filters.SessionSettingsFilter;

public class ExtendedFieldsHandler extends HttpServlet{

	private static final long serialVersionUID = 4051324539558769200L;

	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final UserBean user = SessionSettingsFilter.getUser(request);
		final String currUser = user.getName(); 
		if (currUser == null) {
			response.sendRedirect("/login?referer=/basket");
			return;
		}

		final DBExtendedFieldManager eman = new DBExtendedFieldManager();

		/*
		 * FIXME: added to allow handling of some stuff at /bibtex/HASH/USER
		 */
		final String action = request.getParameter("action");
		final boolean validCkey = ActionValidationFilter.isValidCkey(request);
		if (validCkey && ("addURL".equals(action) || "deleteURL".equals(action))) {
			final String hash = request.getParameter("hash");
			
			/*
			 * add / delete extra URL
			 */
			final String urlString = request.getParameter("url");
			final URL url2;
			try {
				url2 = new URL(urlString);
			} catch (final MalformedURLException ex) {
				request.setAttribute("error", "The URL you entered is invalid (" + ex.getMessage() + ").");
				getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
				return;
			}
			if ("addURL".equals(action)) {
				DBBibtexURLManager.createURL (new BibTexExtra(url2, request.getParameter("text"), null), hash, currUser, validCkey);
			} else if ("deleteURL".equals(action)) {
				DBBibtexURLManager.deleteURL (new BibTexExtra(url2, null, null), hash, currUser, validCkey);
			}
			
			response.sendRedirect("/bibtex/" + HashID.INTRA_HASH.getId() + URLEncoder.encode(hash, "UTF-8") + "/" + URLEncoder.encode(currUser, "UTF-8"));
			return;
		}


		/*
		 * TODO: uncomment this to generate meta data fields for NEPOMUK
		 */
		//eman.createExtendedFieldsMap("nepomuk", create_nepo_fields());
		//eman.createExtendedFieldsMap("prolearn", create_nepo_fields());


		// TODO: check, if user is in this group!
		String group = request.getParameter("group");
		SortedSet<ExtendedFieldMap> extendedFieldsMap = eman.getExtendedFieldsMap(group, currUser);
		if (extendedFieldsMap == null || extendedFieldsMap.size() == 0) {
			request.setAttribute("error", "This group has no extended fields defined.");
			getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
			return;
		}


		request.setAttribute("resources", eman.getExtendedFields(group, currUser));
		request.setAttribute("nonown",    eman.getPickedEntries(currUser));
		request.getSession().setAttribute("extended_fields_map", extendedFieldsMap);


		getServletConfig().getServletContext().getRequestDispatcher("/edit_extended_fields.jsp").forward(request,response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
		if (currUser == null) {
			response.sendRedirect("/login?referer=/basket");
			return;
		}

		/*
		 * action update private note
		 */
		final String action = request.getParameter("action");
		final boolean validCkey = ActionValidationFilter.isValidCkey(request);
		if (action != null && validCkey) {
			if ("updatePrivateNote".equals(action)) {		
				final String hash = request.getParameter("hash");
				
				/*
				 * FIXME: missing check, if currUser owns this publication!
				 */
				final String privnote    = request.getParameter("privnote");
				final String oldprivnote = request.getParameter("oldprivnote");
				if (((privnote == null && oldprivnote != null) || (privnote != null && (oldprivnote == null || !privnote.equals(oldprivnote))))) {
					/*
					 * something has changed --> write it to DB
					 */
					DBPrivnoteManager.setPrivnoteForUser(privnote, currUser, hash);
					
				}
				
				response.sendRedirect("/bibtex/" + HashID.INTRA_HASH.getId() + URLEncoder.encode(hash, "UTF-8") + "/" + URLEncoder.encode(currUser, "UTF-8"));
				return;
			}
		}
		
		DBExtendedFieldManager eman = new DBExtendedFieldManager();

		/*
		 * get map for extended fields either from session or from database
		 * TODO: since set is put into session again (end of this method), it
		 * will never get updated!
		 */
		SortedSet<ExtendedFieldMap> extendedFieldsMap;
		if (request.getSession().getAttribute("extended_fields_map") == null) {
			extendedFieldsMap = eman.getExtendedFieldsMap(request.getParameter("group"), currUser);
		} else {
			extendedFieldsMap = (SortedSet<ExtendedFieldMap>) request.getSession().getAttribute("extended_fields_map");
		}

		LinkedList<Bibtex> currresources = eman.getExtendedFields(request.getParameter("group"), currUser);

		/*
		 * put set into map such that we can access keys via their order
		 */
		HashMap<Integer, ExtendedFieldMap> extendedfields_map = new HashMap<Integer, ExtendedFieldMap>(); 
		for (ExtendedFieldMap e:extendedFieldsMap) {
			extendedfields_map.put(e.getOrder(), e);
		}

		/*
		 * mapping id->Bibtex, we can map Bibtex values to content ids via oldhash
		 */
		HashMap<Integer,Bibtex> resources_map = new HashMap<Integer,Bibtex>();

		/*
		 * parse request parameter for extended fields and put them all into a map
		 */
		Enumeration e = request.getParameterNames() ;
		while (e.hasMoreElements()) {
			String param_name = (String) e.nextElement();
			/*
			 * check for hash
			 */
			if (param_name.length() == 32) {
				/*
				 * hash -> id mapping found!
				 */
				int id = Integer.parseInt(request.getParameter(param_name));
				if (!resources_map.containsKey(id)) {
					/*
					 * create new bibtex object
					 */
					resources_map.put(id, new Bibtex());
				}
				/*
				 * object already exists! --> store hash
				 * 
				 * small hack: use oldhash to store the hash; we retrieve the content id later, when
				 * we insert the data
				 */
				resources_map.get(id).setOldHash(param_name);
				/*
				 * get content_id from existing resources
				 */
			} else if (param_name.matches("[0-9]+_[0-9]+")) {
				/*
				 * found a key-value pair for extended fields
				 * 
				 * name = id_order
				 */
				/*
				 * extract id and order
				 */
				int cutpos = param_name.indexOf('_');
				int id     = Integer.parseInt(param_name.substring(0, cutpos));
				int order  = Integer.parseInt(param_name.substring(cutpos + 1, param_name.length()));
				if (!resources_map.containsKey(id)) {
					/*
					 * create new bibtex object
					 */
					resources_map.put(id, new Bibtex());
				}
				/*
				 * add key-value pair
				 */
				resources_map.get(id).addExtended_fields(extendedfields_map.get(order).getKey(), request.getParameter(param_name));
			}

		}
		/*
		 * insert fields into database
		 */
		if (eman.setExtendedFields(extendedFieldsMap, resources_map, currresources)) {
			request.setAttribute("status", "Successfully changed metadata.");
		} else {
			request.setAttribute("status", "Error changing metadata.");
		}

		request.getSession().setAttribute("resources", currresources);
		request.getSession().setAttribute("extended_fields_map", extendedFieldsMap);
		request.getSession().setAttribute("nonown", eman.getPickedEntries(currUser));

		getServletConfig().getServletContext().getRequestDispatcher("/edit_extended_fields.jsp").forward(request,response);


	}

	private HashSet<ExtendedFieldMap> create_nepo_fields() {
		HashSet<ExtendedFieldMap> list = new HashSet<ExtendedFieldMap>();
		ExtendedFieldMap map;

		int order = 1;

		map = new ExtendedFieldMap();
		map.setKey("Status");
		map.setDescription("might be: actual, planned, canceled");
		map.setOrder(order++);
		list.add(map);

		map = new ExtendedFieldMap();
		map.setKey("Date(s)");
		map.setDescription("planned or actual dates");
		map.setOrder(order++);
		list.add(map);

		map = new ExtendedFieldMap();
		map.setKey("Type");
		map.setDescription("e.g. press release, web presence, article in journal");
		map.setOrder(order++);
		list.add(map);

		map = new ExtendedFieldMap();
		map.setKey("Type of audience");
		map.setDescription("e.g. general public, research, industry");
		map.setOrder(order++);
		list.add(map);

		map = new ExtendedFieldMap();
		map.setKey("Countries addressed");
		map.setDescription("e.g. France, Europe, International");
		map.setOrder(order++);
		list.add(map);

		map = new ExtendedFieldMap();
		map.setKey("Size of audience");
		map.setDescription("");
		map.setOrder(order++);
		list.add(map);

		map = new ExtendedFieldMap();
		map.setKey("Partner responsible");
		map.setDescription("");
		map.setOrder(order++);
		list.add(map);

		map = new ExtendedFieldMap();
		map.setKey("Workpackage");
		map.setDescription("");
		map.setOrder(order++);
		list.add(map);

		map = new ExtendedFieldMap();
		map.setKey("Contact to main author (email)");
		map.setDescription("");
		map.setOrder(order++);
		list.add(map);

		map = new ExtendedFieldMap();
		map.setKey("Comments");
		map.setDescription("");
		map.setOrder(order++);
		list.add(map);

		return list;
	}

}
