package fishinggame;

import java.awt.*;

public class Fish extends GameObject {
    private final String name;
    private final int score;
    private final int speed;
    private int direction = 1;

    public Fish(String name, int x, int y, int score, int speed) {
        super(x, y, name.equals("Big Fish") ? 42 : 30, name.equals("Big Fish") ? 22 : 16);
        this.name = name;
        this.score = score;
        this.speed = speed;
    }

    public void update(int minX, int maxX) {
        setX(getX() + speed * direction);
        if (getX() <= minX || getX() + getWidth() >= maxX) direction *= -1;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(name.equals("Big Fish") ? new Color(230, 120, 60) : new Color(70, 150, 220));
        g.fillOval(getX(), getY(), getWidth(), getHeight());

        // tail behind the fish, eye at the front
        int tailBase = direction == 1 ? getX() : getX() + getWidth();
        int[] tx = {tailBase, tailBase - 10 * direction, tailBase};
        int[] ty = {getY() + 4, getY() + getHeight() / 2, getY() + getHeight() - 4};
        g.fillPolygon(tx, ty, 3);

        g.setColor(Color.BLACK);
        int eyeX = direction == 1 ? getX() + getWidth() - 10 : getX() + 6;
        g.fillOval(eyeX, getY() + 4, 4, 4);
    }

    public String getName() { return name; }
    public int getScore() { return score; }
}