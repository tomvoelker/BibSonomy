package org.bibsonomy.batch.searchlucene;

import java.text.DecimalFormat;

// from http://wiki.apache.org/lucene-java/SearchNumericalFields

public class NumberUtils {
    private static final DecimalFormat formatter = new DecimalFormat("0000000"); // make this as wide as you need
    public static String pad(int n) {
          return formatter.format(n);
    }
    public static String pad(String s) {
          return formatter.format(Integer.parseInt(s));
    }
}
