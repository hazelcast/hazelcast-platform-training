# Lab 3 - Using the IMap for Event Processing

The Hazelcast distributed in-memory map, the `IMap` works together 
with the event processing system and can be an event Source or 
an event Sink. In this lab, we'll do both.

## Objectives
* Use an IMap as an event source
* Learn about timestamps and event sources.
* Use an IMap as an event sink
* React to changes in an IMap using an event listener.

> __NOTE__ 
> 
> If you are having trouble, we provide an example solution in the `Solutions` directory. 

## Instructions

Open `Lab3.java` and complete the `buildPipeline` method. You just need to
read events from the `trades` map, make a small change, and write the 
events out to the `latest_trade` map.

> __NOTE__
>
> The __input__ map has one entry per trade.  The key is a
> sequence number.  The __output__ map should use `symbol`
> as the key.


#### 1. read events from the `trades` IMap

Read the javadoc for the [Sources.mapJournal](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/pipeline/Sources.html#mapJournal-java.lang.String-com.hazelcast.jet.pipeline.JournalInitialPosition-).  

When ingesting events, you must specify _when_ the event happened using 
one of the methods of [StreamSourceStage](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/pipeline/StreamSourceStage.html).

It is important to remember that the time the event actually occurred is often not the same as the time it is ingested.  This creates 
many issues.  You can find a good discussion here: [Event Timestamps](https://docs.hazelcast.com/hazelcast/latest/pipelines/building-pipelines#event-timestamps). 

For this lab, use the timestamp in the `Trade` event and allow a 5000ms for
late events to arrive.

#### 2. change  `Map.Entry<Integer, Trade>` into Tuple2<String, Trade>

See the javadoc for [Tuple2](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/datamodel/Tuple2.html). Note that it 
implements `Map.Entry`.  A `Tuple2` can be written directly to an `IMap` 
so this step will output a `Tuple2`.

As previously mentioned, the key to the input map is a sequence number.
In preparation for writing to the output map, use a `map` step to convert 
the `Map.Entry<Integer,Trade>` from the source into a `Tuple2<String,Trade>`
with `symbol` as the key.

#### 3. Write the result to the `latest_trade` map. 

See [Sinks.map](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/pipeline/Sinks.html#map-java.lang.String-) 

## Ideas for Extra Practice

1. Modify the listener to print the entire trade record.

2. Use Management Center to look at the structure of the job, as well as the map that's been created. 

3. Change the map definition to use an incremental integer for the key, keeping the value as the entire trade. This will cause the map to store all trades. Modify the listener to display the stock symbol and price from the value field. 

4. If you do #3, modify the listener to only display trades on a specific stock symbol. (Hint: you'll use a predicate in the listener to match the symbol.)

## Additional Notes

You can run _Management Center_ with

`docker run -d -p 8080:8080 hazelcast/management-center`.

To connect to your cluster, use

cluster name: `dev`

member address: `host.docker.internal`

