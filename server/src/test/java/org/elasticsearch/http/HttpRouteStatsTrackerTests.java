/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.http;


import org.junit.Test;

import static org.elasticsearch.test.ESTestCase.assertThat;
import static org.hamcrest.CoreMatchers.is;


public class HttpRouteStatsTrackerTests {
    @Test
    public void initialTrackerIsEmpty() {
        HttpRouteStatsTracker tracker = new HttpRouteStatsTracker();
        assertThat(tracker.getStats().requestCount(), is(0L));
    }

    @Test
    public void testAddRequestStats() {
        HttpRouteStatsTracker tracker = new HttpRouteStatsTracker();
        tracker.addRequestStats(1);
        assertThat(tracker.getStats().requestCount(), is(1L));
        assertThat(tracker.getStats().totalRequestSize(), is(1L));
    }

    @Test
    public void testAddResponseStats() {
        HttpRouteStatsTracker tracker = new HttpRouteStatsTracker();
        tracker.addResponseStats(1);
        assertThat(tracker.getStats().responseCount(), is(1L));
        assertThat(tracker.getStats().totalResponseSize(), is(1L));
    }


}
