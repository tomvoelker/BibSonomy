1. WebDAV implementation

Borrowed implementation from Confluence' WebDAV Plugin:

    http://confluence.atlassian.com/display/CONFEXT/WebDAV+Plugin


1.1 Compatible WebDAV clients

Tested our implementation with:

	-> TODO

This may be interesting too:

 	http://confluence.atlassian.com/display/CONFEXT/WebDAV+Client+Compatibility


1.2 Updating

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


2. Implementation details for BibSonomy

After successfully logging in with its username and password a user will see
the following directory structure:

-/
 |-- bibtex-all.bib (current publications from the homepage)
 |-- bookmarks.html (current bookmarks from the homepage)
 |-- user
 |   `-- <username of logged in user>
 |       |-- bibtex-all.bib (all publications from the user)
 |       `-- bookmarks.html (all bookmarks from the user)
 `-- group (contains all groups the user is a member of)
     |-- <group-1>
     |   |-- bibtex-all.bib (all publications from the group)
     |   `-- bookmarks.html (all bookmarks from the group)
     |-- ...
     |   `-- ...
     `-- <group-n>
         `-- ... (like contents of "group-1")

Anonymous/guest login isn't implemented.