package org.bibsonomy.testutil;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

/**
 * Methods to create objects from the model like {@link Bookmark} or
 * {@link BibTex}.
 * 
 * @author Christian Schenk
 */
public class ModelUtils {

	// FIXME
	private static void setResourceDefaults(final Resource resource) {
		// resource.setContentId(1);
		resource.setCount(0);
		// resource.setDate(null);
		// resource.setGroupId(ConstantID.GROUP_KDE.getId());
		// resource.setGroupName("kde");
		// resource.setUrl("");
		// resource.setUserName("kde");
	}

	/**
	 * Creates a bookmark with all properties set.
	 */
	// FIXME
	public static Bookmark getBookmark() {		
		final Bookmark rVal = new Bookmark();
		setResourceDefaults(rVal);
		rVal.setDescription("test");
		rVal.setExtended("test");
		rVal.setUrl("http://www.bibonomy.org");
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
		rVal.setTitle("test");
		rVal.setType("test");
		// rVal.setUrl("test");
		rVal.setVolume("test");
		rVal.setYear("test");
		return rVal;
	}
}