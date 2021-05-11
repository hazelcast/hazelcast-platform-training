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

import com.hazelcast.map.IMap;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import dto.EnrichedTrade;
import dto.Trade;
import sources.TradeSource;

public class Lab4 {

    private static final String LOOKUP_TABLE = "lookup-table";

    public static void main(String[] args) {
        JetInstance jet = Jet.bootstrappedInstance();

        // symbol -> company name
        // random symbols from https://www.nasdaq.com
        IMap<String, String> lookupTable = jet.getHazelcastInstance().getMap(LOOKUP_TABLE);
        lookupTable.put("AAPL", "Apple Inc. - Common Stock");
        lookupTable.put("GOOGL", "Alphabet Inc.");
        lookupTable.put("MSFT", "Microsoft Corporation");

        Pipeline p = buildPipeline(lookupTable);

        try {
            jet.newJob(p).join();
        } finally {
            jet.shutdown();
        }
    }

    private static Pipeline buildPipeline(IMap<String, String> lookupTable) {
        Pipeline p = Pipeline.create();

        p.readFrom(TradeSource.tradeSource())
         .withoutTimestamps()

        // Convert Trade stream to EnrichedTrade stream
        // - Trade (dto.Trade) has a symbol field
        // - Use LOOKUP_TABLE to look up full company name based on the symbol
        // - Create new Enriched Trade (dto.EnrichedTrade) using Trade and company name

        .writeTo(Sinks.logger());

        return p;
    }
}
