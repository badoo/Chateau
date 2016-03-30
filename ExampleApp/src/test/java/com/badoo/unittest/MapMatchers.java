package com.badoo.unittest;

import org.hamcrest.Matcher;
import org.hamcrest.collection.IsMapContaining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;

public class MapMatchers {

    public static <K, V> Matcher<Map<K, V>> matchesEntriesIn(Map<K, V> map) {
        return allOf(buildMatcherArray(map));
    }

    public static <K, V> Matcher<Map<K, V>> matchesAnyEntryIn(Map<K, V> map) {
        return anyOf(buildMatcherArray(map));
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Matcher<Map<? extends K, ? extends V>>[] buildMatcherArray(Map<K, V> map) {
        List<Matcher<Map<? extends K, ? extends V>>> entries = new ArrayList<>();
        for (K key : map.keySet()) {
            entries.add(IsMapContaining.hasEntry(key, map.get(key)));
        }
        return entries.toArray(new Matcher[entries.size()]);
    }
}
