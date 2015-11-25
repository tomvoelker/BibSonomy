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
package org.bibsonomy.rest.renderer.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.EndnoteUtils;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.AbstractPostExportRenderer;
import org.bibsonomy.rest.renderer.RenderingFormat;

/**
 * Simple Renderer for EndNote-Format
 *
 * @author MarcelM
 */
public class EndnoteRenderer extends AbstractPostExportRenderer {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.AbstractPostExportRenderer#getFormat()
	 */
	@Override
	protected RenderingFormat getFormat() {
		return RenderingFormat.ENDNOTE;
	}
	
	@Override
	public void serializePost(final Writer writer, final Post<? extends Resource> post, final ViewModel model) {
		serializePosts(writer, Collections.singletonList(post), model);
	}

	@Override
	public void serializePosts(final Writer writer, final List<? extends Post<? extends Resource>> posts, final ViewModel viewModel) throws InternServerException {
		for (Post<? extends Resource> post : posts) {
			// check if the resource of post is of type BibTex
			if (post.getResource() instanceof BibTex) {
				@SuppressWarnings("unchecked")
				Post<BibTex> bibtex = (Post<BibTex>)post;
				try {
					writer.append(EndnoteUtils.toEndnoteString(bibtex,true));
					writer.append(NEW_LINE);
					writer.flush();
				}  catch (final LayoutRenderingException ex) {
					throw new InternServerException(ex);
				} catch (final IOException ex) {
					throw new InternServerException(ex);
				}
			} else {
				handleUnsupportedMediaType();
			}
		}
	}
}
