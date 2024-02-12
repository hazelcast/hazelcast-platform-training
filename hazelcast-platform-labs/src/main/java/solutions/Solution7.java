package solutions;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.Observable;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.function.Observer;
import com.hazelcast.jet.pipeline.*;
import dto.Trade;

import java.util.Map;

public class Solution7 {
    private static final String INPUT_MAP = "trades";

    public static void main (String[] args) {
        // note we are using HazelcastClient rather than the bootstrapped instance
        HazelcastInstance hz = HazelcastClient.newHazelcastClient();

        // register a new observable with Jet and a new Observer on the Observable
        Observable<Trade> observable = hz.getJet().newObservable();
        Observer<Trade> observer = t -> System.out.println("RECEIVED: " + t);
        observable.addObserver(observer);

        // create a pipeline to observe the Trades
        Pipeline p = buildPipeline(observable);

        // since the job is being deployed by this client rather than CLC, we must add the necessary classes to the
        // job's class path via JobConfig
        JobConfig jobConfig = new JobConfig();
        jobConfig.setName("observer");
        jobConfig.addClass(Solution7.class);
        jobConfig.addClass(Trade.class);
        Job jetJob = hz.getJet().newJob(p, jobConfig);

        // since this job is meant to be used only by this client, cancel it when this client exits
        Runtime.getRuntime().addShutdownHook(new Thread(jetJob::cancel));
    }

    private static Pipeline buildPipeline(Observable<Trade> observable) {
        Pipeline p = Pipeline.create();

        // read events from the INPUT_MAP map journal, starting from the oldest.
        // use the timestamp in the event and allow 5000ms for late arrivals
        StreamStage<Map.Entry<Integer, Trade>> trades =
                p.readFrom(Sources.<Integer, Trade>mapJournal(INPUT_MAP, JournalInitialPosition.START_FROM_OLDEST))
                        .withTimestamps(entry -> entry.getValue().getTime(), 5000);

        // write the result to the output map
        trades.map(Map.Entry::getValue).writeTo(Sinks.observable(observable));

        return p;
    }

}
