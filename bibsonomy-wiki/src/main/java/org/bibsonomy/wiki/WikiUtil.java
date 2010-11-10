package org.bibsonomy.wiki;

import info.bliki.htmlcleaner.TagNode;
import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.filter.SectionHeader;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.model.AbstractWikiModel;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.ITableOfContent;
import info.bliki.wiki.namespaces.INamespace;
import info.bliki.wiki.tags.WPTag;
import info.bliki.wiki.tags.util.TagStack;

import java.util.List;
import java.util.Set;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.wiki.tags.BookmarkListTag;
import org.bibsonomy.wiki.tags.HobbieTag;
import org.bibsonomy.wiki.tags.InterestsTag;
import org.bibsonomy.wiki.tags.PublicationListTag;


/**
 * @author philipp
 * @version $Id$
 */
public class WikiUtil extends AbstractWikiModel {

	static {
		  Configuration.DEFAULT_CONFIGURATION.addTokenTag(InterestsTag.TAG_NAME, new InterestsTag()); 
		  Configuration.DEFAULT_CONFIGURATION.addTokenTag(HobbieTag.TAG_NAME, new HobbieTag());
		  Configuration.DEFAULT_CONFIGURATION.addTokenTag(BookmarkListTag.TAG_NAME, new BookmarkListTag());
		  Configuration.DEFAULT_CONFIGURATION.addTokenTag(PublicationListTag.TAG_NAME, new PublicationListTag());
	}
	
	private User user;
	
	private LogicInterface logic;
	
	/**
	 * @param command 
	 * 
	 */
	public WikiUtil() {
		super(Configuration.DEFAULT_CONFIGURATION, null, null);
	}

	
	
	@Override
	public ITableOfContent appendHead(String rawHead, int headLevel, boolean noToC, int headCounter, int startPosition, int endPosition) {
		TagStack localStack = WikipediaParser.parseRecursive(rawHead.trim(), this, true, true);
		
		WPTag headTagNode = new WPTag("h" + headLevel);
		TagNode spanTagNode = new TagNode("span");
		// Example:
		// <h2><span class="mw-headline" id="Header_level_2">Header level 2</span></h2>
		spanTagNode.addChildren(localStack.getNodeList());
		headTagNode.addChild(spanTagNode);
		String tocHead = headTagNode.getBodyString();
		String anchor = Encoder.encodeDotUrl(tocHead);
		createTableOfContent(false);
		if (!noToC && (headCounter > 3)) {
			fTableOfContentTag.setShowToC(true);
		}
		if (fToCSet.contains(anchor)) {
			String newAnchor = anchor;
			for (int i = 2; i < Integer.MAX_VALUE; i++) {
				newAnchor = anchor + '_' + Integer.toString(i);
				if (!fToCSet.contains(newAnchor)) {
					break;
				}
			}
			anchor = newAnchor;
		}
		SectionHeader strPair = new SectionHeader(headLevel, startPosition, endPosition, tocHead, anchor);
		addToTableOfContents(fTableOfContent, strPair, headLevel);
		if (getRecursionLevel() == 1) {
			buildEditLinkUrl(fSectionCounter++);
		}
		spanTagNode.addAttribute("class", "mw-headline", true);
		spanTagNode.addAttribute("id", anchor, true);

		append(headTagNode);
		return fTableOfContentTag;
	}
	
	private void addToTableOfContents(List<Object> toc, SectionHeader strPair, int headLevel) {
//		if (headLevel == 1) {
//			toc.add(strPair);
//		} else {
//			if (toc.size() > 0) {
//				if (toc.get(toc.size() - 1) instanceof List<?>) {
//					addToTableOfContents((List<Object>) toc.get(toc.size() - 1), strPair, --headLevel);
//					return;
//				}
//			}
//			List<Object> list = new ArrayList<Object>();
//			toc.add(list);
//			addToTableOfContents(list, strPair, --headLevel);
//		}
	}


	public Set<String> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	public INamespace getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseInternalImageLink(String imageNamespace,
			String rawImageLink) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * set the LogicInterface
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}


	/**
	 * @return the LogicInterface
	 */
	public LogicInterface getLogic() {
		return logic;
	}
	
	/**
	 * set the user
	 */
	public void setUser(User user) {
		this.user = user;
	}


	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

}
