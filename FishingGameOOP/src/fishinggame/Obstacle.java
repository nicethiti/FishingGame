package fishinggame;

import java.awt.*;

public class Obstacle extends GameObject {
    public enum Type { ROCK, TRASH, MINE }
    private final Type type;
    private final int penalty;

    public Obstacle(Type type, int x, int y) {
        super(x, y, 38, 30);
        this.type = type;
        this.penalty = type == Type.MINE ? 30 : (type == Type.TRASH ? 15 : 10);
    }

    @Override
    public void draw(Graphics2D g) {
        if (type == Type.ROCK) {
            g.setColor(new Color(100, 100, 100));
            g.fillOval(getX(), getY() + 8, getWidth(), 22);
        } else if (type == Type.TRASH) {
            g.setColor(new Color(70, 160, 90));
            g.fillRect(getX() + 8, getY() + 5, 22, 25);
            drawCenteredLabel(g, "X", getX() + 19, getY() + 17);
        } else {
            g.setColor(Color.RED);
            g.fillOval(getX() + 3, getY() + 2, 32, 32);
            drawCenteredLabel(g, "!", getX() + 19, getY() + 18);
        }
    }

    
    private void drawCenteredLabel(Graphics2D g, String s, int cx, int cy) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, cx - fm.stringWidth(s) / 2, cy + (fm.getAscent() - fm.getDescent()) / 2);
    }

    public Type getType() { return type; }
    public int getPenalty() { return penalty; }
    public String getTypeName() { return type == Type.ROCK ? "Rock" : type == Type.TRASH ? "Trash" : "Mine"; }
}