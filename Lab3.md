# Lab 3 - Using the Platform Capabilities

## Objectives
* Read from a streaming source and write to an in-memory map
* Use an EventListener to capture changes to in-memory map

> Note: if you are having trouble, we provide an example solution in the `Solutions` directory. 


## Steps

1. Open `Lab3.java` in your IDE. In this code skeleton, we've provided you with a streaming source of random stock trade data. Each entry contains a timestamp, a stock symbol, number of shares, and a price. 

    Your task is to write a pipeline that takes the stream and writes it to an already-configured in-memory map. Use the stock symbol as the key, and the entire trade as the value. 

2. Before running your code, open `Listeners > TradeListener.java`. A listener executes code in response to specific activity against an in-memory data structure. In this case, we've only written code for two types of events: when a map entry is added, and when a map entry is updated. 

3. Run your code. Your output should be similar to what's shown here.

![Output](/images/Lab3Output.png)

## Ideas for Extra Practice

1. Modify the listener to print the entire trade record.

2. Use Management Center to look at the structure of the job, as well as the map that's been created. 

3. Change the map definition to use an incremental integer for the key, keeping the value as the entire trade. This will cause the map to store all trades. Modify the listener to display the stock symbol and price from the value field. 

4. If you do #3, modify the listener to only display trades on a specific stock symbol. (Hint: you'll use a predicate in the listener to match the symbol.)

