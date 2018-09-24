OPERATING SYSTEMS
Two-pass linker
---------------
THE PROGRAM

This program is a basic two-pass linker. It is meant for a word-addressable machine, with words of length 4 digits each. The memory of the machine is 300 words.

There are three java classes. One is the main driver class and the other two classes (Module and Symbol) which support the main class and are required to run.

---------------
COMPILING AND RUNNING

In order to run the program, you will need to compile all three java files: Driver.java, Module.java and Symbol.java.

This can be done from the command line by running the following command:

	javac Driver.java Module.java Symbol.java

This should produce three new  .class files—each one representing each .java file compiled.

Next, you will need to run the main method of the Driver.class file; this method will call on other methods to process your input. This method can be called by the following Unix command.

	java Driver

Now you are ready to enter your input.

---------------
ENTERING INPUT

First enter the number of modules. Then you will need to enter information for each module.

After that you will need to enter the definition list of the format:

	#ofDefinitions  symbol value  symbol value  symbol value  etc.

Then enter the use list in the following format:

	#ofUsePairs	symbol instrNumUsed instrNumUsed instrNumUsed -1 nextSymbol instrNumUsed -1 nextSymbol etc.

Then the instructions themselves:

	#ofInstructions	5digitinstruction 5digitinstruction 5digitinstruction etc.

The program will automatically stop taking input once you've entered all the information for the number of modules declared at the start. White space and line breaks are ignored when processing the information, so the structure of the input is relatively flexible provided everything is entered in the correct order.