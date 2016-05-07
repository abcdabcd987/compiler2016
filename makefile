all:
	mkdir -p bin
	find ./src -name *.java | javac -d bin -classpath "lib/antlr-4.5.2-complete.jar" @/dev/stdin

clean:
	rm -rf bin
