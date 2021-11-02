package org.thehive.hiveserverclient.util;

import java.util.Base64;

public class HeaderUtil {

    public static final String HTTP_BASIC_AUTHORIZATION_HEADER_NAME = "Authorization";

    public static String httpBasicToken(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

}
