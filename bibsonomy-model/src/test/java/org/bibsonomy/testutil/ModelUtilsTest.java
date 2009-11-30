/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.testutil;

import java.util.regex.Pattern;

import static junit.framework.Assert.fail;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class ModelUtilsTest {

	/**
	 * tests assertPropertyEquality
	 */
	@Test
	public void assertPropertyEquality() {
		final Post<BibTex> postA = ModelUtils.generatePost(BibTex.class);
		final Post<BibTex> postB = ModelUtils.generatePost(BibTex.class);
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, null, "date");
		postB.getTags().clear();
		try {
			ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, null);
			fail();
		} catch (Throwable ignored) {
		}
		try {
			ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, Pattern.compile(".ate"));
			fail();
		} catch (Throwable ignored) {
		}
		postB.setDate(postA.getDate());
		ModelUtils.assertPropertyEquality(postA, postB, 1, null);
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, null, "tags");
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, Pattern.compile("t[ga]{2}s"));
	}
}