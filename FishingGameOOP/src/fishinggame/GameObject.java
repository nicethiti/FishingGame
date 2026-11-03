package fishinggame;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class GameObject {
    private int x, y, width, height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }
    public abstract void draw(Graphics2D g);
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
