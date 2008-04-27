package com.atlassian.confluence.extra.webdav.servlet.client;

import com.atlassian.confluence.extra.webdav.servlet.WebdavResponse;
//import com.atlassian.confluence.util.GeneralUtil;

public class MsInternetExplorerClient extends AbstractClient {

    public MsInternetExplorerClient( String userAgent ) {
        super( userAgent );
    }

    public boolean isFileNameSafe( String name ) {
        return true;
    }

    public void setContentDisposition( WebdavResponse resp, String filename, String contentEncoding ) {
        resp.addHeader( "content-disposition", "attachment;filename=" + /*GeneralUtil.urlEncode(*/ filename /*)*/ );
    }

}
