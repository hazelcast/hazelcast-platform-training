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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class TradeSource {

    private static final List<String> SYMBOLS = Arrays.asList("AAPL", "GOOGL", "MSFT");

    public static StreamSource<Trade> tradeSource() {
        return tradeSource(1);
    }

    public static StreamSource<Trade> tradeSource(int tradesPerSec) {
        return SourceBuilder.timestampedStream("trade-source", x -> new TradeGenerator(SYMBOLS, tradesPerSec))
                .fillBufferFn(TradeGenerator::fillBuffer)
                .build();
    }

    private static class TradeGenerator {

        private final List<String> symbols;
        private final int tradesPerSec;

        private static final int QUANTITY = 100;

        TradeGenerator(List<String> symbols, int tradesPerSec) {
            this.symbols = symbols;
            this.tradesPerSec = tradesPerSec;
        }

        void fillBuffer(SourceBuilder.TimestampedSourceBuffer<Trade> buffer) {
            ThreadLocalRandom rnd = ThreadLocalRandom.current();

            for (int i = 0; i < tradesPerSec; i++) {
                String ticker = symbols.get(rnd.nextInt(symbols.size()));
                long tradeTime = System.currentTimeMillis();
                Trade trade = new Trade(tradeTime, ticker, QUANTITY, rnd.nextInt(5000));
                buffer.add(trade, tradeTime);
            }

            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1)); // sleep for 1 second
        }
    }
}