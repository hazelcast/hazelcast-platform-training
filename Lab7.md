# Lab 7 - Deploying Your Pipeline

Include Observable 

## Objectives 
* Use CLC to deploy a compiled JAR to a Hazelcast cluster

## Part 1: Set Up Environment

1. Browse to [viridian.hazelcast.com](https://viridian.hazelcast.com). Create an account, then log in. Create a production cluster.

2. In the Quick Connect guide, select CLI and follow the instructions to install and configure the Hazelcast Command Line Client.

3. Verify that there are no jobs currently running.
```shell
\job list
```
4. Exit CLC.
```shell
\exit
```

## Part 2: Build Your Code

1. (whatever steps are needed to build the code)

## Part 3: Submit Your Code

1. (first submission should have missing class info)

```shell
clc -c <your-cluster> job submit <filename.jar>
```
2. (what troubleshooting tools are available to see what went wrong?)

3. Include the class in your submit command.
```shell
clc -c <your cluster> job submit <filename.jar> --class <path>
```

4. Verify that the job is running.
```shell
clc -c <your cluster> job list
```

5. In the Viridian dashboard, open Management Center. Go to Stream Processing > Dashboard

![MC](images/console-mc.png)
 
 (screen shot of jobs dashboard)

 (What are we looking for/at? Any potential problems we can identify here?)

6. Go to Stream Processing > Jobs, then open the detailed view of your job.

(what are we looking for/at? Any potential problems we can identify here?)
