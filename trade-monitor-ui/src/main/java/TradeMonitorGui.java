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

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.datamodel.KeyedWindowResult;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * Displays a live bar chart of each stock and its current trading volume
 * on the simulated stock exchange.
 */
public class TradeMonitorGui {

    private static final Set<String> TICKERS = new HashSet<>(Arrays.asList("MSFT", "FB", "GOOG", "AAPL", "AMZN"));
    private static final int WINDOW_X = 100;
    private static final int WINDOW_Y = 100;
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 650;
    private UUID listenerId;

    private IMap<String, KeyedWindowResult<String, Double>> avgPrices;

    public TradeMonitorGui(IMap<String, KeyedWindowResult<String, Double>> avgPrices) {
        this.avgPrices = avgPrices;
        EventQueue.invokeLater(this::startGui);
    }

    private void startGui() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        createChartFrame(dataset);
        EntryAddedUpdatedListener<String, KeyedWindowResult<String, Double>> listener = (key, value) -> {
            if (!TICKERS.contains(key)) {
                return;
            }
            Long ts = value.end();
            double val = value.result() / 100.0;
            int idx = dataset.getSeriesIndex(key);
            XYSeries series;
            if (idx == -1) {
                series = new XYSeries(key, true, false);
                dataset.addSeries(series);
            } else {
                series = dataset.getSeries(idx);
            }
            series.addOrUpdate((long)ts, val);
        };
        this.listenerId = avgPrices.addEntryListener(listener, true);
    }

    private XYPlot createChartFrame(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Average Stock Price over 1 minute", "Time", "Price in USD", dataset,
                true, true, false
        );
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);

        final JFrame frame = new JFrame();
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setTitle("Trade Monitor");
        frame.setBounds(WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLayout(new BorderLayout());
        frame.add(new ChartPanel(chart));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                avgPrices.removeEntryListener(listenerId);
            }
        });
        frame.setVisible(true);
        return plot;
    }

    @FunctionalInterface
    interface EntryAddedUpdatedListener<K, V> extends EntryAddedListener<K, V>, EntryUpdatedListener<K, V> {

        void entry(K key, V value);

        @Override
        default void entryUpdated(EntryEvent<K, V> event) {
            entry(event.getKey(), event.getValue());
        }

        @Override
        default void entryAdded(EntryEvent<K, V> event) {
            entry(event.getKey(), event.getValue());
        }
    }

    public static void main(String[] args) {
        HazelcastInstance hz = HazelcastClient.newHazelcastClient();
        new TradeMonitorGui(hz.getMap("prices"));
    }
}
