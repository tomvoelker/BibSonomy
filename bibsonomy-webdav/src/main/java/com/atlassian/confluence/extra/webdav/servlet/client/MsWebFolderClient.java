package com.atlassian.confluence.extra.webdav.servlet.client;

import java.util.BitSet;

public class MsWebFolderClient extends AbstractClient {

    private static final BitSet WEBDAV_SAFE_CHARS;

    private static final BitSet MS_SAFE_CHARS;

    static {
        WEBDAV_SAFE_CHARS = new BitSet( 256 );
        WEBDAV_SAFE_CHARS.set( Character.MIN_VALUE, Character.MAX_VALUE );
        WEBDAV_SAFE_CHARS.andNot( UNSAFE_CHARS );
        WEBDAV_SAFE_CHARS.andNot( RESERVED_CHARS );

        // MS Agent-specific variations.
        MS_SAFE_CHARS = new BitSet( 256 );
        MS_SAFE_CHARS.or( WEBDAV_SAFE_CHARS );

        // MS Agent doesn't like '@' to be escaped.
        MS_SAFE_CHARS.set( '@' );
        // Preserve spaces for URLs with i18n characters.
        MS_SAFE_CHARS.set( ' ' );
    }

    public MsWebFolderClient( String userAgent ) {
        super( userAgent );
    }

    public boolean isFileNameSafe( String name ) {
        // Files with '?' will fail to work correctly in Web Folders.
        if ( name.indexOf( '?' ) > -1 )
            return false;

        return true;
    }

    public String encodeFileName( String name, String encoding ) {
        String encoded = encode( MS_SAFE_CHARS, name, encoding );
        // Put ' ' back instead of '+'.

        encoded = encoded.replaceAll( "\\+", " " );
        return encoded;
    }

}
