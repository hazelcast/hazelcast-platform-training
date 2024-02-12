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

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.pipeline.*;
import dto.Trade;

import java.util.Map;


public class Lab7 {

    private static final String INPUT_MAP = "trades";

    public static void main (String[] args) {
        // 1. for the self-deployed job, use HazelcastClient.newHazelcastClient instead of the bootstrappedInstance
        HazelcastInstance hz = Hazelcast.bootstrappedInstance();

        // 2. register a new observable with Jet and a new Observer on the Observable
        //    The Observer should just print the received event to the console
        //    See https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/JetService.html#newObservable--


        // 3. Pass the Observable created above into the buildPipeline method so it can be incorporated into the Pipeline
        Pipeline p = buildPipeline();

        // 4. Since the job is being deployed by this client rather than CLC, we must add the necessary classes to the
        //    job's class path via [JobConfig.addClass](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/config/JobConfig.html)
        Job job = hz.getJet().newJob(p);

        // 5.  Since this job is meant to be used only by this client, cancel it when this client exits
        //     by calling job.cancel() in a shutdown hook using Runtime.getRuntime().addShutdownHook()
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        // read events from the INPUT_MAP map journal, starting from the oldest.
        // use the timestamp in the event and allow 5000ms for late arrivals
        StreamStage<Map.Entry<Integer, Trade>> trades =
                p.readFrom(Sources.<Integer, Trade>mapJournal(INPUT_MAP, JournalInitialPosition.START_FROM_OLDEST))
                        .withTimestamps(entry -> entry.getValue().getTime(), 5000);

        // 6. instead of writing the Entry to the log, write the trade ( i.e. entry.getValue()) to
        //    the observable with Sinks.observable(myObservable)
        trades.writeTo(Sinks.logger( entry -> entry.getValue().toString()));

        return p;
    }


}
