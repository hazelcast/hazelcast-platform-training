# Lab 6 - Windowing and Grouping

## Objectives 
* Compute sum of trades in three second windows.
* Compute sum of trades in three second windows, with results to date in one second intervals. 
* Compute sum of trades over 3 seconds, update every second.
* Compute sum of trades for 3-second intervals per ticker symbol

## Part 1: Three second window, three second update

Set up your pipeline to read from sources.TradeSource, using native timestamps as in previous labs. Use a tumbling window with a size of 3000 milliseconds. Your aggregate will be a sum of prices of all the trades completed in the configured window. Drain the output to the logger.

Run your program and observe the output. You should see an entry in the log every three seconds. 

![Lab 6 Part 1 Output](/images/Lab6Part1Out.png)


## Part 2: Three second window, results to date every second

Use the same source and sink, and the same aggregate. Change the tumbling window to display early results every second. 

![Lab 6 Part 2 Output](/images/Lab6Part2Out.png)

The output shows the values increasing within the three second window. Also note the “is early” flag is set for the results “inside” the three second window.

## Part 3: Three second interval, updating every second

Use a sliding window with a three second interval, sliding every second. The aggregate is the same.

![Lab 6 Part 3 Output](/images/Lab6Part3Out.png)

The output shows the overlapping start times.

## Part 4: Windowing with Grouping

Use the grouping API to group events by ticker symbol. Use the windowing API to define the tumbling window and the aggregate operation. 

![Lab 6 Part 4 Output](/images/Lab6Part4Out.png)


## Ideas for Extra Practice

1. Add enrichment to your output.
2. Calculate the average price per symbol.
3. Use a sliding window with grouping instead of a tumbling window. 
