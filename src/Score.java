

public class Score {
	private int missedWords;
	private int caughtWords;
	private int gameScore;
	
	Score() {
		missedWords=0;
		caughtWords=0;
		gameScore=0;
	}
		
	// all getters and setters must be synchronized
	
	synchronized public int getMissed() {
		return missedWords;
	}

	synchronized public int getCaught() {
		return caughtWords;
	}
	
	synchronized public int getTotal() {
		return (missedWords+caughtWords);
	}

	synchronized public int getScore() {
		return gameScore;
	}
	
	synchronized public void missedWord() {
		missedWords++;
	}
	synchronized public void caughtWord(int length) {
		caughtWords++;
		gameScore+=length;
	}

	synchronized public void reset() {
		caughtWords=0;
		missedWords=0;
		gameScore=0;
	}
}
