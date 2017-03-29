/**
 * BibSonomy-Rest-Client - The REST-client.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.client;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.rest.auth.AuthenticationAccessor;
import org.bibsonomy.rest.client.util.FileFactory;
import org.bibsonomy.rest.client.util.MultiDirectoryFileFactory;
import org.bibsonomy.rest.client.util.ProgressCallbackFactory;
import org.bibsonomy.rest.client.util.ProgressCallbackFactoryImpl;
import org.bibsonomy.rest.renderer.RenderingFormat;

/**
 * TODO: add a builder for the rest logic factory
 * 
 * {@link LogicInterfaceFactory} for the {@link RestLogic}
 */
public class RestLogicFactory implements LogicInterfaceFactory {

	/** the api subpath */
	public static final String API_SUBPATH = "api/";

	/** the url of BibSonomy (bibsonomy.org) */
	public static final String BIBSONOMY_URL = "https://www.bibsonomy.org/";
	
	/** the url of the BibSonomy (bibsonomy.org) API */
	public static final String BIBSONOMY_API_URL = BIBSONOMY_URL + API_SUBPATH;
	
	/** the default rendering format */
	public static final RenderingFormat DEFAULT_RENDERING_FORMAT = RenderingFormat.XML;
	
	/** the dafult callback factory */
	public static final ProgressCallbackFactory DEFAULT_CALLBACK_FACTORY = new ProgressCallbackFactoryImpl();
	
	private final String apiUrl;
	private final RenderingFormat renderingFormat;
	private final ProgressCallbackFactory progressCallbackFactory;

	private FileFactory fileFactory;
	private static final String TMP_DIR;
	static {
		String tmp = System.getProperty("java.io.tmpdir");
		TMP_DIR = (tmp != null) ? tmp : "/tmp/";
	}

	/**
	 * the rest logic factory
	 */
	public RestLogicFactory() {
		this(BIBSONOMY_API_URL);
	}
	
	/**
	 * @param apiUrl 
	 */
	public RestLogicFactory(final String apiUrl) {
		this(apiUrl, DEFAULT_RENDERING_FORMAT);
	}
	
	/**
	 * @param factory
	 */
	public RestLogicFactory(final FileFactory factory) {
		this(BIBSONOMY_API_URL, DEFAULT_RENDERING_FORMAT, DEFAULT_CALLBACK_FACTORY, factory);
	}
	
	/**
	 * @param apiUrl the api url
	 * @param renderingFormat the rendering format to use
	 */
	public RestLogicFactory(final String apiUrl, final RenderingFormat renderingFormat) {
		this(apiUrl, renderingFormat, DEFAULT_CALLBACK_FACTORY);
	}
	
	public RestLogicFactory(String apiUrl, final RenderingFormat renderingFormat, final ProgressCallbackFactory progressCallbackFactory) {
		this(apiUrl, renderingFormat, progressCallbackFactory, new MultiDirectoryFileFactory(TMP_DIR, TMP_DIR, TMP_DIR));
	}

	/**
	 * @param apiUrl the api base url of the REST service
	 * @param renderingFormat the rendering format to use
	 * @param progressCallbackFactory the progress callback factory to use
	 * @param fileFactory 
	 */
	public RestLogicFactory(String apiUrl, final RenderingFormat renderingFormat, final ProgressCallbackFactory progressCallbackFactory, FileFactory fileFactory) {
		this.fileFactory = fileFactory;
		if (!present(apiUrl) || apiUrl.equals("/")) throw new IllegalArgumentException("The given apiURL is not valid.");
		
		// normalize url
		if (!apiUrl.endsWith("/")) apiUrl += "/";
		this.apiUrl = apiUrl;
		
		// currently on JSON and XML are supported
		if (!present(renderingFormat) || (!RenderingFormat.JSON.equals(renderingFormat) && !RenderingFormat.XML.equals(renderingFormat))) throw new IllegalArgumentException("The given rendering format is not supported (use JSON or XML)");
		this.renderingFormat = renderingFormat;
		
		if (!present(progressCallbackFactory)) throw new IllegalArgumentException("The given progress callback factory is not valid");
		this.progressCallbackFactory = progressCallbackFactory;
	}

	@Override
	public LogicInterface getLogicAccess(final String loginName, final String apiKey) throws IllegalArgumentException {
		// check login name and api key
		if (!present(loginName)) throw new IllegalArgumentException("The given username is not valid.");
		if (!present(apiKey)) throw new IllegalArgumentException("The given apiKey is not valid.");
		
		return new RestLogic(loginName, apiKey, this.apiUrl, this.renderingFormat, this.progressCallbackFactory, this.fileFactory);
	}

	/**
	 * create logic access for OAuth api access
	 * @param accessor
	 * @return
	 */
	public LogicInterface getLogicAccess(final AuthenticationAccessor accessor) {
		// check login name and api key
		if (!present(accessor)) throw new IllegalArgumentException("The given OAuth accessor is not valid.");
		
		return new RestLogic(accessor, this.apiUrl, this.renderingFormat, this.progressCallbackFactory, this.fileFactory);
	}
}