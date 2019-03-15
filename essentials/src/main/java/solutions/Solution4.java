package solutions;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.WindowDefinition;
import dto.Trade;
import sources.TradeSource;

public class Solution4 {

    public static void main(String[] args) {
        // configure Hazelcast Jet to use log4j for log messages
        System.setProperty("hazelcast.logging.type", "log4j");

        Pipeline p = buildPipeline();

        JetInstance jet = Jet.newJetInstance();

        try {
            Job job = jet.newJob(p);
            job.join();
        } finally {
            jet.shutdown();
        }
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        p.drawFrom(TradeSource.tradeSource())
                .withIngestionTimestamps()
                .window(WindowDefinition.tumbling(3000).setEarlyResultsPeriod(1000))
                .aggregate(AggregateOperations.summingLong(Trade::getPrice))
                .drainTo(Sinks.logger());

        return p;
    }
}
