/**
 * BibSonomy-Logging - Logs clicks from users of the BibSonomy webapp.
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
package org.bibsonomy.dnbimport.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.dnbimport.model.ClassificationScheme;
import org.bibsonomy.dnbimport.model.DnbPublication;

/**
 * 
 * @author sst
 */
public class DnbDatabaseManager extends AbstractDatabaseManager {
	
	private DBSessionFactory sessionFactory;
	private final Map<ClassificationScheme, Map<String, String>> classificationSchemes = new HashMap<ClassificationScheme, Map<String,String>>(8);
	
	private DBSession openSession() {
		return this.sessionFactory.getDatabaseSession();
	}
	
	/**
	 * inserts the log data into the db
	 * @param logdata
	 */
	public void insertLogdata(final DnbPublication logdata) {
		final DBSession session = this.openSession();
		try {
			this.insert("gImporter.insertgImporterdata", logdata, session);

		} finally {
			session.close();
		}
	}
	
	public List<DnbPublication> selectDnbEntries(DnbPublication param) {
		final DBSession session = this.openSession();
		try {
			return (List<DnbPublication>) this.queryForList("gImporter.selectDissertation", param, session);

		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Pair<String,String>> selectClasses(ClassificationScheme kind) {
		final DBSession session = this.openSession();
		try {
			return (List<Pair<String,String>>) this.queryForList("gImporter.selectClassNames", kind.name(), session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(final DBSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public String getClassName(ClassificationScheme kind, String classId) {
		Map<String,String> schemeMap = getSchemeMap(kind);
		if (schemeMap == null) {
			return null;
		}
		return schemeMap.get(classId);
	}

	/**
	 * @param kind
	 * @return
	 */
	private Map<String, String> getSchemeMap(ClassificationScheme kind) {
		Map<String, String> m = this.classificationSchemes.get(kind);
		if (m == null) {
			m = new HashMap<>();
			for (Pair<String,String> classPair : selectClasses(kind)) {
				m.put(classPair.getFirst(), classPair.getSecond());
			}
			this.classificationSchemes.put(kind, m);
		}
		return m;
	}
}




