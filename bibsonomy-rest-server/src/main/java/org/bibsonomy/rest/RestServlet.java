package org.bibsonomy.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.database.DBLogicApiInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.file.MultiPartRequestParser;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Christian Kramer
 * @version $Id$
 */
public final class RestServlet extends HttpServlet {

	/**
	 * Used in {@link #validateAuthorization(String)} to identify HTTP basic authentication.
	 */
	private static final String HTTP_AUTH_BASIC_IDENTIFIER = "Basic ";

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(RestServlet.class);

	/** name of the servlet-parameter that configures the logicFactoryClass to use */
	public static final String PARAM_LOGICFACTORY_CLASS = "logicFactoryClass";

	private LogicInterfaceFactory logicFactory;
	
	// store some infos about the specific request or the webservice (i.e. rootPath)
	private final Map<String, String> additionalInfos = new HashMap<String, String>();

	@Override
	public void init() throws ServletException {
		super.init();
		// instantiate the bibsonomy database connection
		final String logicFactoryClassName = this.getServletConfig().getInitParameter(PARAM_LOGICFACTORY_CLASS);
		// get the roopath of bibsonomy out of the web.xml
		final ServletContext servletContext = getServletContext();
		additionalInfos.put("rootPath", servletContext.getInitParameter("rootPath"));
		// declare the path where all documents will be stored
		/*
		 * FIXME: make doc path configurable via web.xml (or another config file)
		 */
		additionalInfos.put("docPath", servletContext.getInitParameter("rootPath") + "bibsonomy_docs/"); 
		// get the projectHome out of the web.xml
		additionalInfos.put("projectHome", servletContext.getInitParameter("projectHome"));
		
		if (logicFactoryClassName != null) {
			Object logicFactoryObj;
			try {
				final Class<?> logicFactoryClass = this.getClass().getClassLoader().loadClass(logicFactoryClassName);
				logicFactoryObj = logicFactoryClass.newInstance();
			} catch (Exception e) {
				throw new ServletException("problem while instantiating " + logicFactoryClassName, e);
			}			
			if (logicFactoryObj instanceof LogicInterfaceFactory) {
				this.logicFactory = (LogicInterfaceFactory) logicFactoryObj;
			} else {
				throw new ServletException(logicFactoryClassName + " does not implement " + LogicInterfaceFactory.class.getName());
			}
			log.info("using logicFactoryClass '" + logicFactoryClassName + "'");
		} else {
			final String errorMsg = "no 'logicFactoryClass' initParameter -> using default";
			log.info(errorMsg);
			DBLogicApiInterfaceFactory logicFactory = new DBLogicApiInterfaceFactory();
			logicFactory.setDbSessionFactory(new IbatisDBSessionFactory());
			this.logicFactory = logicFactory;
		}
	}

	/**
	 * Use this setter in junit tests to initialize the test database.
	 * 
	 * FIXME: could be removed if we would use a DI-Framework
	 */
	void setLogicInterface(LogicInterfaceFactory logicInterfaceFactory) {
		this.logicFactory = logicInterfaceFactory;
	}

	/**
	 * Use this class in junit tests to access the test-database
	 * 
	 * @return the {@link LogicInterface}
	 */
	LogicInterfaceFactory getLogic() {
		return this.logicFactory;
	}

