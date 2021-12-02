package org.thehive.hiveserverclient.util;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MessageUtils {

    public static List<String> parseMessageList(@NonNull String message, @NonNull String delimiter) throws IllegalArgumentException {
        return parseMessageList(message, delimiter, null, null);
    }

    public static List<String> parseMessageList(@NonNull String message, @NonNull String delimiter,
                                                @Nullable String prefix, @Nullable String suffix) throws IllegalArgumentException {
        message = extractMessage(message, prefix, suffix);
        var tokenizer = new StringTokenizer(message, delimiter);
        var list = new ArrayList<String>();
        while (tokenizer.hasMoreTokens())
            list.add(tokenizer.nextToken());
        return list;
    }

    public static List<Pair<String, String>> parsePairedMessageList(@NonNull String message, @NonNull String delimiter,
                                                                    @NonNull String separator) throws IllegalArgumentException {
        return parsePairedMessageList(message, delimiter, separator, null, null);
    }

    public static List<Pair<String, String>> parsePairedMessageList(@NonNull String message, @NonNull String delimiter, @NonNull String separator,
                                                                    @Nullable String prefix, @Nullable String suffix) throws IllegalArgumentException {
        message = extractMessage(message, prefix, suffix);
        var tokenizer = new StringTokenizer(message, delimiter);
        var list = new ArrayList<Pair<String, String>>();
        while (tokenizer.hasMoreTokens()) {
            var item = tokenizer.nextToken();
            if (item.contains(separator)) {
                var index = item.indexOf(separator);
                list.add(Pair.of(item.substring(0, index), item.substring(index + 1)));
            } else
                list.add(Pair.of(null, item));
        }
        return list;
    }

    public static String extractMessage(@NonNull String message, @Nullable String prefix, @Nullable String suffix) {
        var beginIndex = 0;
        var endIndex = message.length();
        if (prefix != null) {
            if (message.startsWith(prefix))
                beginIndex = prefix.length();
            else
                throw new IllegalArgumentException("Message doesn't start with given prefix, prefix: " + prefix);
        }
        if (suffix != null) {
            if (message.endsWith(suffix))
                endIndex = message.length() - suffix.length();
            else
                throw new IllegalArgumentException("Message doesn't end with given suffix, suffix: " + suffix);
        }
        return message.substring(beginIndex, endIndex);
    }

}
