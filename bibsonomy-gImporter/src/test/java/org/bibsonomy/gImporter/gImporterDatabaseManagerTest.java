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
/*	public void insert_to_database() {
		testt = (gImporterDatabaseManager)beanFactory.getBean("gImporterDataLogic");
		//gImporterDatabaseManager gidbm = new gImporterDatabaseManager();
		gImporterData logdata = new gImporterData();
		logdata.setTitle("nasim_title");
		testt.insertLogdata(logdata);
	}
	*/
	
	
	public void select_dissertations(){
		
		
		List<gImporterData> posts = new ArrayList<gImporterData>();
		
		gImporterData param = new gImporterData();
		param.setDiss(true);
		posts = testt.select_dissertation(param);
		
		//testt.insert_bibtex(result);
		
		User user = new User();
		user.setName("testuser1");
	
		for(int i =0; i<posts.size();i++){
	//		final DBSession session = this.openSession();
						
			final Post<GoldStandardPublication> gold = new Post<GoldStandardPublication>();
	
			final GoldStandardPublication goldP = new GoldStandardPublication();
			goldP.setAddress(posts.get(i).getAddress());
			goldP.setYear(posts.get(i).getYear());
			goldP.setTitle(posts.get(i).getTitle());
			
			gold.setResource(goldP);
			gold.setUser(user);
			gold.getResource().recalculateHashes();
			
		/*	Group G = new Group();
			G.setGroupId(PUBLIC_GROUP_ID);
			Set<Group> GList=new HashSet<Group>();
			GList.add(G);
			gold.setGroups(GList);
			*/
		/*	Tag T  = new Tag();
			T.setName("imported_from_dnb2");
			Set<Tag> tagSet = new HashSet<Tag>();
			tagSet.add(T);
			gold.setTags(tagSet);
			
			*/
			
			GSDBM.createPost(gold, this.dbSession);
			

		//	posts.add(gold);
		}
		

		
	}
	
	
}