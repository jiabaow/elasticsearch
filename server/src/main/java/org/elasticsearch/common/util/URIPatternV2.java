/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.common.util;
import org.elasticsearch.common.regex.Regex;
import java.net.URI;

public class URIPatternV2 {
    private final URI uriPattern;

    /**
     * Constructs a URIPattern with a URI object.
     * This constructor allows for dependency injection of URI, enhancing testability.
     *
     * @param uriPattern The URI object representing the pattern.
     */
    public URIPatternV2(URI uriPattern) {
        this.uriPattern = uriPattern;
    }


    /**
     * Matches the given URI against this pattern.
     *
     * @param uri The URI to match.
     * @return true if the URI matches the pattern, false otherwise.
     */
    public boolean match(URI uri) {
        return matchNormalized(uri.normalize());
    }

    private boolean matchNormalized(URI uri) {
        if (uriPattern.isOpaque()) {
            return uri.isOpaque()
                && match(uriPattern.getScheme(), uri.getScheme())
                && match(uriPattern.getSchemeSpecificPart(), uri.getSchemeSpecificPart())
                && match(uriPattern.getFragment(), uri.getFragment());
        } else {
            return match(uriPattern.getScheme(), uri.getScheme())
                && match(uriPattern.getAuthority(), uri.getAuthority())
                && match(uriPattern.getPath(), uri.getPath())
                && match(uriPattern.getQuery(), uri.getQuery())
                && match(uriPattern.getFragment(), uri.getFragment());
        }
    }

    private static boolean match(String pattern, String value) {
        if (value == null) {
            // If the pattern is empty or matches anything - it's a match
            if (pattern == null || Regex.isMatchAllPattern(pattern)) {
                return true;
            }
        }
        return Regex.simpleMatch(pattern, value);
    }
}
