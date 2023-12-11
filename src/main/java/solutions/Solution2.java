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

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamSource;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.jet.pipeline.test.TestSources;

public class Solution2 {

    public static void main(String[] args) {

        Pipeline p = buildPipeline();

        HazelcastInstance hz = Hazelcast.bootstrappedInstance();

        hz.getJet().newJob(p);

    }

    private static Pipeline buildPipeline() {
        Pipeline p = Pipeline.create();

        // generate pseudo-random Fahrenheit temperatures in the 0-99 range
        StreamSource<Long> source = TestSources.itemStream(1, (ts, seq) -> (ts*ts - seq*seq) % 100 );

        StreamStage<Long> fahrenheitTemps = p.readFrom(source).withoutTimestamps();

        // calculate Celsius from Fahrenheit using (f - 32) * .555
        StreamStage<Double> celsiusTemps = fahrenheitTemps.map(temp -> (temp - 32) * .555);

        // filter out temperatures >= 0
        StreamStage<Double> negativeCelsiusTemps = celsiusTemps.filter(temp -> (temp < 0));

        negativeCelsiusTemps.writeTo(Sinks.logger());

        return p;
    }
}
