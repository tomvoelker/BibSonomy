/**
 * BibSonomy-Rest-Server - The REST-server.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SyncService;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedMediaTypeException;
import org.bibsonomy.rest.fileupload.DualUploadedFileAccessor;
import org.bibsonomy.rest.fileupload.UploadedFileAccessor;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.util.URLDecodingStringTokenizer;
import org.bibsonomy.rest.utils.HeaderUtils;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Christian Kramer
 */
public final class RestServlet extends HttpServlet {
	private static final long serialVersionUID = -1737804091652029470L;
	private static final Log log = LogFactory.getLog(RestServlet.class);

	/**
	 * the key for the documents path
	 */
	public static final String DOCUMENTS_PATH_KEY = "docPath";

	/**
	 * the key for the project home
	 */
	public static final String PROJECT_HOME_KEY = "projectHome";

	private static final String PROJECT_NAME_KEY = "projectName";

	/**
	 * the response encoding used to encode HTTP responses.
	 */
	public static final String RESPONSE_ENCODING = StringUtils.CHARSET_UTF_8;

	/**
	 * the request default encoding
	 */
	public static final String REQUEST_ENCODING = StringUtils.CHARSET_UTF_8;

	/**
	 * Name of header, that shows successful ssl verification
	 */
	public static final String SSL_VERIFY_HEADER = "SSL_CLIENT_VERIFY";

	/**
	 * String to show successful ssl key check 
	 */
	public static final String SUCCESS = "SUCCESS";

	/**
	 * Distinguish name of the client
	 */
	public static final String SSL_CLIENT_S_DN = "SSL_CLIENT_S_DN";

	private List<AuthenticationHandler<?>> authenticationHandlers;
	private FileLogic fileLogic;
	
	private UrlRenderer urlRenderer;
	private RendererFactory rendererFactory;

	// store some infos about the specific request or the webservice (i.e. document path)
	private final Map<String, String> additionalInfos = new HashMap<String, String>();

	/**
	 * Sets the base URL of the project. Typically "project.home" in the 
	 * file <tt>project.properties</tt>. 
	 * @param projectHome
	 */
	@Required
	public void setProjectHome(final String projectHome) {
		additionalInfos.put(PROJECT_HOME_KEY, projectHome);
	}

	/**
	 * @param projectName the name of the project
	 */
	public void setProjectName(final String projectName) {
		this.additionalInfos.put(PROJECT_NAME_KEY, projectName);
	}

	/**
	 * Renders the URLs returned by the servlet, e.g., in the XML.
	 * @param urlRenderer
	 */
	@Required
	public void setUrlRenderer(final UrlRenderer urlRenderer) {
		this.urlRenderer = urlRenderer;
	}

