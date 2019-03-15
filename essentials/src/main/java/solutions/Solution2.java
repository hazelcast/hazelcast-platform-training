package solutions;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import dto.EnrichedTrade;
import dto.Trade;
import sources.TradeSource;

import java.util.Map;


public class Solution2 {

    private static final String LOOKUP_TABLE = "lookup-table" ;

    public static void main (String[] args) {
        // configure Hazelcast Jet to use log4j for log messages
        System.setProperty("hazelcast.logging.type", "log4j");

        Pipeline p = buildPipeline();

        JetInstance jet = Jet.newJetInstance();

        // ReplicatedMap<String, String> lookupTable = jet.getReplicatedMap(LOOKUP_TABLE);
        Map<String, String> lookupTable = jet.getMap(LOOKUP_TABLE);
        lookupTable.put("A", "Trader A");
        lookupTable.put("B", "Trader B");
        lookupTable.put("C", "Trader C");

        try {
            Job job = jet.newJob(p);

            Thread.sleep(5000);

            lookupTable.put("A", "Trader A_A");
            lookupTable.put("B", "Trader B_B");
            lookupTable.put("C", "Trader C_C");

            job.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            jet.shutdown();
        }
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        p.drawFrom(TradeSource.tradeSource())
                .withIngestionTimestamps()
                .mapUsingIMap(LOOKUP_TABLE, Trade::getTicker,
                        (trade, trader) -> new EnrichedTrade(trade, trader.toString()) )
                .drainTo(Sinks.logger());

        return p;
    }
}
