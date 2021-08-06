/**
 * BibSonomy-Database-Common - Helper classes for database interaction
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
package org.bibsonomy.database.common.typehandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.TimeZone;

import org.junit.Test;

/**
 * @author rja
 */
public class TimezoneTypeHandlerCallbackTest {

    @Test
    public void testSetParameter() {
	/*
	 * FIXME: difficult to test, because iBatis objects are difficult
	 * to instantiate.
	 */
//	new TimezoneTypeHandlerCallback().setParameter(setter, parameter);
    }

    @Test
    public void testValueOf() {
	final Object valueOf = new TimezoneTypeHandlerCallback().valueOf("PST");
	assertNotNull(valueOf);
	
	final Class<? extends Object> valueOfClass = valueOf.getClass();

	assertTrue(TimeZone.class.isAssignableFrom(valueOfClass));
	
	final TimeZone timeZone = (TimeZone) valueOf;
	assertEquals("PST", timeZone.getID());
    }

}
