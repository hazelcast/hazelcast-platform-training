import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.pipeline.Pipeline;

public class Lab3 {

    public static void main (String[] args) {
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


        // read from the Trade Source and ingestion timestamps

        // compute max rolling price - the max will be updated and emitted with each incoming trade
        // use com.hazelcast.jet.aggregate.AggregateOperations library with aggregations
        // use com.hazelcast.jet.function.ComparatorEx library

        // drain to logger sink

        return p;
    }
}
