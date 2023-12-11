# Lab 4 - Enriching the Event Stream

In an event processing solution, all of the data needed for decision-making is not
usually on the event.  For example, in a credit card fraud solution, the credit 
limit and the home address of the card owner are not on the event, they need to 
be looked up based on information that is in the event (e.g. the credit card
number). In an IoT solution, the "healthy" range for a particular reading is not 
on the event, it needs to be looked up.  Doing this type of lookup during event 
processing is called "enrichment" and is a Hazelcast superpower.  

The reference data needed for enrichment is typically stored in a Hazelcast IMap.
Because the event processing and storage capabilities are integrated, Hazelcast is 
able to direct the event processing to the node that stores the required reference 
data resulting in less data movement, lower latency and higher throughput.

In this lab, we will enrich a trade with the name of the company.  Along the 
way we will also learn a little bit about how Hazelcast routes events between 
tasks.

## Objectives
* Enrich the streaming data with the company name information.
* Understand a little about data-aware routing as it pertains to event processing.

> NOTE: 
> 
> If you are having trouble, we provide an example solution in the `Solutions` directory. 

## Instructions

#### 1. Review the main method.  Understand how the symbol map is being used
Let’s look at the top of the code skeleton.

```java
public class Lab4 {
    //...
    public static void main(String[] args) {

        HazelcastInstance hz = Hazelcast.bootstrappedInstance();
        
        //...
        
        // Populate the symbolMap with reference information about each symbol
        IMap<String, String> symbolMap = hz.getMap(LOOKUP_MAP);
        symbolMap.put("AAPL", "Apple Inc. - Common Stock");
        symbolMap.put("GOOGL", "Alphabet Inc.");
        symbolMap.put("MSFT", "Microsoft Corporation");
    
        //...

        Pipeline p = buildPipeline();
        hz.getJet().newJob(p);
    }
    //...
}
```
You can see where we’ve created an IMap called "symbols" that contains the 
company names. In a real application, you would likely use another 
method such as a `MapLoader` and `Pipeline` or just a client application to 
populate the data.  This is the data that you will use to enrich the event
stream.

#### 2. Learn about routing events

Although there is only one node, in general there will be multiple. Take a 
moment to review the documentation on [parallel processing](https://docs.hazelcast.com/hazelcast/5.3/architecture/distributed-computing#parallel-processing). As 
you can see, each job is described by a directed acyclic graph (DAG) of tasks.  
Because Hazelcast is data location-aware, or "partition aware", it directs 
the execution of tasks so as to reduce data movement.  

> __NOTE:__
> 
> Generally, as events are routed between tasks, they  will be routed to a 
> processor on the same node unless the task is partition-aware. For partition 
> aware tasks such as `StreamStage.mapUsingIMap` the events will be routed to the
> node containing the required data. 

`Sources.mapJournal` is partition-aware.  The event originates on the node that 
contains the corresponding map entry.  `StreamStage.mapUsingIMap` is also 
partition-aware.  The initial `Trade` event is emitted on the node that hosts 
the map key, which is a sequence number.  In order to execute `mapUsingIMap` 
the `Trade` event is routed to the node that contains the entry for the required 
symbol.  Since these are not the same, events will be distributed across nodes 
as required.  The diagram below illustrates how this `Pipeline` would be run 
on a 2 machine cluster with each member having 2 cores.

![Enrich Pipeline](images/Lab%204%20Enrich%20Pipeline.png)

#### 3. Perform enrichment using `StreamStage.mapUsingIMap`

Review the documentation for [StreamStage.mapUsingIMap](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/pipeline/StreamStage.html?mapUsingIMap-java.lang.String-com.hazelcast.function.FunctionEx-com.hazelcast.function.BiFunctionEx-), then add a stage to the pipeline to do the 
enhancement.  

> __HINT:__ `mapUsingIMap` takes 3 _type_ parameters.  You may need to explicitly 
> specify them.

Your output should look like this.  
```shell
14:24:03,965 [JobCoordinationService] [127.0.0.1]:5701 [dev] [5.3.5] Starting job 0aea-d464-f580-0001 based on submit request
14:24:03,971 [MasterJobContext] [127.0.0.1]:5701 [dev] [5.3.5] Didn't find any snapshot to restore for job '0aea-d464-f580-0001', execution 0aea-d464-f581-0001
14:24:03,971 [MasterJobContext] [127.0.0.1]:5701 [dev] [5.3.5] Start executing job '0aea-d464-f580-0001', execution 0aea-d464-f581-0001, execution graph in DOT format:
digraph DAG {
	"mapJournalSource(trades)" [localParallelism=2];
	"mapUsingIMap" [localParallelism=10];
	"loggerSink" [localParallelism=1];
	"mapJournalSource(trades)" -> "mapUsingIMap" [queueSize=1024];
	"mapUsingIMap" -> "loggerSink" [queueSize=1024];
}
HINT: You can use graphviz or http://viz-js.com to visualize the printed graph.
14:24:04,009 [JobExecutionService] [127.0.0.1]:5701 [dev] [5.3.5] Execution plan for jobId=0aea-d464-f580-0001, jobName='0aea-d464-f580-0001', executionId=0aea-d464-f581-0001 initialized
14:24:04,012 [JobExecutionService] [127.0.0.1]:5701 [dev] [5.3.5] Start execution of job '0aea-d464-f580-0001', execution 0aea-d464-f581-0001 from coordinator [127.0.0.1]:5701
14:24:04,042 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aea-d464-f580-0001/loggerSink#0] Trade{time=1702322643899, company name='Microsoft Corporation', quantity=349, price=2224}
14:24:04,906 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aea-d464-f580-0001/loggerSink#0] Trade{time=1702322644901, company name='Apple Inc. - Common Stock', quantity=765, price=1602}
14:24:05,910 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aea-d464-f580-0001/loggerSink#0] Trade{time=1702322645904, company name='Alphabet Inc.', quantity=482, price=1602}
14:24:06,908 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aea-d464-f580-0001/loggerSink#0] Trade{time=1702322646902, company name='Microsoft Corporation', quantity=630, price=311}
14:24:07,905 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aea-d464-f580-0001/loggerSink#0] Trade{time=1702322647899, company name='Apple Inc. - Common Stock', quantity=978, price=1215}
14:24:08,910 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aea-d464-f580-0001/loggerSink#0] Trade{time=1702322648902, company name='Alphabet Inc.', quantity=762, price=1810}
14:24:09,910 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aea-d464-f580-0001/loggerSink#0] Trade{time=1702322649902, company name='Alphabet Inc.', quantity=877, price=4961}
14:24:10,903 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aea-d464-f580-0001/loggerSink#0] Trade{time=1702322650899, company name='Microsoft Corporation', quantity=688, price=3168}
14:24:11,906 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aea-d464-f580-0001/loggerSink#0] Trade{time=1702322651899, company name='Alphabet Inc.', quantity=84, price=308}
```


