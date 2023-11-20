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
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.output.Bibliography;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * FIXME: unused
 *
 * Renders bibliographies using a reusable CSL citation processor instance with an internal ItemDataProvider.
 */
public class CSLProcessor implements Closeable {
	// MHO DISABLED
	//private static final Log LOG = CmsLog.getLog(CSLProcessor.class);
	private final CSL csl;
	private final SingleItemDataProvider provider = new SingleItemDataProvider();

	/**
	 * for debugging
	 */
	private final String styleFriendlyName;

	public CSLProcessor(final String style, final String lang) throws IOException {
		styleFriendlyName = getFriendlyName(style);
		// MHO DISABLED
		//LOG.debug("Instantiating CSL for style '" + styleFriendlyName + "'");
		csl = new CSL(provider, style, lang);
		csl.setConvertLinks(true);
	}

	/**
	 * Checks if the given String contains the serialized XML representation
	 * of a style
	 *
	 * @param style the string to examine
	 * @return true if the String is XML, false otherwise
	 */
	// AG 2020-10-27: Copied from de.undercouch.citeproc.CSL#isXml because the method there is private
	private static boolean isXml(final String style) {
		for (int i = 0; i < style.length(); ++i) {
			final char c = style.charAt(i);
			if (!Character.isWhitespace(c)) {
				return (c == '<');
			}
		}
		return false;
	}

	/**
	 * Creates a short string apropriate for logs
	 *
	 * @param style string containing the csl, or path to the template file (without the .csl extension)
	 * @return the style name if it is not a inlined xml string, or a shortened string it it is, that can be printed
	 */
	public static String getFriendlyName(final String style) {
		return isXml(style)
				? "[...]" + style.substring(Math.max(0, style.length() - 25)).replace("\n", "")
				: style;
	}

	@Override
	public void close() {
		if (csl != null) {
			// MHO DISABLED
			//LOG.debug("Closing csl for style " + styleFriendlyName + ".");
			try {
				csl.close();
			} catch (final Error e) {
				// MHO DISABLED
				//LOG.error(e.getMessage(), e);
				throw e;
			}
		}
	}


	public String makeBibliography(final CSLItemData item) {
		if (item == null) {
			return "";
		}
		provider.setItem(item);
		csl.registerCitationItems(provider.getIds());
		final Bibliography bibliography = csl.makeBibliography();
		final String[] entries = bibliography.getEntries();
		if (entries.length != 1) {
			// MHO DISABLED
			//LOG.warn("Bibliography built for " + item + " should have 1 entry but has " + entries.length + "!");
		}
		return entries[0];
	}

	public String makeBibliography(final CSLItemData[] items) {
		return Arrays.stream(items).map(this::makeBibliography).collect(Collectors.joining());
	}

	@Override
	public String toString() {
		return "CSLProcessor for '" + styleFriendlyName + "'" + super.toString();
	}

	private static final class SingleItemDataProvider implements ItemDataProvider {
		private final String[] ids = new String[1];
		private CSLItemData item;

		public void setItem(final CSLItemData item) {
			this.item = item;
			this.ids[0] = item.getId();
		}

		@Override
		public CSLItemData retrieveItem(final String id) {
			if (!Objects.equals(id, ids[0])) {
				throw new UnsupportedOperationException("Trying to retrieve an object that has not been stored previously");
			}
			return item;
		}

		@Override
		public String[] getIds() {
			if (ids[0] == null) {
				throw new UnsupportedOperationException("Trying to retrieve ids without having stored any item previously");
			}
			return ids;
		}
	}
}
