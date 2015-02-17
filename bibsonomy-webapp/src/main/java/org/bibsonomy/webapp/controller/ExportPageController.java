/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller;

import java.io.IOException;

import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.AbstractJabRefLayout;
import org.bibsonomy.layout.standard.StandardLayouts;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.webapp.command.ExportPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian, lsc
 */
public class ExportPageController implements MinimalisticController<ExportPageCommand> {

	private LayoutRenderer<AbstractJabRefLayout> layoutRenderer;
	private StandardLayouts layouts;

	/**
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public ExportPageCommand instantiateCommand() {
		return new ExportPageCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy
	 * .webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final ExportPageCommand command) {
		command.addLayoutMap(this.layoutRenderer.getLayouts());

		// no standard exports in the json export!
		if ("json".equals(command.getFormat())) {
			/*
			 * JSON list about the available JabRef layouts on the /layoutinfo
			 */
			return Views.EXPORTLAYOUTS;
		}

		final RequestWrapperContext context = command.getContext();
		if (context.isUserLoggedIn()) {
			try {
				command.addLayout(this.layoutRenderer.getLayout(LayoutRenderer.CUSTOM_LAYOUT, context.getLoginUser().getName()));
			} catch (final LayoutRenderingException | IOException e) {
				// ignore because reasons 
			}
		}

		command.addLayoutMap(this.layouts.getLayoutMap());

		if (command.getFormatEmbedded()) {
			return Views.EXPORT_EMBEDDED;
		}

		return Views.EXPORT;
	}

	/**
	 * @param layoutRenderer
	 */
	public void setLayoutRenderer(final LayoutRenderer<AbstractJabRefLayout> layoutRenderer) {
		this.layoutRenderer = layoutRenderer;
	}

	/**
	 * @param layouts
	 *            the layouts to set
	 */
	public void setLayouts(StandardLayouts layouts) {
		this.layouts = layouts;
	}
}
