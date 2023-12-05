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
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.Observable;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamSource;
import com.hazelcast.jet.pipeline.test.TestSources;

public class Lab2 {

    private static final String MY_JOB_RESULTS = "my_job_results";

    public static void main (String[] args) {
        Pipeline p = buildPipeline();
        HazelcastInstance hz = Hazelcast.bootstrappedInstance();
        JetService jet = hz.getJet();


        Observable<Object> observable = jet.getObservable(MY_JOB_RESULTS);
        observable.addObserver(e -> System.out.println("Printed from client: " + e));

            hz.getJet().newJob(p).join();
    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        StreamSource<Long> source = TestSources.itemStream(1, (ts, seq) -> seq);

        p.readFrom(source)
         .withoutTimestamps()

        // STEP 1: Filter out odd numbers from the stream

        // Add filter() to  your pipeline
        // - Use lambda to define the predicate

        // Stop the job before continuing to Step 2



        // STEP 2: Process data from a file instead of generated data

        // Create a directory somewhere in your computer and create an empty input.txt file in it

        // Replace itemStream with fileWatcher source from com.hazelcast.jet.pipeline.Sources
        // - (fileWatcher stream lines added to files in a directory.)
        // - Adjust source type - the generator was producing Longs, fileWatcher produces Strings

        // Add a mapping step before the filter to convert the stream from Strings to Longs

        // Run this pipeline to test it!
        // - Add text lines to the file.
        // - Use echo -- some text editors create a new file for every save. That results in replaying the file.
        //
        // echo "0" >> input.txt
        // echo "1" >> input.txt


         .writeTo(Sinks.observable(MY_JOB_RESULTS));

        return p;
    }
}
