package org.bibsonomy.rest;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Map;

import org.bibsonomy.rest.enums.RenderingFormat;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

/**
 * @author dzo
 * @version $Id$
 */
public class RESTUtils {
	
	private static final RenderingFormat DEFAULT_MEDIA_TYPE = RenderingFormat.XML;
	
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

	/**
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
			final RenderingFormat urlMediaType = RenderingFormat.getMediaTypeByFormat(urlParam);
			return urlMediaType != null ? urlMediaType : DEFAULT_MEDIA_TYPE;
		}
		
		// 2. check the accept header of the request
		final RenderingFormat acceptMediaType = RenderingFormat.getMediaType(acceptHeader);
		
		// 3. check the content type of the request 
		if (present(contentType)) {
			final RenderingFormat contentTypeMediaType = RenderingFormat.getMediaType(contentType);
			// check if accept header was sent by the client
			if (present(acceptHeader)) {
				// only Chuck Norris can send data to the server in type A and accepts type B
				if (acceptMediaType != contentTypeMediaType) {
					throw new BadRequestOrResponseException("Only Chuck Norris can send content of another media type than he accepts.");
				}
			}
			
			return contentTypeMediaType != null ? contentTypeMediaType : DEFAULT_MEDIA_TYPE;
		}
		
		return acceptMediaType != null ? acceptMediaType : DEFAULT_MEDIA_TYPE;
	}

}
