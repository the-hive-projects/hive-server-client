package org.thehive.hiveserverclient.util;

import lombok.AllArgsConstructor;
import lombok.With;

@AllArgsConstructor
@With
public class Pair<K,V> {

    public final K key;
    public final V value;

}
