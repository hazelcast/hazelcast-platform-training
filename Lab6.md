# Lab 6 - Windows, Aggregation and Grouping

In this lab, you will explore the aggregation capabilities of the Hazelcast 
platform.  Aggregation is a 3-step process:
1. __Define a grouping key.__  Generally we don't want to aggregate ALL 
of the events, we want to aggregate by something like `customer_id`.   
2. __Define the Window__.  This describes _how much_ data to aggregate. See
the javadoc for [WindowDefinition](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/pipeline/WindowDefinition.html).
3. __Define the aggregation operation__, for example count or sum.  Many 
aggregating operations are provided by the [AggregateOperations](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/aggregate/AggregateOperations.html) class.  It is also possible to define custom aggregations but that
is not covered in this lab.

An example of counting the number of trades in a 3 second window is shown below  
```java
    StreamStage<Trade> tradeStream;  // assume the input is a stream of Trades
    tradeStream.groupingKey(trade -> trade.getSymbol())
        .window(WindowDefinition.tumbling(3000))
        .aggregate(AggregateOperations.counting())
```

## Objectives 
* For each symbol, compute the trading volume (i.e. sum of quantity) in three second windows.
* Extend the example to emit interim results
* 
> __NOTE:__
>
> if you are having trouble, we provide an example solution in the `Solutions`
> directory.

## Instructions

#### 1. Update the pipeline to log the trading volume for each symbol every 3 seconds

Specify the grouping key, window and aggregate operation.  The 
trading volume is the sum of the quantities. 

Run your program and observe the output. You should see an entry in 
the log every three seconds. There will be some initial delay in 
publishing results due to the 5 second late event period.

```shell
08:15:25,245 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5cba-13c0-0001/loggerSink#0] KeyedWindowResult{start=08:15:12.000, end=08:15:15.000, key='MSFT', value='304', isEarly=false}
08:15:25,246 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5cba-13c0-0001/loggerSink#0] KeyedWindowResult{start=08:15:12.000, end=08:15:15.000, key='GOOGL', value='985', isEarly=false}
08:15:25,246 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5cba-13c0-0001/loggerSink#0] KeyedWindowResult{start=08:15:15.000, end=08:15:18.000, key='GOOGL', value='2193', isEarly=false}
08:15:32,238 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5cba-13c0-0001/loggerSink#0] KeyedWindowResult{start=08:15:21.000, end=08:15:24.000, key='MSFT', value='1154', isEarly=false}
08:15:32,238 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5cba-13c0-0001/loggerSink#0] KeyedWindowResult{start=08:15:21.000, end=08:15:24.000, key='AAPL', value='217', isEarly=false}

```
#### 2. Update the program to publish intermediate results

Modify the window definition to publish intermediate results every second.

```shell
08:27:49,614 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5f9b-9ec0-0001/loggerSink#0] KeyedWindowResult{start=08:27:42.000, end=08:27:45.000, key='GOOGL', value='765', isEarly=true}
08:27:49,614 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5f9b-9ec0-0001/loggerSink#0] KeyedWindowResult{start=08:27:45.000, end=08:27:48.000, key='GOOGL', value='817', isEarly=true}
08:27:50,616 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5f9b-9ec0-0001/loggerSink#0] KeyedWindowResult{start=08:27:45.000, end=08:27:48.000, key='MSFT', value='14', isEarly=true}
08:27:50,616 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5f9b-9ec0-0001/loggerSink#0] KeyedWindowResult{start=08:27:45.000, end=08:27:48.000, key='AAPL', value='326', isEarly=true}
08:27:50,616 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5f9b-9ec0-0001/loggerSink#0] KeyedWindowResult{start=08:27:48.000, end=08:27:51.000, key='MSFT', value='498', isEarly=true}
08:27:50,616 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5f9b-9ec0-0001/loggerSink#0] KeyedWindowResult{start=08:27:48.000, end=08:27:51.000, key='AAPL', value='471', isEarly=true}
08:27:50,616 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5f9b-9ec0-0001/loggerSink#0] KeyedWindowResult{start=08:27:42.000, end=08:27:45.000, key='GOOGL', value='765', isEarly=true}
08:27:50,616 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5f9b-9ec0-0001/loggerSink#0] KeyedWindowResult{start=08:27:45.000, end=08:27:48.000, key='GOOGL', value='817', isEarly=true}
08:27:51,485 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aee-5f9b-9ec0-0001/loggerSink#0] KeyedWindowResult{start=08:27:42.000, end=08:27:45.000, key='GOOGL', value='765', isEarly=false}
0
```

#### Extra Credit

The intermediate results can be counter-intuitive and it's worth spending some time to 
understand what's really going on.  The key is to understand that _event time is not 
related to the actual time_.   See [this discussion](https://docs.hazelcast.com/hazelcast/5.3/pipelines/building-pipelines#event-disorder) 
of how Hazelcast keeps track of the "event time" within the stream, which is separate from 
"processing time".  

> __NOTE:__
> 
> A Window will only close when event time advances to the end of the window _plus the 
> maximum lag_.  Unless the steam is using ingestion timestamps, there is no guarantee 
> that the event time will advance at all.  It only advances as events arrive.

Because of this, you can actually have multiple tumbling windows open and publishing 
intermediate results at the same time and they can stay open for longer than 
the maximum lag.  This happens when an event with a given key does not arrive for 
a long period of time.

Study the output from the second exercise that includes intermediate results and 
try to understand what is happening.


## Ideas for Extra Practice

1. Calculate the average price per symbol.
2. Use a sliding window with grouping instead of a tumbling window. 

## Additional Notes

You can run _Management Center_ with

`docker run -d -p 8080:8080 hazelcast/management-center`.

To connect to your cluster, use

cluster name: `dev`

member address: `host.docker.internal`