	/**
	 * Respond to a GET request for the content produced by this servlet.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 * @param response
	 *            The servlet response we are producing
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet error occurs
	 */
	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
		handle(request, response, HttpMethod.GET);
	}

	@Override
	public void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		handle(request, response, HttpMethod.PUT);
	}

	@Override
	public void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		handle(request, response, HttpMethod.DELETE);
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		handle(request, response, HttpMethod.POST);
	}

	@Override
	public void doHead(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		validateAuthorization(request.getHeader("Authorization"));
	}

	/**
	 * @param request
	 *            the servletrequest
	 * @param response
	 *            the servletresponse
	 * @param method
	 *            httpMethod to use, see {@link HttpMethod}
	 * @throws IOException
	 */
	private void handle(final HttpServletRequest request, final HttpServletResponse response, final HttpMethod method) throws IOException {

		log.debug("Incoming Request: " + method.name() + " " + request.getRequestURL() + " from IP " + request.getHeader("x-forwarded-for"));
		Long start = System.currentTimeMillis();

		try {
			// validate the requesting user's authorization
			final LogicInterface logic = validateAuthorization(request.getHeader("Authorization"));
			
			// parse the request object to retrieve a list with all items of the http request
			MultiPartRequestParser parser = new MultiPartRequestParser(request);
			
			// create Context
			final Context context = new Context(request.getInputStream(), logic, method, request.getPathInfo(), request.getParameterMap(), parser.getList(), additionalInfos);

			// validate request
			context.validate();

			// set some response headers
			response.setContentType(context.getContentType(request.getHeader("User-Agent")));
			response.setCharacterEncoding("UTF-8");

			// send answer
			if (method.equals(HttpMethod.POST)) {
				// if a POST request completes successfully this means that a resource has been created
				response.setStatus(HttpServletResponse.SC_CREATED);
			}
			else {
				response.setStatus(HttpServletResponse.SC_OK);
			}
			
			//just define an ByteArrayOutputStream to store all outgoing data
			final ByteArrayOutputStream cachingStream = new ByteArrayOutputStream();
			
			
			context.perform(cachingStream);
			// XXX: cachingStream.size() != cachingStream.toString().length() !!
			// the correct value is the first one!
			response.setContentLength(cachingStream.size());
			
			// some more logging
			log.debug("Size of output sent:" + cachingStream.size());
			Long elapsed = System.currentTimeMillis() - start;
			log.debug("Processing time: " + elapsed + " ms");
			

			cachingStream.writeTo(response.getOutputStream());
		} catch (final AuthenticationException e) {
			log.warn(e.getMessage());
			/*
			 * FIXME: string "BibSonomy" should never occur in source code!
			 */
			response.setHeader("WWW-Authenticate", "Basic realm=\"BibSonomyWebService\"");
			sendError(request, response, HttpURLConnection.HTTP_UNAUTHORIZED, e.getMessage());
		} catch (final InternServerException e) {
			log.error(e.getMessage());
			sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (final NoSuchResourceException e) {
			log.error(e.getMessage());
			sendError(request, response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		} catch (final BadRequestOrResponseException e) {
			log.error(e.getMessage());
			sendError(request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (final ValidationException e) {
			log.error(e.getMessage());
			sendError(request, response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		} catch (final ResourceMovedException e) {
			log.error(e.getMessage());
			/*
			 * sending new location
			 * TODO: add date using  
			 * 
			 */
			response.setHeader("Location", UrlRenderer.getInstance().createHrefForResource(e.getUserName(), e.getNewIntraHash()));
			sendError(request, response, HttpServletResponse.SC_MOVED_PERMANENTLY, e.getMessage());
		} catch (final DatabaseException e ) {
			final StringBuffer returnMessage = new StringBuffer("");
			for (final String hash: e.getErrorMessages().keySet()) {
				for (final ErrorMessage em: e.getErrorMessages(hash)) {
					log.error(em.toString());
					returnMessage.append(em.toString() + "\n ");
				}
			}
			sendError(request, response, HttpServletResponse.SC_BAD_REQUEST, returnMessage.toString());
		}
		catch (final Exception e) {
			log.error(e,e);
			// well, lets fetch each and every error...
			sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	/**
	 * Sends an error to the client.
	 * 
	 * @param request
	 *            the current {@link HttpServletRequest} object.
	 * @param response
	 *            the current {@link HttpServletResponse} object.
	 * @param code
	 *            the error code to send.
	 * @param message
	 *            the message to send.
	 * @throws IOException
	 */
	private void sendError(final HttpServletRequest request, final HttpServletResponse response, final int code, final String message) throws IOException {
		// get renderer
		final String renderingFormatName = Context.getStringAttribute(request.getParameterMap(), "format", "xml");
		RenderingFormat renderingFormat;
		try { 
			renderingFormat = RenderingFormat.getRenderingFormat(renderingFormatName);
		}
		catch (Exception ex) {
			renderingFormat = RenderingFormat.XML;
		}
		final Renderer renderer = RendererFactory.getRenderer(renderingFormat);

		// send error
		response.setStatus(code);
		response.setContentType(RestProperties.getInstance().getContentType());
		final ByteArrayOutputStream cachingStream = new ByteArrayOutputStream();
		final Writer writer = new OutputStreamWriter(cachingStream, Charset.forName("UTF-8"));
		renderer.serializeError(writer, message);
		response.setContentLength(cachingStream.size());
		response.getOutputStream().print(cachingStream.toString("UTF-8"));
	}

	/**
	 * @param authentication
	 *            Authentication-value of the header's request
	 * @throws IOException
	 */
	LogicInterface validateAuthorization(final String authentication) throws AuthenticationException {
		if (authentication == null || !authentication.startsWith(HTTP_AUTH_BASIC_IDENTIFIER)) {
			throw new AuthenticationException("Please authenticate yourself.");
		}

		final String basicCookie;
		try {
			basicCookie = new String(Base64.decodeBase64(authentication.substring(HTTP_AUTH_BASIC_IDENTIFIER.length()).getBytes()),"UTF-8");
		} catch (final IOException e) {
			throw new BadRequestOrResponseException("error decoding authorization header: " + e.toString());
		}

		final int i = basicCookie.indexOf(':');
		if (i < 0) {
			throw new BadRequestOrResponseException("error decoding authorization header: syntax error");
		}

		// check username and password
		final String username = basicCookie.substring(0, i);		
		final String apiKey   = basicCookie.substring(i + 1);
		log.debug("Username/API-key: " + username + " / " + apiKey);
		try {
			return logicFactory.getLogicAccess(username, apiKey);
		} catch (ValidationException ve) {
			throw new AuthenticationException("Authentication failure: " + ve.getMessage());
		}
	}
}