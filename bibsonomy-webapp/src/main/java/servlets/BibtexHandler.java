package servlets;

import helpers.constants;
import helpers.database.DBBibtexGetManager;
import helpers.database.DBBibtexManager;
import helpers.database.DBContentManager;
import helpers.database.DBGroupCopyManager;
import helpers.database.DBScraperMetadataManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.tags.database.RecommenderStatisticsManager;
import org.bibsonomy.scraper.KDEScraperFactory;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.TagStringUtils;

import resources.Bibtex;
import resources.Tag;
import beans.BibtexHandlerBean;
import beans.UploadBean;
import beans.UserBean;
import beans.WarningBean;
import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexPerson;
import bibtex.dom.BibtexPersonList;
import bibtex.dom.BibtexString;
import bibtex.dom.BibtexToplevelComment;
import bibtex.expansions.CrossReferenceExpander;
import bibtex.expansions.ExpansionException;
import bibtex.expansions.MacroReferenceExpander;
import bibtex.expansions.PersonListExpander;
import bibtex.parser.BibtexParser;
import bibtex.parser.ParseException;
import filters.ActionValidationFilter;
import filters.SessionSettingsFilter;

public class BibtexHandler extends HttpServlet {

	private static final long serialVersionUID = 3258132444744921394L;
	private static final Log log = LogFactory.getLog(BibtexHandler.class);

	private static final String AND = " and ";

	private DataSource dataSource;
	private static String tempPath = null;

	private static final String LOGIN_INFO = "login.notice.post.publication";

	private static final Scraper scraper = new KDEScraperFactory().getScraper();

	private SpringWrapper springWrapper = SpringWrapper.getInstance();


	private static final HashSet<String> standardFieldNames = new HashSet<String>();
	static {
		/*
		 * TODO: have a look into the Bibtex object, is has a list of fields;
		 * use this list!
		 */
		// TODO: change keywords here
		standardFieldNames.add("abstract");
		standardFieldNames.add("address");
		standardFieldNames.add("annote");
		standardFieldNames.add("author");
		standardFieldNames.add("booktitle");
		standardFieldNames.add("chapter");
		standardFieldNames.add("crossref");
		standardFieldNames.add("edition");
		standardFieldNames.add("editor");
		standardFieldNames.add("howpublished");
		standardFieldNames.add("institution");
		standardFieldNames.add("journal");
		standardFieldNames.add("key");
		standardFieldNames.add("month");
		standardFieldNames.add("note");
		standardFieldNames.add("number");
		standardFieldNames.add("organization");
		standardFieldNames.add("pages");
		standardFieldNames.add("publisher");
		standardFieldNames.add("school");
		standardFieldNames.add("series");
		standardFieldNames.add("title");
		standardFieldNames.add("type");
		standardFieldNames.add("volume");
		standardFieldNames.add("year");
		// added, because otherwise "day" will go to "misc"
		standardFieldNames.add("day");
		// standard fields for bibsonomy
		standardFieldNames.add("description");
		standardFieldNames.add("tags");
		standardFieldNames.add("url");
		standardFieldNames.add("keywords");
		standardFieldNames.add("comment");	// added because CiteULike uses it for (private) comments, which we put into our field, too
		standardFieldNames.add("biburl");   // added because this way it is not added to "misc"
	}

	/*
	 * The dataSource lookup code is added to the init() method to avoid the
	 * costly JNDI operations for every HTTP request.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			dataSource = (DataSource) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("jdbc/bibsonomy");
			tempPath   = config.getServletContext().getInitParameter("rootPath") + "bibsonomy_temp";
		} catch (NamingException ex) {
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy", ex);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		/*
		 * forward all GET-requests to doPost to handle them
		 */
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
		if (currUser == null) {
			String refer = "/login?referer=" + URLEncoder.encode("/BibtexHandler?"+request.getQueryString(), "UTF-8") +  "&notice=" + URLEncoder.encode(LOGIN_INFO, "UTF-8"); 
			response.sendRedirect(refer);
			return;
		}

		/* Establish all connections, result sets and statements */
		Connection conn = null;
		DBBibtexManager bibman = new DBBibtexManager();
		DBContentManager contentman = new DBContentManager();
		DBBibtexGetManager bibgetman = new DBBibtexGetManager();
		DBGroupCopyManager<Bibtex> groupman = new DBGroupCopyManager<Bibtex>();


		/* WARNING */
		WarningBean warnings = new WarningBean();
		String redirectURL = "/user/" + URLEncoder.encode(currUser, "UTF-8");

