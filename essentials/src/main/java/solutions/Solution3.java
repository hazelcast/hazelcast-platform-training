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

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.Util;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import eventlisteners.TradeListener;
import sources.TradeSource;


public class Solution3 {

    private static final String LATEST_TRADES_PER_SYMBOL = "trades" ;

    public static void main (String[] args) {
        Pipeline p = buildPipeline();

        HazelcastInstance hz = Hazelcast.bootstrappedInstance();
        JetService jet = hz.getJet();

        hz.getMap(LATEST_TRADES_PER_SYMBOL)
           .addEntryListener(new TradeListener(), true);

        hz.getJet().newJob(p).join();
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        p.readFrom(TradeSource.tradeSource())
         .withNativeTimestamps(0)
         .map(trade -> Util.entry(trade.getSymbol(), trade))
         .writeTo(Sinks.map(LATEST_TRADES_PER_SYMBOL));

        return p;
    }


}
