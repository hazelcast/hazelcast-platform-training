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

package solutions;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamSource;
import com.hazelcast.jet.pipeline.test.TestSources;

public class Solution1 {

    private static final String DIRECTORY = "data/";

    public static void main(String[] args) {
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

        StreamSource<Long> source = TestSources.itemStream(1, (ts, seq) -> seq);
        // StreamSource<String> source = Sources.fileWatcher(DIRECTORY);

        p.drawFrom(source)
         .withoutTimestamps()
         // .map( line-> Long.valueOf(line))
         .filter(item -> (item % 2) == 0)
         .drainTo(Sinks.logger());

        return p;
    }
}
