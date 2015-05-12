package org.bibsonomy.gImporter;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.GoldStandardDatabaseManager;
import org.bibsonomy.database.managers.GoldStandardPublicationDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class gImporterDatabaseManagerTest extends AbstractDatabaseManagerTest{
	private static final BeanFactory beanFactory = new ClassPathXmlApplicationContext("org/bibsonomy/gImporter/gImporterTestContext.xml");
	private static gImporterDatabaseManager testt;
	//private static BibTexDatabaseManager bibTexDb;
	private static GoldStandardPublicationDatabaseManager GSDBM;
	//protected LogicInterface logic;
	
	/**
	 * Initializes the test database.
	 */
	@BeforeClass
	public static void initDatabaseManager() {	
		GSDBM = GoldStandardPublicationDatabaseManager.getInstance();
		
	}
	
	/**
	 * 
	 */
	@BeforeClass
	public static void setUpGimporter() {
		testt = (gImporterDatabaseManager)beanFactory.getBean("gImporterDataLogic");
	}

	@Test

	
	public void select_dissertations(){
		
		
		List<gImporterData> posts_param = new ArrayList<gImporterData>();
		
		gImporterData param = new gImporterData();
		param.setDiss(true);
		posts_param = testt.select_dissertation(param);
		
		
		
		User user = new User();
		user.setName("testuser1");
	
		for(int i =0; i<posts_param.size();i++){
						
			final Post<GoldStandardPublication> gold = new Post<GoldStandardPublication>();
			final GoldStandardPublication goldP = new GoldStandardPublication();
			goldP.setAddress(posts_param.get(i).getAddress());
			goldP.setYear(posts_param.get(i).getYear());
			goldP.setTitle(posts_param.get(i).getTitle());
			
			List<PersonName> authors = new ArrayList<PersonName>(); 
			PersonName author = new PersonName(posts_param.get(i).getFirstName(), posts_param.get(i).getLastName());
			authors.add(author);
			goldP.setAuthor(authors);
			
			gold.setResource(goldP);
			gold.setUser(user);
			gold.getResource().recalculateHashes();
			
	
			
			GSDBM.createPost(gold, this.dbSession);
		
		}
		

		
	}
	
	
}