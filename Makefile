JFLAGS = -cp "AUL.jar:antlr/antlr-4.5.3-complete.jar:."
ANTLR4 = java $(JFLAGS) -jar antlr/antlr-4.5.3-complete.jar
GRUN = java $(JFLAGS) org.antlr.v4.runtime.misc.TestRig

AULDIR = src/com/gleme/unifei
G4DIR = src/com/gleme/unifei/antlr4

all: jar
	@cat res/stub.sh AUL.jar > aulc && chmod +x aulc

grammar:
	@$(ANTLR4) $(G4DIR)/AUL.g4

antlr_class: grammar
	@javac $(JFLAGS) $(G4DIR)/*.java $(AULDIR)/*.java

jar: antlr_class
	@jar -cmf res/MANIFEST.MF AUL.jar -C src/ .	

test: jar
	@$(GRUN) com.gleme.unifei.antlr4.AUL program -tokens -tree -gui sample/helloworld.aul

clean:
	@echo "Cleaning unnecessary files..."
	@rm -rf $(AULDIR)/*.class $(G4DIR)/*.class $(G4DIR)/*.tokens $(G4DIR)/*.java *.jar *.class *.tokens *.java aulc

install: all
	@echo "export CLASSPATH='.:/usr/local/lib/antlr-4.5.3-complete.jar:$CLASSPATH'" >> ~/.bashrc
	@sudo cp antlr/antlr-4.5.3-complete.jar /usr/local/lib/antlr-4.5.3-complete.jar
	@sudo cp aulc /usr/bin/aulc
