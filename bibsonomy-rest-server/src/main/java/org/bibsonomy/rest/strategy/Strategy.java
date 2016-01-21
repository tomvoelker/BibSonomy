/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.RestServlet;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.fileupload.UploadedFileAccessor;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public abstract class Strategy {
	private static final Log log = LogFactory.getLog(Strategy.class);
	private final Context context;
	
	protected Writer writer;

	/**
	 * @param context
	 */
	public Strategy(final Context context) {
		this.context = context;
	}

	/**
	 * @see Context#canAccess()
	 */
	public void canAccess() {
		// noop
	}
	
	/**
	 * @param outputStream the output stream
	 */
	public void initWriter(final ByteArrayOutputStream outputStream) {
		this.writer = RESTUtils.getOutputWriterForStream(outputStream, RestServlet.RESPONSE_ENCODING);
	}
	

	/**
	 * flush writer or whatever needs to be done with it
	 * @param outStream the outputstream for potential cases where it needs to be delt with
	 */
	public void shutdownWriter(ByteArrayOutputStream outStream) {
		if (this.writer != null) {
			try {
				writer.flush();
			} catch (IOException e) {
				log.error("cannot flush writer");
			}
		}
	}

	/**
	 * @param outStream 
	 * @throws InternServerException
	 * @throws NoSuchResourceException
	 * @throws ResourceNotFoundException 
	 * @throws ResourceMovedException 
	 */
	public abstract void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ObjectNotFoundException;

	/**
	 * @param userAgent
	 * @return true if the client uses this webservice api, false if its a
	 *         browser for example
	 */
	@Deprecated
	private static boolean apiIsUserAgent(final String userAgent) {
		return (userAgent != null) && userAgent.startsWith(RESTConfig.API_USER_AGENT);
	}
	
	/**
	 * Get Content type to be set for response, depending on the specified user agent.
	 * 
	 * @param userAgent - 
	 * @return the contentType of the answer document
	 */
	public final String getContentType(final String userAgent) {
		final String contentType = this.getContentType();
		if ((contentType != null) && apiIsUserAgent(userAgent)) {
			// Use special content type if request comes from BibSonomy REST client
			// (like bibsonomy/post+XML )
			// FIXME: check if the client has ever used this content type
			return "bibsonomy/" + contentType + "+" + this.getRenderingFormat().toString();
		}
		
		return this.getRenderingFormat().getMimeType();
	}

	protected RenderingFormat getRenderingFormat() {
		return this.context.getRenderingFormat();
	}

	@Deprecated
	protected String getContentType() {
		return null;
	}

	/**
	 * Chooses a GroupingEntity based on the parameterMap in the {@link Context}.
	 * 
	 * @return The GroupingEntity; it defaults to ALL.
	 */
	protected GroupingEntity chooseGroupingEntity() {
		if (this.context.getStringAttribute("user", null) != null) {
			return GroupingEntity.USER;
		}
		if (this.context.getStringAttribute("group", null) != null) {
			return GroupingEntity.GROUP;
		}
		if (this.context.getStringAttribute("viewable", null) != null) {
			return GroupingEntity.VIEWABLE;
		}
		if (this.context.getStringAttribute("friend", null) != null) {
			return GroupingEntity.FRIEND;
		}
		return GroupingEntity.ALL;
	}

	protected LogicInterface getLogic() {
		return this.context.getLogic();
	}
	
	protected UrlRenderer getUrlRenderer() {
		return this.context.getUrlRenderer();
	}

	protected Renderer getRenderer() {
		return this.context.getRenderer();
	}
	
	protected UploadedFileAccessor getUploadAccessor() {
		return this.context.getUploadAccessor();
	}
}