		try {
			/*
			 * Because the dataSource instance variable is potentially shared across
			 * multiple threads, access to the variable must be from within a
			 * synchronized block.
			 */
			synchronized (dataSource) {
				if (dataSource != null) {
					conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}

			boolean isSnippet    = false;
			boolean isFileUpload = false;
			boolean isManual     = false;

			BibtexHandlerBean bean = (BibtexHandlerBean) request.getAttribute("bibtexHandlerBean");

			if ("upload".equals(request.getParameter("requTask"))) {

				UploadBean bibtexUploadBean = new UploadBean(); // Create UploadBean (for giving error messages to user)

				String contentType   = request.getContentType();
				Reader bibReader     = null;
				String oldhash       = ""; 		// to remember old hash from bean for "move" operation				
				String group         = "public"; 	// default group setting
				String description   = ""; 		// default description setting
				String privnote      = "";
				String delimiter     = null;
				String whitespace    = null;
				boolean substitute   = false;
				int rating = 0;
				int scraperid        = -1;        // database id of scraping metadata row

				if (bean != null) {
					/* ********************************************************
					 * manual post - create bibtex entry as a string to present
					 * it to the parser
					 * ********************************************************/

					if (!ActionValidationFilter.isValidCkey(request)) {
						throw new BibtexException("invalid credentials");
					}

					isManual = true;


					Bibtex bib = bean.getResource();
					group      = bib.getGroup();
					oldhash    = bean.getOldhash(); // remember oldhash for "move" operation
					scraperid  = bean.getScraperid();

					rating     = bib.getRating();

					// get description
					if (bib.getDescription() != null) { description = bib.getDescription().trim();	}
					if (bib.getPrivnote() != null) { privnote = bib.getPrivnote().trim();}

					// get string representation of this object
					StringBuffer entryBuffer = getBibtexString(bib);

					// add tags and tagrelations as last field of entry
					entryBuffer.append("keywords = {" + bean.getTagstring() + " " + buildRelevantForTagString(bean.getRelevantFor()) + "}}"); 

					// put entry into reader
					bibReader = new BufferedReader(new StringReader(entryBuffer.toString()));

				} else if (contentType != null && contentType.startsWith("multipart/form-data")) {

					/* *********************************************
					 * prepare upload
					 * *********************************************/
					List<FileItem> items = null;
					DiskFileItemFactory factory;
					ServletFileUpload upload;
					/* restrict the request size for uploading files */
					int maxRequestSize = 1024 * 1024 * 51;
					int maxThreshold = 1024 * 1024 * 10;

					factory = new DiskFileItemFactory();
					// maximum size that will be stored in memory
					factory.setSizeThreshold(maxThreshold);
					// the location for saving data that is larger than
					// getSizeThreshold()
					factory.setRepository(new File((tempPath)));

					upload = new ServletFileUpload(factory);
					// maximum size before a FileUploadException will be thrown
					upload.setSizeMax(maxRequestSize);

					/* ********************************************
					 * handle file upload
					 * ********************************************/

					redirectURL = "/uploadinfo.jsp";

					isFileUpload = true;

					/* Parse this request by the handler that gives us a list of items from the request	 */
					items = upload.parseRequest(request); // FileUploadException is catched below

					/* Convert list of items into map for convinience */
					Map<String,FileItem> fieldMap = new HashMap<String,FileItem>();
					for (FileItem temp:items) {
						fieldMap.put(temp.getFieldName(), temp);
					}
					items.clear();


					description = getParameter(fieldMap, "description");
					group       = getParameter(fieldMap, "group"); // TODO: necessary: groupid = getGroup(stmtP_select_group, currUser, groupString); ?
					// get parameters for substitution of comma, semicolon, etc. in tagstring
					delimiter   = getParameter(fieldMap, "delimiter");
					whitespace  = getParameter(fieldMap, "whitespace");
					substitute  = !delimiter.trim().equals(""); // something different than " " chosen


					// retrieve form field "file"
					FileItem upFile = fieldMap.get("file");
					String currFile = upFile.getName();

					// retrieve form field "encoding"
					String encoding = getParameter(fieldMap, "encoding");
					if (encoding == null) {
						encoding = "UTF-8";
					}

					final String fileExtension = currFile.substring(currFile.lastIndexOf(".") + 1);

					// check validity of file --> on error, throw exception (handling below)
					if ("".equals(currFile)) {
						throw new BibUploadException ("Please choose a BibTeX or EndNote file!");
					} else if (!fileExtension.equals("bib") && !fileExtension.equals("endnote")) {
						throw new BibUploadException ("Please check your file. Our parser accepts only \".bib\" and \".endnote\" extensions!");
					}
					if (upFile.getSize() < 1) {
						throw new BibUploadException ("Your file is empty or does not exist!");
					}

					/*
					 * check if the user "checked" the checkbox that he is uploading an EndNote-file
					 * else nothing will be done
					 */
					if ("endnote".equals(fileExtension)){
						bibReader = new EndnoteToBibtexConverter().EndnoteToBibtex(new BufferedReader(new InputStreamReader(upFile.getInputStream(), encoding)));
					} else {
						bibReader = new BufferedReader(new InputStreamReader(upFile.getInputStream(), encoding));
					}

					fieldMap.clear();
					upFile.delete();

				} else {
					/* ***************************************************************
					 * try to scrape
					 * ***************************************************************/
					isSnippet = true;
					URL url;
					try {
						url = new URL(request.getParameter("url"));
					} catch (MalformedURLException e) {
						url = null;
					}

					description    = request.getParameter("description"); // nur desc der URL!!!
					group          = request.getParameter("group");
					String snippet = request.getParameter("selection");

					/*
					 * TODO: another dirty scraper hack ... read comment above private class ScraperId ...
					 */
					ScraperId tmp = new ScraperId();
					bibReader = callScrapers(url, snippet, tmp);
					scraperid = tmp.id;
				}

				/* *****************************************************************
				 * parse the BibTeX entries
				 * *****************************************************************/
				final LinkedList<Bibtex> bibtexList = new LinkedList<Bibtex>();
				final int bibTotalCounter = parseBibtex(currUser, warnings, bibtexList, bibReader, description, group, substitute, delimiter, whitespace, rating, privnote);

				// TODO: a lot of comments removed, have look into versions 1.146 or 1.145 which contains the comments
				if (isSnippet) {
					if (bibtexList.isEmpty()) {
						warnings.setWarning("The snippet you posted is not a valid BibTeX entry.");
					}
					redirectURL = "/uploadinfo.jsp";
				}


				/* ********************************************************
				 * just one entry (from file or snippet) -> edit
				 * ********************************************************/
				if (bibtexList.size() + warnings.getIncompleteCount() == 1 && (isSnippet || isFileUpload)) {

					final Bibtex bibtex;

					if (bibtexList.size() == 1) {
						// entry is valid (in bibtexList)
						bibtex = bibtexList.getFirst();						
					} else {
						// entry is invalid (in IncompleteList)
						bibtex = warnings.getIncomplete().getFirst();
					}

					bibtex.setScraperid(scraperid);
					// put bibtex object into bean
					final BibtexHandlerBean bibBean = new BibtexHandlerBean (bibtex);

					bibBean.setPostID(RecommenderStatisticsManager.getNewPID());


					contentman.prepareStatementsForBibtex(conn);
					/* Test, if this bibtex is a duplicate! If yes, get the
					 * old one from DB and send it as oldentry!	 */
					bibgetman.prepareStatements(conn);
					final Bibtex oldBib = bibgetman.getBibtex(bibtex.getHash(), currUser);
					if (oldBib != null) {
						/* this bibtex entry exists for that user --> get existing entry */
						bibBean.setOldentry(oldBib);
					}

					// forward to edit_bibtex page 
					request.setAttribute("bibtexHandlerBean", bibBean);
					getServletConfig().getServletContext().getRequestDispatcher(response.encodeRedirectURL("/edit_bibtex")).forward(request,response);
					return;
				}

				/* TODO: dirty hack (that whole scraperid thing!) 
				 * put scraperid into bibtex object 
				 */
				if (scraperid != -1 && bibtexList.size() > 0) {
					bibtexList.getFirst().setScraperid(scraperid);
				}


				/* ********************************************************
				 * do database stuff
				 * ********************************************************/
				try {
					bibman.prepareStatements(conn);
					groupman.prepareStatements(conn);
					contentman.prepareStatementsForBibtex(conn);

					/* *********************************************************
					 * group copy handling
					 * *********************************************************/
					/* Iterate all bibtex from bibtexList and check current object on for:tags and clone it if
					 * needed. Add cloned objects to bibtexCopyList, which will be merged with bibtexList at the end! */

					/* will be used to save cloned bibtex objects end */
					LinkedList<Bibtex> bibtexCopyList = new LinkedList<Bibtex>();

					for (Bibtex bib:bibtexList) {
						bib.setGroupid(groupman.getGroup(currUser, bib.getGroup()));
						bibtexCopyList.addAll(groupman.getCopiesForGroup(bib, contentman));
					} 
					// merge bibtexList and bibtexCopyList
					bibtexList.addAll(bibtexCopyList);

					/* *****************************************************
					 * Insert/Update Bibtex
					 * *****************************************************/
					int bibSuccessCounter = bibman.updateBibtex(bibtexList, warnings.getDuplicate(), currUser, conn, isManual, oldhash, warnings.getErrors());

					/* send bibcounter with request for user info */
					request.setAttribute("bibCounter", bibSuccessCounter);
					request.setAttribute("bibTotalCounter", bibTotalCounter);

					// for fast tag editing after upload
					if (!isManual) {
						bibtexList.removeAll(warnings.getDuplicate()); 
						bibtexUploadBean.setBibtex(bibtexList);
					} else {
						/*
						 * handle manual post: store post ID for recommender
						 */
						if (bibSuccessCounter == 1) {
							/*
							 * copy post into new model, such that the recommender
							 * can handle it
							 */
							final Post<BibTex> post = copyPostIntoNewModel(currUser, bibtexList.getFirst(), bean.getPostID());

							/*
							 * update recommender table such that recommendations are linked to the final post
							 */
							try {
								springWrapper.getTagRecommender().setFeedback(post);
							} catch (final Exception ex) {
								log.warn("Could not connect post with recommendation: " + ex);
								/*
								 * fail silently to not confuse user with error 500 when recommender fails 
								 */
							}
						}
					}
					// send inserted bibtex for user info
					request.setAttribute("bibtexUploadBean", bibtexUploadBean);

				} catch (SQLException e) {
					conn.rollback(); // rollback all queries, if transaction fails
					log.fatal("Could not insert bibtex objects, failed finally: " + e);
					getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
					return;
				} 

			}// END OF UPLOAD

			/*
			 * *****************************************************************************************************
			 * START REGULAR DELETE
			 * ****************************************************************************************************
			 */
			if ("delete".equals(request.getParameter("requTask"))) {
				if (ActionValidationFilter.isValidCkey(request)) {

					String hash = request.getParameter("hash");

					bibman.prepareStatements(conn);
					bibman.deleteBibtex(conn, currUser, hash);

				}

				response.sendRedirect(redirectURL);
				return;

			} // requTask == TASK_DELETE

			/*
			 * *****************************************************************************************************
			 * END OF DELETE
			 * ***************************************************************************************************
			 */

			request.setAttribute("WarningBean", warnings);

			if (isManual) {
				/* TODO: This is a hack!
				 * If we have a manual post (i.e. only one entry) and this entry is incomplete or erroneous, 
				 * send user back to edit page. 
				 * This hack affects also BibtexHandlerBean: getBibtex() and setBibtex() had to be added!
				 */
				if (warnings.getIncomplete().isEmpty() && warnings.getErrors().isEmpty()) {
					response.sendRedirect("/user/" + URLEncoder.encode(currUser, "UTF-8"));
				} else {
					final Bibtex bibold = bean.getResource();
					if (!warnings.getIncomplete().isEmpty()) {
						/* 
						 * - get the original Bibtex object (which has erroneous fields) and save it (done above)
						 * - then get the one where these fields are missing, put it into bean and call isValid(), 
						 *   so that the error-map of the bean is filled
						 * - afterwards put the old object again into the bean, so that we have the erroneous entries
						 */
						bean.setBibtex(warnings.getIncomplete().getFirst());
						bean.isValid();
					}
					/*
					 * put BibTeX as given by user again into bean
					 */
					bean.setBibtex(bibold);
					request.setAttribute("bibtexHandlerBean", bean);
					/*
					 * add errors
					 */
					for (final String msg: warnings.getErrors().values()) {
						bean.addError("" + msg.hashCode(), msg);
					}

					getServletConfig().getServletContext().getRequestDispatcher("/edit_bibtex").forward(request, response);
				}
				return;
			} 

			/*
			 * FIXME: hack to do a redirect ("after POST") when everything is OK 
			 */
			if (redirectURL.startsWith("/user/")) {
				response.sendRedirect(redirectURL);
			}
			getServletConfig().getServletContext().getRequestDispatcher(response.encodeRedirectURL(redirectURL)).forward(request, response);


			/* **************************************************************************************************
			 * Exception handling block
			 * ***************************************************************************************************/
		} catch (SQLException e) {
			log.fatal(e);
			getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
		} catch (FileUploadException e) {
			// something went wrong, getting the bibtex file from the request
			UploadBean bibtexUploadBean = new UploadBean();
			bibtexUploadBean.setErrors("file","Your request failed: " + e);
			request.setAttribute("upBean", bibtexUploadBean);
			getServletConfig().getServletContext().getRequestDispatcher("/upload_error").forward(request, response);
		} catch (BibUploadException e) {
			// something with the bibtex file is wrong
			UploadBean bibtexUploadBean = new UploadBean();
			bibtexUploadBean.setErrors("file", e.getMessage());
			request.setAttribute("upBean", bibtexUploadBean);
			getServletConfig().getServletContext().getRequestDispatcher("/post_bibtex").forward(request, response);
		} catch (ParseException pe) {
			// parser found error
			UploadBean bibtexUploadBean = new UploadBean();
			bibtexUploadBean.setErrors("file", "Parsing failed: Following error occured " + pe.getMessage());
			request.setAttribute("upBean", bibtexUploadBean);
			getServletConfig().getServletContext().getRequestDispatcher("/upload_error").forward(request, response);
		} catch (FileNotFoundException fnf) {
			// file not found while parsing
			UploadBean bibtexUploadBean = new UploadBean();
			bibtexUploadBean.setErrors("file", "ERROR: " + fnf);
			request.setAttribute("upBean", bibtexUploadBean);
			getServletConfig().getServletContext().getRequestDispatcher("/upload_error").forward(request, response);
		} catch (BibtexException e) {
			BibtexHandlerBean b = (BibtexHandlerBean) request.getAttribute("bibtexHandlerBean");
			b.addError("bibtexexception", e.getMessage());
			request.setAttribute("bibtexHandlerBean", b);
			getServletConfig().getServletContext().getRequestDispatcher("/edit_bibtex").forward(request, response);
		} catch(ScrapingException se) {
			request.setAttribute("error", "Sorry, I was not able to get your bibtex data.\nFollowing error occured: " + se);
			getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
		} finally {
			if (conn != null) {try {conn.close();} catch (SQLException e) {	} conn = null;}
			contentman.closeStatements();
			bibman.closeStatements();
			bibgetman.closeStatements();
			groupman.closeStatements();
		}

	}

