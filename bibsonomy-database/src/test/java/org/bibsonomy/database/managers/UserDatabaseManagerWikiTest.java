package org.bibsonomy.database.managers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * Tests for wiki template functionality in UserDatabaseManager.
 * Verifies that default wiki templates are correctly inserted when creating/activating users.
 */
public class UserDatabaseManagerWikiTest extends AbstractDatabaseManagerTest {
    
    private static final UserDatabaseManager userDb = UserDatabaseManager.getInstance();
    private static final WikiDatabaseManager wikiDb = WikiDatabaseManager.getInstance();
    
    /**
     * Tests that creating and activating a user creates a default wiki with the expected template content.
     * 
     * <p>The wiki is stored per user because users can edit and customize their wiki content.
     * Initially, new users get the default template with placeholders (like {@code <name />}).
     * These placeholders are replaced with actual user data at render time by {@code CVWikiModel}.
     * Users can later edit their wiki to add custom content or modify the template.
     */
    @Test
    public void testCreateUserCreatesDefaultWiki() {
        final User newUser = new User("wikiTestUser");
        newUser.setRealname("Wiki Test User");
        newUser.setEmail("wiki-test@bibsonomy.org");
        newUser.setPassword("password");
        newUser.setApiKey("00000000000000000000000000000000");
        newUser.setSpammer(false);
        newUser.setRole(Role.DEFAULT);
        
        final String userName = userDb.createUser(newUser, this.dbSession);
        userDb.activateUser(newUser, this.dbSession);
        
        // Verify wiki was created with default template
        final org.bibsonomy.model.Wiki wiki = wikiDb.getCurrentWiki(userName, this.dbSession);
        
        assertNotNull("Wiki should be created", wiki);
        assertNotNull("Wiki text should not be null", wiki.getWikiText());
        
        // Verify it contains the default template structure
        assertTrue("Wiki should contain default template content", 
                   wiki.getWikiText().contains("== personal data =="));
        assertTrue("Wiki should contain publications tag", 
                   wiki.getWikiText().contains("<publications"));
        
        // Verify placeholders are present (confirming it's the default template, not customized)
        assertTrue("Wiki should contain name placeholder (default template)", 
                   wiki.getWikiText().contains("<name />"));
        assertTrue("Wiki should contain location placeholder (default template)", 
                   wiki.getWikiText().contains("<location />"));
    }
}


