

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;

import  java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;

public class GamePanel extends JPanel implements Runnable {
    private AtomicBoolean done; //REMOVE
    private AtomicBoolean started; //REMOVE
    private AtomicBoolean won; //REMOVE

    private FallingWord[] words;
    private int noWords;
    private final static int borderWidth = 25; //appearance - border
    private HungryWord[] hungryWord;

    GamePanel(FallingWord[] words, int maxY,
              AtomicBoolean d, AtomicBoolean s, AtomicBoolean w,HungryWord[] hungryWord) {
        this.words = words; //shared word list
        noWords = words.length; //only need to do this once
        done = d; //REMOVE
        started = s; //REMOVE
        won = w; //REMOVE
        this.hungryWord = hungryWord;
    }

    public void paintComponent(Graphics g) {
        int width = getWidth() - borderWidth * 2;
        int height = getHeight() - borderWidth * 2;
        g.clearRect(borderWidth, borderWidth, width, height);//the active space
        g.setColor(Color.pink); //change colour of pen
        g.fillRect(borderWidth, height, width, borderWidth); //draw danger zone

        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 26));
        //draw the words
        if (!started.get()) {
            g.setFont(new Font("Arial", Font.BOLD, 26));
            g.drawString("Type all the words before they hit the red zone,press enter after each one.", borderWidth * 2, height / 2);

        } else if (!done.get()) {
            FontMetrics fm = g.getFontMetrics(new Font("Arial",Font.PLAIN,26));
            for (int i = 0; i < noWords; i++) {
                g.drawString(words[i].getWord(), words[i].getX() + borderWidth, words[i].getY());
                Rectangle2D bounds = fm.getStringBounds(words[i].getWord(), g); //falling

                Rectangle2D bounds2 = fm.getStringBounds(hungryWord[0].getWord(), g);//crossing

                int fallWidth = (int)bounds.getWidth();
                int fallHeight = (int)bounds.getHeight();
                words[i].setHeight(fallHeight);
                words[i].setWidth(fallWidth);
                int crossWidth = (int)bounds2.getWidth()*10;
                int crossHeight = (int)bounds2.getHeight();
                hungryWord[0].setHeight(crossHeight);
                hungryWord[0].setWidth(crossWidth);
            }
            g.setColor(Color.lightGray); //change colour of pen
            g.fillRect(borderWidth, 0, width, borderWidth);

            for (int i = 0; i < 1; i++) {
                g.setColor(Color.green);
                g.drawString(hungryWord[i].getWord(), hungryWord[i].getX() , hungryWord[i].getY()+ borderWidth);
            }

        } else {
            if (won.get()) {
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.drawString("Well done!", width / 3, height / 2);
            } else {
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.drawString("Game over!", width / 2, height / 2);
            }
        }
    }

    public int getValidXpos() {
        int width = getWidth() - borderWidth * 4;
        int x = (int) (Math.random() * width);
        return x;
    }

    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ;
        }
    }

}


