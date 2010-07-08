/*
 * Copyright (c) Members of the EGEE Collaboration. 2006-2010.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.common.model.util;

import java.util.List;
import java.util.StringTokenizer;

/** Helper class for working with Strings. */
public final class Strings {

    /** Constructor. */
    private Strings() {

    }

    /**
     * A "safe" null/empty check for strings.
     * 
     * @param s The string to check
     * 
     * @return true if the string is null or the trimmed string is length zero
     */
    public static boolean isEmpty(String s) {
        if (s != null) {
            String sTrimmed = s.trim();
            if (sTrimmed.length() > 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compares two strings for equality, allowing for nulls.
     * 
     * @param <T> type of object to compare
     * @param s1 The first operand
     * @param s2 The second operand
     * 
     * @return true if both are null or both are non-null and the same string value
     */
    public static <T> boolean safeEquals(T s1, T s2) {
        if (s1 == null || s2 == null) {
            return s1 == s2;
        }

        return s1.equals(s2);
    }

    /**
     * A safe string trim that handles nulls.
     * 
     * @param s the string to trim
     * 
     * @return the trimmed string or null if the given string was null
     */
    public static String safeTrim(String s) {
        if (s != null) {
            return s.trim();
        }

        return null;
    }

    /**
     * Removes preceding or proceeding whitespace from a string or return null if the string is null or of zero length
     * after trimming (i.e. if the string only contained whitespace).
     * 
     * @param s the string to trim
     * 
     * @return the trimmed string or null
     */
    public static String safeTrimOrNullString(String s) {
        if (s != null) {
            String sTrimmed = s.trim();
            if (sTrimmed.length() > 0) {
                return sTrimmed;
            }
        }

        return null;
    }
    
    /**
     * Creates a list of strings from delimited string.
     * 
     * @param valuesStr the delimited string of values
     * @param delimiter the delimiter, if null, then ',' (comma) will be used
     * 
     * @return the list of values, without nulls, or null if the delimited value string is null or empty
     */
    public static List<String> toList(String valuesStr, String delimiter){
        String trimmedValueStr = safeTrimOrNullString(valuesStr);
        if(trimmedValueStr == null){
            return null;
        }
        
        StringTokenizer valueTokens = new StringTokenizer(trimmedValueStr, delimiter);
        LazyList<String> values = new LazyList<String>();
        String value;
        
        while(valueTokens.hasMoreTokens()){
            value = safeTrimOrNullString(valueTokens.nextToken());
            if(value != null){
                values.add(value);
            }
        }
        
        return values;
    }
}