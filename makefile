#to run: java -cp  .:./commons-cli-1.4.jar NBodyParallel 8 10 10 500
#to run: java -cp  .:./commons-cli-1.4.jar NBodySequential 0 10 10 500

JFLAGS = -g 
JC = javac
CLASSES = $(wildcard *.java)

.PHONY: clean all classes
.SUFFIXES: .java .class


all: NBodyParallel.class NBodySequential.class

clean:
	rm -f *.class NBodyParallelFinalPositions.txt NBodySequentialFinalPositions.txt NBodyResultsParallel.csv NBodyResultsSequential.csv

NBodyParallel.class: BodyP.class StdDraw.class
	javac -g -cp .:./commons-cli-1.4.jar NBodyParallel.java

NBodySequential.class: Body.class StdDraw.class
	javac -g -cp .:./commons-cli-1.4.jar NBodySequential.java


BodyP.class:
	javac -g Body.java

StdDraw.class:
	javac -g StdDraw.java

Body.class:
	javac -g Body.java

