# Lab 1

## Objectives 

* Validate lab environment
* Use Management Center to manage and monitor job
* Use Hazelcast CLI to manage and monitor job

## Steps

1. Open `Lab1` file in the `essentials` module 

 ![Open Lab1](/images/setup-3.png)

2. Run Lab 1. 

 ![Run Lab1](/images/setup-4.png)

You will see the logger output in the `Run` tab.

 ![Setup done](/images/setup-5.png)

3. Open a terminal window, navigate to the Haelcast Platform directory, then use `sh management-center/bin/start.sh` to launch Management Center.

4. In your browser, open [localhost:8080](http://localhost:8080).

![Management Center Dashboard](/images/mchome.png)

5. Go to `Streaming > Jobs`. Click on the running job. 

![List of Jobs](/images/mcjoblist.png)

![Lab1 Job Detail](/images/mclab1jobdetail.png)

6. Use Management Center to suspend your job. Verify that it is suspended by looking at the output in the IDE `Run` window. Resume your job.

> Note: The job will restart because this source and sink do not support snapshots, which are required for jobs to be resumable. 

7. Open a terminal window, navigate to the Hazelcast Platform directory, then use `bin/hz-cli help` to access the list of CLI commands.

![CLI help](/images/cli-help.png)

8.  Use the CLI to get cluster information, display the list of jobs, suspend, resume, and cancel the job. 

9. Click on the red `stop` button next to the `Run` window to stop the execution of Lab1. 

![Stop IDE run](/images/ideStopButton.png)