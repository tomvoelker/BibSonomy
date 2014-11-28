/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.util;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.database.AbstractDatabaseTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.User;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author dzo
 */
public class DatabaseSchemaInformationTest extends AbstractDatabaseTest {
	private static DatabaseSchemaInformation dBSchemaInformation;
	
	/**
	 * sets up the instance
	 */
	@BeforeClass
	public static void setup() {
		dBSchemaInformation = testDatabaseContext.getBean(DatabaseSchemaInformation.class);
	}
	
	/**
	 * tests {@link DatabaseSchemaInformation#getMaxColumnLengthForProperty(Class, String)}
	 */
	@Test
	public void testGetMaxColumnLengthForProperty() {
		assertEquals(45, dBSchemaInformation.getMaxColumnLengthForProperty(BibTex.class, "year"));
		assertEquals(45, dBSchemaInformation.getMaxColumnLengthForProperty(GoldStandardPublication.class, "year"));
		assertEquals(255, dBSchemaInformation.getMaxColumnLengthForProperty(User.class, "realname"));
	}
}
