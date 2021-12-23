# Lab 2 - Transforming a Data Stream
## Objectives

* Run pipeline that prints a stream to the console.
* Create a filter that only prints even numbers from the stream.
* Create pipeline that processes data from a file.

> Note: if you are having trouble, we provide an example solution in the `Solutions` directory. 

## Steps

### Part 1 - Print Stream to Console

1. Run the code provided in the Lab2.java file. You should see output similar to this in the `Run` window. 

![Lab 2 Step 1 output](/images/Lab2Step1.png)

### Part 2 - Filter for Even Numbers

1. Add a filter so that odd numbers are not displayed.

![Lab 2 Step 2 output](/images/Lab2Step2.png)

### Part 3 - Read from a File in Real Time

1. Open a terminal window. Use touch to create an empty file. 

2. In your code, set the DIRECTORY variable to the location of your file.

3. Change the source to use FileWatcher. FileWatcher monitors a directory and captures changes to any files in the directory as input. 

4. We want our output to be Long, but FileWatcher input is String. We need to use a map to convert the String to Long before outputting to the console. 

5. Run your code.

6. To test it, go back to your terminal window. Use `echo “some number” >> filename.txt` to add lines to the data file. 

![Input](/images/Lab2Part3input.png)

You should see all your input being echoed to the console.

![Output](/images/Lab2Part3Output.png)

 ### Extra Practice

 Add a filter to Part 3 so that only numbers divisible by 3 are copied to the console.