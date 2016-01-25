/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.BibTexReader;
import org.bibsonomy.testutil.CommonModelUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.PublicationRendererCommand;
import org.bibsonomy.webapp.view.Views;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.MapBindingResult;

/**
 * @author jensi
 */
public class PublicationRenderingControllerTest {

	/** test bibtex generation for uploaded files which are read by some {@link BibTexReader} 
	 * @throws IOException */
	@Test
	public void testBibtexReaderIntegration() throws IOException {
		
		final List<BibTex> bib = getBibtexFromFile();
		final byte[] bytes = IOUtils.toByteArray(getTestBibFileStream());
		
		Map<String, BibTexReader> m = new HashMap<String, BibTexReader>();
		m.put("bla", new BibTexReader() {
			
			@Override
			public Collection<BibTex> read(ImportResource r) {
				byte[] receivedBytes;
				try {
					receivedBytes = IOUtils.toByteArray(getTestBibFileStream());
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				assertTrue(ArrayUtils.isEquals(bytes, receivedBytes));
				return bib;
			}
		});
		
		PublicationRenderingController ctrl = new PublicationRenderingController();
		ctrl.setBibtexReaders(m);
		ctrl.setErrors(new MapBindingResult(new HashMap<Object, Object>(), "dummy"));
		PublicationRendererCommand cmd = ctrl.instantiateCommand();
		cmd.setFormat(Views.FORMAT_STRING_BIBTEX);
		cmd.setFile(new MockMultipartFile("test.bib", "test.bib", "bla", bytes));
		
		assertEquals(Views.BIBTEX, ctrl.workOn(cmd));
		assertEquals(1, cmd.getBibtex().getList().size() );
		CommonModelUtils.assertPropertyEquality(bib.get(0), cmd.getBibtex().getList().get(0).getResource(), 5, null);
	}

	protected List<BibTex> getBibtexFromFile() {
		SimpleBibTeXParser parser = new SimpleBibTeXParser();
		try {
			BufferedReader sr = new BufferedReader(new InputStreamReader(getTestBibFileStream(), StringUtils.CHARSET_UTF_8));
			return parser.parseInternal(sr, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream getTestBibFileStream() {
		return getClass().getResourceAsStream("/test.bib");
	}
}
