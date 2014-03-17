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
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.AbstractPostExportRenderer;
import org.bibsonomy.rest.renderer.RenderingFormat;

/**
 * Simple Renderer for BibTex-Format
 *
 * @author MarcelM
 */
public class BibTexRenderer extends AbstractPostExportRenderer {

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.AbstractPostExportRenderer#getFormat()
	 */
	@Override
	protected RenderingFormat getFormat() {
		return RenderingFormat.BIBTEX;
	}
	
	@Override
	public void serializeError(final Writer writer, final String errorMessage) {
		// TODO Auto-generated method stub
	}

	@Override
	public void serializePost(final Writer writer, final Post<? extends Resource> post, final ViewModel model) {
		this.serializePosts(writer, Collections.singletonList(post), model);
	}

	@Override
	public void serializePosts(final Writer writer, final List<? extends Post<? extends Resource>> posts, final ViewModel viewModel) throws InternServerException {
		for (Post<? extends Resource> post : posts) {
			//Check if the resource of post is of type BibTex
			if (post.getResource() instanceof BibTex) {
				@SuppressWarnings("unchecked")
				Post<BibTex> bibtex = (Post<BibTex>)post;
				try {
					writer.append(BibTexUtils.toBibtexString(bibtex));
					writer.append(NEW_LINE); // add a new line between each post
					writer.flush();
				} catch (final LayoutRenderingException ex) {
					throw new InternServerException(ex);
				} catch (final IOException ex) {
					throw new InternServerException(ex);
				}
			} else {
				this.handleUnsupportedMediaType();
			}
		}
	}
}
