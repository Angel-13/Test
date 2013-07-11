JC = javac
JVM = java
MAIN = Test
TOCOMPILE = Error
TOCOMPILE1 = Milestone5
sources = $(wildcard Test.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /scanner/*java /symbolTable/*.java /tokens/*.java)
classes = $(sources:.java=.class)
all: $(classes)
	$(JVM) $(MAIN) $(TOCOMPILE).java
	$(JVM) $(MAIN) $(TOCOMPILE1).java
	$(JVM) $(TOCOMPILE)
clean:
	rm -f *.class
.SUFFIXES: .class .java
.java.class :
	$(JC) $<