package org.bibsonomy.wiki;

import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.Utils;
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
import org.bibsonomy.wiki.tags.AbstractTag;
import org.bibsonomy.wiki.tags.general.BirthdayTag;
import org.bibsonomy.wiki.tags.general.EmailTag;
import org.bibsonomy.wiki.tags.general.ImageTag;
import org.bibsonomy.wiki.tags.general.InstitutionTag;
import org.bibsonomy.wiki.tags.general.LocationTag;
import org.bibsonomy.wiki.tags.general.NameTag;
import org.bibsonomy.wiki.tags.general.ProfessionTag;
import org.bibsonomy.wiki.tags.general.RegDateTag;
import org.bibsonomy.wiki.tags.old.BookmarkListTag;
import org.bibsonomy.wiki.tags.old.HeaderTag;
import org.bibsonomy.wiki.tags.old.HobbieTag;
import org.bibsonomy.wiki.tags.old.InterestsTag;
import org.bibsonomy.wiki.tags.old.PublicationListTag;
import org.bibsonomy.wiki.tags.old.TestTag;

/**
 * @author philipp
 * @version $Id$
 */
public class WikiUtil extends AbstractWikiModel {

	static {
		/* New Tags */
		register(NameTag.TAG_NAME, new NameTag());
		register(LocationTag.TAG_NAME, new LocationTag());
		register(BirthdayTag.TAG_NAME, new BirthdayTag());
		register(ProfessionTag.TAG_NAME, new ProfessionTag());
		register(InstitutionTag.TAG_NAME, new InstitutionTag());
		register(ImageTag.TAG_NAME, new ImageTag());
		register(EmailTag.TAG_NAME, new EmailTag());
		register(RegDateTag.TAG_NAME, new RegDateTag());
		/* Old Tags */
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(TestTag.TAG_NAME, new TestTag());
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(HeaderTag.TAG_NAME, new HeaderTag());
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(InterestsTag.TAG_NAME, new InterestsTag());
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(HobbieTag.TAG_NAME, new HobbieTag());
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(BookmarkListTag.TAG_NAME, new BookmarkListTag());
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(PublicationListTag.TAG_NAME, new PublicationListTag());
	}
	
	private static void register(String tagName, AbstractTag tag) {
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(tagName, tag);
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
		// <h2><span class="mw-headline" id="Header_level_2">Header level
		// 2</span></h2>
		spanTagNode.addChildren(localStack.getNodeList());
		headTagNode.addChild(spanTagNode);
		String tocHead = headTagNode.getBodyString();
		String anchor = Encoder.encodeDotUrl(tocHead);
		createTableOfContent(true);
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
	}

	@Override
	public Set<String> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INamespace getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseInternalImageLink(String imageNamespace, String rawImageLink) {
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

	public static String formatAndAppend(String string1, String string2) {
		return "<tr><td>" + string1 + ":</td><td>" + Utils.escapeXmlChars(string2) + "</td></tr>";
	}

}
