Borrowed implementation from Confluence' WebDAV Plugin:

    http://confluence.atlassian.com/display/CONFEXT/WebDAV+Plugin


Updating:

The plugin's version on the last update was: 1.2.5-SNAPSHOT

For the next update we're interested in these files:
  * com.atlassian.confluence.extra.webdav.servlet.client.*
  * com.atlassian.confluence.extra.webdav.servlet.Webdav*
  * com.atlassian.confluence.extra.webdav.servlet.resource.WebdavResourceServlet


Since Confluence' core isn't Open Source we have to guess the implementation
of certain base classes like:
  * com.atlassian.user.User
  * com.atlassian.confluence.user.AuthenticatedUserThreadLocal
This may be easy for interfaces but for "real" classes this might be tricky.