	/**
	 * Copies the post into the new model.
	 * 
	 * @param currUser
	 * @param bibtex
	 * @param postID - to allow the recommender to identify the post.
	 * @return
	 */
	private Post<BibTex> copyPostIntoNewModel(String currUser, final Bibtex bibtex, final int postID) {
		/*
		 * post
		 */
		final Post<BibTex> post = new Post<BibTex>();
		post.setUser(new User(currUser));
		post.setResource(new BibTex());
		post.setDate(bibtex.getDate());
		post.setContentId(postID);
		post.setDescription(bibtex.getDescription());
		post.setGroups(Collections.singleton(new Group(bibtex.getGroup())));
		/*
		 * tags
		 */
		for (final String t: bibtex.getTags()) {
			post.addTag(t);
		}
		/*
		 * bibtex
		 */
		final BibTex resource = post.getResource();
		resource.setAbstract(bibtex.getAbstract());
		resource.setAddress(bibtex.getAddress());
		resource.setAnnote(bibtex.getAnnote());
		resource.setAuthor(bibtex.getAuthor());
		resource.setBibtexKey(bibtex.getBibtexKey());
		resource.setBooktitle(bibtex.getBooktitle());
		resource.setChapter(bibtex.getChapter());
		resource.setCrossref(bibtex.getCrossref());
		resource.setDay(bibtex.getDay());
		resource.setEdition(bibtex.getEdition());
		resource.setEditor(bibtex.getEditor());
		resource.setEntrytype(bibtex.getEntrytype());
		resource.setHowpublished(bibtex.getHowpublished());
		resource.setInstitution(bibtex.getInstitution());
		resource.setJournal(bibtex.getJournal());
		resource.setMisc(bibtex.getMisc());
		resource.setMonth(bibtex.getMonth());
		resource.setNote(bibtex.getNote());
		resource.setNumber(bibtex.getNumber());
		resource.setOrganization(bibtex.getOrganization());
		resource.setPages(bibtex.getPages());
		resource.setPrivnote(bibtex.getPrivnote());
		resource.setPublisher(bibtex.getPublisher());
		resource.setSchool(bibtex.getSchool());
		resource.setSeries(bibtex.getSeries());
		resource.setTitle(bibtex.getTitle());
		resource.setType(bibtex.getType());
		resource.setUrl(bibtex.getUrl());
		resource.setVolume(bibtex.getVolume());
		resource.setYear(bibtex.getYear());
		/*
		 * new hashes
		 */
		resource.recalculateHashes();

		return post;
	}


