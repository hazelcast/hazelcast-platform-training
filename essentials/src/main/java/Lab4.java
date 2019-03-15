import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.pipeline.Pipeline;

public class Lab4 {


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

        // How much did we sell over last 3 minutes?
        //
        // STEP 1
        // Use 3 sec tumbling windows (defined in WindowDef.tumbling with size 3000
        // Sum trade prices

        // STEP 2
        // Get speculative results every second
        // Use early results when defining the window
        // Watch the early result flag in the console output

        return p;
    }
}
