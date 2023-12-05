# Lab 1 - The Lab Environment

## Objectives 

* Validate lab environment
* Use Management Center to manage and monitor job
* Use Hazelcast CLI to manage and monitor job

## Instructions 

### 1. Review the Code
Open `src/main/java/Lab1.java` in your IDE and review the code. This program 
will start an embedded Hazelcast instance and submit a simple Pipeline that 
writes log statements to the console.

### 2. Run It
You can use the IDE or run it from the command line using maven as shown here. 
```shell
mvn package exec:java -Dexec.mainClass=Lab1
```

You should see output similar to the example below.
```shell
14:08:22,895 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0ae3-1743-df80-0001/loggerSink#0] 0
14:08:23,900 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0ae3-1743-df80-0001/loggerSink#0] 1
14:08:24,899 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0ae3-1743-df80-0001/loggerSink#0] 2
14:08:25,900 [WriteLoggerP] [127.0.0.1]:5701 [dev] [5.3.5] [0ae3-1743-df80-0001/loggerSink#0] 3
```
> __NOTE:__ Leave it running for the next steps.

### 3. Create a New CLC Configuration for Your Cluster
If you haven't already installed the Hazelcast Command Line Client (CLC),
do so now by navigating to https://docs.hazelcast.com/clc/latest/install-clc
and following the directions for your system.  Use the 
following command to create a CLC configuration named "local" that points 
to your local cluster.
```shell
clc config add local cluster.name=dev cluster.address=localhost:5701
```

> __NOTE:__ This configuration persists.  You will be able to use the same configuration for labs 1-6

### 4. Use CLC to View and Control Running Jobs

Use `clc job -h` to see the list of available job-related commands.

```console
% clc job -h
Jet job operations

Usage:
  clc job [command] [flags]

Available Commands:
  cancel          Cancels the job with the given ID or name
  export-snapshot Exports a snapshot for a job
  list            List jobs
  restart         Restarts the job with the given ID or name
  resume          Resumes a suspended job
  submit          Submits a jar file to create a Jet job
  suspend         Suspends the job with the given ID or name

Flags:
  -h, --help   help for job

Global Flags:
  -c, --config string      set the configuration
  -f, --format string      set the output format, one of: csv, delimited, json, table (default "delimited")
      --log.level string   set the log level (default "info")
      --log.path string    set the log path, use stderr to log to stderr (default "/Users/happleton/.hazelcast/logs/2023-10-18.log")
  -q, --quiet              disable unnecessary output
      --timeout string     timeout for operation to complete
      --verbose            enable verbose output

Use "clc job [command] --help" for more information about a command.
```

Use the CLC to get cluster information, display the list of jobs, then suspend the job. You will need to include the cluster configuration you created in the previous step, using the -c option. Example:

```console
% clc -c local job list
0aa5-4667-6780-0001	N/A	RUNNING	2023-10-18 11:48:30	-
OK
```

Verify that the job has been suspended by looking at the output in the IDE `Run` window. Use the CLC to resume the job.

> __NOTE:__ When you suspend, then resume your job, the integer value will reset to 1, effectively restarting the job. This is because this source and sink do not support snapshots, which are required for jobs to be resumable from the suspend point. 

### 5. Use Management Center to View Your Cluster

Start the Hazelcast Management Center using the Docker image.
```shell
docker run -d -p 8080:8080 hazelcast/management-center 
```

In your browser, open [localhost:8080](http://localhost:8080). Select Dev as the authentication method.

Add a cluster using the following parameters:
* Cluster name: dev
* Address: host.docker.internal

![MC Cluster Setup](images/MC_clusterconfig.png)

> __NOTE:__ `host.docker.internal` is the name of the docker host (i.e. your machine) referenced from within the Docker guest (Management Center)

Click on `View Cluster`.

![Open cluster view](images/mc_viewcluster.png)

![Management Center Dashboard](images/mchome.png)

Go to `Stream Processing > Jobs`. Click on the running job. 

![Management Center Dashboard](images/mchome.png)

![List of Jobs](images/mcjoblist.png)

![Lab1 Job Detail](images/mclab1jobdetail.png)

Use Management Center to suspend your job. Examine the screen output. Resume the job and examine the screen output. 

### 6. Cleanup

Stop the Management Center from the Docker Dashboard or from the command line
(example below)

```shell
rmay@HZLCST-MBP-42 hazelcast-platform-training % docker ps
CONTAINER ID   IMAGE                         COMMAND                  CREATED          STATUS          PORTS                                        NAMES
5db9706ffc81   hazelcast/management-center   "bash ./bin/mc-start…"   10 minutes ago   Up 10 minutes   8081/tcp, 0.0.0.0:8080->8080/tcp, 8443/tcp   infallible_ptolemy
rmay@HZLCST-MBP-42 hazelcast-platform-training % docker stop infallible_ptolemy
in
```

Stop the embedded Hazelcast instance using `Ctrl-C` or with the stop button if 
it was running in the IDE.