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

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Util;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import eventlisteners.TradeListener;
import sources.TradeSource;

public class Lab3 {

    private static final String LATEST_TRADES_PER_SYMBOL = "trades" ;

    public static void main (String[] args) {
        Pipeline p = buildPipeline();

        JetInstance jet = Jet.bootstrappedInstance();

        // Subscribe for map events
        jet.getHazelcastInstance().getMap(LATEST_TRADES_PER_SYMBOL)
           .addEntryListener(new TradeListener(), true);

        try {
            jet.newJob(p).join();
        } finally {
            jet.shutdown();
        }
    }

    private static Pipeline buildPipeline() {

        Pipeline p = Pipeline.create();

        p.readFrom(TradeSource.tradeSource())
         .withNativeTimestamps(0);

         // Transform Trade events to  map entries with
         // the Trade symbol as the key and the trade itself as a value
         // Use Util.entry as an implementation of java.util.Map.Entry

         // .map(trade -> Util.entry( , ))

         // Write the entry stream to LATEST_TRADES_PER_SYMBOL map


        return p;
        // Stop the job

    }
}
