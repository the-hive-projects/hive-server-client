package org.thehive.hiveserverclient.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageUtilsTest {

    @DisplayName("Parse message list without both prefix and suffix")
    @Test
    void parseMessageListWithoutBothPrefixAndSuffix() {
        var expectedList = Arrays.asList("item01", "item02", "item03");
        final var delimiter = ",";
        var joiner = new StringJoiner(delimiter);
        expectedList.forEach(joiner::add);
        var message = joiner.toString();
        log.info("Message: {}", message);
        var resultList = MessageUtils.parseMessageList(message, delimiter);
        log.info("Parsed messages: {}", resultList);
        assertEquals(expectedList, resultList);
    }

    @DisplayName("Parse message list with both prefix and suffix")
    @Test
    void parseMessageListWithBothPrefixAndSuffix() {
        var expectedList = Arrays.asList("item01", "item02", "item03");
        final var delimiter = ",";
        final var prefix = "[";
        final var suffix = "]";
        var joiner = new StringJoiner(delimiter);
        expectedList.forEach(joiner::add);
        var message = prefix + joiner + suffix;
        log.info("Message: {}", message);
        var resultList = MessageUtils.parseMessageList(message, delimiter, prefix, suffix);
        log.info("Parsed messages: {}", resultList);
        assertEquals(expectedList, resultList);
    }

    @DisplayName("Parse message list with prefix only")
    @Test
    void parseMessageListWithPrefixOnly() {
        var expectedList = Arrays.asList("item01", "item02", "item03");
        final var delimiter = ",";
        final var prefix = "|";
        var joiner = new StringJoiner(delimiter);
        expectedList.forEach(joiner::add);
        var message = prefix + joiner;
        log.info("Message: {}", message);
        var resultList = MessageUtils.parseMessageList(message, delimiter, prefix, null);
        log.info("Parsed messages: {}", resultList);
        assertEquals(expectedList, resultList);
    }

    @DisplayName("Parse message list with suffix only")
    @Test
    void parseMessageListWithSuffixOnly() {
        var expectedList = Arrays.asList("item01", "item02", "item03");
        final var delimiter = ",";
        final var suffix = "|";
        var joiner = new StringJoiner(delimiter);
        expectedList.forEach(joiner::add);
        var message = joiner + suffix;
        log.info("Message: {}", message);
        var resultList = MessageUtils.parseMessageList(message, delimiter, null, suffix);
        log.info("Parsed messages: {}", resultList);
        assertEquals(expectedList, resultList);
    }

    @DisplayName("Parse paired message list without both prefix and suffix")
    @Test
    void parsePairedMessageListWithoutBothPrefixAddSuffix() {
        var expectedList = Arrays.asList(Pair.of("key01", "value01"), Pair.of("key02", "value02"), Pair.of("key03", "value03"));
        final var delimiter = ",";
        final var separator = ":";
        var joiner = new StringJoiner(delimiter);
        expectedList.stream()
                .map(pair -> pair.key + separator + pair.value)
                .forEach(joiner::add);
        var message = joiner.toString();
        log.info("Message: {}", message);
        var resultList = MessageUtils.parsePairedMessageList(message, delimiter, separator);
        log.info("Parsed paired messages: {}", resultList);
        assertEquals(expectedList, resultList);
    }

    @DisplayName("Parse paired message list with both prefix and suffix")
    @Test
    void parsePairedMessageListWithBothPrefixAndSuffix() {
        var expectedList = Arrays.asList(Pair.of("key01", "value01"), Pair.of("key02", "value02"), Pair.of("key03", "value03"));
        final var delimiter = ",";
        final var separator = ":";
        final var prefix = "[";
        final var suffix = "]";
        var joiner = new StringJoiner(delimiter);
        expectedList.stream()
                .map(pair -> pair.key + separator + pair.value)
                .forEach(joiner::add);
        var message = prefix + joiner + suffix;
        log.info("Message: {}", message);
        var resultList = MessageUtils.parsePairedMessageList(message, delimiter, separator, prefix, suffix);
        log.info("Parsed paired messages: {}", resultList);
        assertEquals(expectedList, resultList);
    }

    @DisplayName("Parse paired message list with prefix only")
    @Test
    void parsePairedMessageListWithPrefixOnly() {
        var expectedList = Arrays.asList(Pair.of("key01", "value01"), Pair.of("key02", "value02"), Pair.of("key03", "value03"));
        final var delimiter = ",";
        final var separator = ":";
        final var prefix = "|";
        var joiner = new StringJoiner(delimiter);
        expectedList.stream()
                .map(pair -> pair.key + separator + pair.value)
                .forEach(joiner::add);
        var message = prefix + joiner;
        log.info("Message: {}", message);
        var resultList = MessageUtils.parsePairedMessageList(message, delimiter, separator, prefix, null);
        log.info("Parsed paired messages: {}", resultList);
        assertEquals(expectedList, resultList);
    }

    @DisplayName("Parse paired message list with suffix only")
    @Test
    void parsePairedMessageListWithSuffixOnly() {
        var expectedList = Arrays.asList(Pair.of("key01", "value01"), Pair.of("key02", "value02"), Pair.of("key03", "value03"));
        final var delimiter = ",";
        final var separator = ":";
        final var suffix = "|";
        var joiner = new StringJoiner(delimiter);
        expectedList.stream()
                .map(pair -> pair.key + separator + pair.value)
                .forEach(joiner::add);
        var message = joiner + suffix;
        log.info("Message: {}", message);
        var resultList = MessageUtils.parsePairedMessageList(message, delimiter, separator, null, suffix);
        log.info("Parsed paired messages: {}", resultList);
        assertEquals(expectedList, resultList);
    }

}