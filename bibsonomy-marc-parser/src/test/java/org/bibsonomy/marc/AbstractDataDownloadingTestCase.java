/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
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
package org.bibsonomy.marc;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.data.ByteArrayData;
import org.bibsonomy.model.util.data.DualData;
import org.bibsonomy.model.util.data.DualDataWrapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * @author jensi
 */
public abstract class AbstractDataDownloadingTestCase {
	
	private final MarcToBibTexReader reader = new MarcToBibTexReader();

	protected BibTex get(final String hebisId) {
		final Collection<BibTex> bibs = reader.read(new ImportResource(downloadMarcWithPica(hebisId)));
		assertEquals(1, bibs.size());
		return bibs.iterator().next();
	}
	
	protected DualData downloadMarcWithPica(final String hebisId) {
		Document doc;
		doc = download(hebisId, false);
		Node node = doc.selectSingleNode( "//str[@name='fullrecord']" );
		if (node == null) {
			doc = download(hebisId, true);
			node = doc.selectSingleNode( "//str[@name='fullrecord']" );
			if (node == null) {
				throw new NoSuchElementException("no object with hebis id='" + hebisId + "' found");
			}
		}
		String name = node.getText();
		name = name.replace("#29;", "\u001D");
		name = name.replace("#30;", "\u001E");
		name = name.replace("#31;", "\u001F");
		
		final ByteArrayData marcData = new ByteArrayData(name.getBytes(), "application/marc");
		
		final String picaString = doc.selectSingleNode( "//str[@name='raw_fullrecord']" ).getText();
		final ByteArrayData picaData = new ByteArrayData(picaString.getBytes(), "application/pica");
		return new DualDataWrapper(marcData, picaData);
	}

	public Document download(final String hebisId, final boolean overwriteWithAlternative) {
		try {
			return parse(readCached(hebisId, overwriteWithAlternative));
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream readCached(final String hebisId, final boolean overwriteWithAlternative) throws MalformedURLException, IOException {
		final String fileName = hebisId + ".xml";
		InputStream rVal = (overwriteWithAlternative) ? null : this.getClass().getResourceAsStream(fileName);
		if (rVal == null) {
			final String absoluteFileName = "src/test/resources/" + getClass().getPackage().getName().replace('.','/') + "/"  + fileName;
			InputStream is;
			if (overwriteWithAlternative) {
				is = new URL("http://solr.hebis.de/solr/hebis/select?q=id%3A" + hebisId + "&wt=xml&indent=true").openStream();
			} else {
				//new URL("http://wastl.hebis.uni-frankfurt.de:8983/solr/hebis_neu/select?q=id%3A" + hebisId + "&wt=xml&indent=true"));
				is = new URL("http://solr.hebis.de/solr/hebis_neu/select?q=id%3A" + hebisId + "&wt=xml&indent=true").openStream();
			}
			final OutputStream os = new FileOutputStream(absoluteFileName);
			IOUtils.copy(is, os);
			is.close();
			os.close();
			rVal = new FileInputStream(absoluteFileName);
		}
		return rVal;
	}

	public static Document parse(final InputStream is) throws DocumentException, IOException {
		final SAXReader reader = new SAXReader();
		final Document document = reader.read(is);
		is.close();
		return document;
	}
}
