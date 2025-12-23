# Removed Features from BibSonomy REST API v2

This document provides a comprehensive reference for features that existed in the legacy BibSonomy system (v1) but were **deliberately excluded** from REST API v2. This is a technical reference for developers who need to understand what was removed and why, with complete codebase references.

## Table of Contents

1. [Friends & Friend Groups (Spheres)](#1-friends--friend-groups-spheres)
2. [Followers](#2-followers)
3. [Post Sharing & Inbox](#3-post-sharing--inbox)
4. [Recommendations Engine](#4-recommendations-engine)
5. [Clipboard](#5-clipboard)
6. [MySearch (Saved Searches)](#6-mysearch-saved-searches)
7. [CV/Wiki](#7-cvwiki)
8. [Comments & Discussion System](#8-comments--discussion-system)
9. [Migration Guide for Users](#9-migration-guide-for-users)

---

## 1. Friends & Friend Groups (Spheres)

### Description

The legacy system included a comprehensive social networking feature that allowed users to:
- **Add other users as "friends"** (bidirectional or unidirectional relationships)
- **Organize friends into "spheres"** (friend groups with custom names)
- **View aggregated posts from friends** (filtered by sphere/group)
- **Share posts with specific friend groups** using visibility settings
- **Special system tag**: `sys:network:bibsonomy-friend` and custom sphere tags like `sys:network:relation:SphereNameHere`

This created a Facebook-like social layer on top of the publication/bookmark sharing system.

### Why Removed

**Rationale:**
- **Low adoption**: Social networking features were underutilized compared to core tagging/publication features
- **Complexity**: Friend management added significant complexity to permissions, queries, and UI
- **Maintenance burden**: Social graph queries are expensive; friend relationships complicate data access patterns
- **Modern alternatives**: Users can achieve similar functionality through **Groups** (which are retained in v2)
- **Focus**: v2 focuses on collaborative scholarly work (via Groups) rather than personal social networks

**Decision**: Replace friend-based collaboration with group-based collaboration.

### Codebase References

#### Controllers (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/FriendPageController.java` - Single friend management
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/FriendsPageController.java` - Friends overview page (shows posts from friends)
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/FriendsOverviewController.java` - Friends list management
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/SpheresPageController.java` - Friend groups/"spheres" management

#### Commands (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/command/FriendsResourceViewCommand.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/command/FriendsOverviewCommand.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/command/SphereResourceViewCommand.java`

#### Database Layer

**Database Managers:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/user/get/GetFriendsOfUser.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/user/get/GetUserFriends.java`

**Query Chain Elements:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/resource/get/GetResourcesByFriends.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/resource/get/GetResourcesOfFriendsByUser.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/resource/get/GetResourcesOfFriendsByTags.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/tag/get/GetTagsByFriendOfUser.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/statistic/user/GetFriendHistoryCount.java`

**System Tags:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/systemstags/executable/ForFriendTag.java`

#### Database Schema

**Tables:**

```sql
-- bibsonomy-database/src/main/resources/database/bibsonomy-db-schema.sql (lines 480-490)
CREATE TABLE `friends` (
  `friends_id` int(11) NOT NULL auto_increment,
  `user_name` varchar(30) NOT NULL default '',
  `f_user_name` varchar(30) NOT NULL default '',
  `tag_name` varchar(255) NOT NULL DEFAULT 'sys:network:bibsonomy-friend',
  `f_network_user_id` int(10) DEFAULT NULL,
  `friendship_date` datetime NOT NULL default '1815-12-10 00:00:00',
  PRIMARY KEY  (`friends_id`),
  UNIQUE KEY `unique_friendship` (`user_name`,`f_user_name`,`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- bibsonomy-database/src/main/resources/database/bibsonomy-db-schema.sql (line 872)
CREATE TABLE `log_friends` (...)  -- Audit log for friend relationships
```

#### LogicInterface Methods

```java
// bibsonomy-model/src/main/java/org/bibsonomy/model/logic/LogicInterface.java
List<User> getUserRelationship(String sourceUser, UserRelation relation, String tag);
void createUserRelationship(String sourceUser, String targetUser, UserRelation relation, String tag);
void deleteUserRelationship(String sourceUser, String targetUser, UserRelation relation, String tag);
```

**Enum:** `UserRelation.FRIEND_OF`, `UserRelation.OF_FRIEND` (in `org.bibsonomy.common.enums.UserRelation`)

#### REST API (Legacy v1)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-rest-client/src/main/java/org/bibsonomy/rest/client/queries/get/GetFriendsQuery.java`

**URL Patterns:**
- `/friends` - View posts from friends
- `/friends/RELATION` - View specific friend relationship
- `/spheres` - List all friend groups
- `/spheres/RELATION_NAME` - View posts from specific sphere
- `/spheres/RELATION_NAME/TAG` - Filter sphere posts by tag

### Migration Notes

**For users who relied on friends:**
1. **Use Groups instead**: Create a Group and invite collaborators
2. **Public profiles**: User profiles and posts remain visible to all (no "friends-only" visibility in v2)
3. **Follow functionality**: Not replaced; users must manually check profiles or use RSS feeds
4. **Spheres**: Not replaced; use multiple Groups if you need to organize different collaboration circles

---

## 2. Followers

### Description

The legacy system allowed users to "follow" other users (similar to Twitter), creating asymmetric relationships where:
- User A could follow User B without B following back
- Followers would see updates from followed users
- Statistics tracked follower counts

This was separate from the bidirectional "friends" feature.

### Why Removed

**Rationale:**
- **Overlap with Friends**: Followers and Friends served similar purposes, creating confusion
- **Low value in academic context**: Following is more relevant for social media than scholarly collaboration
- **Replaced by Groups**: Group membership provides similar "stay updated" functionality
- **Simplification**: Removing asymmetric social relationships simplifies the data model

### Codebase References

#### Controllers (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/FollowersPageController.java`

#### Commands (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/command/FollowersViewCommand.java`

#### Database Layer

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/user/get/GetFollowersOfUser.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/user/get/GetUserFollowers.java`

#### Database Schema

**Note:** Followers were stored in the same `friends` table with different `tag_name` values (e.g., `sys:network:follower` vs `sys:network:bibsonomy-friend`)

#### LogicInterface Methods

Same as Friends: `getUserRelationship()` with `UserRelation.FOLLOWER` enum value.

**URL Patterns:**
- `/followers/USERNAME` - View followers of a user

### Migration Notes

**For users who relied on followers:**
1. **RSS feeds**: Subscribe to a user's RSS feed to get updates
2. **Groups**: Join groups where the user is active
3. **Manual bookmarking**: Bookmark user profile pages to check periodically

---

## 3. Post Sharing & Inbox

### Description

The legacy system included a messaging/sharing system that allowed users to:
- **Send posts to other users** (bookmarks or publications)
- **Receive shared posts in an "Inbox"**
- **Attach messages/notes** to shared posts
- **Track inbox statistics** (unread count, etc.)

This was like email for posts, enabling direct peer-to-peer sharing.

### Why Removed

**Rationale:**
- **Low usage**: Inbox feature was rarely used in practice
- **Replaced by Groups**: Users can share posts by posting to shared Groups
- **Maintenance overhead**: Inbox required separate message tables, notification logic, and UI
- **Modern alternatives**: Email, Slack, or group chat for communication; Groups for collaboration
- **Spam potential**: Direct messaging systems require moderation and spam filtering

### Codebase References

#### Controllers (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/InboxPageController.java`

#### Database Manager

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/InboxDatabaseManager.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/resource/get/GetResourcesFromInbox.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/statistic/post/get/GetResourcesForUserInboxCount.java`

**Database Params:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/params/InboxParam.java`

**Tests:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/test/java/org/bibsonomy/database/managers/InboxDatabaseManagerTest.java`

#### Domain Model

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/Inbox.java`

```java
public class Inbox implements Serializable {
    private int numPosts; // Number of posts in inbox
}
```

#### Database Schema

```sql
-- bibsonomy-database/src/main/resources/database/bibsonomy-db-schema.sql (lines 634-679)
CREATE TABLE `inboxMail` (
  `message_id` int(10) unsigned NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `intraHash` varchar(32) NOT NULL default '',
  `sender_user` varchar(30) NOT NULL,
  `receiver_user` varchar(30) NOT NULL,
  `date` datetime default NULL,
  `content_type` tinyint(1) unsigned,
  PRIMARY KEY  (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `log_inboxMail` (...); -- Audit log

CREATE TABLE `inbox_tas` (
  `message_id` int(10) unsigned NOT NULL,
  `tag_name` varchar(255) NOT NULL,
  PRIMARY KEY (message_id, tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

#### REST API (Legacy v1)

No direct REST API endpoints found (likely AJAX-only in webapp).

**URL Patterns:**
- `/inbox` - View inbox messages
- `/inbox/TAG` - Filter inbox by tag

### Migration Notes

**For users who relied on inbox:**
1. **Use Groups**: Share posts to a group instead of individual inboxes
2. **Email**: Send post URLs via email if direct communication is needed
3. **Export/Import**: Export posts as BibTeX and share via email/file transfer

---

## 4. Recommendations Engine

### Description

The legacy system included a sophisticated machine learning-based recommendation engine:
- **Publication recommendations**: Suggest relevant publications to users based on their library
- **Tag recommendations**: Suggest tags when creating/editing posts
- **Content-based filtering**: Recommend based on publication metadata (title, authors, abstract)
- **Collaborative filtering**: Recommend based on what similar users tagged
- **Feedback loop**: Users could rate recommendations to improve accuracy

This was powered by the `bibsonomy-recommender` module with multiple ML algorithms.

### Why Removed

**Rationale:**
- **Complexity**: Recommendation engine requires separate infrastructure (item/tag recommender databases)
- **Performance overhead**: Real-time recommendations are computationally expensive
- **Maintenance burden**: ML models require training, tuning, and updating
- **Limited adoption**: Many users ignored recommendations or found them irrelevant
- **Alternative approach**: v2 focuses on **search and discovery** (better search, filters, popular posts) instead of personalized recommendations
- **External tools**: Users can use Zotero, Mendeley, or ResearchGate for ML-based recommendations

### Codebase References

#### Recommender Module

**Entire module:** `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-recommender/`

**Key classes:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-recommender/src/main/java/org/bibsonomy/recommender/item/AbstractItemRecommender.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-recommender/src/main/java/org/bibsonomy/recommender/item/content/ContentBasedItemRecommender.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-recommender/src/main/java/org/bibsonomy/recommender/item/content/TagBasedItemRecommender.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-recommender/src/main/java/org/bibsonomy/recommender/tag/AbstractTagRecommender.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-recommender/src/main/java/org/bibsonomy/recommender/tag/popular/MostPopularByResourceTagRecommender.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-recommender/src/main/java/org/bibsonomy/recommender/tag/popular/MostPopularByUserTagRecommender.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-recommender/src/main/java/org/bibsonomy/recommender/tag/simple/SimpleContentBasedTagRecommender.java`

#### Controllers (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/RecommendedPostsPageController.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/GetPublicationRecommendedTagsController.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/GetBookmarkRecommendedTagsController.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/ItemRecommenderFeedbackController.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/RecommendationsAjaxController.java`

#### Admin Interface

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/admin/AdminRecommendersController.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/command/admin/AdminRecommendersCommand.java`

#### Domain Models

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/RecommendedPost.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/PhDRecommendation.java`

```java
public class RecommendedPost<T extends Resource> implements Serializable {
    private double score;      // Recommendation score
    private double confidence; // Confidence level
    private Post<T> post;      // The recommended post
}
```

#### Database

**Separate databases:**
- `item_recommender_db` - Stores item recommendation data
- `tag_recommender_db` - Stores tag recommendation data

**Configuration:** See `.gitlab-ci.yml` for test database setup.

**URL Patterns:**
- `/recommendations` - View recommended publications
- AJAX endpoints for tag suggestions during post creation

### Migration Notes

**For users who relied on recommendations:**
1. **Search instead**: Use full-text search with filters to discover relevant posts
2. **Popular posts**: Browse popular posts and trending tags
3. **Related tags**: v2 retains "related tags" feature (tags frequently used together)
4. **External tools**: Use Zotero, Mendeley, ResearchGate, or Google Scholar for ML-based recommendations
5. **Manual curation**: Follow groups and users whose work is relevant

**Note:** Tag suggestions based on **most popular tags** may be retained in v2 (simpler, non-ML approach).

---

## 5. Clipboard

### Description

The legacy system included a "clipboard" feature allowing users to:
- **Temporarily save posts** (like a shopping cart)
- **Pick/unpick posts** to clipboard
- **Batch operations** on clipboard items (export, tag, share)
- **Clipboard statistics** (number of items)

This was intended as a temporary workspace before deciding what to do with posts.

### Why Removed

**Rationale:**
- **Incomplete implementation**: Code comments note "TODO: implement full clipboard functionality"
- **Low usage**: Feature was not widely adopted
- **Browser alternatives**: Modern browsers have bookmark managers, reading lists, and "save for later" features
- **Redundant**: Users can create a temporary private group or use the `to-read` tag instead
- **Simplification**: Removes an incomplete feature that adds little value

### Codebase References

#### Controllers (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ClipboardPageController.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/ClipboardController.java`

#### Commands & Actions

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/command/ajax/ClipboardManagerCommand.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/command/ajax/action/ClipboardAction.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/validation/ajax/ClipboardValidator.java`

#### Database Manager

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/ClipboardDatabaseManager.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/bibtex/get/GetBibtexFromClipboardForUser.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/statistic/post/GetClipboardPostsCount.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/statistic/post/GetClipboardPostsHistoryCount.java`

**Plugin:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/plugin/plugins/ClipboardPlugin.java`

**Params:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/params/ClipboardParam.java`

**Tests:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/test/java/org/bibsonomy/database/managers/ClipboardDatabaseManagerTest.java`

#### Domain Model

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/Clipboard.java`

```java
public class Clipboard implements Serializable {
    private int numPosts; // TODO: implement full clipboard functionality
}
```

#### Database Schema

**Note:** Clipboard was implemented as a flag on posts table (`picked` column), not a separate table.

```sql
-- From bibsonomy-db-schema.sql (line 1379)
`picked` tinyint(1) default '1',  -- Flag indicating if post is "picked" to clipboard
```

#### REST API (Legacy v1)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-rest-server/src/main/java/org/bibsonomy/rest/strategy/clipboard/GetClipboardStrategy.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-rest-server/src/main/java/org/bibsonomy/rest/strategy/clipboard/PostClipboardStrategy.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-rest-server/src/main/java/org/bibsonomy/rest/strategy/clipboard/DeleteClipboardStrategy.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-rest-client/src/main/java/org/bibsonomy/rest/client/queries/delete/UnpickClipboardQuery.java`

**URL Patterns:**
- `/clipboard` - View clipboard items
- REST API: `POST /clipboard` (pick post), `DELETE /clipboard` (unpick post)

### Migration Notes

**For users who relied on clipboard:**
1. **Use tags**: Create a temporary tag like `to-process` or `to-read`
2. **Browser bookmarks**: Use browser's native bookmark manager for temporary saves
3. **Private group**: Create a private group as a staging area
4. **Export**: Export selected posts to BibTeX file for offline processing

---

## 6. MySearch (Saved Searches)

### Description

The legacy system included a **MySearch** feature that:
- **Analyzed user's publications** to build relational tables
- **Cross-referenced** titles, authors, and tags
- **Provided interactive search interface** to find connections
- **Built correlation matrices** (which publications share authors? which tags co-occur?)

This was a visualization/exploration tool for a user's own bibliography.

### Why Removed

**Rationale:**
- **Narrow use case**: Only useful for users with large personal publication libraries
- **Performance intensive**: Required loading ALL user publications and computing relations in-memory
- **Better alternatives**: Modern citation managers (Zotero, Mendeley) provide similar analysis
- **Not core functionality**: This was a power-user feature, not essential for basic use
- **Code quality**: Implementation had FIXMEs noting inefficiency (e.g., `indexOf` in loops)

### Codebase References

#### Controllers (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/MySearchController.java` (283 lines)

#### Commands

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/command/MySearchCommand.java`

#### Implementation Details

The controller builds several relation tables:
- `tagTitle` - Maps tags to titles
- `authorTitle` - Maps authors to titles
- `tagAuthor` - Maps tags to authors
- `titleAuthor` - Maps titles to authors

```java
// From MySearchController.java (lines 154-269)
private void buildRelationTables(final ListCommand<Post<BibTex>> bibtex, final MySearchCommand command) {
    // Build SortedSet arrays for cross-referencing
    // FIXME: indexOf is inefficient!
    // ...
}
```

**URL Patterns:**
- `/mysearch` - MySearch for logged-in user's publications
- `/mysearch/GROUP` - MySearch scoped to a group

### Migration Notes

**For users who relied on MySearch:**
1. **Export to citation manager**: Export BibTeX to Zotero, Mendeley, or EndNote for analysis
2. **Use filters and search**: v2 search supports filtering by author, tag, title
3. **External tools**: Use bibliometric analysis tools like VOSviewer, CiteSpace, or Gephi
4. **Related tags feature**: v2 retains "related tags" (tags frequently used together)

---

## 7. CV/Wiki

### Description

The legacy system allowed users to maintain a **personal wiki page** that could function as a:
- **Curriculum Vitae (CV)**: Professional profile with publications, education, etc.
- **Personal homepage**: Custom formatted content using MediaWiki-style markup
- **Version history**: Track changes to wiki content over time

This used the `bibsonomy-wiki` module with WikiModel parsing.

### Why Removed

**Rationale:**
- **Maintenance burden**: Wiki parsing requires external library (WikiModel), potential XSS vulnerabilities
- **Low adoption**: Most users did not maintain wiki CVs
- **Better alternatives**: LinkedIn, ResearchGate, ORCID, institutional profiles, personal websites
- **Out of scope**: BibSonomy v2 focuses on bibliography management, not personal website hosting
- **Simplification**: Removing wiki reduces attack surface and code complexity

**Decision:** Replace with simple **user bio/description field** (plain text or Markdown, no complex wiki markup).

### Codebase References

#### Wiki Module

**Module:** `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-wiki/`

**Key class:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-wiki/src/main/java/org/bibsonomy/wiki/CVWikiModel.java`

#### Controllers (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/WikiCvPageController.java`

#### Database Manager

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/WikiDatabaseManager.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/params/WikiParam.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/util/WikiTemplateLoader.java`

**Tests:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/test/java/org/bibsonomy/database/util/WikiTemplateLoaderTest.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/test/java/org/bibsonomy/database/managers/UserDatabaseManagerWikiTest.java`

#### Domain Model

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/Wiki.java`

```java
public class Wiki implements Serializable {
    private String wikiText; // MediaWiki-style markup
    private Date date;       // Version timestamp
}
```

#### LogicInterface Methods

```java
// bibsonomy-model/src/main/java/org/bibsonomy/model/logic/LogicInterface.java
List<Date> getWikiVersions(String userName);
Wiki getWiki(String userName, Date date);
void createWiki(String userName, Wiki wiki);
void updateWiki(String userName, Wiki wiki);
```

#### Database Schema

```sql
-- bibsonomy-database/src/main/resources/database/bibsonomy-db-schema.sql (lines 1576+)
CREATE TABLE `user_wiki` (
  -- Stores wiki content per user with version history
  -- Schema details in migrations/2.0.38/add_cvwiki.sql
);

CREATE TABLE `log_wiki` (...); -- Audit log
```

**Migration:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/resources/database/migrations/2.0.38/add_cvwiki.sql`

**URL Patterns:**
- `/cv/USERNAME` - View user's wiki/CV page
- `/cv/USERNAME?date=YYYY-MM-DD` - View historical version

### Migration Notes

**For users who relied on wiki/CV:**
1. **Export wiki content**: Before migrating, export wiki text via legacy webapp
2. **Use external profile**: Maintain CV on LinkedIn, ResearchGate, ORCID, or personal website
3. **User bio field**: v2 may include a simple bio/description field (plain text or Markdown)
4. **Link to external CV**: Add URL to external CV in user profile

**Note:** Publication lists are still accessible via user profile pages (no change).

---

## 8. Comments & Discussion System

### Description

The legacy system included a **threaded discussion system** allowing users to:
- **Comment on posts** (publications or bookmarks)
- **Nested replies**: Comments could have sub-comments (threaded discussions)
- **Anonymous comments**: Option to post anonymously
- **Group visibility**: Comments could be scoped to specific groups
- **Discussion spaces**: Each post has a "discussion space" identified by hash

This enabled peer review and collaborative annotation.

### Why Removed

**Rationale:**
- **Low adoption**: Discussion feature was rarely used
- **Moderation burden**: Requires spam filtering, abuse reporting, and moderation tools
- **Better alternatives**: Use group discussion forums, Slack/Discord, or annotation tools like Hypothes.is
- **Complexity**: Threaded discussions add significant database and UI complexity
- **Focus**: v2 focuses on bibliography management, not social discussion

**Note:** This is **removed** from MVP but could be **reconsidered post-MVP** if user demand emerges.

### Codebase References

#### Controllers (Webapp)

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/DiscussionItemAjaxController.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/CommentAjaxController.java`

#### Commands & Validators

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/command/ajax/DiscussionItemAjaxCommand.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/validation/DiscussionItemValidator.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/validation/CommentValidator.java`

**Tests:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/test/java/org/bibsonomy/webapp/validation/CommentValidatorTest.java`

#### Database Managers

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/discussion/DiscussionDatabaseManager.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/discussion/DiscussionItemDatabaseManager.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/discussion/CommentDatabaseManager.java`

**Chain Elements:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/discussion/DiscussionChainElement.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/discussion/get/GetDiscussionSpaceByHash.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/resource/get/GetResourcesWithDiscussions.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/statistic/post/get/GetResourcesWithDiscussionsCount.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/statistic/post/get/GetUserDiscussionsStatistics.java`

**Plugin:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/plugin/plugins/DiscussionPlugin.java`

**Params:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/params/discussion/DiscussionItemParam.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/params/discussion/CommentParam.java`

**Tests:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/test/java/org/bibsonomy/database/managers/discussion/DiscussionDatabaseManagerTest.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/test/java/org/bibsonomy/database/managers/discussion/CommentDatabaseManagerTest.java`

#### Domain Models

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/DiscussionItem.java` (138 lines)
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/Comment.java`

```java
public class DiscussionItem implements Serializable {
    private Integer id;
    private String hash;
    private Set<Group> groups;
    private User user;
    private Date date;
    private Date changeDate;
    private List<DiscussionItem> subDiscussionItems; // Nested comments
    private String parentHash;
    private boolean anonymous;
    private Class<? extends Resource> resourceType;
}
```

#### LogicInterface

- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/logic/DiscussionLogicInterface.java` (sub-interface of LogicInterface)

**Utilities:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/util/DiscussionItemUtils.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/test/java/org/bibsonomy/model/util/DiscussionItemUtilsTest.java`

#### Database Schema

```sql
-- bibsonomy-database/src/main/resources/database/bibsonomy-db-schema.sql (lines 1637+)
CREATE TABLE `discussion` (
  -- Stores discussion items (comments) with threading support
  -- Schema details in migrations/2.0.16/modify_discussion.sql
);

CREATE TABLE `log_discussion` (...); -- Audit log
```

**Enums:**
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database-common/src/main/java/org/bibsonomy/database/common/enums/DiscussionItemType.java`

**URL Patterns:**
- AJAX endpoints for creating/viewing comments (no dedicated page URLs found)

### Migration Notes

**For users who relied on comments/discussions:**
1. **Group forums**: Use external group discussion tools (Slack, Discord, Google Groups)
2. **Annotation tools**: Use Hypothes.is or Diigo for collaborative annotation
3. **Issue trackers**: For academic collaboration, use GitHub Issues or GitLab Issues
4. **Email threads**: Export post details and discuss via email

**Note:** If strong user demand emerges, a simplified comment system could be added post-MVP (non-threaded, no anonymous comments).

---

## 9. Migration Guide for Users

### Summary of Feature Removals

| Removed Feature | Replacement in v2 | External Alternative |
|-----------------|-------------------|---------------------|
| **Friends** | Groups | LinkedIn, ResearchGate |
| **Followers** | Groups, RSS feeds | Twitter, RSS reader |
| **Inbox/Sharing** | Groups, email | Email, Slack |
| **Recommendations** | Search, filters, popular posts | Zotero, Mendeley, ResearchGate |
| **Clipboard** | Tags (e.g., `to-read`), browser bookmarks | Browser bookmarks |
| **MySearch** | Search with filters | Zotero, Mendeley, bibliometric tools |
| **Wiki/CV** | User bio field | LinkedIn, ORCID, personal website |
| **Comments** | (Not replaced) | Hypothes.is, Slack, email |

### General Migration Steps

1. **Before migrating from v1 to v2:**
   - Export all data (posts, friends lists, inbox messages, wiki content) via v1 webapp
   - Screenshot or save important relationships (friends, spheres)
   - Document any custom workflows using removed features

2. **After migrating to v2:**
   - Reorganize friend relationships into Groups
   - Replace clipboard with tags or browser bookmarks
   - Set up RSS feeds for users you previously followed
   - Link external profiles (LinkedIn, ORCID) in user bio

3. **For advanced users:**
   - Consider running legacy v1 webapp alongside v2 temporarily (they share the same database)
   - Export BibTeX and import into citation managers for features v2 doesn't support
   - Use external tools for recommendations, CV hosting, and discussions

---

## Technical Notes for Developers

### Database Migration Considerations

When migrating from v1 to v2:
- **Friends/Followers tables**: `friends` table can be ignored (no foreign key constraints to posts/users)
- **Inbox tables**: `inboxMail`, `log_inboxMail`, `inbox_tas` can be archived/dropped
- **Clipboard**: `picked` column on posts can be dropped or ignored
- **Wiki**: `user_wiki`, `log_wiki` tables can be archived
- **Discussion**: `discussion`, `log_discussion` tables can be archived
- **Recommender databases**: `item_recommender_db`, `tag_recommender_db` not needed for v2

**Caution:** If running v1 and v2 simultaneously, do NOT drop these tables until v1 is fully decommissioned.

### Code Removal Strategy

When removing legacy features:
1. **DO NOT** delete database tables immediately (schema compatibility with v1)
2. **DO** remove controllers, commands, and webapp UI code
3. **DO** remove or deprecate LogicInterface methods (document as deprecated if v1 still running)
4. **DO** remove recommender module from v2 dependencies
5. **DO** remove wiki module from v2 dependencies
6. **CONSIDER** keeping domain models (`Inbox`, `Clipboard`, `Wiki`, `DiscussionItem`) as deprecated classes for v1 compatibility

### REST API Compatibility

REST API v2 endpoints will **NOT** support:
- `GET /api/v2/users/{username}/friends` (removed)
- `GET /api/v2/users/{username}/followers` (removed)
- `GET /api/v2/inbox` (removed)
- `POST /api/v2/clipboard` (removed)
- `GET /api/v2/recommendations` (removed)
- `GET /api/v2/cv/{username}` (removed)
- Comment/discussion endpoints (removed)

Clients using v1 REST API must migrate to v2-supported features or continue using v1 API.

---

## Questions or Concerns?

If you have questions about removed features or need help migrating workflows, please:
1. Check the [FEATURE_SCOPE.md](FEATURE_SCOPE.md) for MVP features
2. Check [DEFERRED_FEATURES.md](DEFERRED_FEATURES.md) for post-MVP planned features
3. Open a GitHub issue with the `question` or `migration` label
4. Contact the development team via the mailing list

**Note:** Some removed features may be reconsidered post-MVP if strong user demand emerges.
