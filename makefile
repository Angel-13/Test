JC = javac
JVM = java
MAIN = Test
TOCOMPILE = Error
sources = $(wildcard Test.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /scanner/*java /symbolTable/*.java /tokens/*.java)
classes = $(sources:.java=.class)
all: $(classes)
	$(JVM) $(MAIN) $(TOCOMPILE).java
	$(JVM) $(TOCOMPILE)
clean:
	rm -f *.class
.SUFFIXES: .class .java
.java.class :
	$(JC) $<