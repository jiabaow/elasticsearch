/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.plugins.spi;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SPIClassIteratorTest {
    @Test
    public void testIsParentClassLoader() {
        ClassLoader parentClassLoader = new ClassLoader() {};
        ClassLoader childClassLoader = new CustomClassLoader(parentClassLoader);

        assertTrue(SPIClassIterator.isParentClassLoader(parentClassLoader, childClassLoader));

        ClassLoader grandchildClassLoader = new CustomClassLoader(childClassLoader);
        assertTrue(SPIClassIterator.isParentClassLoader(parentClassLoader, grandchildClassLoader));

        ClassLoader unrelatedClassLoader = new ClassLoader() {};
        assertFalse(SPIClassIterator.isParentClassLoader(parentClassLoader, unrelatedClassLoader));
    }

    // Custom ClassLoader for testing
    static class CustomClassLoader extends ClassLoader {
        CustomClassLoader(ClassLoader parent) {
            super(parent);
        }
    }
}
