package com.atlassian.confluence.extra.webdav.servlet;

import javax.servlet.http.HttpServletRequest;

public interface WebdavRequest extends HttpServletRequest {

    String USER_AGENT = "User-Agent";

    String OVERWRITE = "Overwrite";

    String LOCK_TOKEN = "Lock-Token";

    String DESTINATION = "Destination";

    String DEPTH = "Depth";

    int DEPTH_0 = 0;

    int DEPTH_1 = 1;

    int DEPTH_INFINITY = -1;

    int getDepth();

    String getDestination();

    String getLockToken();

    boolean isOverwrite();

    String getUserAgent();
    
    WebdavClient getClient();
}