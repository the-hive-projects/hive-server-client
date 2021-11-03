package org.thehive.hiveserverclient.test.util;

import java.util.Random;

public class StringUtils {

    private static final int LEFT_ALPHANUMERIC_LIMIT = 48;
    private static final int LEFT_ALPHABETIC_LIMIT = 97;
    private static final int RIGHT_LIMIT = 122;
    private static final Random random = new Random();

    public static String randomAlphabeticString(int length) {
        return randomString(length, LEFT_ALPHABETIC_LIMIT, RIGHT_LIMIT);
    }

    public static String randomAlphanumericString(int length) {
        return randomString(length, LEFT_ALPHANUMERIC_LIMIT, RIGHT_LIMIT);
    }

    public static String randomString(int length, int leftLimit, int rightLimit) {
        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
