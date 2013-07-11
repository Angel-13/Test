JC = javac
JVM = java
MAIN = Test1
sources = $(wildcard Test1.java /code/*.java /compileTable/*.java /mapsTable/*.java /milestone2/*.java /scanner/*java /symbolTable/*.java /tokens/*.java)
all:
	$(JC) $(sources)
	$(JVM) $(MAIN)
clean:
	rm -f *.class