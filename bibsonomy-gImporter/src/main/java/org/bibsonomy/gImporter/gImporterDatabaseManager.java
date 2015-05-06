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
package org.bibsonomy.gImporter;

import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * 
 * @author sst
 */
public class gImporterDatabaseManager extends AbstractDatabaseManager {
	
	private gImporterData gImporterData;
	
	private DBSessionFactory sessionFactory;
	
	private DBSession openSession() {
		return this.sessionFactory.getDatabaseSession();
	}
	
	/**
	 * inserts the log data into the db
	 * @param logdata
	 */
	public void insertLogdata(final gImporterData logdata) {
		final DBSession session = this.openSession();
		try {
			this.insert("gImporter.insertgImporterdata", logdata, session);

		} finally {
			session.close();
		}
	}
	
	public List<gImporterData> select_dissertation(gImporterData param) {
		final DBSession session = this.openSession();
		try {
			return (List<gImporterData>) this.queryForList("gImporter.selectDissertation", param, session);

		} finally {
			session.close();
		}
	
	}
	public void insert_bibtex(List<gImporterData> posts){
		BibTexDatabaseManager bibTexDb = BibTexDatabaseManager.getInstance();
		//	List<Post<? extends Resource>> posts = new ArrayList<Post<?>>();
		User user = new User();
		user.setName("testuser1");
	
		for(int i =0; i<posts.size();i++){
			final DBSession session = this.openSession();
			try {
						
				final Post<BibTex> gold = new Post<BibTex>();
		
				final GoldStandardPublication goldP = new GoldStandardPublication();
				goldP.setAddress(posts.get(i).getAddress());
				goldP.setYear(posts.get(i).getYear());
				goldP.setTitle(posts.get(i).getTitle());
				
				gold.setResource(goldP);
				gold.setUser(user);
				gold.getResource().recalculateHashes();
				bibTexDb.createPost(gold, session);
			} finally {
				session.close();
			}

		//	posts.add(gold);
		}
		
		
		//final String createdPost = this.logic.createPosts(posts).get(0);

	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(final DBSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return the gImporterData
	 */
	public gImporterData getgImporterData() {
		return this.gImporterData;
	}

	/**
	 * @param gImporterData the gImporterData to set
	 */
	public void setgImporterData(gImporterData gImporterData) {
		this.gImporterData = gImporterData;
	}
}




