/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.common.util;

import org.junit.Test;
import java.net.URI;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class URIPatternTestsV2 {

    @Test
    public void testMatchExact() throws Exception {
        URI uriPattern = new URI("http://test.local/somepath");
        URI uriToMatch = new URI("http://test.local/somepath");
        URIPatternV2 uriPatternObj = new URIPatternV2(uriPattern);

        assertTrue(uriPatternObj.match(uriToMatch));
    }

    @Test
    public void testMatchDifferentScheme() throws Exception {
        URI uriPattern = new URI("http://test.local/somepath");
        URI uriToMatch = new URI("https://test.local/somepath");
        URIPatternV2 uriPatternObj = new URIPatternV2(uriPattern);

        assertFalse(uriPatternObj.match(uriToMatch));
    }

    @Test
    public void testMatchDifferentPath() throws Exception {
        URI uriPattern = new URI("http://test.local/somepath");
        URI uriToMatch = new URI("http://test.local/anotherpath");
        URIPatternV2 uriPatternObj = new URIPatternV2(uriPattern);

        assertFalse(uriPatternObj.match(uriToMatch));
    }

    @Test
    public void testMatchWildcardPath() throws Exception {
        URI uriPattern = new URI("http://test.local/*");
        URI uriToMatch = new URI("http://test.local/somepath/anotherpath");
        URIPatternV2 uriPatternObj = new URIPatternV2(uriPattern);

        // Assuming the match method can handle wildcards, this should return true.
        // This will require additional logic in the match method to handle wildcards properly.
        assertTrue(uriPatternObj.match(uriToMatch));
    }

    @Test
    public void testMatchOpaqueURI() throws Exception {
        URI uriPattern = new URI("mailto:user@example.com");
        URI uriToMatch = new URI("mailto:user@example.com");
        URIPatternV2 uriPatternObj = new URIPatternV2(uriPattern);

        assertTrue(uriPatternObj.match(uriToMatch));
    }
}
