package org.bibsonomy.marc;

import java.net.URL;
import java.util.Collection;

import junit.framework.Assert;

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
 * @version $Id$
 */
public abstract class AbstractDataDownloadingTestCase {
	
	private final MarcToBibTexReader reader = new MarcToBibTexReader();

	protected BibTex get(String hebisId) {
		Collection<ImportResource> bibs = reader.read(new ImportResource(downloadMarcWithPica(hebisId)));
		Assert.assertEquals(1, bibs.size());
		return bibs.iterator().next();
	}
	
	protected DualData downloadMarcWithPica(String hebisId) {
		Document doc;
		try {
			//doc = parse(new URL("http://wastl.hebis.uni-frankfurt.de:8983/solr/hebis_neu/select?q=id%3A" + hebisId + "&wt=xml&indent=true"));
			doc = parse(new URL("http://solr.hebis.de/solr/hebis_neu/select?q=id%3A" + hebisId + "&wt=xml&indent=true"));
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Node node = doc.selectSingleNode( "//str[@name='fullrecord']" );
        String name = node.getText();
        name = name.replace("#29;", "\u001D");
        name = name.replace("#30;", "\u001E");
        name = name.replace("#31;", "\u001F");
        
        ByteArrayData marcData = new ByteArrayData(name.getBytes(), "application/marc");
        
        String picaString = doc.selectSingleNode( "//str[@name='raw_fullrecord']" ).getText();
        ByteArrayData picaData = new ByteArrayData(picaString.getBytes(), "application/pica");
        return new DualDataWrapper(marcData, picaData);
	}

	public static Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }
}
