package fishinggame;

import java.awt.*;

public class Player extends GameObject {
    private int score;
    private int fishCaught;
    private final int speed = 6;

    public Player(int x, int y) { super(x, y, 42, 48); }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(245, 190, 80)); g.fillOval(getX() + 12, getY(), 20, 20);
        g.setColor(new Color(70, 110, 180)); g.fillRect(getX() + 8, getY() + 18, 28, 27);
        g.setColor(Color.DARK_GRAY); g.drawLine(getX() + 35, getY() + 25, getX() + 35, getY() + 48);
    }

    public void move(int dx, int minX, int maxX) {
        setX(Math.max(minX, Math.min(maxX - getWidth(), getX() + dx * speed)));
    }
    public void catchFish(int points) { fishCaught++; score += points; }
    public void hitObstacle(int penalty) { score = Math.max(0, score - penalty); }
    public void resetFishCaught() { fishCaught = 0; }   // per-level counter, score is kept
    public void resetStats() { score = 0; fishCaught = 0; }
    public int getScore() { return score; }
    public int getFishCaught() { return fishCaught; }
}