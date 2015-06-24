package org.bibsonomy.dnbimport;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.dnbimport.database.DnbDatabaseManager;
import org.bibsonomy.dnbimport.model.ClassificationScheme;
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
				final Post<BibTex> post = new Post<BibTex>();
				final BibTex pub = new BibTex();
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
				pub.setSchool(school.toString());
				
				//set year
				if (!StringUtils.isEmpty(p.getSubYear())) {
					pub.setYear(p.getSubYear());
				} else if (!StringUtils.isEmpty(p.getPubYear())) {
					pub.setYear(p.getPubYear());
				} else {
					w.append(p.getTitleId() + "\t" + "noYear\n");
					continue;
				}
				
				if (StringUtils.isEmpty(p.getMainTitle())) {
					w.append(p.getTitleId() + "\t" + "noTitle\n");
					continue;
				} else if (!StringUtils.isEmpty(p.getSubTitle())) {
					pub.setTitle(p.getMainTitle() + ": " + p.getSubTitle());
				} else {
					pub.setTitle(p.getMainTitle());
				}
				//set authors
				List<PersonName> authors = new ArrayList<PersonName>();
				pub.setAuthor(authors);
				
				List<PersonName> editors = new ArrayList<PersonName>();
				pub.setEditor(editors);
				
				List<ResourcePersonRelation> relationsToInsert = new ArrayList<>();
				
				
				for (DnbPerson dp : p.getPersons()) {
					final String dnbId = dp.getUniquePersonId().trim();
					Person per = adminLogic.getPersonById(PersonIdType.DNB_ID, dnbId);
					boolean personNeedsToBeStored = false;
					if (per == null) {
						per = new Person();
						if (StringUtils.contains(dp.getGender(), "1")) {
							per.setGender(Gender.m);
						} else if (StringUtils.contains(dp.getGender(), "2")) {
							per.setGender(Gender.F);
						}
						per.setDnbPersonId(dp.getUniquePersonId());
						final PersonName name = new PersonName(dp.getFirstName(), dp.getLastName());
						per.setMainName(name);
						personNeedsToBeStored = true;
					}
					if (StringUtils.isEmpty(per.getMainName().getLastName())) {
						w.append(p.getTitleId() + "\t" + " authorWithoutFirstOrLastname\n");
						if (!StringUtils.isEmpty(per.getMainName().getFirstName())) {
							w.append(p.getTitleId() + "\t" + " usingFirstname\n");
							per.getMainName().setLastName(per.getMainName().getFirstName());
							per.getMainName().setFirstName(null);
						}
						if (StringUtils.isEmpty(per.getMainName().getLastName())) {
							w.append(p.getTitleId() + "\t" + " aborting\n");
							continue;
						}
					}
					
					
					
					final ResourcePersonRelation rel = new ResourcePersonRelation();
					rel.setPerson(per);
					rel.setPersonIndex(0);
					rel.setPost(post);
					if (StringUtils.contains(dp.getPersonFunction(), "aut")) {
						rel.setPersonIndex(authors.size());
						rel.setRelationType(PersonResourceRelationType.AUTHOR);
						authors.add(per.getMainName());
					} else if (StringUtils.contains(dp.getPersonFunction(), "edt")) {
						rel.setPersonIndex(editors.size());
						rel.setRelationType(PersonResourceRelationType.EDITOR);
						editors.add(per.getMainName());
					} else if (StringUtils.contains(dp.getPersonFunction(), "gut1")) {
						//rel.setRelationType(PersonResourceRelationType.FIRST_REVIEWER);
						continue; // mail von Dominik: erstmal nicht importieren
					} else if (StringUtils.contains(dp.getPersonFunction(), "gut2")) {
						continue; // mail von Dominik: erstmal nicht importieren
					} else if (StringUtils.contains(dp.getPersonFunction(), "gut")) {
						rel.setRelationType(PersonResourceRelationType.REVIEWER);
					} else if (StringUtils.contains(dp.getPersonFunction(), "btr")) {
						rel.setRelationType(PersonResourceRelationType.ADVISOR);
					//} else if (StringUtils.contains(dp.getPersonFunction(), "ctb")) {
					} else {
						rel.setRelationType(PersonResourceRelationType.OTHER);
					}
					if (personNeedsToBeStored == true) {
						adminLogic.createOrUpdatePerson(per);
					}
					relationsToInsert.add(rel);
				}
				
				if (authors.isEmpty()) {
					w.append(p.getTitleId() + "\t" + "noAuthor\n");
					continue;
				}
				
				//set entrytype and type
				if (p.isDiss()) {
					pub.setEntrytype("phdthesis");
				}
				else if (p.isHabil()) {
					pub.setEntrytype("phdthesis");
					pub.setType("habilitation");
				}
				post.setResource(pub);
				post.setUser(user);
				post.addTag("dnb");
				for (Pair<ClassificationScheme, String> classPair : p.getClassInfos()) {
					final String className = dnbDatabaseManager.getClassName(classPair.getFirst(), classPair.getSecond());
					if (className == null) {
						continue;
					}
					String[] classesParts = className.split(",");
					for (String classesPart : classesParts) {
						String tag = classesPart.trim().replace('', 'ü').replace('™', 'Ö').replace('”', 'ö').replace('„', 'ä').replace("(", "").replace(")", "").replace("- ", "_").replace(' ', '_');
						if (!tag.matches(".*\\p{Alpha}.*")) {
							// not at least one alphabetic character
							continue;
						}
						if (tag.length() > 0) {
							post.addTag(tag);
						}
					}
				}
				post.getResource().recalculateHashes();
				pub.parseMiscField();
				pub.addMiscField("dnbTitleId", p.getTitleId());
				pub.serializeMiscFields();
				pub.setBibtexKey(BibTexUtils.generateBibtexKey(pub));
				
				List<Post<? extends Resource>> goldies = new ArrayList<>();
				goldies.add(post);
				
				Post<? extends Resource> existingingPost = adminLogic.getPostDetails(post.getResource().getIntraHash(), userName);
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
				e.printStackTrace(System.out);
				e.printStackTrace(new PrintWriter(w));
			}
		}
		try {
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
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