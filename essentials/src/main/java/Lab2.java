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

import com.hazelcast.core.IMap;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.pipeline.Pipeline;

import java.util.Map;


public class Lab2 {

    private static final String LOOKUP_TABLE = "lookup-table";

    public static void main(String[] args) throws InterruptedException {
        JetInstance jet = Jet.newJetInstance();

        // ReplicatedMap<String, String> lookupTable = jet.getReplicatedMap(LOOKUP_TABLE);
        IMap<String, String> lookupTable = jet.getMap(LOOKUP_TABLE);
        lookupTable.put("A", "Trader A");
        lookupTable.put("B", "Trader B");
        lookupTable.put("C", "Trader C");

        Pipeline p = buildPipeline(lookupTable);

        try {
            Job job = jet.newJob(p);

            Thread.sleep(5000);

            lookupTable.put("A", "Trader A_A");
            lookupTable.put("B", "Trader B_B");
            lookupTable.put("C", "Trader C_C");

            job.join();
        } finally {
            jet.shutdown();
        }
    }

    private static Pipeline buildPipeline(IMap<String, String> lookupTable) {
        Pipeline p = Pipeline.create();

        // read from the Trade Source (sources.TradeSource) - it's custom source generating Trades (dto.Trade)
        // Use ingestion timestamps

        // Convert Trade (dto.Trade) into Enriched Trade (dto.EnrichedTrade)
        // - use the Trade ticker to look up the trader name in the IMap
        // -

        // drain to sink

        return p;
    }
}
