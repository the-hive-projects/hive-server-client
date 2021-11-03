package org.thehive.hiveserverclient.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageUtilsTest {

    @Disabled("Parse message list")
    @Test
    void parseMessageList() {
        var expectedList = Arrays.asList("item-one", "item02", "item03");
        final var delimiter = ",";
        var joiner = new StringJoiner(delimiter);
        expectedList.forEach(joiner::add);
        var textMessage = joiner.toString();
        var list = MessageUtils.parseMessageList(textMessage, delimiter);
        assertEquals(expectedList, list);
    }

    @Disabled("Parse message list with prefix and suffix")
    @Test
    void parseMessageListWithPrefixAndSuffix() {
        var expectedList = Arrays.asList("item-one", "item02", "item03");
        final var delimiter = ",";
        final var prefix = "[";
        final var suffix = "]";
        var joiner = new StringJoiner(delimiter);
        expectedList.forEach(joiner::add);
        var textMessage = prefix + joiner.toString() + suffix;
        var list = MessageUtils.parseMessageList(textMessage, delimiter, prefix, suffix);
        assertEquals(expectedList, list);
    }

    @Disabled("Parse paired message list")
    @Test
    void parsePairedMessageList() {
        var expectedList = Arrays.asList(Pair.of("key01", "value01"), Pair.of("key02", "value02"), Pair.of("key03", "value03"));
        final var delimiter = ",";
        final var separator = ":";
        var joiner = new StringJoiner(delimiter);
        expectedList.stream()
                .map(pair -> pair.key + separator + pair.value)
                .forEach(joiner::add);
        var textMessage = joiner.toString();
        var list = MessageUtils.parsePairedMessageList(textMessage, delimiter, separator);
        assertEquals(expectedList, list);
    }

    @Disabled("Parse paired message list with prefix and suffix")
    @Test
    void parsePairedMessageListWithPrefixAndSuffix() {
        var expectedList = Arrays.asList(Pair.of("key01", "value01"), Pair.of("key02", "value02"), Pair.of("key03", "value03"));
        final var delimiter = ",";
        final var separator = ":";
        final var prefix = "[";
        final var suffix = "]";
        var joiner = new StringJoiner(delimiter);
        expectedList.stream()
                .map(pair -> pair.key + separator + pair.value)
                .forEach(joiner::add);
        var textMessage = prefix + joiner.toString() + suffix;
        var list = MessageUtils.parsePairedMessageList(textMessage, delimiter, separator, prefix, suffix);
        assertEquals(expectedList, list);
    }

}