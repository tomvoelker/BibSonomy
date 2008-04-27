package com.atlassian.confluence.extra.webdav.servlet.client;

import java.util.BitSet;

public class MacWebdavfsClient extends AbstractClient {
    
    private static final BitSet MAC_SAFE_CHARS;

    static {
        // Mac Agent doesn't like i18n characters.
        MAC_SAFE_CHARS = new BitSet( 256 );
        MAC_SAFE_CHARS.or( URL_SAFE_CHARS );
        MAC_SAFE_CHARS.or( UNSAFE_CHARS );
        MAC_SAFE_CHARS.or( RESERVED_CHARS );
    }

    public MacWebdavfsClient( String userAgent ) {
        super( userAgent );
    }

    public boolean isFileNameSafe( String name ) {
        char[] chars = name.toCharArray();
        for ( int i = 0; i < chars.length; i++ )
            if ( !MAC_SAFE_CHARS.get( chars[i] ) )
                return false;

        return true;
    }

    public boolean requiresEmptyCollectionAfterCreate() {
        return true;
    }

    public boolean requiresEmptyCollectionForDelete() {
        return true;
    }

    public boolean requiresMoveBeforeSaving() {
        return true;
    }

}
