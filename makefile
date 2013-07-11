JC = javac
JVM = java
MAIN = Test
sources = $(wildcard Test1.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /scanner/*java /symbolTable/*.java /tokens/*.java)
classes = $(sources:.java=.class)
all: $(classes)
	$(JVM) $(MAIN)
clean:
	rm -f *.class
.SUFFIXES: .class .java
.java.class :
	$(JC) $<