all:
	mkdir -p bin
	find ./src -name *.java | javac -d bin -classpath "lib/antlr-4.5.2-complete.jar" @/dev/stdin
	cp runtime.s bin/

clean:
	rm -rf bin
