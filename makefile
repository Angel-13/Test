JC = javac
JVM = java
MAIN = Test.jar
TOCOMPILE = Error
TOCOMPILE1 = Milestone5
JAR = jar
JARARGS = cvfm
MANIFEST = manifest.txt
sources = $(wildcard Test.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /scanner/*java /symbolTable/*.java /tokens/*.java)
classes = $(sources:.java=.class)
all: $(classes)
	$(JAR) $(JARARGS) $(MAIN) $(MANIFEST)
	$(JVM) -jar $(MAIN) $(TOCOMPILE).java
	$(JVM) -jar $(MAIN) $(TOCOMPILE1).java
	$(JVM) $(TOCOMPILE)
clean:
	rm -f *.class
.SUFFIXES: .class .java
.java.class :
	$(JC) $<