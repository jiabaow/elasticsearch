/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.common.util;

import org.elasticsearch.test.ESTestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

public class CopyOnFirstWriteMapTests extends ESTestCase {

    @Test
    public void testMapIsEmptyMethod() {
        var source = new HashMap<String, String>();
        var copyOnFirstWrite = new CopyOnFirstWriteMap<>(source);
        assertTrue(copyOnFirstWrite.isEmpty());
    }
    @Test
    public void testMapSizeMethod() {
        var source = new HashMap<String, String>();
        var copyOnFirstWrite = new CopyOnFirstWriteMap<>(source);
        int size = copyOnFirstWrite.size();
        assertEquals(0, size);
    }

    public void testMapContainsKeyMethod() {
        var source = new HashMap<String, String>();
        var copyOnFirstWrite = new CopyOnFirstWriteMap<>(source);
        copyOnFirstWrite.put("key1", "value1");
        assertTrue(copyOnFirstWrite.containsKey("key1"));
    }


    public void testMapContainsValueMethod() {
        var source = new HashMap<String, String>();
        var copyOnFirstWrite = new CopyOnFirstWriteMap<>(source);
        copyOnFirstWrite.put("key1", "value1");
        assertTrue(copyOnFirstWrite.containsValue("value1"));
    }



    public void testShouldNotCopyIfThereWereNoUpdates() {
        var source = Map.of("key", "value");
        var copyOnFirstWrite = new CopyOnFirstWriteMap<>(source);
        var copy = copyOnFirstWrite.toImmutableMap();

        assertThat(copy, sameInstance(source));
        assertThat(copy, equalTo(source));
    }

    public void testShouldNotCopyWhenPuttingTheSameValue() {
        var source = Map.of("key", "value");
        var copyOnFirstWrite = new CopyOnFirstWriteMap<>(source);
        copyOnFirstWrite.put("key", "value");
        var copy = copyOnFirstWrite.toImmutableMap();

        assertThat(copy, sameInstance(source));
        assertThat(copy, equalTo(source));
    }

    public void testShouldBeUpdatable() {
        var source = Map.of("key", "value");
        var copyOnFirstWrite = new CopyOnFirstWriteMap<>(source);
        copyOnFirstWrite.put("key", "new_value");
        var copy = copyOnFirstWrite.toImmutableMap();

        assertThat(copy, not(sameInstance(source)));
        assertThat(copy, equalTo(Map.of("key", "new_value")));
    }
}
