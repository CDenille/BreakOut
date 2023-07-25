package breakout;

public class Scoring {
    private int score;
    private int lives;
    
    public void addPoints(double y) {
        if (y==35 || y==51) {
            score+=7;
        }
        if(y==67 || y==83) {
        score+=5;
        }
        if (y==99 || y==115) {
            score+=3;
        }else {
            score++;
        }
    }
    public void loseLives() {
        lives--;
    }
    public int getScore() {
        return score;
    }
    public int getLives() {
        return lives;
    }
    public void setLives() {
        this.lives=5;
    }
    public void setPoints() {
        this.score=0;
    }
}
