/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.common.logging;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.elasticsearch.test.ESTestCase;
import org.junit.After;
import org.junit.Before;

import static org.apache.logging.log4j.core.Filter.Result;
import static org.hamcrest.Matchers.equalTo;

public class RateLimitingFilterTests extends ESTestCase {

    private RateLimitingFilter filter;

    @Before
    public void setup() {
        this.filter = new RateLimitingFilter();
        filter.start();
    }

    /**
     * Partitioning Test For RateLimitingFilter
     *
     *
     * Because RateLimitingFilter works by using a lruKeyCache
     * and the lruKeyCache has a size limited to 128,
     * which when breached will remove the oldest entries.
     *
     * So, we define our partitioning as "limit(128)" of lruKeyCache
     * We design 2 partitions:
     * Also, we add some values of boundaries for our partition: 127(smaller than 1), 128(equal), 129(greater than 1)
     *
     * For 2 partitions, the expected behaviors are:
     * for partition type 1 (choose 50 as input)
     * because cache store below limit size,
     * if we access key0, this key still in the cache, we will get deny message(rate limit)
     *
     * for partition type 2 (choose 150 as input)
     * because cache store has already exceeded the limit size,
     * the key0 will be evicted, if we access key0, this key will not in the cache, it will be allowed, we will get accept message
     *
     * For boundary values:
     *
     * boundary value 127
     * because cache store below limit size,
     * if we access key0, this key still in the cache, we will get deny message
     *
     * boundary value 128
     * because cache store equal the limit size,
     * if we access key0, this key still in the cache, we will get deny message
     *
     * boundary value 129
     * because cache store has already exceeded the limit size,
     * the key0 will be evicted, if we access key0, this key will not in the cache, it will be allowed, we will get accept message
     */

    public void testBelowLimit() {
        // construct the input
        for (int i = 0; i < 50; i++) {
            Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key " + i, "", "", "msg " + i);
            assertThat("Expected key" + i + " to be accepted", filter.filter(message), equalTo(Result.ACCEPT));
        }

        // Should be rate-limited because it's still in the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "", "", "msg " + 0);
        assertThat(filter.filter(message), equalTo(Result.DENY));
    }


