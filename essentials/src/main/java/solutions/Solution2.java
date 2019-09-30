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

import com.hazelcast.core.IMap;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import dto.EnrichedTrade;
import dto.Trade;
import sources.TradeSource;


public class Solution2 {

    private static final String LOOKUP_TABLE = "lookup-table" ;

    public static void main (String[] args) {
        JetInstance jet = Jet.newJetInstance();

        // symbol -> company name
        IMap<String, String> lookupTable = jet.getMap(LOOKUP_TABLE);
        lookupTable.put("AAPL", "Apple Inc. - Common Stock");
        lookupTable.put("GOOGL", "Alphabet Inc.");
        lookupTable.put("MSFT", "Microsoft Corporation");

        Pipeline p = buildPipeline();
        try {
            jet.newJob(p).join();
        } finally {
            jet.shutdown();
        }
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        p.drawFrom(TradeSource.tradeSource())
                .withNativeTimestamps(0)
                .mapUsingIMap(LOOKUP_TABLE, Trade::getSymbol,
                        (trade, companyName) -> new EnrichedTrade(trade, companyName.toString()) )
                .drainTo(Sinks.logger());

        return p;
    }
}
