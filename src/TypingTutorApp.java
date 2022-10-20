

import javax.swing.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;


import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
//model is separate from the view.

public class TypingTutorApp {
//shared class variables
	static int noWords=4;
	static int totalWords;

   	static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;

	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

	static FallingWord[] words;

	static WordMover[] wrdShft;
	static HungryWord[] hungryWord;
	static HungryWordMover[] hungryWordMover;
	static CountDownLatch startLatch; //so threads can start at once

	static AtomicBoolean started;
	static AtomicBoolean pause;
	static AtomicBoolean done;
	static AtomicBoolean won;

	static Score score = new Score();
	static GamePanel gameWindow;
	static ScoreUpdater scoreD ;
	static Thread gameWindowThread;
	static Thread scoreThread;
	static FallingWord myWord;

	public static void setupGUI(int frameX,int frameY,int yLimit) {
		// Frame init and dimensions
    	JFrame frame = new JFrame("Typing Tutor");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameX, frameY);

      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));
      	g.setSize(frameX,frameY);

		gameWindow = new GamePanel(words,yLimit,done,started,won,hungryWord);
		gameWindow.setSize(frameX,yLimit+100);
	    g.add(gameWindow);

	    JPanel txt = new JPanel();
	    txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS));
	    JLabel caught =new JLabel("Caught: " + score.getCaught() + "    ");
	    caught.setForeground(Color.blue);
	    JLabel missed =new JLabel("Missed:" + score.getMissed()+ "    ");
	    missed.setForeground(Color.red);
	    JLabel scr =new JLabel("Score:" + score.getScore()+ "    ");

	    txt.add(caught);
	    txt.add(missed);
	    txt.add(scr);

	    scoreD = new ScoreUpdater(caught, missed,scr,score,done,won,totalWords);      //thread to update score

	   final JTextField textEntry = new JTextField("",20);
	   textEntry.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent evt) {
	    	  //what happens when user pressed enter
	    	  if (!pause.get()) {
	    		  String text = textEntry.getText();
	    		  CatchWord catchThread = new CatchWord(text);
	    		  catchThread.start(); //set 'm running
	    		  textEntry.setText("");
	    		  textEntry.requestFocus();
	    	  }
	    	  else textEntry.setText("");
	      }
	    });

	   txt.add(textEntry);
	   txt.setMaximumSize( txt.getPreferredSize() );
	   g.add(txt);

	    JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));

        //create all the buttons
        //The Start Button
	   	JButton startB = new JButton("Start");
	    // add the listener to the jbutton to handle the "pressed" event
		startB.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	won.set(false);
		    	done.set(false);
		    	started.set(true);
				textEntry.setEnabled(true);
		    	if (pause.get()) { //this is a restart from pause
		    		pause.set(false);
		    	} else { //user quit last game
		    		score.reset();
					FallingWord.resetSpeed();
		    		done.set(false);
					startLatch = new CountDownLatch(1); //so threads can start at once
					createWordMoverThreads();
					HungryT();
					//create new threads for next game
			    	startLatch.countDown(); //set wordMovers going - must have barrier[]
		    	}
		    	textEntry.requestFocus();
		      }
		});//finish addActionListener

	   //the Pause Button
		JButton pauseB = new JButton("Pause");;
		// add the listener to the jbutton to handle the "pressed" event
		pauseB.addActionListener(new ActionListener(){
				   public void actionPerformed(ActionEvent e){
					   	pause.set(true);  // signal pause
					   	done.set(false); //double check for safety
					   textEntry.setEnabled(false);
				      }
	    }); //finish addActionListener

		//the QuitGameButton
	     JButton quitB = new JButton("Quit Game");;
	    // add the listener to the jbutton to handle the "pressed" event
		quitB.addActionListener(new ActionListener() {
				  public void actionPerformed(ActionEvent e) {
					  done.set(true);  // signal stop
					  pause.set(false); //set for safety
					  gameWindow.repaint();
					 //word movers waiting on starting line
					   	for (int i=0;i<noWords;i++) {
					     		try {
					     			if (wrdShft[i].isAlive())	{
									wrdShft[i].join();}
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
					    }
				}
		});  //finish addActionListener

		//the Exit Button
		JButton endB = new JButton("Exit");;
				// add the listener to the jbutton to handle the "pressed" event
				endB.addActionListener(new ActionListener()
			    {
			      public void actionPerformed(ActionEvent e)
			      {
			    	  System.exit(0);
			      }
			    });

       //add all the buttons
		b.add(startB);
		b.add(pauseB);
		b.add(quitB);
		b.add(endB);
		g.add(b);

      	frame.setLocationRelativeTo(null);  // Center window on screen.
      	frame.add(g); //add contents to window
        frame.setContentPane(g);
       	//frame.pack();  // don't do this - packs it into small space
        frame.setVisible(true);
	}


	public static void createThreads() {
		score.reset();
        //main Display Thread
      	gameWindowThread = new Thread(gameWindow);  //updating panel
        //scoreThread - for updating score
      	scoreThread = new Thread(scoreD);
       	scoreThread.start();
    	gameWindowThread.start();
    	createWordMoverThreads();

	}

	public static void HungryT() {
		score.reset();
		//initialize shared array of current words with the words for this game
		for (int i=0;i<1;i++) {
			hungryWord[i]=new HungryWord(dict.getNewWord(),240,900);
		}

		//create threads to move them
		for (int i=0;i<1;i++) {
			hungryWordMover[i]=new HungryWordMover(hungryWord[0],words[i],dict,score,startLatch,done,pause);
		}

		//word movers waiting on starting line
		for (int i=0;i<1;i++) {
			hungryWordMover[i].start();
		}

	}

	public static void createWordMoverThreads() {
		score.reset();
	  	//initialize shared array of current words with the words for this game
		for (int i=0;i<noWords;i++) {
			words[i]=new FallingWord(dict.getNewWord(),gameWindow.getValidXpos(),860);
		}
		//create threads to move them
	    for (int i=0;i<noWords;i++) {
	    		wrdShft[i] = new WordMover(words[i],dict,score,startLatch,done,pause);
	    }
     	for (int i=0;i<noWords;i++) {
     		wrdShft[i] .start();
     	}
	}


