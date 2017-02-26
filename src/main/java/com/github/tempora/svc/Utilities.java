/* 
 * Copyright 2017 Faissal Elamraoui.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tempora.svc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Set of helper functions that do a variety of things.
 */
public class Utilities {

    private Utilities() {}

    /**
     * Parses a string to a {@link Date}.
     * @param str the value to parse.
     * @return
     * @throws ParseException
     */
    public static Date str2date(String str) throws ParseException {
        String target = "Thu Sep 28 20:29:30 JST 2000";
        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
        return df.parse(target);
    }

    /**
     * Converts a byte array to a {@link String}.
     * @param arr the byte array.
     * @return a {@link String}.
     */
    public static String bytes2str(byte[] arr) {
        return new String(arr);
    }

    public static String base64ToStr(String b64str) {
        return Utilities.bytes2str(Base64.getDecoder().decode(b64str));
    }

    /**
     * Returns the Gmail string representation of a {@link Date}.
     * @param date the date to convert.
     * @return a {@link String}.
     */
    public static String gmailDateFormat(Date date) {
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }

    public static String searchByDateQuery(Date after, Date before) {
        StringBuffer sb = new StringBuffer();
        sb.append("after:")
                .append(Utilities.gmailDateFormat(after))
                .append(" ")
                .append("before:")
                .append(Utilities.gmailDateFormat(before));
        return sb.toString();
    }


    /**
     * Extracts a tag from the passed string.
     * A tag is in the format "[valueOfTag]", and the function should
     * return the "valueOfTag" value.
     *
     * @param value the string containing a tag.
     * @return a {@link String}.
     */
    public static String extractTag(String value) {
        if (value != null) {
            Pattern p = Pattern.compile("\\[(.*?)\\]");
            Matcher m = p.matcher(value);
            try {
                if (m.find()) {
                    String theTag = m.group();
                    return theTag.replace("[", "").replace("]", "");
                }
            } catch (IllegalStateException ex) {
                return "";
            }
        }
        return "";
    }

}
