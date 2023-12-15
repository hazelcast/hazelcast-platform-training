# Lab 2 - Basic Data Transformations 

In this lab you will use the Pipeline API to do filtering and simple 
transformation on an event stream.

## Objectives

* become familiar with the `Pipeline` API 
* learn how to do basic data transformation and filtering

> __NOTE:__ if you are having trouble, we provide an example solution in the `Solutions` directory.

## Instructions
In this lab, the source is a stream of Fahrenheit temperatures.  You 
will write a `Pipeline` to process the stream.  The `Pipeline` will 
- use `StreamStage.map` to convert the Fahrenheit temperatures to Celsius
- use `StreamStage.filter` to keep only temperature that are below 0
- write the stream of negative Celsius temperatures to a log 

Start by reviewing the javadoc for these two methods in 
https://docs.hazelcast.org/docs/latest/javadoc/index.html?com/hazelcast/jet/pipeline/StreamStage.html

Open `Lab2.java` and follow the instructions.

> __NOTE__ 
> 
> Notice that in this exercise, the Pipeline is being constructed 
> in multiple statements while, in the previous exercise it was 
> constructed with one long statement.
> 
> The two formats are logically equivalent but the multi-statement approach 
> can make the pipeline code easier to understand. You can see that 
> each statement adds a new operation to a previous `StreamStage` 
> and each statement that is not a `writeTo` returns a new `StreamStage`. 

## Additional Notes

You can run _Management Center_ with 

`docker run -d -p 8080:8080 hazelcast/management-center`.

To connect to your cluster, use 

cluster name: `dev`

member address: `host.docker.internal`


