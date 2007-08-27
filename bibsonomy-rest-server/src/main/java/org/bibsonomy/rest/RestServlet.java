package org.bibsonomy.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.DBLogic;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.strategy.Context;

import sun.misc.BASE64Decoder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class RestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(RestServlet.class);

	public static final String PARAM_LOGICFACTORY_CLASS = "logicFactoryClass";

	private LogicInterfaceFactory logicFactory;

	@Override
	public void init() throws ServletException {
		super.init();
		// instantiate the bibsonomy database connection
		final String logicFactoryClassName = this.getServletConfig().getInitParameter(PARAM_LOGICFACTORY_CLASS);
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
			log.info("no 'logicFactoryClass' initParameter -> using default");
			this.logicFactory = new LogicInterfaceFactory() {
				public LogicInterface getLogicAccess(final String loginName, final String apiKey) {
					return DBLogic.getApiAccess(loginName, apiKey);
				}
			};
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

		log.debug("Incoming URL:" + request.getRequestURL() + " from IP " + request.getRemoteAddr());
		Long start = System.currentTimeMillis();

		try {
			// validate the requesting user's authorization
			final LogicInterface logic = validateAuthorization(request.getHeader("Authorization"));

			// create Context
			final Context context = new Context(request.getInputStream(), logic, method, request.getPathInfo(), request.getParameterMap());

			// validate request
			context.validate();

			// set some response headers
			response.setContentType(context.getContentType(request.getHeader("User-Agent")));
			response.setCharacterEncoding("UTF-8");

			// send answer
			response.setStatus(HttpServletResponse.SC_OK);
			final ByteArrayOutputStream cachingStream = new ByteArrayOutputStream();
			final Writer writer = new OutputStreamWriter(cachingStream, Charset.forName("UTF-8"));

			context.perform(writer);
			// XXX: cachingStream.size() != cachingStream.toString().length() !!
			// the correct value is the first one!
			response.setContentLength(cachingStream.size());
			
			// some more logging
			log.debug("Size of output sent:" + cachingStream.size());
			Long elapsed = System.currentTimeMillis() - start;
			log.debug("Processing time: " + elapsed + " ms");
			
			response.getOutputStream().print(cachingStream.toString("UTF-8"));
		} catch (final AuthenticationException e) {
			log.error(e,e);
			response.setHeader("WWW-Authenticate", "Basic realm=\"BibsonomyWebService\"");
			sendError(request, response, HttpURLConnection.HTTP_UNAUTHORIZED, e.getMessage());
		} catch (final InternServerException e) {
			log.error(e,e);
			sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (final NoSuchResourceException e) {
			log.error(e,e);
			sendError(request, response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		} catch (final BadRequestOrResponseException e) {
			log.error(e,e);
			sendError(request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (final ValidationException e) {
			log.error(e,e);
			sendError(request, response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());			
		} catch (final Exception e) {
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
		log.error(message);
		// get renderer
		final String renderingFormatName = Context.getStringAttribute(request.getParameterMap(), "format", "xml");
		final RenderingFormat renderingFormat = RenderingFormat.getRenderingFormat(renderingFormatName);
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
		if (authentication == null || !authentication.startsWith("Basic ")) {
			throw new AuthenticationException("Please authenticate yourself.");
		}

		final String basicCookie;
		try {
			final BASE64Decoder decoder = new BASE64Decoder();
			basicCookie = new String(decoder.decodeBuffer(authentication.substring(6)));
		} catch (final IOException e) {
			throw new BadRequestOrResponseException("error decoding authorization header: " + e.toString());
		}

		final int i = basicCookie.indexOf(':');
		if (i < 0) {
			throw new BadRequestOrResponseException("error decoding authorization header: syntax error");
		}

		// check username and password
		final String username = basicCookie.substring(0, i);
		final String apiKey = basicCookie.substring(i + 1);
		try {
			return logicFactory.getLogicAccess(username, apiKey);
		} catch (ValidationException ve) {
			throw new AuthenticationException("Please authenticate yourself: " + ve.getClass().getSimpleName() + ": " + ve.getMessage());
		}
	}
}