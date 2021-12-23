# Lab 4 - Enriching the Stream

## Objectives
* Enrich the streaming data with the company name information.

> Note: if you are having trouble, we provide an example solution in the `Solutions` directory. 

## Procedure

Let’s look at the top of the code skeleton.

![Map Creation](/images/Lab4-1.jpg)

You can see where we’ve created a local IMap called lookupTable that contains the company names. In a “real world” application, you would likely use a MapLoader function to populate the local cache from an external data source.

Now add the enrichment to your pipeline. You’ll use .mapUsingIMap as explained in the course material to
* Reference the cached IMap data
* Get the Trade ticker info – it is the foreign key
* Create the Enriched Trade that contains the company name from the cache and the quantity and price from the stream. 

![Output](/images/Lab4-Output.png)

Once you’ve verified that you are enriching the trade data, stop your program.

