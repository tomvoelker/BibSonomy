//    Copyright (c) 2009 Elwyn Malethan
//
//    This file is part of java-pingback.
//
//    java-pingback is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    java-pingback is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.

//    You should have received a copy of the GNU General Public License
//    along with java-pingback.  If not, see <http://www.gnu.org/licenses/>.

package com.malethan.pingback;

public class Link {
    //----------------------------------------------------------------------- Static Properties and Constants
    //----------------------------------------------------------------------- Static Methods

    //----------------------------------------------------------------------- Instance Properties

    private String title;
    private String url;
    private String pingbackUrl;
    private boolean success;

    //----------------------------------------------------------------------- Constructors

    public Link(String title, String url, String pingbackUrl, boolean success) {
        this.title = title;
        this.url = url;
        this.pingbackUrl = pingbackUrl;
        this.success = success;
    }

    //----------------------------------------------------------------------- Getters and Setters

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPingbackUrl() {
        return pingbackUrl;
    }

    public boolean isSuccess() {
        return success;
    }

//----------------------------------------------------------------------- Instance Methods

    public boolean isPingbackEnabled() {
        return pingbackUrl != null && pingbackUrl.length() > 0;
    }
}
