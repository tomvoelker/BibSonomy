package de.unikassel.puma.openaccess.sherparomeo;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Ignore;
import org.junit.Test;

import de.unikassel.puma.openaccess.sherparomeo.model.Romeoapi;

/**
 * @author rja
 * @version $Id$
 */
public class SherpaRomeoImplTest {

	private static final SherpaRomeoImpl SHERPA_ROMEO_IMPL = new SherpaRomeoImpl();

	@Test
	@Ignore // calls external service
	public void testGetPolicyForJournal() {
		final String start = "{\"publishers\":[{\"name\":\"Association for the Advancement of Artificial Intelligence\"";
		final String policy = SHERPA_ROMEO_IMPL.getPolicyForJournal("AI Magazine", null);
		
		assertEquals(start, policy.substring(0, start.length()));
	}
	
	@Test
	public void testOffline() throws Exception {
    	final String start = "{\"publishers\":[{\"name\":\"American Institute of Physics\"";
    	final JAXBContext jc = JAXBContext.newInstance("de.unikassel.puma.openaccess.sherparomeo.model");
    	final Unmarshaller unmarshaller = jc.createUnmarshaller();
    	
    	final Romeoapi sp = (Romeoapi) unmarshaller.unmarshal(new File("./src/test/resources/data/institute_of_physics.xml"));            
        assertEquals(start, SHERPA_ROMEO_IMPL.extractInformations(sp).substring(0, start.length()));
	}
}
