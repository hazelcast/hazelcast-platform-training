# Lab 5 - Stateful Processing

## Objectives
* Detect if price between two consecutive trades drops by more then 200.
* Return the price difference if drop is detected


> Note: if you are having trouble, we provide an example solution in the `Solutions` directory. 

## Procedure

Use the mapStateful structure to store the price of the previous trade. Then write code that compares the current trade with the previous trade, and prints output to the console only if the trade price drops by 200 or more. 

Your output will differ depending the randomly-generated trade data, but you will eventually see price drop output.

![Lab 5 Output](/images/Lab5Output.png)

## Ideas for Extra Practice

1. Add the stock symbol to the output.

2. Add the enrichment from Lab 4 to display company name rather than stock symbol.

3. Use Management Center to view the DAG for the job.

