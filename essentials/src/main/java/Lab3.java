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
import com.hazelcast.jet.Job;
import com.hazelcast.jet.pipeline.Pipeline;

public class Lab3 {

    public static void main (String[] args) {
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
