/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.user.settings;

import org.bibsonomy.model.enums.FavouriteLayoutSource;
import org.bibsonomy.model.enums.SimpleExportLayout;


/**
 * @author jp
 */
public class FavouriteLayout {
	private FavouriteLayoutSource source;
	private String style;
	private String displayName;
	
	/**
	 * @param source
	 * @param style
	 * 
	 * creates a new favourite layout with source as source, STYLE as style and a display Name
	 */
	public FavouriteLayout(String source, String style) {
		this(FavouriteLayoutSource.valueOf(source), style);
	}
	/**
	 * @param source
	 * @param style
	 * 
	 * creates a new favourite layout with source as source, STYLE as style and a display Name
	 */
	public FavouriteLayout(FavouriteLayoutSource source, String style) {
		this.source = source;
		this.style = style.toUpperCase();
		this.displayName = style.toLowerCase();
		if (SimpleExportLayout.BIBTEX.getDisplayName().equalsIgnoreCase(style)) {
			displayName = SimpleExportLayout.BIBTEX.getDisplayName();
		} else if (SimpleExportLayout.ENDNOTE.getDisplayName().equalsIgnoreCase(style)) {
			displayName = SimpleExportLayout.ENDNOTE.getDisplayName();
		} else if (FavouriteLayoutSource.CUSTOM.equals(source)) {
			if(style.toLowerCase().endsWith(".csl")){
				displayName = style.substring(style.indexOf('_', style.indexOf('_')+1)+1, style.toLowerCase().indexOf(".csl")).trim();
			}
		}
	}
	
	/**
	 * @return the source
	 */
	public FavouriteLayoutSource getSource() {
		return this.source;
	}
	
	/**
	 * ATTENTION
	 * @return the displayName. Works only for "SIMPLE" source. Nothing else!!
	 */
	public String getDisplayName() {
		return this.displayName;
	}
	
	/**
	 * @return the style
	 */
	public String getStyle() {
		return this.style;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FavouriteLayout other = (FavouriteLayout) obj;
		if (source != other.source)
			return false;
		if (style == null) {
			if (other.style != null)
				return false;
		} else if (!style.equals(other.style))
			return false;
		return true;
	}
}
