/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.strategy;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.fileupload.UploadedFileAccessor;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.util.URLDecodingPathTokenizer;
import org.bibsonomy.services.filesystem.FileLogic;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class Context {

	private static final Map<String, ContextHandler> urlHandlers = new HashMap<String, ContextHandler>();

	static {
		Context.urlHandlers.put(RESTConfig.TAGS_URL, new TagsHandler());
		Context.urlHandlers.put(RESTConfig.USERS_URL, new UsersHandler());
		Context.urlHandlers.put(RESTConfig.GROUPS_URL, new GroupsHandler());
		Context.urlHandlers.put(RESTConfig.POSTS_URL, new PostsHandler());
		Context.urlHandlers.put(RESTConfig.CONCEPTS_URL, new ConceptsHandler());
		Context.urlHandlers.put(RESTConfig.SYNC_URL, new SynchronizationHandler());
	}

	private final Reader doc;

	/**
	 * the logic
	 */
	private final LogicInterface logic;
	
	private final FileLogic fileLogic;

	/**
	 * the factory that provides instances of the renderer
	 */
	private final RendererFactory rendererFactory;

	/**
	 * the rendering format for the request
	 */
	private final RenderingFormat renderingFormat;

	/**
	 * the currently set strategy
	 */
	private final Strategy strategy;

	private final Map<?, ?> parameterMap;

	/**
	 * the list with all items out of the http request - never null
	 */
	private final UploadedFileAccessor uploadAccessor;

	/**
	 * this should hold all additional infos of the webservice or request
	 * i.e. the documents path
	 */
	private final Map<String, String> additionalInfos;

	/**
	 * @param httpMethod
	 *            method used in the request: GET, POST, PUT or DELETE
	 * @param url
	 * @param renderingFormat	the mediatype of the request and response
	 * @param rendererFactory	the renderfactory to use to create a
	 * 							renderer for the specified rendering format
	 * @param doc 
	 * @param uploadAccessor 
	 * @param logic 
	 * @param fileLogic
	 * @param parameterMap
	 *            map of the attributes
	 * @param additionalInfos 
	 * @throws NoSuchResourceException
	 *             if the requested url doesnt exist
	 * @throws ValidationException
	 *             if '/' is requested
	 */
	public Context(final HttpMethod httpMethod, final String url, final RenderingFormat renderingFormat, final RendererFactory rendererFactory, final Reader doc, final UploadedFileAccessor uploadAccessor,
			final LogicInterface logic, final FileLogic fileLogic, final Map<?, ?> parameterMap, final Map<String, String> additionalInfos) throws ValidationException, NoSuchResourceException {
		this.doc = doc;
		this.logic = logic;
		this.fileLogic = fileLogic;

		this.rendererFactory = rendererFactory;

		if (parameterMap == null) {
			throw new RuntimeException("Parameter map is null");
		}
		this.parameterMap = parameterMap;

		if (uploadAccessor != null) {
			this.uploadAccessor = uploadAccessor;
		} else {
			this.uploadAccessor = new UploadedFileAccessor(null);
		}
		
		this.additionalInfos = additionalInfos;

		if ((url == null) || "/".equals(url)) {
			throw new AccessDeniedException("It is forbidden to access '/'.");
		}

		// choose rendering format (defaults to xml)
		this.renderingFormat = renderingFormat;

		// choose the strategy
		this.strategy = this.chooseStrategy(httpMethod, url);
		if (this.strategy == null) {
			throw new NoSuchResourceException("The requested resource does not exist: " + url);
		}
	}

	private Strategy chooseStrategy(final HttpMethod httpMethod, final String url) {
		final URLDecodingPathTokenizer urlTokens = new URLDecodingPathTokenizer(url, "/");
		/*
		 * skip "/api" token FIXME: is this OK?
		 */
		urlTokens.next();
		if (urlTokens.countRemainingTokens() > 0) {
			final String nextElement = urlTokens.next();
			final ContextHandler contextHandler = Context.urlHandlers.get(nextElement);
			if (contextHandler != null) {
				return contextHandler.createStrategy(this, urlTokens, httpMethod);
			}
		}
		return null;
	}

	/**
	 * checks if the user is allowed to access the resource
	 * if not throws {@link AccessDeniedException}
	 */
	public void canAccess() {
		this.strategy.canAccess();
	}

	/**
	 * @param outStream
	 * @throws InternServerException
	 * @throws ResourceMovedException 
	 * @throws ResourceNotFoundException 
	 * @throws NoSuchResourceException 
	 */
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ObjectNotFoundException, ResourceMovedException {
		this.strategy.initWriter(outStream);
		this.strategy.perform(outStream);
		this.strategy.shutdownWriter(outStream);
	}

	/**
	 * @param userAgent
	 * @return The contentType depending on the userAgent
	 */
	public String getContentType(final String userAgent) {
		return this.strategy.getContentType(userAgent);
	}

	/**
	 * @param parameterName
	 *            The key from a map whose value holds the tags tags
	 * @return a list of all tags, which might be empty.
	 */
	public List<String> getTags(final String parameterName) {
		final List<String> tags = new LinkedList<String>();
		final String joinParams = this.getStringAttribute(parameterName, null);
		if ((joinParams != null) && (joinParams.length() > 0)) {
			final String[] params = joinParams.split("\\s");
			for (final String param : params) {
				tags.add(param);
			}
		}
		return tags;
	}

	/**
	 * @param parameterName
	 *            name of the parameter
	 * @param defaultValue
	 * @return paramter value
	 */
	public int getIntAttribute(final String parameterName, final int defaultValue) {
		if (this.parameterMap.containsKey(parameterName)) {
			final Object obj = this.parameterMap.get(parameterName);
			if (obj instanceof String[]) {
				final String[] tmp = (String[]) obj;
				if (tmp.length == 1) {
					try {
						return Integer.valueOf(tmp[0]);
					} catch (final NumberFormatException e) {
						// TODO ignore or throw exception ?
						return defaultValue;
					}
				}
			}
		}
		return defaultValue;
	}

	/**
	 * @param parameterName
	 *            name of the parameter
	 * @param defaultValue
	 * @return paramter value
	 */
	public String getStringAttribute(final String parameterName, final String defaultValue) {
		return RESTUtils.getStringAttribute(this.parameterMap, parameterName, defaultValue);
	}
	
	/**
	 * @param parameterName
	 * @param enumType name of enum type to be searched for the constant
	 * @param defaultValue
	 * @return parameter value
	 */
	public <X extends Enum<X>> X getEnumAttribute(final String parameterName, Class<X> enumType, X defaultValue) {
		String valueStr = RESTUtils.getStringAttribute(this.parameterMap, parameterName, null);
		if (valueStr == null) {
			return defaultValue;
		}

		// Keep in mind, that every enum value MUST be written in full upper case.
		return Enum.valueOf(enumType, valueStr.toUpperCase());
	}

	/**
	 * @return Returns the logic.
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * @return the fileLogic
	 */
	public FileLogic getFileLogic() {
		return this.fileLogic;
	}

	/**
	 * Do not use, only for junit tests
	 * 
	 * @return Returns the strategy.
	 */
	Strategy getStrategy() {
		return this.strategy;
	}

	/**
	 * @return TODO: improve documentation
	 */
	public Reader getDocument() {
		return this.doc;
	}

	/**
	 * @return the renderingFormat
	 */
	public RenderingFormat getRenderingFormat() {
		return this.renderingFormat;
	}

	/**
	 * 
	 * @return additionalInfos
	 */
	public Map<String, String> getAdditionalInfos() {
		return this.additionalInfos;
	}

	/**
	 * @return The renderer for URLs
	 */
	public UrlRenderer getUrlRenderer() {
		return this.rendererFactory.getUrlRenderer();
	}

	/**
	 * @return The factory to access renderers
	 */
	public Renderer getRenderer() {
		return this.rendererFactory.getRenderer(this.getRenderingFormat());
	}

	/**
	 * @return the uploadAcessor - never null
	 */
	public UploadedFileAccessor getUploadAccessor() {
		return this.uploadAccessor;
	}
}