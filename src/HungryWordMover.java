

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

//import static TypingTutorApp.gameWindow;

public class HungryWordMover extends Thread {
    private HungryWord myWord;

    private  FallingWord myWord2;
    private int noWords=6;
    //private FallingWord[] myWord2;
    private AtomicBoolean done;
    private AtomicBoolean pause;
    private Score score;
    CountDownLatch startLatch; //so all can start at once
    //static WordDictionary dict = new WordDictionary();

    HungryWordMover(HungryWord word) {
        myWord = word;
    }

    HungryWordMover(HungryWord word,FallingWord myWord2,WordDictionary dict, Score score,
                    CountDownLatch startLatch, AtomicBoolean d, AtomicBoolean p) {
        this(word);
        this.myWord2 = myWord2;
        this.startLatch = startLatch;
        this.score=score;
        this.done=d;
        this.pause=p;
    }



    public void run() {

        //System.out.println(myWord.getWord() + " falling speed = " + myWord.getSpeed());
        try {
            System.out.println(myWord.getWord() + " waiting to start " );
            startLatch.await();
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } //wait for other threads to start
        System.out.println(myWord.getWord() + " started" );
        while (!done.get()) {
            //animate the word
            while (!myWord.dropped() && !done.get()) {
                int randomWord = (int)(Math.random() *100); 
                if(randomWord>10){
                myWord.drop(20);}
                try {
                    sleep(myWord.getSpeed());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                };
                while(pause.get()&&!done.get()) {};
            }
            if (!done.get() && myWord.dropped()) {
                score.missedWord();
                myWord.resetWord();
            }
            for (int i=0;i<noWords;i++) {
                //words[i] = new FallingWord(dict.getNewWord(), gameWindow.getValidXpos(), 860);
                //myWord2 = words[i];//acess
                //e = myWord.getY();

                //myWord2 = new FallingWord( r, w, e,500);
                if (!done.get() && myWord2.getX() < myWord.getX() + myWord.getWidth() &&
                        myWord2.getX() + myWord2.getWidth() > myWord.getX() &&
                        myWord2.getY() < myWord.getY() + myWord.getHeight() &&
                        myWord2.getY() + myWord2.getHeight() > myWord.getY()) {
                        System.out.println("heelo "+myWord2.getWord());
                    score.missedWord();
                    myWord.resetWord();

                }
            }
            myWord.resetWord();
        }
    }

}
