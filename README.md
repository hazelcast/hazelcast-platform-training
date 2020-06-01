# Stream Processing Essentials

This repository contains the labs for the Stream Processing Essentials training with [Hazelcast Jet](https://jet-start.sh/).

If you're not attending the instructor-led training session please consider signing-up for the
[free online training](https://training.hazelcast.com/stream-processing-essentials).

## Lab setup

The Labs are available as a Maven project and require Java 8 JDK or higher and Maven installed in your computer. 

Please clone this GitHub repository to your computer and build it so that Maven downloads the dependencies.

```bash
mvn clean compile  
```

If you aren't sure regarding your setup please follow the step-by-step guide to prepare the lab environment in your computer from
the scratch.

### Step-by-step instructions 

1. Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/).

2. Download and install [Git](https://git-scm.com/downloads)

3. Start IntelliJ IDEA.

4. Get New Project from Version control. 

 ![Import New Project from Version control.](/images/setup-1.png)

5. Enter the URL of the repository and Clone it. 

 ![Clone the GitHub repository](/images/setup-2.png)

6. IDEA imported the project. Open `Lab1` file in the `essentials` module 

 ![Open Lab1](/images/setup-3.png)

7. Setup the Java SDK to run the labs. Use the Download SDK option unless you have Java 8 or higher installed in your computer.

8. Run Lab 1 to validate your setup.

 ![Run Lab1](/images/setup-4.png)

9. You will see the logger output in the `Run` tab.

 ![Setup done](/images/setup-5.png)

10. Stop the execution of Lab1