public static String[] getDictFromFile(String filename) {
	//read in the list of words.
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt(); //file starts with number of words in file
			dictStr=new String[dictLength];
			for (int i=0;i<dictLength;i++) {
				dictStr[i]=new String(dictReader.next());
				//System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;
	}

public static void main(String[] args) {
		started=new AtomicBoolean(false);
		done = new AtomicBoolean(false);
		pause = new AtomicBoolean(false);
		won = new AtomicBoolean(false);

		totalWords=24;
		noWords=6;
		dict= new WordDictionary();

		//deal with command line arguments
		if (args.length==2) {
					totalWords=Integer.parseInt(args[0]);  //total words to fall
					noWords=Integer.parseInt(args[1]); // total words falling at any point
					assert(totalWords>=noWords); //
		} else if (args.length==3) {
					totalWords=Integer.parseInt(args[0]);  //total words to fall
					noWords=Integer.parseInt(args[1]); // total words falling at any point
					assert(totalWords>=noWords); //
					String[] tmpDict=getDictFromFile(args[2]); //file of words
					if (tmpDict!=null)
						dict= new WordDictionary(tmpDict);
		}

		FallingWord.dict=dict; //set the class dictionary for the words.

		words = new FallingWord[noWords];  //array for the  current chosen words from dict
		wrdShft = new WordMover[noWords]; //array for the threads that animate the words
		hungryWord=new HungryWord[1];
		hungryWordMover=new HungryWordMover[1];
		CatchWord.setWords(words);  //class setter - static method
		CatchWord.setScore(score);  //class setter - static method
		CatchWord.setFlags(done,pause); //class setter - static method

		setupGUI(frameX, frameY, yLimit);

 		startLatch = new CountDownLatch(1); //REMOVE so threads can start at once
    	createThreads();
       	}
	}

