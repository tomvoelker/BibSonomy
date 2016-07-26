/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.csl;

import java.io.File;
import java.util.GregorianCalendar;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import org.bibsonomy.layout.csl.model.Date;
import org.bibsonomy.layout.csl.model.DateParts;
import org.bibsonomy.layout.csl.model.DocumentCslWrapper;
import org.bibsonomy.layout.csl.model.Person;
import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.layout.csl.model.RecordList;
import org.bibsonomy.model.Document;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author dbe
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
		
		Document d = new Document();
		d.setDate(new GregorianCalendar(2014,2,18, 14, 20, 0).getTime());
		d.setFile(new File("./pom.xml"));
		d.setFileHash("fileHashTrallalla");
		d.setFileName("pom.xml");
		d.setTemp(false);
		d.setUserName("testcase");
		rec.getDocuments().add(new DocumentCslWrapper(d));

		final RecordList list = new RecordList();
		list.add(rec);
		final JSON json = JSONSerializer.toJSON(list, CslModelConverter.getJsonConfig());
		//Assert.assertEquals("{\"keyhere\":{\"author\":[{\"family\":\"Benz\",\"given\":\"Dominik\"},{\"family\":\"Jackson\",\"given\":\"Peter\"}],\"call-number\":\"4\",\"documents\":[],\"edition\":\"3\",\"editor\":[],\"id\":\"keyhere\",\"issued\":{\"date-parts\":[[\"2010\",\"10\",\"14\"],[\"2011\",\"11\"]]},\"title\":\"Test title!\"}}", json.toString());
		
		final String jsonString = json.toString();
		Assert.assertTrue(jsonString.contains("\"family\":\"Benz\""));
		Assert.assertTrue(jsonString.contains("\"title\":\"Test title!\""));
		Assert.assertTrue(jsonString.contains("\"author\":[{"));
		Assert.assertTrue(jsonString.contains("\"edition\":\"3\""));
		Assert.assertTrue(jsonString.contains("\"documents\":[{"));
		Assert.assertTrue(jsonString.contains("\"fileHash\":\"fileHashTrallalla\""));
		
		
		
	}
}
