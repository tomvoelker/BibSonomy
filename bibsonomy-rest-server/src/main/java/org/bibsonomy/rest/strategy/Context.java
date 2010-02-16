package org.bibsonomy.rest.strategy;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.validation.ServersideModelValidator;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class Context {

	private static final Log log = LogFactory.getLog(Context.class);
	
	private static final Map<String, ContextHandler> urlHandlers = new HashMap<String, ContextHandler>();

	static {
		Context.urlHandlers.put(RestProperties.getInstance().getTagsUrl(), new TagsHandler());
		Context.urlHandlers.put(RestProperties.getInstance().getUsersUrl(), new UsersHandler());
		Context.urlHandlers.put(RestProperties.getInstance().getGroupsUrl(), new GroupsHandler());
		Context.urlHandlers.put(RestProperties.getInstance().getPostsUrl(), new PostsHandler());
		Context.urlHandlers.put(RestProperties.getInstance().getConceptUrl(), new ConceptsHandler());
		/*
		 * configure validation
		 */
		RestProperties.getInstance().setValidator(ServersideModelValidator.getInstance());
	}

	private final InputStream doc;
	
	/**
	 * the logic
	 */
	private final LogicInterface logic;

	/**
	 * the renderer by which the output gets rendered
	 */
	private final Renderer renderer;

	/**
	 * the currently set strategy
	 */
	private final Strategy strategy;

	private final StringTokenizer urlTokens;
	private final Map<?, ?> parameterMap;
	// FIXME: never used locally ?
	// private final HttpMethod httpMethod;
	private RenderingFormat renderingFormat;

	/**
	 * the list with all items out of the http request
	 */
	private final List<FileItem> items;
	
	/**
	 * this should hold all additional infos of the webservice or request
	 * i.e. the rootpath which have been declared in the web.xml
	 */
	private final Map<String, String> additionalInfos;

	/**
	 * @param doc 
	 * @param logic 
	 * @param url
	 * @param httpMethod
	 *            httpMethod used in the request: GET, POST, PUT or DELETE
	 * @param parameterMap
	 *            map of the attributes
	 * @param items 
	 * @param additionalInfos 
	 * @throws NoSuchResourceException
	 *             if the requested url doesnt exist
	 * @throws ValidationException
	 *             if '/' is requested
	 */
	public Context(final InputStream doc, final LogicInterface logic, final HttpMethod httpMethod, final String url, final Map<?, ?> parameterMap, final List<FileItem> items, final Map<String, String> additionalInfos) throws ValidationException, NoSuchResourceException {
		this.doc = doc;
		this.logic = logic;
		// FIXME this.httpMethod = httpMethod;
		if (parameterMap == null) throw new RuntimeException("Parameter map is null");
		this.parameterMap = parameterMap;
		
		this.items = items;
		this.additionalInfos = additionalInfos;

		if (url == null || "/".equals(url)) throw new ValidationException("It is forbidden to access '/'.");
		this.urlTokens = new StringTokenizer(url, "/");

		// choose rendering format (defaults to xml)
		this.renderingFormat = RenderingFormat.getRenderingFormat(getStringAttribute("format", "xml"));
		this.renderer = RendererFactory.getRenderer(this.renderingFormat);

		// choose the strategy
		this.strategy = this.chooseStrategy(httpMethod);
		if (this.strategy == null) throw new NoSuchResourceException("The requested resource does not exist: " + url);
	}

	private Strategy chooseStrategy(final HttpMethod httpMethod) {
		if (this.urlTokens.countTokens() > 0) {
			final String nextElement = (String) this.urlTokens.nextElement();
			final ContextHandler contextHandler = Context.urlHandlers.get(nextElement);
			if (contextHandler != null) {
				return contextHandler.createStrategy(this, this.urlTokens, httpMethod);
			}
		}
		return null;
	}

	/**
	 * Validates a strategy: correct userName, etc
	 * 
	 * @throws ValidationException
	 */
	public void validate() throws ValidationException {
		this.strategy.validate();
	}

	/**
	 * @param outStream
	 * @throws InternServerException
	 */
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		this.strategy.perform(outStream);
	}


	/**
	 * @param userAgent
	 * @return The contentType depending on the userAgent
	 */
	public String getContentType(final String userAgent) {
		return this.strategy.getContentType(userAgent);
	}

	/**
	 * @param userAgent
	 * @return true if the client uses this webservice api, false if its a
	 *         browser for example
	 */
	public boolean apiIsUserAgent(final String userAgent) {
		return userAgent != null && userAgent.startsWith(RestProperties.getInstance().getApiUserAgent());
	}

	/**
	 * @param parameterName
	 *            The key from a map whose value holds the tags tags
	 * @return a list of all tags, which might be empty.
	 */
	public List<String> getTags(final String parameterName) {
		final List<String> tags = new LinkedList<String>();
		final String param = getStringAttribute(parameterName, null);
		if ((param != null) && (param.length() > 0)) {
			final String[] params = param.split("\\s");
			for (int i = 0; i < params.length; ++i) {
				tags.add(params[i]);
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
						int tmpStart = Integer.valueOf(tmp[0]);
						return tmpStart;
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
		return Context.getStringAttribute(this.parameterMap, parameterName, defaultValue);
	}

	/** 
	 * @param parameterMap
	 * @param parameterName
	 * @param defaultValue
	 * @return a {@link String} parameter of the request's parametermap, if any.
	 */
	public static String getStringAttribute(final Map<?, ?> parameterMap, final String parameterName, final String defaultValue) {
		if (parameterMap.containsKey(parameterName)) {
			final Object obj = parameterMap.get(parameterName);
			if (obj instanceof String[]) {
				final String[] tmp = (String[]) obj;
				if (tmp.length == 1) {
					return tmp[0];
				}
			}
		}
		return defaultValue;
	}

	/**
	 * @return Returns the renderer.
	 */
	public Renderer getRenderer() {
		return this.renderer;
	}

	/**
	 * @return Returns the logic.
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * @return Returns the renderingFormat.
	 */
	public RenderingFormat getRenderingFormat() {
		return this.renderingFormat;
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
	public Reader getDocument()  {
		try {
			// returns InputStream with correct encoding
			return new InputStreamReader(this.doc, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			// returns InputStream with default encoding if a exception
			// is thrown with utf-8 support
			log.fatal(ex.getStackTrace());
			return new InputStreamReader(this.doc);
		}
	}
	
	/**
	 * 
	 * @return additionalInfos
	 */
	public Map<String, String> getAdditionalInfos() {
		return this.additionalInfos;
	}
	
	/**
	 * 
	 * @return the previously committed item list parsed out of a http request object
	 */
	public List<FileItem> getItemList(){
		return this.items;
	}
	
	/**
	 * Need to set another RenderingFormat to download .pdf files as pdf files :)
	 * 
	 * @param renderingFormat
	 */
	public void setRenderingFormat(RenderingFormat renderingFormat) {
		this.renderingFormat = renderingFormat;
	}
}