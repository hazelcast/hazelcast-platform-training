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

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import dto.Trade;
import eventlisteners.TradeListener;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TradeSource implements Runnable {

    private static final List<String> SYMBOLS = Arrays.asList("AAPL", "GOOGL", "MSFT");

    public TradeSource(IMap<Integer, Trade> map){
        this.map = map;
        rnd = new Random();
    }

    private final IMap<Integer, Trade> map;
    private final Random rnd;
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

    // assuming a 1 tps input event rate, we will configure the event journal to hold 24 hours of events
    // after that time, the older events will be lost
    private static final int EVENT_JOURNAL_CAPACITY = 24 * 3600;
    private static final String INPUT_MAP = "trades";

    public static void main(String []args){
        HazelcastInstance hz = HazelcastClient.newHazelcastClient(); // use default configuration mechanism

        // configure the input map to have an event journal
        // note that we are configuring the map journal on the server even though hz is a client
        EventJournalConfig eventJournalConfig =
                new EventJournalConfig().setEnabled(true).setCapacity(EVENT_JOURNAL_CAPACITY);
        MapConfig inputMapConfig = new MapConfig().setName(INPUT_MAP).setEventJournalConfig(eventJournalConfig);
        hz.getConfig().addMapConfig(inputMapConfig);

        // start sending trades to the input map
        IMap<Integer, Trade> inputMap = hz.getMap(INPUT_MAP);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new TradeSource(inputMap),0,1, TimeUnit.SECONDS);

        // the program will not exit because both the Hazelcast client and the ScheduledExecutor have daemon threads
    }
}