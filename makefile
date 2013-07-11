JC = javac
JVM = java
MAIN = Test
TOCOMPILE = Error.java
sources = $(wildcard Test.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /scanner/*java /symbolTable/*.java /tokens/*.java)
classes = $(sources:.java=.class)
all: $(classes)
	$(JVM) $(MAIN) $(TOCOMPILE)
clean:
	rm -f *.class
.SUFFIXES: .class .java
.java.class :
	$(JC) $<