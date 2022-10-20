
SRCDIR=src
BINDIR=bin
JAVAC=/usr/bin/javac
JAVA=/usr/bin/java

$(BINDIR)/%.class: $(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<
	
CLASSES2 = WordDictionary.class Score.class ScoreUpdater.class FallingWord.class HungryWord.class CatchWord.class HungryWordMover.class WordMover.class GamePanel.class  TypingTutorApp.class


CLASSES=$(CLASSES2:%.class=$(BINDIR)/%.class)

default: $(CLASSES)

run: $(CLASSES)
	$(JAVA) -cp $(BINDIR) TypingTutorApp $(tWords) $(Wscreen) $(Dfile)
	 
clean:
	rm $(BINDIR)/*.class
