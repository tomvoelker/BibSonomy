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
package org.bibsonomy.rest.renderer.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.layout.csl.CslModelConverter;
import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.AbstractPostExportRenderer;
import org.bibsonomy.rest.renderer.RenderingFormat;

import net.sf.json.JSONSerializer;

/**
 * @author wla
 */
public class CSLRenderer extends AbstractPostExportRenderer {

	private static final Log LOGGER = LogFactory.getLog(CSLRenderer.class);
	/** used for sending errors via "error : ..." */
	public static final String ERROR_MESSAGE_KEY = "error";

	private static final String BEGIN = "{\n";
	private static final String END = "\n}";
	private static final String DELIMITER = ",\n";
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.AbstractPostExportRenderer#getFormat()
	 */
	@Override
	protected RenderingFormat getFormat() {
		return RenderingFormat.CSL;
	}

	@Override
	public void serializePosts(final Writer writer, final List<? extends Post<? extends Resource>> posts, final ViewModel viewModel) throws InternServerException {
		final ListIterator<? extends Post<? extends Resource>> iter = posts.listIterator();
		try {
			writer.append(BEGIN);
			while (iter.hasNext()) {
				final Post<? extends Resource> post = iter.next();
				writer.append("\"" + post.getResource().getIntraHash() + post.getUser().getName() + "\":");
				serializePost(writer, post, viewModel);
				if (iter.hasNext()) {
					writer.append(DELIMITER);
				}
			}
			writer.append(END);
			writer.flush();
		} catch (final IOException ex) {
			LOGGER.error(ex);
		}
	}

	@Override
	public void serializePost(final Writer writer, final Post<? extends Resource> post, final ViewModel model) {
		final Record record = CslModelConverter.convertPost(post);
		try {
			final String string = JSONSerializer.toJSON(record, CslModelConverter.getJsonConfig()).toString();
			writer.append(string);
			writer.flush();
		} catch (final IOException ex) {
			LOGGER.error(ex);
		}
	}
	
	@Override
	public void serializeError(final Writer writer, final String errorMessage) {
		final Map<String, String> errorMsg = new HashMap<String, String>();
		errorMsg.put(ERROR_MESSAGE_KEY, errorMessage);
		try {
			final String string = JSONSerializer.toJSON(errorMsg, CslModelConverter.getJsonConfig()).toString();
			writer.append(string);
			writer.flush();
		} catch (final IOException ex) {
			LOGGER.error(ex);
		}
	}
}
