

import java.util.concurrent.atomic.AtomicBoolean;

//Thread to monitor the word that has been typed.
public class CatchWord extends Thread {
	String target;
	static AtomicBoolean done ; //REMOVE
	static AtomicBoolean pause; //REMOVE
    private static HungryWord[] hungryWords;
	private static  FallingWord[] words; //list of words
	private static int noWords; //how many
	private static Score score; //user score

	CatchWord(String typedWord) {
		target=typedWord;
	}

	public static void setWords(FallingWord[] wordList) {
		words=wordList;
		noWords = words.length;
	}

	public static void setScore(Score sharedScore) {
		score=sharedScore;
	}

	public static void setFlags(AtomicBoolean d, AtomicBoolean p) {
		done=d;
		pause=p;
	}

	public void run() {
		int i=0;
		while (i<noWords) {

			while(pause.get()) {};
			int compare=-1;
			int counter =-1;
			if(words[i].getWord().equals(target)){
				for (int k=0; k<words.length; k++){
					if(words[k].getWord().equals(words[i].getWord())){
						if(compare<words[k].getY()){
							compare = words[k].getY();
							counter =k;
						}
					}
				}
				if (words[counter].matchWord(target)) {
					System.out.println( " score! '" + target); //for checking
					score.caughtWord(target.length());
					//FallingWord.increaseSpeed();
					break;
				}
				System.out.println(" mistry"+hungryWords[0].getWord());
				if(hungryWords[0].matchWord(target)){
					score.caughtWord(target.length());
					hungryWords[0].resetWord();
					
					break;
				}
			}

			i++;
		}

	}
}
