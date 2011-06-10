//    Copyright (c) 2009 Elwyn Malethan
//
//    This file is part of java-pingback.
//
//    java-pingback is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    java-pingback is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.

//    You should have received a copy of the GNU General Public License
//    along with java-pingback.  If not, see <http://www.gnu.org/licenses/>.

package com.malethan.pingback;

import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * <p>Copyright &copy; 2009 Elwyn Malethan</p>
 */
public class LinkTest {
    //----------------------------------------------------------------------- Static Properties and Constants
    //----------------------------------------------------------------------- Static Methods
    //----------------------------------------------------------------------- Instance Properties
    //----------------------------------------------------------------------- Constructors
    //----------------------------------------------------------------------- Tests

    @Test
    public void shouldNotReportAsPingbackEnabledIfPingbackUrlNull() {
        assertFalse(new Link("", "", null, true).isPingbackEnabled());
    }

    @Test
    public void shouldNotReportAsPingbackEnabledIfPingbackUrlEmpty() {
        assertFalse(new Link("", "", "", true).isPingbackEnabled());
    }

    @Test
    public void shouldReportAsPingbackEnabledIfPingbackUrlOk() {
        assertTrue(new Link("", "", "http://www.xx.ccc/", true).isPingbackEnabled());
    }


    //----------------------------------------------------------------------- Getters and Setters
    //----------------------------------------------------------------------- Instance Methods
}
