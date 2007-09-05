/*
 *  BookBean is used by processBook.jsp to check the integrity 
 *  and validate the entries on edit_bookmark
 *
 */
package beans;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import resources.Bookmark;


public class BookmarkHandlerBean extends ResourceSuperBean<Bookmark> {
	
	private static final long serialVersionUID = 3257850961094522929L;
	
	private boolean jump; 						// used for redirecting requests in BookmarkHandler (?!)
	private String taggedTag;
	private String direction;
		
	/*
	 * checks if the bookmark is valid and sets error messages correspondingly
	 */
	public boolean isValid () {
		if (!resource.hasValidTags()) { 
			addError ("tags", "please enter valid tags");
		}
		if (!resource.isValidurl()) {
			addError ("url", "please enter a valid URL");
		}
		if (!resource.isValidtitle()) {
			addError("description", "please enter a valid title");
		}
		return (resource.hasValidTags() && 
				resource.isValidurl() && 
				resource.isValidtitle());
	}
		
	/*
	 * creates a new bean - which contains an empty 
	 * bookmark and a hashtable for the error messages
	 */
	public BookmarkHandlerBean() {
		super();
		resource = new Bookmark();
		jump     = false;
	}
	
	/*
	 * creates a new bean from an existing bookmark
	 */
	public BookmarkHandlerBean(Bookmark b) {
		super();
		resource = b;
		jump     = false;
	}

	public Bookmark getBookmark () {
		return resource;
	}

	/*
	 * getting and setting the user name from/to the bookmark
	 */
	public String getUser_name() {
		return resource.getUser();
	}
	public void  setUser_name(String un) {
		resource.setUser(un);
	}

	/*
	 * delegates the URL to the bookmark
	 */
	public void setUrl(String u) {
		resource.setUrl(getTrimmed(u));
	}
	public String getUrl() {
		return resource.getUrl();
	}
	
	/*
	 * delegates the title (old: description) to the
	 * bookmark
	 * NOTE: internally this is the title but due to
	 * compatibillity with older PostBookmark buttons
	 * (and also the current ones) we didn't change 
	 * this to the outside, therefore
	 * INTERN       EXTERN
	 * title        description
	 * description  extended
	 * 
	 */
	public void setDescription(String d) {
		resource.setTitle(getTrimmed(d));
	}
	public String getDescription() {
		return resource.getTitle();
	}
	
	public String getExtended() {
		return resource.getExtended();
	}
	public void setExtended(String e) {
		resource.setExtended(e);
	}
	

	/*
	 * jump is used to redirect the user back to the URL
	 * he came from; it's either "yes" or "no" and could
	 * be switched to a boolean, but since the postBookmark
	 * button uses "yes" and "no" we can't to that
	 * 
	 * that is only partially true: jump is also used to
	 * check, whether we "move" a bookmark or if we make
	 * a copy of it, when the user changes the URL
	 * if jump=yes (i.e., from the bookmarklet), then we
	 * have two entries: the one with the original URL, which
	 * already existed and the one the user posted (and where
	 * he changed the URL). if jump=no (i.e., from pressing
	 * the "edit" button), the bookmarks URL is changed instead
	 * of having two (one old, one new) bookmarks.
	 */
	/*
	 * NOTE: these two methods are only for use inside JSPs and 
	 * for filling the bean with the value from the request parameter
	 * (i.e., from the bookmarklet or edit_bookmark JSP)
	 */
	public String getJump() {
		if (jump) return "yes"; else return "no";
	}
	public void setJump(String c) {
		jump = "yes".equals(c);
	}
	
	/*
	 * use this method in Servlets or elsewhere
	 */
	public boolean isJump () {
		return jump;
	}
	public void setJump(boolean j) {
		this.jump = j;
	}
	
	/*
	 * the old url is the url before the user changed it
	 * i.e. the user presses postBookmark on the page
	 * http://www.bibsonomy.org/user/foo
	 * and changes then the URL to
	 * http://www.bibsonomy.org/
	 * then 
	 * bookmark.url = http://www.bibsonomy.org/
	 * and 
	 * oldurl = http://www.bibsonomy.org/user/foo
	 */
	public String getOldurl() {
		return resource.getOldurl();
	}
	public void setOldurl(String oldurl) {
		resource.setOldurl(oldurl);
	}
	
	/*
	 * remembers the way how the user wants to "tag a tag"
	 * i.e., if the tagged tag should be a supertag or a subtag
	 * of the tags with which the tag is tagged
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}


	/**
	 * checks, if user is tagging a tag and then adds the tag as upper or lower tag
	 * (depending on the choosen direction) to the tag object
	 * 
	 * @param projectHome somethling like http://www.bibsonomy.org
	 * @param currUser    user which is doing the action (i.e., logged in)
	 */
	public void doTaggingOfTags (String projectHome, String currUser) {
		String tag = getTaggedTag(projectHome, currUser);
		if (tag != null) {
			/* tagging of tags detected --> as lower or as upper tag of relation? */
			if("upper".equals(direction)) {
				/* TAG<- */
				resource.addUpperTag(tag);
			} else if ("lower".equals(direction)) {
				/* TAG-> */
				resource.addLowerTag(tag);
			}
		}
	}

	/**
	 * extracts the tag from the URL (if tagging of tags takes place)
	 * 
	 * @param projectHome  something like http://www.bibsonomy.org/
	 * @param currUser     user which is doing the action (i.e., logged in)
	 * @return             null if no "tagging of tags" takes places, the tagged tag otherwise
	 */
	public String getTaggedTag(String projectHome, String currUser) {
		if (taggedTag == null) {
			String userUrlPrefix        = projectHome + "user/" + currUser + "/";
			String tagUrlPrefix         = projectHome + "tag/";
			String conceptUserUrlPrefix = projectHome + "concept/user/" + currUser + "/";
			int startposition = 0;
			
			/* 
			 * check, if the user wants to tag a tag, i.e., 
			 * if the URL starts with http://www.bibsonomy.org/tag/ 
			 */
			/*
			 * TODO: this will not work, if currUser contains non ANSI-characters:
			 * these are encoded inside the URL 
			 */
			if (resource.getUrl().indexOf(tagUrlPrefix)==0) {
				/* http://www.bibsonomy.org/tag/TAG */
				startposition = tagUrlPrefix.length();
			} else if(resource.getUrl().indexOf(userUrlPrefix)==0) {
				/* http://www.bibsonomy.org/user/USER/TAG */
				startposition = userUrlPrefix.length();
			} else if(resource.getUrl().indexOf(conceptUserUrlPrefix)==0) {
				/* http://www.bibsonomy.org/concept/USER/TAG */
				startposition = conceptUserUrlPrefix.length();
			}
			/* extract tag from URL 
			 * TODO:
			 *  - what happens, if parts of the URL are still encoded? (can this happen?)
			 *  - removing of parameter names is NOT done! (look in Bookmark object ...)
			 *  ---> how does "?" look like? If it's in the tag name, it should be encoded
			 *  if not: it's the start of the parameters
			 *  THEREFORE: normally the URL is encoded, so the tags have to be decoded before
			 *  adding them ... due to the broken URLRewriteFilter this does not work!
			 */
			if (startposition != 0) {
				try {
					this.taggedTag = URLDecoder.decode(resource.getUrl().substring(startposition), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return taggedTag;
	}
	
	public String getTaggedTag () {
		return taggedTag;
	}
	
}
