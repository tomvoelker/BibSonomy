package org.bibsonomy.webapp.controller;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests the {@link ResourceListController#getListsToInitialize(String, Set)} method
 * 
 * @author dzo
 * @version $Id$
 */
public class ResourceListControllerTest {	
	
	private static class TestResourceListController extends ResourceListController {
		@Override
		protected void handleTagsOnly(ResourceViewCommand cmd, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, String hash, int max, String search) {
			// no logic no web request
			this.setInitializeNoResources(true);
		}		
	}
	
	private static Set<Class<? extends Resource>> STANDARD_VIEW_CLASSES;
	private static Set<Class<? extends Resource>> ALL_CLASSES;
	private static Set<Class<? extends Resource>> BOOKMARK_CLASS;
	private static Set<Class<? extends Resource>> PUBLICATION_CLASS;
	private static Set<Class<? extends Resource>> GOLD_PUB_CLASS;
	
	private static UserSettings DEFAULT_SETTINGS;
	
	private static final UserSettings getSettings(final boolean showPublication, final boolean showBookmark) {
		final UserSettings settings = new UserSettings();
		settings.setShowBibtex(showPublication);
		settings.setShowBookmark(showBookmark);
		return settings;
	}
		
	@BeforeClass
	public static void createSets() {
		STANDARD_VIEW_CLASSES = new HashSet<Class<? extends Resource>>();
		STANDARD_VIEW_CLASSES.add(Bookmark.class);
		STANDARD_VIEW_CLASSES.add(BibTex.class);
		
		ALL_CLASSES = new HashSet<Class<? extends Resource>>(STANDARD_VIEW_CLASSES);
		ALL_CLASSES.add(GoldStandardPublication.class);
		
		BOOKMARK_CLASS = new HashSet<Class<? extends Resource>>();
		BOOKMARK_CLASS.add(Bookmark.class);
		
		PUBLICATION_CLASS = new HashSet<Class<? extends Resource>>();
		PUBLICATION_CLASS.add(BibTex.class);
		
		GOLD_PUB_CLASS =  new HashSet<Class<? extends Resource>>();
		GOLD_PUB_CLASS.add(GoldStandardPublication.class);
		
		DEFAULT_SETTINGS = getSettings(true, true);
	}
	
	@Test
	public void handleTagsOnly() {
		final TestResourceListController testController = new TestResourceListController();
		testController.setSupportedResources(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		testController.setUserSettings(DEFAULT_SETTINGS);
		final ResourceViewCommand cmd = new ResourceViewCommand();
		cmd.setTagstype("default");
		testController.handleTagsOnly(cmd, null, null, null, null, null, 0, null);
		
		assertEquals(Collections.emptySet(), testController.getListsToInitialize("", null));
	}
	
	@Test
	public void test10() {
		final TestResourceListController testController = new TestResourceListController();
		testController.setSupportedResources(new HashSet<Class<? extends Resource>>(BOOKMARK_CLASS));
		testController.setUserSettings(getSettings(true, false));
		assertEquals(BOOKMARK_CLASS, testController.getListsToInitialize("html", null));
		
		assertEquals(Collections.emptySet(), testController.getListsToInitialize("bibtex", PUBLICATION_CLASS));
		
		assertEquals(Collections.emptySet(), testController.getListsToInitialize("bibtex", null));
		assertEquals(Collections.emptySet(), testController.getListsToInitialize("bibtex", BOOKMARK_CLASS));
	}
	
	@Test
	public void JSONFormat() {
		final TestResourceListController testController = new TestResourceListController();
		testController.setSupportedResources(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		testController.setUserSettings(DEFAULT_SETTINGS);
		/*
		 *  NEW
		 */
		final String format = "json";
		assertEquals(STANDARD_VIEW_CLASSES, testController.getListsToInitialize(format, null));
		assertEquals(PUBLICATION_CLASS, testController.getListsToInitialize(format, PUBLICATION_CLASS));
		assertEquals(GOLD_PUB_CLASS, testController.getListsToInitialize(format, GOLD_PUB_CLASS));
	}
	
	@Test
	public void wrongUsage() {
		final TestResourceListController testController = new TestResourceListController();
		testController.setSupportedResources(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		testController.setUserSettings(DEFAULT_SETTINGS);
		
		final String format = "bookpubl";
		// TODO: DISCUSS: bookmark vs empty list (here the format is enforcing to initialize bookmarks)
		assertEquals(BOOKMARK_CLASS, testController.getListsToInitialize(format, PUBLICATION_CLASS));
	}
	
	@Test
	public void wrongUsagePublication() {
		final TestResourceListController testController = new TestResourceListController();
		testController.setSupportedResources(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		testController.setUserSettings(DEFAULT_SETTINGS);
		
		final String format = "bibtex";
		// TODO: DISCUSS: publication vs empty list (here the format is enforcing to initialize publications)
		assertEquals(PUBLICATION_CLASS, testController.getListsToInitialize(format, BOOKMARK_CLASS));
	}
	
	@Test
	public void respectUserSettings() {
		final TestResourceListController testController = new TestResourceListController();
		testController.setSupportedResources(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		
		/*
		 * bookmark and publication settings activated
		 */
		testController.setUserSettings(getSettings(true, true));
		assertEquals(STANDARD_VIEW_CLASSES, testController.getListsToInitialize("html", null));
		
		/*
		 * respects user settings
		 */
		testController.setUserSettings(getSettings(true, false));
		assertEquals(PUBLICATION_CLASS, testController.getListsToInitialize("html", null));
		
		/*
		 * url param "overrides" user settings
		 */
		assertEquals(BOOKMARK_CLASS, testController.getListsToInitialize("html", BOOKMARK_CLASS));
		
		
		testController.setUserSettings(getSettings(false, true));
		
		assertEquals(PUBLICATION_CLASS, testController.getListsToInitialize("bibtex", BOOKMARK_CLASS));
	}	
	
	@Test
	public void complexOne() {
		final TestResourceListController testController = new TestResourceListController();
		testController.setSupportedResources(new HashSet<Class<? extends Resource>>(PUBLICATION_CLASS));
		testController.setUserSettings(DEFAULT_SETTINGS);
		
		assertEquals(Collections.emptySet(), testController.getListsToInitialize("bookpubl", PUBLICATION_CLASS));
	}
	
	@Test
	public void enforceTest() {
		final TestResourceListController testController = new TestResourceListController();
		testController.setSupportedResources(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		testController.setUserSettings(DEFAULT_SETTINGS);
		testController.setForcedResources(STANDARD_VIEW_CLASSES);
		assertEquals(STANDARD_VIEW_CLASSES, testController.getListsToInitialize("bibtex", GOLD_PUB_CLASS));
	}
}
