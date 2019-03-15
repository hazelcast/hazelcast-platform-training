import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.Pipeline;

public class Lab1 {

    public static void main (String[] args) {
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

        // DEFINE THE DATA Directory and create an empty text file in it.
        // New lines added to this file will be our source data
        final String DIRECTORY = "data/";

        // STEP 1:
        // draw using file watcher from the com.hazelcast.jet.pipeline.Sources source library
        // hint: it's non-timestamped stream

        // drain to logger using the sink from the library com.hazelcast.jet.pipeline.Sinks;

        // run this pipeline to test it!

        // STEP 2: add a filter and filter out all "hello" words
        // Add text lines to the file.
        // Use echo -- some text editors create a new file for every save. That results in replaying the file.
        //
        // echo "hello" >> filename.txt
        // echo "hello world" >> filename.txt

        return p;
    }
}
