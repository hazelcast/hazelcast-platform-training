# Lab 5 - Stateful Operations on Streams

Suppose that you are monitoring the temperature of a refrigerated
truck.  You receive readings every minute, and you want to raise an alert whenever 
the temperature goes above a threshold.  It is simple enough to use a filter 
to pick out events that are over a certain threshold but, you only want to 
raise the alert one time, not every time.  

For example, If the threshold is 10 degrees, then the sequence of events might 
look like: 0,1,0,4,7,9,11,12,13,9.  We only want to raise one alert not one each 
for 11,12,13.  We have to "remember" that we already raised the alert for this 
truck. In other words, this transformation requires state.

In this lab, we are going to use  `StreamStageWithKey.mapStateful` to handle 
a similar scenario.  We will detect when the price of a certain stock has 
changed by 25% or more.  

## Objectives
* Understand how to use the `mapStateful` operation.
* Understand the difference between a `StreamStage` and a `StreamStageWithKey`

> __NOTE:__ 
> 
> if you are having trouble, we provide an example solution in the `Solutions`
> directory. 

## Instructions

First, read the java doc on [StreamStageWithKey.mapStateful](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/pipeline/StreamStageWithKey.html#mapStateful-com.hazelcast.function.SupplierEx-com.hazelcast.jet.function.TriFunction-).  

Note that the platform will automatically use the create function you provide 
to create a state object _for each grouping key_.  You can convert a regular 
`StreamStage` into a `StreamStageWithKey` using [StreamStage.groupingKey](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/pipeline/StreamStage.html#groupingKey-com.hazelcast.function.FunctionEx-).

Hazelcast provides a number of useful state objects in the 
`com.hazelcast.jet.accumulator` package.  For this lab, we can use 
[LongAccumulator](https://docs.hazelcast.org/docs/5.3.5/javadoc/com/hazelcast/jet/accumulator/LongAccumulator.html).  You are also 
free to use your own state object so long as it is serializable.

#### 1. Use the `StreamStage.groupingKey` method to create a `StreamStageWithKey`
The resulting stream should use `symbol` as the key.

#### 2. Implement the `mapStateful` step.
If the price changed by less than 25% relative to the previous price for the same
symbol, do not emit an event (return null), otherwise, return a `Tuple4` 
containing (symbol, previous price, current price, %change).

#### 3. Test your solution

Some sample output is shown below.

```shell
15:52:19,000 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aec-322c-03c0-0001/loggerSink#0] (GOOGL, 3536, 349, -91)
15:52:19,994 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aec-322c-03c0-0001/loggerSink#0] (AAPL, 3885, 615, -85)
15:52:20,998 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aec-322c-03c0-0001/loggerSink#0] (GOOGL, 349, 218, -38)
15:52:21,996 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aec-322c-03c0-0001/loggerSink#0] (AAPL, 615, 4042, 557)
15:52:22,996 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aec-322c-03c0-0001/loggerSink#0] (AAPL, 4042, 52, -99)
15:52:24,004 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aec-322c-03c0-0001/loggerSink#0] (MSFT, 2762, 4485, 62)
15:52:24,995 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0aec-322c-03c0-0001/loggerSink#0] (AAPL, 52, 4014, 7619)
15
```

