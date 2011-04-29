package org.bibsonomy.database.common.typehandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.TimeZone;

import org.junit.Test;

/**
 * @author rja
 * @version $Id$
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
