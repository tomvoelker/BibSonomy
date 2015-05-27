package org.bibsonomy.dnbimport;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.dnbimport.database.DnbDatabaseManager;
import org.bibsonomy.dnbimport.model.DnbPerson;
import org.bibsonomy.dnbimport.model.DnbPublication;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Gender;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.BibTexUtils;



public class DnbImporter /* extends AbstractDatabaseManagerTest */ implements Runnable {
	private LogicInterface adminLogic;
	
	private DnbDatabaseManager dnbDatabaseManager;

	private String userName;

	@Override
	public void run() {
		List<DnbPublication> dnbEntries = new ArrayList<DnbPublication>();
		
		Writer w = openErrorLogFile();
		
		DnbPublication param = new DnbPublication();
		param.setDiss(true);
		dnbEntries = dnbDatabaseManager.selectDnbEntries(param);
		
		User user = new User();
		user.setName(userName);
	
		for (DnbPublication p : dnbEntries) {
			try {
				final Post<BibTex> gold = new Post<BibTex>();
				final BibTex goldP = new BibTex();
				//set school
				StringBuilder school = new StringBuilder();
				if (!StringUtils.isEmpty(p.getSchoolP1())) {
					school.append(p.getSchoolP1());
				}
				if (!StringUtils.isEmpty(p.getSchoolP2())) {
					if (school.length() > 0) {
						school.append(' ');
					}
					school.append(p.getSchoolP2());
				}
				goldP.setSchool(school.toString());
				
				//set year
				if (!StringUtils.isEmpty(p.getSubYear())) {
					goldP.setYear(p.getSubYear());
				} else if (!StringUtils.isEmpty(p.getPubYear())) {
					goldP.setYear(p.getPubYear());
				} else {
					w.append(p.getTitleId() + "\t" + "noYear");
					continue;
				}
				
				if (StringUtils.isEmpty(p.getMainTitle())) {
					w.append(p.getTitleId() + "\t" + "noTitle");
					continue;
				} else if (!StringUtils.isEmpty(p.getSubTitle())) {
					goldP.setTitle(p.getMainTitle() + ": " + p.getSubTitle());
				} else {
					goldP.setTitle(p.getMainTitle());
				}
				//set authors
				List<PersonName> authors = new ArrayList<PersonName>();
				goldP.setAuthor(authors);
				
				List<PersonName> editors = new ArrayList<PersonName>();
				goldP.setEditor(editors);
				
				List<ResourcePersonRelation> relationsToInsert = new ArrayList<>();
				
				
				for (DnbPerson dp : p.getPersons()) {
					final String dnbId = dp.getPersonId().trim();
					Person per = adminLogic.getPersonById(PersonIdType.DNB_ID, dnbId);
					if (per == null) {
						per = new Person();
						if (StringUtils.contains(dp.getGender(), "1")) {
							per.setGender(Gender.m);
						} else if (StringUtils.contains(dp.getGender(), "2")) {
							per.setGender(Gender.F);
						}
						per.setDnbPersonId(dp.getPersonId());
						final PersonName name = new PersonName(dp.getFirstName(), dp.getLastName());
						per.setMainName(name);
						adminLogic.createOrUpdatePerson(per);
					} else {
						// TODO: maybe update person
						// TODO: implement history of all person-related tables
					}
					
					
					final ResourcePersonRelation rel = new ResourcePersonRelation();
					rel.setPerson(per);
					rel.setPersonIndex(0);
					rel.setPost(gold);
					if (StringUtils.contains(dp.getPersonFunction(), "aut")) {
						rel.setPersonIndex(authors.size());
						rel.setRelationType(PersonResourceRelationType.AUTHOR);
						authors.add(per.getMainName());
					} else if (StringUtils.contains(dp.getPersonFunction(), "edt")) {
						rel.setPersonIndex(editors.size());
						rel.setRelationType(PersonResourceRelationType.EDITOR);
						editors.add(per.getMainName());
					} else if (StringUtils.contains(dp.getPersonFunction(), "gut1")) {
						rel.setRelationType(PersonResourceRelationType.FIRST_REVIEWER);
					} else if (StringUtils.contains(dp.getPersonFunction(), "gut")) {
						rel.setRelationType(PersonResourceRelationType.REVIEWER);
					} else if (StringUtils.contains(dp.getPersonFunction(), "btr")) {
						rel.setRelationType(PersonResourceRelationType.ADVISOR);
					//} else if (StringUtils.contains(dp.getPersonFunction(), "ctb")) {
					} else {
						rel.setRelationType(PersonResourceRelationType.OTHER);
					}
					relationsToInsert.add(rel);
					
					
				}
				
				if (authors.isEmpty()) {
					w.append(p.getTitleId() + "\t" + "noAuthor");
					continue;
				}
				
				//set entrytype and type
				if(p.isDiss()){
					goldP.setEntrytype("phdthesis");
				}
				else if(p.isHabil()){
					goldP.setEntrytype("phdthesis");
					goldP.setType("habilitation");
				}
				gold.setResource(goldP);
				gold.setUser(user);
				gold.addTag("dnbimport");
				gold.getResource().recalculateHashes();
				goldP.parseMiscField();
				goldP.addMiscField("dnbTitleId", p.getTitleId());
				goldP.serializeMiscFields();
				goldP.setBibtexKey(BibTexUtils.generateBibtexKey(goldP));
				
				List<Post<? extends Resource>> goldies = new ArrayList<>();
				goldies.add(gold);
				
				Post<? extends Resource> existingingPost = adminLogic.getPostDetails(gold.getResource().getIntraHash(), userName);
				if (existingingPost != null) {
					adminLogic.updatePosts(goldies, PostUpdateOperation.UPDATE_ALL);
				} else {
					adminLogic.createPosts(goldies);
				}
				
			
				for (ResourcePersonRelation rel : relationsToInsert) {
					try {
						adminLogic.addResourceRelation(rel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private OutputStreamWriter openErrorLogFile() {
		try {
			return new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(new File("importErrors_" + System.currentTimeMillis() + ".log"))), "UTF-8");
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public DnbDatabaseManager getDnbDatabaseManager() {
		return this.dnbDatabaseManager;
	}

	public void setDnbDatabaseManager(DnbDatabaseManager dnbDatabaseManager) {
		this.dnbDatabaseManager = dnbDatabaseManager;
	}

	public LogicInterface getAdminLogic() {
		return this.adminLogic;
	}

	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}