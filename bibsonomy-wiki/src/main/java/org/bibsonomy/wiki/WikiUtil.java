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

import org.bibsonomy.model.Layout;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.wiki.tags.AbstractTag;
import org.bibsonomy.wiki.tags.BookmarkListTag;
import org.bibsonomy.wiki.tags.HobbieTag;
import org.bibsonomy.wiki.tags.InterestsTag;
import org.bibsonomy.wiki.tags.PublicationListTag;
import org.bibsonomy.wiki.tags.general.BirthdayTag;
import org.bibsonomy.wiki.tags.general.EmailTag;
import org.bibsonomy.wiki.tags.general.ImageTag;
import org.bibsonomy.wiki.tags.general.InstitutionTag;
import org.bibsonomy.wiki.tags.general.LocationTag;
import org.bibsonomy.wiki.tags.general.NameTag;
import org.bibsonomy.wiki.tags.general.ProfessionTag;
import org.bibsonomy.wiki.tags.general.RegDateTag;

/**
 * @author philipp
 * @author Bernd
 * @version $Id$
 */
public class WikiUtil extends AbstractWikiModel {

	static {
		/* About-Me Tags */
		register(NameTag.TAG_NAME, new NameTag());
		register(LocationTag.TAG_NAME, new LocationTag());
		register(BirthdayTag.TAG_NAME, new BirthdayTag());
		register(ProfessionTag.TAG_NAME, new ProfessionTag());
		register(InstitutionTag.TAG_NAME, new InstitutionTag());
		register(ImageTag.TAG_NAME, new ImageTag());
		register(EmailTag.TAG_NAME, new EmailTag());
		register(RegDateTag.TAG_NAME, new RegDateTag());
		
		/* Other Tags */
		register(InterestsTag.TAG_NAME, new InterestsTag());
		register(HobbieTag.TAG_NAME, new HobbieTag());
		register(BookmarkListTag.TAG_NAME, new BookmarkListTag());
		register(PublicationListTag.TAG_NAME, new PublicationListTag());
	}
	
	private static void register(final String tagName, final AbstractTag tag) {
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(tagName, tag);
	}

	private User user;

	private LogicInterface logic;

	private LayoutRenderer<Layout> layoutRenderer;
	
	/**
	 * Default Constructor
	 */
	public WikiUtil() {
		super(Configuration.DEFAULT_CONFIGURATION, null, null);
	}

	@Override
	public ITableOfContent appendHead(final String rawHead, final int headLevel, final boolean noToC, final int headCounter, final int startPosition, final int endPosition) {
		final TagStack localStack = WikipediaParser.parseRecursive(rawHead.trim(), this, true, true);

		final WPTag headTagNode = new WPTag("h" + headLevel);
		final TagNode spanTagNode = new TagNode("span");
		// Example:
		// <h2><span class="mw-headline" id="Header_level_2">Header level
		// 2</span></h2>
		spanTagNode.addChildren(localStack.getNodeList());
		headTagNode.addChild(spanTagNode);
		final String tocHead = headTagNode.getBodyString();
		String anchor = Encoder.encodeDotUrl(tocHead);
		this.createTableOfContent(false);
		if (!noToC && headCounter > 3) {
			this.fTableOfContentTag.setShowToC(true);
		}
		if (this.fToCSet.contains(anchor)) {
			String newAnchor = anchor;
			for (int i = 2; i < Integer.MAX_VALUE; i++) {
				newAnchor = anchor + '_' + Integer.toString(i);
				if (!this.fToCSet.contains(newAnchor)) {
					break;
				}
			}
			anchor = newAnchor;
		}
		final SectionHeader strPair = new SectionHeader(headLevel, startPosition, endPosition, tocHead, anchor);
		this.addToTableOfContents(this.fTableOfContent, strPair, headLevel);
		if (this.getRecursionLevel() == 1) {
			this.buildEditLinkUrl(this.fSectionCounter++);
		}
		spanTagNode.addAttribute("class", "mw-headline", true);
		spanTagNode.addAttribute("id", anchor, true);

		this.append(headTagNode);
		return this.fTableOfContentTag;
	}

	private void addToTableOfContents(final List<Object> toc, final SectionHeader strPair, final int headLevel) {
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
	public void parseInternalImageLink(final String imageNamespace, final String rawImageLink) {
		// TODO Auto-generated method stub

	}

	/**
	 * set the LogicInterface
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return the LogicInterface
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * set the user
	 */
	public void setUser(final User user) {
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	public static String formatAndAppend(final String string1, final String string2) {
		return "<tr><td>" + string1 + ":</td><td>" + Utils.escapeXmlChars(string2) + "</td></tr>";
	}

	/**
	 * @return the layoutRenderer
	 */
	public LayoutRenderer<Layout> getLayoutRenderer() {
		return this.layoutRenderer;
	}

	/**
	 * @param layoutRenderer the layoutRenderer to set
	 */
	public void setLayoutRenderer(final LayoutRenderer<Layout> layoutRenderer) {
		this.layoutRenderer = layoutRenderer;
	}

}
