/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.user.settings;

import java.io.Serializable;

import org.bibsonomy.webapp.view.constants.ViewLayout;

/**
 * Compined settings influencing the layout viewed to a user
 *
 * @author jil
 */
public class LayoutSettings implements Serializable {
	private static final long serialVersionUID = 4543802036448366427L;

	/** 
	 * are advanced buttons & ui options hidden from the user?
	 */
	private boolean simpleInterface = true;
	
	/**
	 * which layout should be used?
	 */
	private ViewLayout viewLayout = ViewLayout.BOOTSTRAP;

	/**
	 * @return {@link ViewLayout} 
	 */
	public ViewLayout getViewLayout() {
		return viewLayout;
	}

	/**
	 * @param layout
	 */
	public void setViewLayout(ViewLayout layout) {
		this.viewLayout = layout;
	}

	/**
	 * @return the simpleInterface
	 */
	public boolean isSimpleInterface() {
		return this.simpleInterface;
	}

	/**
	 * @param simpleInterface the simpleInterface to set
	 */
	public void setSimpleInterface(final boolean simpleInterface) {
		this.simpleInterface = simpleInterface;
	}

}
