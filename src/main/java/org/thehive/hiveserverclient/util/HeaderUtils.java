package org.thehive.hiveserverclient.util;

import lombok.NonNull;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.Base64;

public class HeaderUtils {

    public static final String HTTP_BASIC_AUTHENTICATION_HEADER_NAME = "Authorization";

    public static String httpBasicAuthenticationToken(@NonNull String username, @NonNull String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public static Header httpBasicAuthenticationHeader(@NonNull String token) {
        return new BasicHeader(HTTP_BASIC_AUTHENTICATION_HEADER_NAME, token);
    }

    public static Header httpBasicAuthenticationHeader(@NonNull String username, @NonNull String password) {
        return httpBasicAuthenticationHeader(httpBasicAuthenticationToken(username, password));
    }

}
