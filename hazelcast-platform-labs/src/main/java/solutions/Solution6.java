/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solutions;

import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.pipeline.*;
import com.hazelcast.map.IMap;
import dto.Trade;
import sources.TradeSource;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Solution6 {

    private static final String INPUT_MAP = "trades";

    // assuming a 1 tps input event rate, we will configure the event journal to hold 24 hours of events
    // after that time, the older events will be lost
    private static final int EVENT_JOURNAL_CAPACITY = 24 * 3600;

    public static void main (String[] args) {

        HazelcastInstance hz = Hazelcast.bootstrappedInstance();

        // configure the input map to have an event journal
        EventJournalConfig eventJournalConfig =
                new EventJournalConfig().setEnabled(true).setCapacity(EVENT_JOURNAL_CAPACITY);
        MapConfig inputMapConfig = new MapConfig().setName(INPUT_MAP).setEventJournalConfig(eventJournalConfig);
        hz.getConfig().addMapConfig(inputMapConfig);

        // start sending trades to the input map
        IMap<Integer, Trade> inputMap = hz.getMap(INPUT_MAP);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new TradeSource(inputMap),0,1, TimeUnit.SECONDS);

        Pipeline p = buildPipeline();
        hz.getJet().newJob(p);
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        // read events from the INPUT_MAP map journal, starting from the oldest.
        // use the timestamp in the event and allow 5000ms for late arrivals
        StreamStage<Map.Entry<Integer, Trade>> tradeEntries =
                p.readFrom(Sources.<Integer, Trade>mapJournal(INPUT_MAP, JournalInitialPosition.START_FROM_OLDEST))
                        .withTimestamps(entry -> entry.getValue().getTime(), 5000);

        // compute trade volume by symbol in 3s tumbling windows
        tradeEntries.groupingKey(entry -> entry.getValue().getSymbol())
                .window(WindowDefinition.tumbling(3000).setEarlyResultsPeriod(1000))
                .aggregate(AggregateOperations.summingLong(entry -> entry.getValue().getQuantity()))
                .writeTo(Sinks.logger());

        return p;
    }
}
