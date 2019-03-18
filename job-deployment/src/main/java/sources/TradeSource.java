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

package sources;

import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;
import dto.Trade;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

public class TradeSource {

    public static StreamSource<Trade> tradeSource(List<String> tickers, int tradesPerSec) {
        return SourceBuilder.timestampedStream("trade-source", ctx -> {
            Map<Integer, List<String>> partitions = partitionTickers(tickers, ctx.totalParallelism());
            return new TradeGenerator(partitions.get(ctx.globalProcessorIndex()), tradesPerSec);
        })
                .fillBufferFn(TradeGenerator::fillBuffer)
                .distributed(1)
                .build();
    }

    public static Map<Integer, List<String>> partitionTickers(List<String> tickers, int numPartitions) {
        return tickers.stream().collect(Collectors.groupingBy(t -> t.hashCode() % numPartitions));
    }

    private static class TradeGenerator {

        private final List<String> tickers;
        private final int tradesPerSec;

        private static final int QUANTITY = 100;

        TradeGenerator(List<String> tickers, int tradesPerSec) {
            this.tickers = tickers;
            this.tradesPerSec = tradesPerSec;
        }

        void fillBuffer(SourceBuilder.TimestampedSourceBuffer<Trade> buffer) {
            ThreadLocalRandom rnd = ThreadLocalRandom.current();

            for (int i = 0; i < tradesPerSec; i++) {
                String ticker = tickers.get(rnd.nextInt(tickers.size()));
                long tradeTime = System.currentTimeMillis();
                Trade trade = new Trade(tradeTime, ticker, QUANTITY, rnd.nextInt(5000));
                buffer.add(trade, tradeTime);
            }

            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1)); // sleep for 1 second
        }
    }
}