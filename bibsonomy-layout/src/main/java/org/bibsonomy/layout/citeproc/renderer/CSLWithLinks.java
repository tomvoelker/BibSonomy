/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.layout.citeproc.renderer;

import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.ListItemDataProvider;
import de.undercouch.citeproc.LocaleProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.output.Bibliography;

import java.io.IOException;

/**
 * @author ???
 */
public class CSLWithLinks {

	/**
	 * Creates an ad hoc bibliography from the given citation items. Calling
	 * this method is rather expensive as it initializes the CSL processor.
	 * If you need to create bibliographies multiple times in your application
	 * you should create the processor yourself and cache it if necessary.
	 * <p>
	 * This version of {@link CSL#makeAdhocBibliography(String, String, CSLItemData...)} sets the option
	 * {@code development_extensions.wrap_url_and_doi = true}, resulting in the rendering of urls as html anchors.
	 *
	 * @param style        the citation style to use. May either be a serialized
	 *                     XML representation of the style or a style's name such as <code>ieee</code>.
	 *                     In the latter case, the processor loads the style from the classpath (e.g.
	 *                     <code>/ieee.csl</code>)
	 * @param outputFormat the processor's output format (one of
	 *                     <code>"html"</code>, <code>"text"</code>, <code>"asciidoc"</code>,
	 *                     <code>"fo"</code>, or <code>"rtf"</code>)
	 * @param items        the citation items to add to the bibliography
	 * @return the bibliography
	 * @throws IOException if the underlying JavaScript files or the CSL style
	 *                     could not be loaded
	 */
	static Bibliography makeAdhocBibliography(final String style, final LocaleProvider localeProvider, final String outputFormat, final CSLItemData... items) throws IOException {
		final ItemDataProvider provider = new ListItemDataProvider(items);
		try (final CSL csl = new CSL(provider, localeProvider, style, "en-US", false)) {
			csl.setConvertLinks(true);
			csl.setOutputFormat(outputFormat);

			final String[] ids = new String[items.length];
			for (int i = 0; i < items.length; ++i) {
				ids[i] = items[i].getId();
			}
			csl.registerCitationItems(ids);

			return csl.makeBibliography();
		}
	}
}
