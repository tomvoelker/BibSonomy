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

import de.undercouch.citeproc.LocaleProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.output.Bibliography;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.citeproc.CSLUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This renderer is single threaded
 * use {@link CachedCSLRenderer} if you care about performance
 */
public class AdhocRenderer {
	private static final Log LOG = LogFactory.getLog(AdhocRenderer.class);

	/**
	 * Creates a bibliography that contains all BibTeX elements in posts. The entries in this bibliography are
	 * context sensitive and names are disambiguates if the style dictates it so.
	 *
	 * @param posts       list of posts with bibtex resources to render as html
	 * @param cslTemplate the citation style to use. May either be a serialized XML representation of the style or a
	 *                    style's name such as {@code ieee}. In the latter case, the processor loads the style
	 *                    from the classpath (e.g. {@code /ieee.csl})
	 * @return a map of postIDs to their corresponding html
	 */
	public static Map<String, String> renderPosts(final List<Post<? extends BibTex>> posts, final String cslTemplate, LocaleProvider localeProvider, boolean addSurroundingTextTags) {

		final CSLItemData[] cslItems = Arrays.stream(CSLUtils.convertConcurretlyToCslItemData(posts, addSurroundingTextTags))
				.map(CSLUtils.CSLItemDataConversionResult::getItemData)
				.toArray(CSLItemData[]::new);
		try {
			final Bibliography bibliography = CSLWithLinks.makeAdhocBibliography(cslTemplate, localeProvider, "html", cslItems);
			// Each element of the entryIds array correspond with the same element of the entries array.
			final String[] ids = bibliography.getEntryIds();
			final String[] entries = bibliography.getEntries();
			final Map<String, String> idToEntryMap = new HashMap<>();
			for (int i = 0; i < ids.length; i++) {
				idToEntryMap.put(ids[i], entries[i]);
			}

			if (LOG.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("posts: [");
				for (final Post<? extends BibTex> p : posts) {
					sb.append("\"" + p.getResource().getTitle() + "\", ");
				}
				sb.append("]");
				LOG.debug(sb.toString());

				sb = new StringBuilder();
				sb.append("posts converted to cslItems: [");
				for (final CSLItemData cslItem : cslItems) {
					sb.append(cslItem.getId() + " (\"" + cslItem.getTitle() + "\"), ");
				}
				sb.append("]");
				LOG.debug(sb.toString());

				LOG.debug("entryIds: " + Arrays.toString(ids));
				LOG.debug("entries: " + Arrays.toString(entries));
			}
			return idToEntryMap;
		} catch (IOException e) {
			LOG.error("error creating post rendering using CSL", e);
		}

		return Collections.emptyMap();
	}

}
