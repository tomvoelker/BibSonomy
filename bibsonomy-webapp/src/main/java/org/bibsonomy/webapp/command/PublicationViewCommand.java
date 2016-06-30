/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.command;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author jensi
 */
public interface PublicationViewCommand {
	/**
	 * @return the bibtex ListView
	 */
	public ListCommand<Post<BibTex>> getBibtex();
	
	/**
	 * @return The requested format.
	 * 
	 */
	public String getFormat();

	/** @return whether the result should be presented as a download */
	public boolean isDownload();

	/**
	 * @return true iff fields with automatically imported dummy values (noauthor/noyear) for required fields should be skipped during rendering (or alternatively replaced by more readable values such as N.N. by some views)
	 */
	public boolean isSkipDummyValues();
}
