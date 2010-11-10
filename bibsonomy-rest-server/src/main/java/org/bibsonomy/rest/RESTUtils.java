package org.bibsonomy.rest;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.utils.HeaderUtils;

/**
 * @author dzo
 * @version $Id$
 */
public class RESTUtils {
	private static final Log log = LogFactory.getLog(RESTUtils.class);
	
	private static final RenderingFormat DEFAULT_RENDERING_FORMAT = RenderingFormat.XML;
	
	private static final Set<RenderingFormat> SUPPORTED_RENDERING_FORMAT = new HashSet<RenderingFormat>(Arrays.asList(RenderingFormat.XML, RenderingFormat.JSON));
	
	/**
	 * param name for the format of the request and response
	 */
	public static final String FORMAT_PARAM = "format";

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
	
	private static RenderingFormat getAcceptHeaderMediaType(final String acceptHeader) {
		// parse the accept header
		final SortedMap<Double, Vector<String>> preferredTypes = HeaderUtils.getPreferredTypes(acceptHeader);
		for (final Entry<Double, Vector<String>> preferredType : preferredTypes.entrySet()) {
			for (final String mediaTypeString : preferredType.getValue()) {
				try {
					final RenderingFormat renderingFormat = RenderingFormat.getMediaType(mediaTypeString);
					for (final RenderingFormat supportedMediaType : SUPPORTED_RENDERING_FORMAT) {
						if (supportedMediaType.isCompatible(renderingFormat)) {
							return renderingFormat;
						}
					}
				} catch (final IllegalArgumentException e) {
					// don't care
				}
			}
		}
		
		return null;
	}

	/** 
	 * 
	 * if the url contains a format param (in parameterMap) this format is used
	 * if the a content type is set, this media type is used and must be the
	 * same as the accept header value
	 * if the accept header is present, the accept media type is used
	 * 
	 * @param parameterMap
	 * @param acceptHeader
	 * @param contentType
	 * @return the RenderingFormat
	 */
	public static RenderingFormat getRenderingFormatForRequest(final Map<?, ?> parameterMap, final String acceptHeader, final String contentType) {
		// 1. check the url for the format parameter (e.g. ?format=xml)
		final String urlParam = getStringAttribute(parameterMap, FORMAT_PARAM, null);
		if (present(urlParam)) {
			final RenderingFormat urlRenderingFormat = RenderingFormat.getMediaTypeByFormat(urlParam);
			return urlRenderingFormat != null ? urlRenderingFormat : DEFAULT_RENDERING_FORMAT;
		}
		
		// 2. check the accept header of the request
		final RenderingFormat acceptMediaType = getAcceptHeaderMediaType(acceptHeader);
		
		// 3. check the content type of the request 
		if (present(contentType)) {
			final RenderingFormat contentTypeMediaType = RenderingFormat.getMediaType(contentType);
			// check if accept header was sent by the client
			if (present(acceptHeader)) {
				// only Chuck Norris can send data to the server in type A and accepts type B
				if (!acceptMediaType.isCompatible(contentTypeMediaType)) {
					throw new BadRequestOrResponseException("Only Chuck Norris can send content of another media type than he accepts.");
				}
			}
			
			return contentTypeMediaType != null ? contentTypeMediaType : DEFAULT_RENDERING_FORMAT;
		}
		
		return acceptMediaType != null ? acceptMediaType : DEFAULT_RENDERING_FORMAT;
	}

	/**
	 * an inputreader for the provided stream with the provided encoding. If the
	 * encoding is not supported the default encoding is used
	 * 
	 * @param stream
	 * @param encoding
	 * @return the inputreader for the stream
	 */
	public static Reader getInputReaderForStream(InputStream stream, final String encoding) {
		if (!present(stream)) return null;
		try {
			// returns InputStream with correct encoding
			return new InputStreamReader(stream, encoding);
		} catch (UnsupportedEncodingException ex) {
			// returns InputStream with default encoding if a exception
			// is thrown with utf-8 support
			log.fatal(ex.getStackTrace());
			return new InputStreamReader(stream);
		}
	}

}
