package org.thehive.hiveserverclient.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pair<K, V> {

    public final K key;
    public final V value;

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

}
