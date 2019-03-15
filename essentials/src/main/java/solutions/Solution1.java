package solutions;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;

public class Solution1 {

    private static final String DIRECTORY = "data/";

    public static void main(String[] args) {
        // configure Hazelcast Jet to use log4j for log messages
        System.setProperty("hazelcast.logging.type", "log4j");

        Pipeline p = buildPipeline();

        JetInstance jet = Jet.newJetInstance();
        try {
            jet.newJob(p).join();
        } finally {
            jet.shutdown();
        }
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        p.drawFrom(Sources.fileWatcher(DIRECTORY))
                .withoutTimestamps()
                .filter(a -> !"hello".equals(a))
                .drainTo(Sinks.logger());

        return p;
    }
}