	private static String buildRelevantForTagString(final Collection<String> relevantForGroups) {
		final StringBuffer buf = new StringBuffer();
		if (relevantForGroups != null) {
			for(final String group: relevantForGroups) {
				if (group != null && !group.trim().equals("")) {
					buf.append(SystemTagsUtil.buildSystemTagString(SystemTags.RELEVANTFOR, group) + " ");
				}
			}
		}

		return buf.toString();
	}

	private String getParameter (Map<String,FileItem> fieldMap, String parameterName) {
		FileItem itemValue = fieldMap.get(parameterName);
		if (itemValue != null) {
			String value = itemValue.getString().trim();
			itemValue.delete();
			return value;
		}
		return null;
	}


	/**
	 * Another dirty hack for the scraping stuff ... :-(
	 * this allows callScrapers to return the scraperid in the parameter
	 *
	 */
	private class ScraperId {
		public int id = -1; 
	}

	/** Calls some parsers which try to extract a valid bibtex string from either the snippet
	 * or the URL. 
	 *  
	 * @param url  
	 * @param snippet
	 * @param bibreader TODO
	 * @return an empty string ("") if parsing failed, otherwise a bibtex entry as string
	 * @throws ScrapingException
	 */
	private Reader callScrapers(final URL url, final String snippet, final ScraperId scraperid) throws ScrapingException {
		final ScrapingContext sc = new ScrapingContext(url);
		sc.setSelectedText(snippet);


		/*		CompositeScraper scraper = new CompositeScraper();
		scraper.addScraper(new URLCompositeScraper());
		scraper.addScraper(new SnippetScraper());
		scraper.addScraper(new IEScraper());
		 */

		if (scraper.scrape(sc)) {
			/*
			 * scraping was successful: save metadata (if neccessary)
			 */
			if (sc.getMetaResult() != null) {
				scraperid.id = new DBScraperMetadataManager().insertMetadata(sc);
			}
		} else {
			/* scraping was not successful:
			 * ensure that the result set is empty because the bibtex parser does not accept null values */
			sc.setBibtexResult("");
		}
		return new BufferedReader(new StringReader(sc.getBibtexResult()));
	}

