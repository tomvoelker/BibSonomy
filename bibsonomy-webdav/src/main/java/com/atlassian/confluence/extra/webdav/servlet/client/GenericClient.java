package com.atlassian.confluence.extra.webdav.servlet.client;

public class GenericClient extends AbstractClient {

    public GenericClient( String userAgent ) {
        super( userAgent );
    }

    public boolean isFileNameSafe( String name ) {
        return true;
    }

}
