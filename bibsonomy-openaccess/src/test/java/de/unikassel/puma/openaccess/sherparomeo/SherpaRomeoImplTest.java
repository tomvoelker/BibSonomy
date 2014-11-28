/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