	/**
	 * @param currUser
	 * @param warnings
	 * @param bibtexList
	 * @param bibReader
	 * @param description
	 * @param privnote TODO
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	private int parseBibtex(String currUser, WarningBean warnings, LinkedList<Bibtex> bibtexList, Reader bibReader, String description, String group, boolean substitute, String delimiter, String whitespace, int rating, String privnote) throws ParseException, IOException {
		/* **************************************************
		 * BibTeX file parsing starts here
		 * **************************************************/

		final BibtexParser parser = new BibtexParser(true);
		/*
		 * To allow several "keywords" fields (as done by Connotea), we set the policy
		 * to keep all fields, such that we can access all keywords.
		 * 
		 * Default was KEEP_FIRST, changed by rja on 2008-08-26.
		 */
//		parser.setMultipleFieldValuesPolicy(BibtexMultipleFieldValuesPolicy.KEEP_ALL);
		final BibtexFile bibtexFile = new BibtexFile();

		// parse file, exceptions are catched below
		parser.parse(bibtexFile, bibReader);


		// boolean topComment = false;
		// String topLevelComment;//stores comment or snippet, depending on bibtex entries

		// boolean standard = true;

		/* ****************************************************************
		 * expand all macros, crossrefs and convert author/editor field
		 * values into BibtexPersonList objects
		 * ****************************************************************/

		MacroReferenceExpander macroExpander = new MacroReferenceExpander(true, true, false, false);
		try {
			macroExpander.expand(bibtexFile);
		} catch (ExpansionException ee) {
			warnings.setWarning(ee.getMessage());
		}

		CrossReferenceExpander crossExpander = new CrossReferenceExpander(true);
		try {
			crossExpander.expand(bibtexFile);
		} catch (ExpansionException ee) {
			warnings.setWarning(ee.getMessage());
		}

		PersonListExpander pListExpander = new PersonListExpander(true,	true, false);
		try {
			pListExpander.expand(bibtexFile);
		} catch (ExpansionException ee) {
			warnings.setWarning(ee.getMessage());
		}



		/* ****************************************************************
		 * iterate over all entries and put them in Bibtex objects
		 * ****************************************************************/
		int bibTotalCounter = 0;        // counts all bibtex entries
		Date currDate = new Date();


