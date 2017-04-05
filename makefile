JFLAGS = -g -cp ./commons-cli-1.4.jar
JC = javac
CLASSES = $(wildcard *.java)

.PHONY: clean all classes
.SUFFIXES: .java .class


all: classes

clean:
	rm -f *.class NBodyParallelFinalPositions.txt NBodySequentialFinalPositions.txt NBodyResultsParallel.csv NBodyResultsSequential.csv

.java.class:
	$(JC) $(JFLAGS) $*.java

classes: $(CLASSES:.java=.class)

