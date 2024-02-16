/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.client.internal.transport;

import org.elasticsearch.rest.RestStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NoNodeAvailableExceptionTest {
    @Test
    public void testConstructorWithMessage() {
        String errorMessage = "Test error message";
        NoNodeAvailableException exception = new NoNodeAvailableException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testConstructorWithMessageAndThrowable() {
        String errorMessage = "Test error message";
        Throwable cause = new RuntimeException("Test cause");
        NoNodeAvailableException exception = new NoNodeAvailableException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testStatus() {
        NoNodeAvailableException exception = new NoNodeAvailableException("Test error message");

        assertEquals(RestStatus.SERVICE_UNAVAILABLE, exception.status());
    }

}
