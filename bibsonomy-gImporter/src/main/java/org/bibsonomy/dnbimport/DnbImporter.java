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
	private static final int SQL_BLOCK_SIZE = 8192;
	private LogicInterface adminLogic;
	
	private DnbDatabaseManager dnbDatabaseManager;

	private String userName;
	
	private int offset;

	@Override
	public void run() {
		
		final Writer logWriter = openErrorLogFile();
		
		final User user = new User();
		user.setName(userName);
		
		List<DnbPublication> dnbEntries;
		do {
			dnbEntries = dnbDatabaseManager.selectDnbEntries(SQL_BLOCK_SIZE, offset);
			offset += SQL_BLOCK_SIZE;
			System.out.println("offset=" +  offset);

			for (DnbPublication dnbPub : dnbEntries) {
				try {
					final Post<BibTex> post = new Post<BibTex>();
					final BibTex bibPub = new BibTex();
					//set school
					StringBuilder school = new StringBuilder();
					if (!StringUtils.isEmpty(dnbPub.getSchoolP1())) {
						school.append(dnbPub.getSchoolP1());
					}
					if (!StringUtils.isEmpty(dnbPub.getSchoolP2())) {
						if (school.length() > 0) {
							school.append(' ');
						}
						school.append(dnbPub.getSchoolP2());
					}
					bibPub.setSchool(school.toString());
					
					//set year
					if (!StringUtils.isEmpty(dnbPub.getSubYear())) {
						bibPub.setYear(dnbPub.getSubYear());
					} else if (!StringUtils.isEmpty(dnbPub.getPubYear())) {
						bibPub.setYear(dnbPub.getPubYear());
					} else {
						logWriter.append(dnbPub.getTitleId() + "\t" + "noYear\n");
						continue;
					}
					
					if (StringUtils.isEmpty(dnbPub.getMainTitle())) {
						logWriter.append(dnbPub.getTitleId() + "\t" + "noTitle\n");
						continue;
					} else if (!StringUtils.isEmpty(dnbPub.getSubTitle())) {
						bibPub.setTitle(dnbPub.getMainTitle() + ": " + dnbPub.getSubTitle());
					} else {
						bibPub.setTitle(dnbPub.getMainTitle());
					}
					//set authors
					List<PersonName> authors = new ArrayList<PersonName>();
					bibPub.setAuthor(authors);
					
					List<PersonName> editors = new ArrayList<PersonName>();
					bibPub.setEditor(editors);
					
					List<ResourcePersonRelation> relationsToInsert = new ArrayList<>();
					
					
					for (DnbPerson dnbPer : dnbPub.getPersons()) {
						final String dnbId = dnbPer.getUniquePersonId().trim();
						Person bibPer = adminLogic.getPersonById(PersonIdType.DNB_ID, dnbId);
						boolean personNeedsToBeStored = false;
						if (bibPer == null) {
							bibPer = new Person();
							if (StringUtils.contains(dnbPer.getGender(), "1")) {
								bibPer.setGender(Gender.m);
							} else if (StringUtils.contains(dnbPer.getGender(), "2")) {
								bibPer.setGender(Gender.F);
							}
							bibPer.setDnbPersonId(dnbPer.getUniquePersonId());
							final PersonName name = new PersonName(dnbPer.getFirstName(), dnbPer.getLastName());
							bibPer.setMainName(name);
							personNeedsToBeStored = true;
						}
						if (StringUtils.isEmpty(bibPer.getMainName().getLastName())) {
							logWriter.append(dnbPub.getTitleId() + "\t" + " authorWithoutFirstOrLastname\n");
							if (!StringUtils.isEmpty(bibPer.getMainName().getFirstName())) {
								logWriter.append(dnbPub.getTitleId() + "\t" + " usingFirstname\n");
								bibPer.getMainName().setLastName(bibPer.getMainName().getFirstName());
								bibPer.getMainName().setFirstName(null);
							}
							if (StringUtils.isEmpty(bibPer.getMainName().getLastName())) {
								logWriter.append(dnbPub.getTitleId() + "\t" + " aborting\n");
								continue;
							}
						}
						
						
						
						final ResourcePersonRelation rel = new ResourcePersonRelation();
						rel.setPerson(bibPer);
						rel.setPersonIndex(0);
						rel.setPost(post);
						if (StringUtils.contains(dnbPer.getPersonFunction(), "aut")) {
							rel.setPersonIndex(authors.size());
							rel.setRelationType(PersonResourceRelationType.AUTHOR);
							authors.add(bibPer.getMainName());
						} else if (StringUtils.contains(dnbPer.getPersonFunction(), "edt")) {
							rel.setPersonIndex(editors.size());
							rel.setRelationType(PersonResourceRelationType.EDITOR);
							editors.add(bibPer.getMainName());
						} else if (StringUtils.contains(dnbPer.getPersonFunction(), "gut1")) {
							//rel.setRelationType(PersonResourceRelationType.FIRST_REVIEWER);
							continue; // mail von Dominik: erstmal nicht importieren
						} else if (StringUtils.contains(dnbPer.getPersonFunction(), "gut2")) {
							continue; // mail von Dominik: erstmal nicht importieren
						} else if (StringUtils.contains(dnbPer.getPersonFunction(), "gut")) {
							rel.setRelationType(PersonResourceRelationType.REVIEWER);
						} else if (StringUtils.contains(dnbPer.getPersonFunction(), "btr")) {
							rel.setRelationType(PersonResourceRelationType.ADVISOR);
						//} else if (StringUtils.contains(dp.getPersonFunction(), "ctb")) {
						} else {
							rel.setRelationType(PersonResourceRelationType.OTHER);
						}
						if (personNeedsToBeStored == true) {
							adminLogic.createOrUpdatePerson(bibPer);
						}
						relationsToInsert.add(rel);
					}
					
					if (authors.isEmpty()) {
						logWriter.append(dnbPub.getTitleId() + "\t" + "noAuthor\n");
						continue;
					}
					
					//set entrytype and type
					if (dnbPub.isDiss()) {
						bibPub.setEntrytype("phdthesis");
					}
					else if (dnbPub.isHabil()) {
						bibPub.setEntrytype("phdthesis");
						bibPub.setType("habilitation");
					}
					post.setResource(bibPub);
					post.setUser(user);
					post.addTag("dnb");
					for (Pair<ClassificationScheme, String> classPair : dnbPub.getClassInfos()) {
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
					bibPub.parseMiscField();
					bibPub.addMiscField("dnbTitleId", dnbPub.getTitleId());
					bibPub.serializeMiscFields();
					bibPub.setBibtexKey(BibTexUtils.generateBibtexKey(bibPub));
					
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
					System.out.print(dnbPub.getTitleId() + "\t");
					e.printStackTrace(System.out);
					final PrintWriter pw = new PrintWriter(logWriter);
					pw.print(dnbPub.getTitleId() + "\t");
					e.printStackTrace(pw);
					pw.flush();
				}
			}
		} while (dnbEntries.size() > 0);
		
		try {
			logWriter.close();
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

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
}