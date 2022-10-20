public class HungryWord {
    private String word; // the word
    private int x; //position - width
    private int y; // postion - height
    private int maxY; //maximum height
    private  int height;
    private int width;
    private int maxX; //maximum width
    private boolean dropped; //flag for if user does not manage to catch word in time

    private int fallingSpeed; //how fast this word is
    private static int maxWait=1000;
    private static int minWait=100;

    public static WordDictionary dict;

    HungryWord() { //constructor with defaults
        word="computer"; // a default - not used
        x=0;
        y=300;
        maxX=300;
        dropped=false;
        fallingSpeed=(int)(Math.random() * (maxWait-minWait)+minWait);
    }
     HungryWord(String s, int x, int y, int maxX){
        this.word = s;
        this.x=x;
        this.y=y;
        this.maxX = maxX;
    }

    HungryWord(String text) {
        this();
        this.word=text;
    }

    HungryWord(String text,int y, int maxX) { //most commonly used constructor - sets it all.
        this(text);
        this.y=y; //only need to set x, word is at top of screen at start
        this.maxX=maxX;
    }
    public synchronized void setHeight(int h) {
        this.height=h;
    }

    public synchronized void setWidth(int w) {
        this.width=w;
    }

    public synchronized int getHeight(){
        return height;

    }

    public synchronized int getWidth(){
        return width;

    }

    public static void increaseSpeed( ) {
        minWait+=50;
        maxWait+=50;
    }

    public static void resetSpeed( ) {
        maxWait=1000;
        minWait=100;
    }


    // all getters and setters must be synchronized
    public synchronized  void setX(int x) {
        if (x>maxX) {
            x=0;
            dropped=true; //user did not manage to catch this word
        }
        this.x=x;
    }

    public synchronized  void setY(int y) {
        this.y=y;
    }

    public synchronized  void setWord(String text) {
        this.word=text;
    }

    public synchronized  String getWord() {
        return word;
    }

    public synchronized  int getX() {
        return x;
    }

    public synchronized  int getY() {
        return y;
    }

    public synchronized  int getSpeed() {
        return fallingSpeed;
    }

    public synchronized void setPos(int x, int y) {
        setY(y);
        setX(x);
    }
    public synchronized void resetPos() {
        setX(0);
    }

    public synchronized void resetWord() {
        WordDictionary d=new WordDictionary();
        resetPos();
        word=d.getNewWord();
        dropped=false;
        fallingSpeed=(int)(Math.random() * (maxWait-minWait)+minWait);
        //System.out.println(getWord() + " falling speed = " + getSpeed());
    }

    public synchronized boolean matchWord(String typedText) {
        //System.out.println("Matching against: "+text);
        if (typedText.equals(this.word)) {
            resetWord();
            return true;
        }
        else
            return false;
    }

    public synchronized  void drop(int inc) {
        setX(x+inc);
    }

    public synchronized  boolean dropped() {
        return dropped;
    }
}
