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

        private static final int MAX_BATCH_SIZE = 16*1024;

        private final List<String> tickers;
        private final int tradesPerSec;
        private final Map<String, Integer> tickerToPrice;
        private long emitSchedule;

        private static final int QUANTITY = 100;

        TradeGenerator(List<String> tickers, int tradesPerSec) {
            this.tickers = tickers;
            this.tickerToPrice  = tickers.stream().collect(Collectors.toMap(t -> t, t -> 2500));
            this.tradesPerSec = tradesPerSec;
            this.emitSchedule = System.nanoTime();
        }

        void fillBuffer(SourceBuilder.TimestampedSourceBuffer<Trade> buffer) {
            long interval = TimeUnit.SECONDS.toNanos(1) / tradesPerSec;
            ThreadLocalRandom rnd = ThreadLocalRandom.current();
            for (int i = 0; i < MAX_BATCH_SIZE; i++) {
                if (System.nanoTime() < emitSchedule) {
                    break;
                }
                String ticker = tickers.get(rnd.nextInt(tickers.size()));
                int price = tickerToPrice.compute(ticker, (t, v) -> v + rnd.nextInt(-1, 2));
                Trade trade = new Trade(System.currentTimeMillis(), ticker, QUANTITY, price);
                buffer.add(trade, trade.getTime());
                emitSchedule += interval;
            }
        }
    }
}