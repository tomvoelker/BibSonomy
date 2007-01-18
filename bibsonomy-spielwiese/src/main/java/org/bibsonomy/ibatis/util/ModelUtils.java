package org.bibsonomy.ibatis.util;


import java.util.ArrayList;
import java.util.Date;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * Methods to create objects from the model like {@link Bookmark} or
 * {@link BibTex}.
 * 
 * @author Christian Schenk
 */
public class ModelUtils {
     
	private static void setResourceDefaults(final Resource resource) {
		// resource.setContentId(1);
		resource.setCount(0);
		resource.setDate(null);
		resource.setGroupId(ConstantID.GROUP_KDE.getId());
		resource.setGroupName("kde");
		resource.setUrl("");
		resource.setUserName("kde");
		
	}

	/**
	 * Creates a bookmark with all properties set.
	 */
	public static Bookmark getBookmark() {
		Date date = new Date();
		final Bookmark rVal = new Bookmark();
		rVal.setUserName("hallo");	
		rVal.setDescription("test");
		rVal.setExtended("test");
		rVal.setTags(new ArrayList<Tag>());
		rVal.setUrl("http://www.bibonomy.org");
		rVal.setUrlHash("123453747590");
		rVal.setDate(date);
		rVal.setContentId(2);
	    
		return rVal;
	}

	/**
	 * Creates a BibTex with all properties set.
	 */
	public static BibTex getBibTex() {
		final BibTex rVal = new BibTex();
		setResourceDefaults(rVal);
		rVal.setAddress("test");
		rVal.setAnnote("test");
		rVal.setAuthor("test");
		rVal.setBKey("test");
		rVal.setBibtexAbstract("test");
		rVal.setBibtexKey("test");
		rVal.setBooktitle("test");
		rVal.setChapter("test");
		rVal.setCrossref("test");
		rVal.setCount(0);
		rVal.setDay("test");
		rVal.setDescription("test");
		rVal.setEdition("test");
		rVal.setEditor("test");
		rVal.setEntrytype("test");
		rVal.setHowpublished("test");
		rVal.setInstitution("test");
		rVal.setJournal("test");
		rVal.setMisc("test");
		rVal.setMonth("test");
		rVal.setNote("test");
		rVal.setNumber("test");
		rVal.setOrganization("test");
		rVal.setPages("test");
		rVal.setPublisher("test");
		rVal.setSchool("test");
		rVal.setScraperId(1);
		rVal.setSeries("test");
		rVal.setTags(new ArrayList<Tag>());
		rVal.setTitle("test");
		rVal.setType("test");
		rVal.setUrl("test");
		rVal.setVolume("test");
		rVal.setYear("test");
		return rVal;
	}
}