	/**
	 * @param rendererFactory the rendererFactory to set
	 */
	public void setRendererFactory(final RendererFactory rendererFactory) {
		this.rendererFactory = rendererFactory;
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
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
		validateAuthorization(request);
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
		final long start = System.currentTimeMillis();

		try {
			// validate the requesting user's authorization
			final LogicInterface logic = validateAuthorization(request);

			/*
			 * Extract a file from the request if it is a MultiPartRequest.
			 * XXX: This expects that the extraction of the file has been done
			 * before - typically by Spring's DispatcherServlet. If this is not
			 * the case, the document upload fails! 
			 */
			UploadedFileAccessor uploadAccessor = new DualUploadedFileAccessor(request);

			// choose rendering format (defaults to xml)
			final RenderingFormat renderingFormat = RESTUtils.getRenderingFormatForRequest(request.getParameterMap(), request.getHeader(HeaderUtils.HEADER_ACCEPT), getMainContentType(request));

			// create Context
			final Reader reader = RESTUtils.getInputReaderForStream(getMainInputStream(request), REQUEST_ENCODING);
			final Context context = new Context(method, request.getRequestURI(), renderingFormat, rendererFactory, reader, uploadAccessor, logic, this.fileLogic, request.getParameterMap(), additionalInfos);

			// validate request
			context.canAccess();

			// set some response headers
			final String userAgent = request.getHeader(HeaderUtils.HEADER_USER_AGENT);
			log.debug("[USER-AGENT] " + userAgent);
			response.setContentType(context.getContentType(userAgent));
			response.setCharacterEncoding(RESPONSE_ENCODING);

			// send answer
			if (method.equals(HttpMethod.POST)) {
				// if a POST request completes successfully this means that a resource has been created
				response.setStatus(HttpServletResponse.SC_CREATED);
			} else {
				response.setStatus(HttpServletResponse.SC_OK);
			}

			// just define an ByteArrayOutputStream to store all outgoing data
			final ByteArrayOutputStream cachingStream = new ByteArrayOutputStream();
			context.perform(cachingStream);

			/*
			 * XXX: note: cachingStream.size() != cachingStream.toString().length() !!
			 * the correct value is the first one!
			 */
			response.setContentLength(cachingStream.size());

			// some more logging
			log.debug("Size of output sent:" + cachingStream.size());
			final long elapsed = System.currentTimeMillis() - start;
			log.debug("Processing time: " + elapsed + " ms");

			cachingStream.writeTo(response.getOutputStream());
		} catch (final AuthenticationException e) {
			log.warn(e.getMessage());
			response.setHeader("WWW-Authenticate", "Basic realm=\"" + this.additionalInfos.get(PROJECT_NAME_KEY) + "WebService\"");
			sendError(request, response, HttpURLConnection.HTTP_UNAUTHORIZED, e.getMessage());
		} catch (final InternServerException e) {
			log.error(e.getMessage());
			sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (final NoSuchResourceException e) {
			log.error(e.getMessage());
			sendError(request, response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		} catch (final BadRequestOrResponseException e) {
			log.error(e.getMessage(), e);
			sendError(request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (final AccessDeniedException e) {
			log.error(e.getMessage());
			sendError(request, response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		} catch (final ResourceMovedException e) {
			log.error(e.getMessage());
			/*
			 * sending new location
			 * TODO: add date using
			 */
			response.setHeader("Location", urlRenderer.createHrefForResource(e.getUserName(), e.getNewIntraHash()));
			sendError(request, response, HttpServletResponse.SC_MOVED_PERMANENTLY, e.getMessage());
		} catch (final DatabaseException e) {
			final StringBuilder returnMessage = new StringBuilder("");
			for (final String hash : e.getErrorMessages().keySet()) {
				for (final ErrorMessage em : e.getErrorMessages(hash)) {
					log.error(em.toString());
					returnMessage.append(em.toString() + "\n ");
				}
			}
			sendError(request, response, HttpServletResponse.SC_BAD_REQUEST, returnMessage.toString());
		} catch (final UnsupportedMediaTypeException e) {
			log.error(e.getMessage());
			sendError(request, response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, e.getMessage());
		} catch (final Exception e) {
			log.error(e, e);
			// well, lets fetch each and every error...
			sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	protected static String getMainContentType(HttpServletRequest request) {
		if (request instanceof MultipartHttpServletRequest) {
			// TODO: add comment
			final MultipartFile mainFile = ((MultipartHttpServletRequest) request).getFile("main");
			if (mainFile != null) {
				return mainFile.getContentType();
			}
			return null;
		}
		return request.getContentType();
	}

	/**
	 * @param request
	 * @return bei einem {@link MultipartHttpServletRequest} der {@link InputStream} des "main" files - falls keines da ist oder es kein {@link MultipartHttpServletRequest} ist, dann request.getInputStream()
	 * @throws IOException
	 */
	protected static InputStream getMainInputStream(HttpServletRequest request) throws IOException {
		if (request instanceof MultipartHttpServletRequest) {
			MultipartFile main = ((MultipartHttpServletRequest) request).getFile("main");
			if (main != null) {
				return main.getInputStream();
			}
		}
		return request.getInputStream();
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
		final RenderingFormat mediaType = RESTUtils.getRenderingFormatForRequest(request.getParameterMap(), request.getHeader(HeaderUtils.HEADER_ACCEPT), getMainContentType(request));
		final Renderer renderer = rendererFactory.getRenderer(mediaType);

		// send error
		response.setCharacterEncoding(RESPONSE_ENCODING);
		response.setStatus(code);
		response.setContentType(mediaType.getErrorFormat().getMimeType());
		final ByteArrayOutputStream cachingStream = new ByteArrayOutputStream();
		final Writer writer = new OutputStreamWriter(cachingStream, Charset.forName(RESPONSE_ENCODING));
		
		renderer.serializeError(writer, message);
		writer.close();
		response.setContentLength(cachingStream.size());
		response.getOutputStream().print(cachingStream.toString(RESPONSE_ENCODING));
	}

	/**
	 * @param request
	 *            the reuqest
	 * @return the val
	 * @throws AuthenticationException 
	 * @throws IOException
	 */
	protected LogicInterface validateAuthorization(final HttpServletRequest request) throws AuthenticationException {
		for (final AuthenticationHandler<?> authenticationHandler : this.authenticationHandlers) {
			final LogicInterface logic = getLogic(authenticationHandler, request);
			if (present(logic)) {
				validateSyncAuthorization(request, logic);
				return logic;
			}
		}
		throw new AuthenticationException(AuthenticationHandler.NO_AUTH_ERROR);
	}
	
	private static <T> LogicInterface getLogic(final AuthenticationHandler<T> authenticationHandler, HttpServletRequest request) {
		final T extractAuthentication = authenticationHandler.extractAuthentication(request);
		if (authenticationHandler.canAuthenticateUser(extractAuthentication)) {
			return authenticationHandler.authenticateUser(extractAuthentication);
		}
		return null;
	}

	/**
	 * Checks the SSL headers for configured sync client
	 * 
	 * @param request
	 * @param logic
	 */
	private static void validateSyncAuthorization(final HttpServletRequest request, final LogicInterface logic) {
		log.debug("start ssl header check for synchronization");
		final String verifyHeader = request.getHeader(SSL_VERIFY_HEADER);
		if (!SUCCESS.equals(verifyHeader)) {
			log.debug("ssl_verify_header not found or not '" + SUCCESS + "'");
			return;
		}

		final String sslClientSDn = request.getHeader(SSL_CLIENT_S_DN);
		if (!present(sslClientSDn)) {
			log.debug("ssl_client_verify was set, but ssl_client_s_dn not found");
			return;
		}

		/*
		 * get syncClient
		 */
		log.debug("checking available sync client against SSL_CLIENT_S_DN '" + sslClientSDn + "'.");
		URI serviceURI = null;

		// check that request URI contains service URI
		final StringTokenizer urlTokens = new URLDecodingStringTokenizer(request.getRequestURI(), "/");
		final String userName = logic.getAuthenticatedUser().getName();

		// skip /api token
		urlTokens.nextToken();

		if (urlTokens.nextElement() == "sync") {
			try {
				serviceURI = new URI(urlTokens.nextToken());
			} catch (URISyntaxException e) {
				throw new NoSuchResourceException("cannot process url - please check url syntax ");
			}

			// get sync client by serviceURI 
			if (present(serviceURI)) {
				final List<SyncService> syncClient = logic.getSyncService(userName, serviceURI, false);
				if (log.isDebugEnabled()) {
					log.debug("sync client:" + syncClient.get(0).getService() + " | "
							+ "service ssl_s_dn:" + syncClient.get(0).getSslDn());
				}

				if (sslClientSDn.equals(syncClient.get(0).getSslDn())) {
					/*
					 * service with requested ssl_client_s_dn found in available client list -> give user the sync-role
					 */
					log.debug("setting user role to SYNC");
					logic.getAuthenticatedUser().setRole(Role.SYNC);
					return;
				}
			}
		}
	}

	/**
	 * @param authenticationHandlers the authenticationHandlers to set
	 */
	public void setAuthenticationHandlers(List<AuthenticationHandler<?>> authenticationHandlers) {
		this.authenticationHandlers = authenticationHandlers;
	}
}