    public void testAboveLimit() {
        // construct the input
        for (int i = 0; i < 150; i++) {
            Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key " + i, "", "", "msg " + i);
            assertThat("Expected key" + i + " to be accepted", filter.filter(message), equalTo(Result.ACCEPT));
        }

        // Should be allowed because key0 was evicted from the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "", "", "msg " + 0);
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));
    }


    public void testBoundaryValue127() {
        // construct the input of boundary value1 : 127
        for (int i = 0; i < 127; i++) {
            Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key " + i, "", "", "msg " + i);
            assertThat("Expected key" + i + " to be accepted", filter.filter(message), equalTo(Result.ACCEPT));
        }
        // Should be rate-limited because it's still in the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "", "", "msg " + 0);
        assertThat(filter.filter(message), equalTo(Result.DENY));
    }

    public void testBoundaryValue128() {
        // construct the input of boundary value1 : 128
        for (int i = 0; i < 128; i++) {
            Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key " + i, "", "", "msg " + i);
            assertThat("Expected key" + i + " to be accepted", filter.filter(message), equalTo(Result.ACCEPT));
        }
        // Should be rate-limited because it's still in the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "", "", "msg " + 0);
        assertThat(filter.filter(message), equalTo(Result.DENY));
    }

    public void testBoundaryValue129() {
        // construct the input of boundary value1 : 129
        for (int i = 0; i < 129; i++) {
            Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key " + i, "", "", "msg " + i);
            assertThat("Expected key" + i + " to be accepted", filter.filter(message), equalTo(Result.ACCEPT));
        }
        // Should be allowed because key0 was evicted from the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "", "", "msg " + 0);
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));
    }







    @After
    public void cleanup() {
        this.filter.stop();
    }

    /**
     * Check that messages are rate-limited by their key.
     */
    public void testMessagesAreRateLimitedByKey() {
        // Fill up the cache
        for (int i = 0; i < 128; i++) {
            Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key " + i, "", "", "msg " + i);
            assertThat("Expected key" + i + " to be accepted", filter.filter(message), equalTo(Result.ACCEPT));
        }

        // Should be rate-limited because it's still in the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "", "", "msg " + 0);
        assertThat(filter.filter(message), equalTo(Result.DENY));

        // Filter a message with a previously unseen key, in order to evict key0 as it's the oldest
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 129", "", "", "msg " + 129);
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));

        // Should be allowed because key0 was evicted from the cache
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "", "", "msg " + 0);
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));
    }

    /**
     * Check that messages are rate-limited by their x-opaque-id value
     */
    public void testMessagesAreRateLimitedByXOpaqueId() {
        // Fill up the cache
        for (int i = 0; i < 128; i++) {
            Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "", "id " + i, "", "msg " + i);
            assertThat("Expected key" + i + " to be accepted", filter.filter(message), equalTo(Result.ACCEPT));
        }

        // Should be rate-limited because it's still in the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "", "id 0", "", "msg 0");
        assertThat(filter.filter(message), equalTo(Result.DENY));

        // Filter a message with a previously unseen key, in order to evict key0 as it's the oldest
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "", "id 129", "", "msg 129");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));

        // Should be allowed because key0 was evicted from the cache
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "", "id 0", "", "msg 0");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));
    }

    /**
     * Check that messages are rate-limited by their key and x-opaque-id value
     */
    public void testMessagesAreRateLimitedByKeyAndXOpaqueId() {
        // Fill up the cache
        for (int i = 0; i < 128; i++) {
            Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key " + i, "opaque-id " + i, null, "msg " + i);
            assertThat("Expected key" + i + " to be accepted", filter.filter(message), equalTo(Result.ACCEPT));
        }

        // Should be rate-limited because it's still in the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "opaque-id 0", null, "msg 0");
        assertThat(filter.filter(message), equalTo(Result.DENY));

        // Filter a message with a previously unseen key, in order to evict key0 as it's the oldest
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 129", "opaque-id 129", null, "msg 129");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));

        // Should be allowed because key 0 was evicted from the cache
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "opaque-id 0", null, "msg 0");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));
    }

    /**
     * Check that it is the combination of key and x-opaque-id that rate-limits messages, by varying each
     * independently and checking that a message is not filtered.
     */
    public void testVariationsInKeyAndXOpaqueId() {
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "opaque-id 0", null, "msg 0");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));

        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "opaque-id 0", null, "msg 0");
        // Rejected because the "x-opaque-id" and "key" values are the same as above
        assertThat(filter.filter(message), equalTo(Result.DENY));

        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 1", "opaque-id 0", null, "msg 0");
        // Accepted because the "key" value is different
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));

        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "opaque-id 1", null, "msg 0");
        // Accepted because the "x-opaque-id" value is different
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));
    }

    /**
     * Check that rate-limiting is not applied to messages if they are not an EsLogMessage.
     */
    public void testOnlyEsMessagesAreFiltered() {
        Message message = new SimpleMessage("a message");
        assertThat(filter.filter(message), equalTo(Result.NEUTRAL));
    }

    /**
     * Check that the filter can be reset, so that previously-seen keys are treated as new keys.
     */
    public void testFilterCanBeReset() {
        final Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key", "", "", "msg");

        // First time, the message is a allowed
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));

        // Second time, it is filtered out
        assertThat(filter.filter(message), equalTo(Result.DENY));

        filter.reset();

        // Third time, it is allowed again
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));
    }

    public void testMessagesXOpaqueIsIgnoredWhenDisabled() {
        RateLimitingFilter filter = new RateLimitingFilter();
        filter.setUseXOpaqueId(false);
        filter.start();

        // Should NOT be rate-limited because it's not in the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "opaque-id 0", null, "msg 0");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));

        // Should be rate-limited because it was just added to the cache
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "opaque-id 0", null, "msg 0");
        assertThat(filter.filter(message), equalTo(Result.DENY));

        // Should be rate-limited because X-Opaque-Id is not used
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 0", "opaque-id 1", null, "msg 0");
        assertThat(filter.filter(message), equalTo(Result.DENY));

        // Should NOT be rate-limited because "key 1" it not in the cache
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key 1", "opaque-id 1", null, "msg 0");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));
    }

    public void testXOpaqueIdNotBeingUsedFromElasticOriginatingRequests() {
        RateLimitingFilter filter = new RateLimitingFilter();
        filter.setUseXOpaqueId(true);
        filter.start();

        // Should NOT be rate-limited because it's not in the cache
        Message message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key", "opaque-id 0", "kibana", "msg 0");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));

        // Should be rate-limited even though the x-opaque-id is unique because it originates from kibana
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key", "opaque-id 1", "kibana", "msg 0");
        assertThat(filter.filter(message), equalTo(Result.DENY));

        // Should not be rate-limited - it is the first request from beats. (x-opaque-id ignored as it originates from elastic)
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key", "opaque-id 0", "beats", "msg 0");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));

        // second request from beats (elastic originating), should be rate-limited
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key", "opaque-id 1", "beats", "msg 0");
        assertThat(filter.filter(message), equalTo(Result.DENY));

        // request from beats (elastic originating), but with a different key- should not be rate-limited
        message = DeprecatedMessage.of(DeprecationCategory.OTHER, "key2", "opaque-id 1", "beats", "msg 1");
        assertThat(filter.filter(message), equalTo(Result.ACCEPT));
    }
}
