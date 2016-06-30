/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
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
package org.bibsonomy.rest.renderer;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.impl.json.JSONRenderer;
import org.bibsonomy.rest.renderer.impl.xml.XMLRenderer;

/**
 * A factory to get implementations of the
 * {@link org.bibsonomy.rest.renderer.Renderer}-interface.
 * 
 * @author Christian Schenk
 */
public class RendererFactory {

	/**
	 * Holds the available renderers. New renderers can be added using
	 */
	private Map<RenderingFormat, Renderer> renderers;

	private UrlRenderer urlRenderer;
	
	/**
	 * constructs an empty renderer factory without any renderers
	 */
	public RendererFactory() {
	}
	
	/**
	 * constructs a renderer factory with a {@link JSONRenderer} and a
	 * {@link XMLRenderer}
	 * @param urlRenderer 
	 */
	public RendererFactory(final UrlRenderer urlRenderer) {
		this.urlRenderer = urlRenderer;
		this.renderers = new HashMap<RenderingFormat, Renderer>();
		this.renderers.put(RenderingFormat.JSON, new JSONRenderer(urlRenderer));
		final XMLRenderer renderer = new XMLRenderer(urlRenderer);
		renderer.init();
		this.renderers.put(RenderingFormat.XML, renderer);
	}

	/**
	 * Registers the provided renderer with the given renderingFormat.
	 * 
	 * @param renderingFormat
	 * @param renderer
	 */
	public void addRenderer(final RenderingFormat renderingFormat, final Renderer renderer) {
		this.renderers.put(renderingFormat, renderer);
	}

	/**
	 * Returns the renderer for the given format; it defaults to the XML
	 * renderer.
	 * 
	 * @param renderingFormat
	 * @return the renderer
	 */
	public Renderer getRenderer(final RenderingFormat renderingFormat) {
		if (renderingFormat == null) {
			throw new InternServerException("RenderingFormat is null");
		}

		if (this.renderers.containsKey(renderingFormat)) {
			return this.renderers.get(renderingFormat);
		}

		// the default is the XML renderer
		return this.renderers.get(RenderingFormat.XML);
	}

	/**
	 * @param renderers
	 *            the renderer to set
	 */
	public void setRenderers(final Map<RenderingFormat, Renderer> renderers) {
		this.renderers = renderers;
	}

	/**
	 * @return the urlRenderer
	 */
	public UrlRenderer getUrlRenderer() {
		return this.urlRenderer;
	}

	/**
	 * @param urlRenderer
	 *            the urlRenderer to set
	 */
	public void setUrlRenderer(final UrlRenderer urlRenderer) {
		this.urlRenderer = urlRenderer;
	}

}