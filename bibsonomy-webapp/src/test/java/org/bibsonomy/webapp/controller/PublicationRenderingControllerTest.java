package org.bibsonomy.webapp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.BibTexReader;
import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.testutil.CommonModelUtils;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.view.Views;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.MapBindingResult;

/**
 * @author jensi
 * @version $Id$
 */
public class PublicationRenderingControllerTest {

	/** test bibtex generation for uploaded files which are read by some {@link BibTexReader} 
	 * @throws IOException */
	@Test
	public void testBibtexReaderIntegration() throws IOException {
		
		final List<ImportResource> bib = getBibtexFromFile();
		final byte[] bytes = IOUtils.toByteArray(getTestBibFileStream());
		
		Map<String, BibTexReader> m = new HashMap<String, BibTexReader>();
		m.put("bla", new BibTexReader() {
			
			@Override
			public Collection<ImportResource> read(Data d) {
				byte[] receivedBytes;
				try {
					receivedBytes = IOUtils.toByteArray(getTestBibFileStream());
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				Assert.assertTrue(ArrayUtils.isEquals(bytes, receivedBytes));
				return bib;
			}
		});
		
		PublicationRenderingController ctrl = new PublicationRenderingController();
		ctrl.setMimeTypeReaders(m);
		ctrl.setErrors(new MapBindingResult(new HashMap<Object, Object>(), "dummy"));
		PostPublicationCommand cmd = ctrl.instantiateCommand();
		cmd.setFormat(Views.FORMAT_STRING_BIBTEX);
		cmd.setFile(new MockMultipartFile("test.bib", "test.bib", "bla", bytes));
		
		Assert.assertEquals(Views.BIBTEX, ctrl.workOn(cmd));
		Assert.assertEquals(1, cmd.getBibtex().getList().size() );
		CommonModelUtils.assertPropertyEquality(bib.get(0), cmd.getBibtex().getList().get(0).getResource(), 5, null);
		
	}

	protected List<ImportResource> getBibtexFromFile() {
		SimpleBibTeXParser parser = new SimpleBibTeXParser();
		try {
			BufferedReader sr = new BufferedReader(new InputStreamReader(getTestBibFileStream(), "UTF-8"));
			// actually not 100% ok but easier
			return (List) parser.parseInternal(sr, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream getTestBibFileStream() {
		return getClass().getResourceAsStream("/test.bib");
	}
}
