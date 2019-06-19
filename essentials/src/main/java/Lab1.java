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

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.Pipeline;

public class Lab1 {

    public static void main (String[] args) {
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

        // 1 - Stream new lines into the job
        // - use drawFrom on your pipeline
        // - Use fileWatcher source from com.hazelcast.jet.pipeline.Sources

        // 2 - Without timestamps - we don't need timestamped stream now

        // 3 - Print results
        // - Use drainTo on your pipeline
        // - Use logger sink from com.hazelcast.jet.pipeline.Sinks;

        // 4 - Run this pipeline to test it!
        // Add text lines to the file.
        // Use echo -- some text editors create a new file for every save. That results in replaying the file.
        //
        // echo "hello" >> filename.txt
        // echo "hello world" >> filename.txt


        // STEP 2: Filter out all lines which are equal to the string "hello"
        // use filter() on your pipeline
        // use lambda to define the predicate

        return p;
    }
}
