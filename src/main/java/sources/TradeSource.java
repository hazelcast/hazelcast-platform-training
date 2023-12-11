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

import com.hazelcast.map.IMap;
import dto.Trade;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TradeSource implements Runnable {

    private static final List<String> SYMBOLS = Arrays.asList("AAPL", "GOOGL", "MSFT");

    public TradeSource(IMap<Integer, Trade> map){
        this.map = map;
        rnd = new Random();
    }

    private IMap<Integer, Trade> map;
    private Random rnd;
    private int tradeId = 1001;

    @Override
    public void run() {
        String ticker = SYMBOLS.get(rnd.nextInt(SYMBOLS.size()));
        long tradeTime = System.currentTimeMillis();
        int quantity = rnd.nextInt(1000);
        int price = rnd.nextInt(5000);
        Trade trade = new Trade(tradeTime, ticker, quantity, price);
        map.put(tradeId++, trade);
    }
}