		// TODO: teste, ob snippet && entries leer && toplevelcomment
		// vorhanden -> falls ja, dann(snippet) in db
		for (Object potentialEntry:bibtexFile.getEntries()) {


			// TODO: insert handling of invalid snippets which go into "to parse" table HERE
			if (!(potentialEntry instanceof BibtexEntry)) {
				/*
				 * Process top level comment, but drop macros, because
				 * they are already expanded!
				 */
				if (potentialEntry instanceof BibtexToplevelComment) {
					/*
					 * Retrieve and process Toplevel Comment if
					 * needed??? BibtexToplevelComment comment =
					 * (BibtexToplevelComment) potentialEntry; String
					 * topLevelComment = comment.getContent();
					 */
					continue;
				} else {
					continue;
				}
			}

			// finally we got an entry
			bibTotalCounter++;
			Bibtex bib = new Bibtex();
			bib.setDescription(description);
			bib.setPrivnote(privnote);
			bib.setGroup(group);
			bib.setUser(currUser);
			bib.setRating(rating);


			// fill other fields from entry
			fillBibtexFromEntry(potentialEntry, bib, substitute, delimiter, whitespace);

			/*
			 * handle date
			 * TODO: this is another DBLP hack: for the DBLP user we use the "date" (misc-)field 
			 * to set the date of the entry  
			 */
			if (constants.dblpUser.equals(currUser)) {
				try {
					bib.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(((BibtexString) ((BibtexEntry) potentialEntry).getFieldValue("date")).getContent()));
				} catch (java.text.ParseException e) {
					bib.setDate(currDate);
				}
			} else {
				bib.setDate(currDate);
			}

