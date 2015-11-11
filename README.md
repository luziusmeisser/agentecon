# Mastering Agent-Based Economics, #Smoothing

This configuration is part of the master's thesis [Mastering Agent-Based Economics](http://master.agentecon.com/thesis.pdf).

Results can be browsed online on http://master.agentecon.com/sim.html?id=Smoothing

Run class com.agentecon.sim.config.SavingConsumerConfiguration to get [output.txt](https://github.com/kronrod/agentecon/blob/Smoothing/src/com/agentecon/sim/config/output.txt) which can be used to generate charts [smoothing2.pdf](https://github.com/kronrod/agentecon/blob/Smoothing/chart/smoothing2.pdf) and [pizzaincarnation.pdf](https://github.com/kronrod/agentecon/blob/Smoothing/chart/pizzaincarnation.pdf).

###General instructions

In order to modify and run the model yourself, you should follow these steps:

1. Make sure a git client is installed. I am using SourceTree: https://www.sourcetreeapp.com/
2. Make sure an IDE for Java is installed. I am using eclipse for Java EE developers, obtainable from https://eclipse.org/downloads/ .
3. Add this repository (https://github.com/kronrod/agentecon.git) in your git and checkout the tag or branch from the title.
4. Import the project to eclipse. In case you are using a different IDE, make sure to add jar\agenteconinterface.jar and jar\jacop-4.3.0.jar library to the classpath.
5. Run the class mentioned above.