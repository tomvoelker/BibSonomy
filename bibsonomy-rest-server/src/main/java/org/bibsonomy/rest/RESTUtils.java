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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.UnsupportedMediaTypeException;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.utils.HeaderUtils;
import org.bibsonomy.util.io.xml.FilterInvalidXMLCharsWriter;

/**
 * @author dzo
 */
public class RESTUtils {
	private static final Log log = LogFactory.getLog(RESTUtils.class);
	
	/** the default rendering format */
	public static final RenderingFormat DEFAULT_RENDERING_FORMAT = RenderingFormat.XML;

	/**
	 * all supported rendering formats by the rest server
	 * ordered by preference
	 */
	private static final List<RenderingFormat> SUPPORTED_RENDERING_FORMAT = Collections.unmodifiableList(Arrays.asList(
			RenderingFormat.XML,
			RenderingFormat.APP_XML,
			RenderingFormat.JSON,
			RenderingFormat.CSL
	));
	
	private static final List<RenderingFormat> SUPPORTED_POST_FORMATS = Collections.unmodifiableList(Arrays.asList(
			RenderingFormat.XML,
			RenderingFormat.APP_XML,
			RenderingFormat.JSON
	));

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

	private static List<RenderingFormat> getSupportedAcceptHeaderMediaTypes(final String acceptHeader) {
		final List<RenderingFormat> formats = new LinkedList<RenderingFormat>();
		// parse the accept header
		final SortedMap<Double, List<String>> preferredTypes = HeaderUtils.getPreferredTypes(acceptHeader);
		for (final Entry<Double, List<String>> preferredType : preferredTypes.entrySet()) {
			/*
			 * which media type do we accept?
			 */
			for (final String mediaTypeString : preferredType.getValue()) {
				try {
					final RenderingFormat renderingFormat = RenderingFormat.getMediaType(mediaTypeString);
					for (final RenderingFormat supportedMediaType : SUPPORTED_RENDERING_FORMAT) {
						if (supportedMediaType.isCompatible(renderingFormat)) {
							formats.add(supportedMediaType);
						}
					}
				} catch (final IllegalArgumentException e) {
					// don't care
				}
			}
		}

		return formats;
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
			/* 
			 * FIXME: because of the "!urlRenderingFormat.isWildcardSubtype()" 
			 * condition we can't use "layout/simplehtml" as url param?   
			 */
			return (urlRenderingFormat != null) && !urlRenderingFormat.isWildcardSubtype() ? urlRenderingFormat : DEFAULT_RENDERING_FORMAT;
		}

		// 2. check the accept header of the request
		final List<RenderingFormat> acceptMediaTypes = getSupportedAcceptHeaderMediaTypes(acceptHeader);

		// 3. check the content type of the request 
		if (present(contentType)) {
			final RenderingFormat contentTypeMediaType = RenderingFormat.getMediaType(contentType);
			
			final boolean supportedFormat = SUPPORTED_POST_FORMATS.contains(contentTypeMediaType);
			if (!supportedFormat) {
				throw new UnsupportedMediaTypeException("'" + contentType + "' not supported");
			}

			// check if accept header was sent by the client
			if (present(acceptMediaTypes)) {
				if (!acceptMediaTypes.contains(contentTypeMediaType)) {
					throw new BadRequestOrResponseException("Only Chuck Norris can send content of another media type than he accepts.");
				}
			}

			return contentTypeMediaType != null ? contentTypeMediaType : DEFAULT_RENDERING_FORMAT;
		}

		if (present(acceptMediaTypes)) {
			return acceptMediaTypes.get(0);
		}

		return DEFAULT_RENDERING_FORMAT;
	}

	/**
	 * a reader for the provided stream with the provided encoding. If the
	 * encoding is not supported the default encoding is used
	 * 
	 * @param stream
	 * @param encoding
	 * @return the reader for the stream
	 */
	public static Reader getInputReaderForStream(final InputStream stream, final String encoding) {
		if (!present(stream)) {
			return null;
		}
		try {
			// returns InputStream with correct encoding
			return new InputStreamReader(stream, encoding);
		} catch (final UnsupportedEncodingException ex) {
			// returns InputStream with default encoding if a exception
			// is thrown with utf-8 support
			log.fatal(ex.getStackTrace());
			return new InputStreamReader(stream);
		}
	}

	/**
	 * a writer for the provided stream with the provided encoding. If the
	 * encoding is not supported the default encoding is used
	 * 
	 * @param stream
	 * @param encoding
	 * @return the writer for the stream
	 */
	public static Writer getOutputWriterForStream(OutputStream stream, final String encoding) {
		if (!present(stream)) {
			return null;
		}
		
		try {
			// returns InputStream with correct encoding
			return new PrintWriter(new FilterInvalidXMLCharsWriter(new OutputStreamWriter(stream, encoding), true));
		} catch (final UnsupportedEncodingException ex) {
			// returns InputStream with default encoding if a exception
			// is thrown with utf-8 support
			log.fatal("Could not get output writer for stream with encoding " + encoding, ex);
			return new PrintWriter(new FilterInvalidXMLCharsWriter(new OutputStreamWriter(stream), true));
		}
	}

	/**
	 * maps special user tokens to corresponding user entities - currently <code>@me</code>
	 * is mapped to the login user name
	 * 
	 * @param userName user name token from url
	 * @param context context object
	 * @return the normalized user
	 */
	public static String normalizeUser(final String userName, final Context context) {
		if (RESTConfig.USER_ME.equals(userName)) {
			// map @me to login user name
			return context.getLogic().getAuthenticatedUser().getName();
		}
	
		return userName;
	}
}
