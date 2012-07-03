/**
 *
 *  BibSonomy-Layout - Layout engine for the webapp.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.layout.csl;

import static org.junit.Assert.assertEquals;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import org.bibsonomy.layout.csl.model.Date;
import org.bibsonomy.layout.csl.model.DateParts;
import org.bibsonomy.layout.csl.model.Person;
import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.layout.csl.model.RecordList;
import org.junit.Test;

/**
 * 
 * @author dbe
 * @version $Id$
 */
public class ModelToJsonTest {

	@Test
	public void testToJSon() {
		final Record rec = new Record();

		/*
		 * author
		 */
		final Person auth1 = new Person();
		auth1.setGiven("Dominik");
		auth1.setFamily("Benz");
		final Person auth2 = new Person();
		auth2.setGiven("Peter");
		auth2.setFamily("Jackson");

		/*
		 * date
		 */
		final Date date = new Date();
		date.getDate_parts().add(new DateParts("2010", "10", "14"));
		date.getDate_parts().add(new DateParts("2011", "11"));

		/*
		 * fields
		 */
		rec.setTitle("Test title!");
		rec.getAuthor().add(auth1);
		rec.getAuthor().add(auth2);
		rec.setEdition("3");
		rec.setCall_number("4");
		rec.setIssued(date);
		rec.setId("keyhere");

		final RecordList list = new RecordList();
		list.add(rec);
		final JSON json = JSONSerializer.toJSON(list, CslModelConverter.getJsonConfig());
		assertEquals("{\"keyhere\":{\"author\":[{\"family\":\"Benz\",\"given\":\"Dominik\"},{\"family\":\"Jackson\",\"given\":\"Peter\"}],\"call-number\":\"4\",\"edition\":\"3\",\"editor\":[],\"id\":\"keyhere\",\"issued\":{\"date-parts\":[[\"2010\",\"10\",\"14\"],[\"2011\",\"11\"]]},\"title\":\"Test title!\"}}", json.toString());
	}
}