			// collect all valid bibtex entries in a list
			if (bib.isValidBibtex()) {
				bibtexList.add(bib);
			} else {
				warnings.addIncomplete(bib);
			}

		} // end of entry iteration
		return bibTotalCounter;
	}

	/** Concatenates all fields of the bibtex object into a string and builds a bibtex string representation.
	 * TODO: this is also done in Bibtex.getBibtex(), but there the authors are already normalized (i.e., 
	 * divided by " and " and not new line. Furthermore there a biburl={} field is added and there are 
	 * other small differences. Nevertheless it would be good, to have just one method for creating a bibtex
	 * string from a bibtex object
	 * 
	 * @param bib the bibtex object
	 * @return a string representation of bib
	 */
	private StringBuffer getBibtexString(Bibtex bib) {
		StringBuffer entryBuffer = new StringBuffer();

		// author/editor handling for manual bibtex upload
		String authors = "";
		String editors = "";

		// replace line breaks with " and " to help bibtex parser
		if (bib.getAuthor()      != null) { authors = replaceLinebreak(bib.getAuthor().trim()); }
		if (bib.getEditor()      != null) { editors = replaceLinebreak(bib.getEditor().trim()); }

		// create bibtex entry
		entryBuffer.append("@" + bib.getEntrytype().trim() + "{"); // set entry type like "article"
		entryBuffer.append(bib.getBibtexKey().trim() + ",");       // set bibtex key
		entryBuffer.append("title = {" + bib.getTitle() + "},");
		entryBuffer.append("author = {" + authors + "},");
		entryBuffer.append("editor = {" + editors + "},");

		if (bib.getType()           != null && !bib.getType().equals(""))           { entryBuffer.append("type = {"         + bib.getType()           + "},"); }
		if (bib.getPages()          != null && !bib.getPages().equals(""))          { entryBuffer.append("pages = {"        + bib.getPages()          + "},"); }
		if (bib.getUrl()            != null && !bib.getUrl().equals(""))            { entryBuffer.append("url = {"          + bib.getUrl()            + "},"); }
		if (bib.getAnnote()         != null && !bib.getAnnote().equals(""))         { entryBuffer.append("annote= {"        + bib.getAnnote()         + "},"); }
		if (bib.getAddress()        != null && !bib.getAddress().equals(""))        { entryBuffer.append("address = {"      + bib.getAddress()        + "},"); }	
		if (bib.getBooktitle()      != null && !bib.getBooktitle().equals(""))      { entryBuffer.append("booktitle = {"    + bib.getBooktitle()      + "},"); }
		if (bib.getChapter()        != null && !bib.getChapter().equals(""))        { entryBuffer.append("chapter = {"      + bib.getChapter()        + "},"); }
		if (bib.getCrossref()       != null && !bib.getCrossref().equals(""))       { entryBuffer.append("crossref = {"     + bib.getCrossref()	    + "},"); }
		if (bib.getEdition()        != null && !bib.getEdition().equals(""))        { entryBuffer.append("edition = {"      + bib.getEdition()        + "},"); }
		if (bib.getHowpublished()   != null && !bib.getHowpublished().equals(""))   { entryBuffer.append("howpublished = {" + bib.getHowpublished()   + "},"); }
		if (bib.getInstitution()    != null && !bib.getInstitution().equals(""))    { entryBuffer.append("institution = {"	+ bib.getInstitution()    + "},"); }
		if (bib.getJournal()        != null && !bib.getJournal().equals(""))        { entryBuffer.append("journal = {"      + bib.getJournal()        + "},"); }
		if (bib.getKey()            != null && !bib.getKey().equals(""))            { entryBuffer.append("key = {"          + bib.getKey()            + "},"); }
		if (bib.getYear()           != null && !bib.getYear().equals(""))           { entryBuffer.append("year = {"         + bib.getYear()           + "},"); }
		if (bib.getMonth()          != null && !bib.getMonth().equals(""))          { entryBuffer.append("month = {"        + bib.getMonth()          + "},"); }
		if (bib.getDay()            != null && !bib.getDay().equals(""))            { entryBuffer.append("day = {"          + bib.getDay()            + "},"); }				
		if (bib.getNote()           != null && !bib.getNote().equals(""))           { entryBuffer.append("note = {"         + bib.getNote()           + "},"); }
		if (bib.getNumber()         != null && !bib.getNumber().equals(""))         { entryBuffer.append("number = {"       + bib.getNumber()         + "},"); }
		if (bib.getOrganization()   != null && !bib.getOrganization().equals(""))   { entryBuffer.append("organization = {"	+ bib.getOrganization()   + "},"); }
		if (bib.getPublisher()      != null && !bib.getPublisher().equals(""))      { entryBuffer.append("publisher = {"    + bib.getPublisher()		+ "},"); }
		if (bib.getSchool()         != null && !bib.getSchool().equals(""))         { entryBuffer.append("school = {"       + bib.getSchool()         + "},"); }
		if (bib.getSeries()         != null && !bib.getSeries().equals(""))         { entryBuffer.append("series = {"       + bib.getSeries()         + "},"); } 
		if (bib.getVolume()         != null && !bib.getVolume().equals(""))         { entryBuffer.append("volume = {"       + bib.getVolume()         + "},"); }
		if (bib.getBibtexAbstract() != null && !bib.getBibtexAbstract().equals("")) { entryBuffer.append("abstract = {"		+ bib.getBibtexAbstract() + "},"); }

		// handle "misc" fields
		String misc = bib.getMisc();
		if (misc != null) {
			misc = misc.trim();
			if (! "".equals(misc)) {
				if (!misc.endsWith(",")) {
					// append colon
					misc = misc + ",";
				}
				entryBuffer.append(misc);
			}
		}
		return entryBuffer;
	}


	/*
	 * this method does the main bibtex part - after parsing it gets all field 
	 * values from the parsed Entry and fills the bibtex object
	 */

	private void fillBibtexFromEntry(Object potentialEntry, Bibtex bib, boolean substitute, String delimiter, String whitespace) {
		BibtexEntry entry = (BibtexEntry) potentialEntry;
		/* ************************************************
		 * process non standard bibtex fields 
		 * ************************************************/

		// get set of all current fieldnames - like address, author etc.
		ArrayList<String> nonStandardFieldNames = new ArrayList<String>(entry.getFields().keySet());
		// remove standard fields from list to retrieve nonstandard ones
		nonStandardFieldNames.removeAll(standardFieldNames);

		// iter over arraylist to retrieve nonstandard field values
		StringBuffer miscBuffer = new StringBuffer();
		for (String next:nonStandardFieldNames) {
			miscBuffer.append(next + " = {"	+ ((BibtexString) entry.getFieldValue(next)).getContent() + "}, ");
		}
		// remove last colon
		if (miscBuffer.length() > 3) {
			miscBuffer.delete(miscBuffer.length() - 2, miscBuffer.length());
		}

		bib.setMisc(miscBuffer.toString());

		/* ************************************************
		 * process standard bibtex fields 
		 * ************************************************/


		/* ************************************************
		 * mandatory fields
		 * ************************************************/

		BibtexString field = null;

		// retrieve entry/bibtex key
		bib.setBibtexKey(entry.getEntryKey());
		// retrieve entry type - should not be null or ""
		bib.setEntrytype(entry.getEntryType());

		// TODO: remove ELSE parts - just added for DB compatibility with last release (also do it for authors, editors below)

		field = (BibtexString) entry.getFieldValue("title"); if (field != null) bib.setTitle(field.getContent());
		field = (BibtexString) entry.getFieldValue("year");  if (field != null) bib.setYear(field.getContent()); 

		/* ************************************************
		 * optional fields
		 * ************************************************/

		field = (BibtexString) entry.getFieldValue("crossref");     if (field != null) bib.setCrossref(field.getContent());     
		field = (BibtexString) entry.getFieldValue("address");      if (field != null) bib.setAddress(field.getContent());      
		field = (BibtexString) entry.getFieldValue("annote");       if (field != null) bib.setAnnote(field.getContent());       
		field = (BibtexString) entry.getFieldValue("booktitle");    if (field != null) bib.setBooktitle(field.getContent());    
		field = (BibtexString) entry.getFieldValue("chapter");      if (field != null) bib.setChapter(field.getContent());      
		field = (BibtexString) entry.getFieldValue("day");          if (field != null) bib.setDay(field.getContent());
		field = (BibtexString) entry.getFieldValue("edition");      if (field != null) bib.setEdition(field.getContent());      
		field = (BibtexString) entry.getFieldValue("howpublished"); if (field != null) bib.setHowpublished(field.getContent()); 
		field = (BibtexString) entry.getFieldValue("institution");	if (field != null) bib.setInstitution(field.getContent());  
		field = (BibtexString) entry.getFieldValue("journal");      if (field != null) bib.setJournal(field.getContent());      
		field = (BibtexString) entry.getFieldValue("key");	        if (field != null) bib.setKey(field.getContent());          
		field = (BibtexString) entry.getFieldValue("month");        if (field != null) bib.setMonth(field.getContent());        
		field = (BibtexString) entry.getFieldValue("note");         if (field != null) bib.setNote(field.getContent());         
		field = (BibtexString) entry.getFieldValue("number");       if (field != null) bib.setNumber(field.getContent());       
		field = (BibtexString) entry.getFieldValue("organization"); if (field != null) bib.setOrganization(field.getContent()); 
		field = (BibtexString) entry.getFieldValue("pages");        if (field != null) bib.setPages(field.getContent());        
		field = (BibtexString) entry.getFieldValue("publisher");    if (field != null) bib.setPublisher(field.getContent());    
		field = (BibtexString) entry.getFieldValue("school");       if (field != null) bib.setSchool(field.getContent());       
		field = (BibtexString) entry.getFieldValue("series");       if (field != null) bib.setSeries(field.getContent());       
		field = (BibtexString) entry.getFieldValue("url");          if (field != null) bib.setUrl(field.getContent());           
		field = (BibtexString) entry.getFieldValue("volume");		if (field != null) bib.setVolume(field.getContent());        
		field = (BibtexString) entry.getFieldValue("abstract");		if (field != null) bib.setBibtexAbstract(field.getContent());
		field = (BibtexString) entry.getFieldValue("type");  		if (field != null) bib.setType(field.getContent());          
		field = (BibtexString) entry.getFieldValue("description");	if (field != null) bib.setDescription(field.getContent());

		
		/*
		 * rja, 2009-06-30
		 * CiteULike uses the "comment" field to export (private) notes in the form
		 * 
		 * comment = {(private-note)This is a test note!}, 
		 * 
		 * Thus, we here extract the field and remove the "(private-note)" part
		 */
		field = (BibtexString) entry.getFieldValue("comment");	if (field != null) bib.setPrivnote(field.getContent().replace("(private-note)", ""));
		
		
		
		/*
		 * parse person names for author + editor
		 */
		bib.setAuthor(createPersonString(entry.getFieldValue("author")));
		bib.setEditor(createPersonString(entry.getFieldValue("editor")));


		/* ************************************************
		 * tags
		 * ************************************************/
		// merge "tags" and "keywords"
		StringBuffer allTags = new StringBuffer();
		BibtexAbstractValue tagAbstractValue;

		tagAbstractValue = entry.getFieldValue("tags");
		if (tagAbstractValue != null) {
			// clean tags
			allTags.append(TagStringUtils.cleanTags(((BibtexString) tagAbstractValue).getContent(), substitute, delimiter, whitespace) + " ");
		}
		tagAbstractValue = entry.getFieldValue("keywords");
		if (tagAbstractValue != null) {
			// clean tags
			allTags.append(TagStringUtils.cleanTags(((BibtexString) tagAbstractValue).getContent(), substitute, delimiter, whitespace));
		}
		/*
		 * parsing of several tags - replace above by code below
		 */ 
//		System.out.println("Getting keywords");
//		final List tagAbstractValueList = entry.getFieldValuesAsList("keywords");

//		System.out.println("got list: " + tagAbstractValueList);
//		if (tagAbstractValueList != null) {
//		System.out.println("list is not null");

//		for (final Object keyword: tagAbstractValueList) {
//		System.out.println("found keyword: " + keyword);
//		// clean tags
//		allTags.append(TagStringUtils.cleanTags(((BibtexString) keyword).getContent(), substitute, delimiter, whitespace) + " ");
//		System.out.println("allTags is now: " + allTags);
//		}
//		}



//		field = (BibtexString) entry.getFieldValue("tags");     if (field != null) {allTags.append(field.getContent() + " ");}
		//field = (BibtexString) entry.getFieldValue("keywords"); if (field != null) {allTags.append(field.getContent());      }
		// see, if tag is available or not
		if (allTags.toString().trim().equals("")) {
			bib.addTag(Tag.IMPORTED_TAG);
		} else {
			bib.setTags(allTags.toString());
		}
	}

	/** (Re-)creates a valid BibTeX person string from the parsed list of persons.
	 * 
	 * @param fieldValue
	 * @return
	 */
	private String createPersonString (final BibtexAbstractValue fieldValue) {
		if (fieldValue != null && fieldValue instanceof BibtexPersonList) {
			/*
			 * cast into a person list and extract the persons
			 */
			final List<BibtexPerson> personList = ((BibtexPersonList) fieldValue).getList();
			/*
			 * result buffer
			 */
			final StringBuffer personBuffer = new StringBuffer();
			/*
			 * build person names
			 */
			for (final BibtexPerson person:personList) {
				/*
				 * build one person
				 */
				final StringBuffer personName = new StringBuffer();
				/*
				 * first name
				 */
				final String first = person.getFirst();
				if (first != null) personName.append(first);
				/*
				 * between first and last name
				 */
				final String preLast = person.getPreLast();
				if (preLast != null) personName.append(" " + preLast);
				/*
				 * last name
				 */
				final String last = person.getLast();
				if (last != null) personName.append(" " + last);
				/*
				 * "others" has a special meaning in BibTeX (it's converted to "et al."),
				 * so we must not ignore it! 
				 */
				if (person.isOthers()) personName.append("others");
				/*
				 * next name
				 */
				personBuffer.append(personName.toString().trim() + AND);
			}
			/* 
			 * remove last " and " 
			 */
			if (personBuffer.length() > AND.length()) {
				return personBuffer.substring(0, personBuffer.length() - AND.length());
			} 
		}
		return null;
	}

	// Exception to be called, when something went wront on the upload process
	private class BibUploadException extends Exception {
		private static final long serialVersionUID = 23978;

		public BibUploadException (String m) {
			super(m);
		}
	}

	/*
	 * if we find an error which has nothing to do with upload or so: throw this exception
	 */ 
	public static class BibtexException extends Exception {
		private static final long serialVersionUID = 23978;

		public BibtexException (String m) {
			super(m);
		}
	}


	/*
	 * //for entrytype parsing private boolean isStandardType (String type){
	 * 
	 * HashSet standardTypes = new HashSet();
	 * 
	 * standardTypes.add("article"); standardTypes.add("book");
	 * standardTypes.add("booklet"); standardTypes.add("inbook");
	 * standardTypes.add("incollection"); standardTypes.add("inproceedings");
	 * standardTypes.add("manual"); standardTypes.add("masterthesis");
	 * standardTypes.add("misc"); standardTypes.add("phdthesis");
	 * standardTypes.add("proceedings"); standardTypes.add("techreport");
	 * standardTypes.add("unpublished");
	 * 
	 * if(standardTypes.contains(type)){ return true; }else{ return false; } }
	 */


	// exchanges linebreaks by " and " 
	private String replaceLinebreak(String s) {
		return s.replaceAll("\r\n", " and ");
	}

}// END OF CLASS
