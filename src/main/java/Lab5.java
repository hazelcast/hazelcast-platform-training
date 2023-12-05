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

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import sources.TradeSource;

public class Lab5 {

    public static void main(String[] args) {
        Pipeline p = buildPipeline();

        HazelcastInstance hz = Hazelcast.bootstrappedInstance();
        JetService jet = hz.getJet();

        hz.getJet().newJob(p).join();
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        p.readFrom(TradeSource.tradeSource(1))
          .withNativeTimestamps(0 )

         // Detect if price between two consecutive trades drops by more than 200

         // Use the mapStateful to keep price of previous Trade
         // - Consider using com.hazelcast.jet.accumulator.LongAccumulator as a mutable container for long values
         // - Return the price difference if drop is detected, nothing otherwise

         .writeTo(Sinks.logger( m -> "Price drop: " + m));

        return p;
    }
}