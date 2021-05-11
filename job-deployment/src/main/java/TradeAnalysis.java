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
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.config.ProcessingGuarantee;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.WindowDefinition;
import dto.Trade;
import sources.TradeSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static com.hazelcast.jet.Util.entry;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class TradeAnalysis {

    public static final int TRADES_PER_SEC = 100_000;

    public static void main(String[] args) {
        Pipeline p = buildPipeline();

        JetInstance jet = Jet.bootstrappedInstance();

        try {
            JobConfig jobConfig = new JobConfig()
                    .setAutoScaling(true)
                    .setName("TradeAnalysis")
                    .setProcessingGuarantee(ProcessingGuarantee.EXACTLY_ONCE);

            jet.newJob(p, jobConfig).join();
        } finally {
            Hazelcast.shutdownAll();
        }
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        p.readFrom(TradeSource.tradeSource(loadTickers(), TRADES_PER_SEC))
                .withNativeTimestamps(0)
                .groupingKey(Trade::getTicker)
                .window(WindowDefinition.sliding(60_000, 1_000))
                .aggregate(AggregateOperations.averagingLong(Trade::getPrice))
                .map(res -> entry(res.getKey(), res))
                .writeTo(Sinks.map("prices"));

        return p;
    }

    private static List<String> loadTickers() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                TradeAnalysis.class.getResourceAsStream("/nasdaqlisted.txt"), UTF_8))
        ) {
            return reader.lines()
                    .skip(1)
                    .map(l -> l.split("\\|")[0])
                    .collect(toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
