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

import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.map.IMap;
import dto.Trade;
import eventlisteners.TradeListener;
import sources.TradeSource;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Lab3 {

    private static final String INPUT_MAP = "trades";
    private static final String OUTPUT_MAP = "latest_trade";

    // assuming a 1 tps input event rate, we will configure the event journal to hold 24 hours of events
    // after that time, the older events will be lost
    private static final int EVENT_JOURNAL_CAPACITY = 24 * 3600;

    public static void main (String[] args) {
        Pipeline p = buildPipeline();

        HazelcastInstance hz = Hazelcast.bootstrappedInstance();

        // configure the input map to have an event journal
        EventJournalConfig eventJournalConfig =
                new EventJournalConfig().setEnabled(true).setCapacity(EVENT_JOURNAL_CAPACITY);
        MapConfig inputMapConfig = new MapConfig().setName(INPUT_MAP).setEventJournalConfig(eventJournalConfig);
        hz.getConfig().addMapConfig(inputMapConfig);

        // listen to the output map
        // note that this same code will work from a client
        hz.getMap(OUTPUT_MAP).addEntryListener(new TradeListener(), true);

        // start sending trades to the input map
        IMap<Integer, Trade> inputMap = hz.getMap(INPUT_MAP);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new TradeSource(inputMap),0,1, TimeUnit.SECONDS);

        // start a pipeline to move data between the two
        hz.getJet().newJob(p);
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        // TODO: read events from the INPUT_MAP map journal, starting from the oldest.
        // use the timestamp in the event and allow 5000ms for late arrivals
        StreamStage<Map.Entry<Integer, Trade>> trades = null;

        // TODO: map the Map.Entry<Integer, Trade> with sequence number key to a Tuple2<String,Trade>
        // with symbol key
        StreamStage<Tuple2<String, Trade>> tradesBySymbol = null;

        // TODO: write the result to the output map

        return p;
    }

}
