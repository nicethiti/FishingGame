package fishinggame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.Iterator;

public class FishingGame extends JFrame {
    public FishingGame() {
        setTitle("Fishing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(FishingGame::new); }

    static class GamePanel extends JPanel implements KeyListener, MouseListener {
        private static final int W = 900, H = 600;
        private static final int WATER_TOP = 105;      // where the water starts / hook starts
        private static final int HOOK_SPEED = 5;
        private static final int HOOK_MAX_Y = 560;
        private static final int HOOK_WIDTH = 14;
        private static final int MESSAGE_TICKS = 50;   // 50 ticks x 40 ms = 2 seconds
        private static final int MAX_LEVEL = 3;
        private static final String HELP = "[\u2190 \u2192] Move  |  [SPACE] Drop Hook  |  [R] Home";

        private Player player = new Player(430, 60);
        private int levelNo = 1, timeLeft;
        private Level level = new Level(1);
        private Timer timer;
        private boolean started = false, hook = false, gameOver = false, win = false;
        private boolean leftPressed = false, rightPressed = false;
        private int hookY = WATER_TOP;
        private String message = "";
        private int messageTicks = 0;
        private long lastSecond = System.currentTimeMillis();
        private final Rectangle startButton = new Rectangle(350, 390, 200, 58);

        GamePanel() {
            setPreferredSize(new Dimension(W, H));
            setFocusable(true);
            addKeyListener(this);
            addMouseListener(this);
            startLevel();
            timer = new Timer(40, e -> updateGame());
            timer.start();
        }

        //game logic
        void startLevel() {
            level = new Level(levelNo);
            timeLeft = level.getTimeLimit();
            hook = false;
            hookY = WATER_TOP;
            lastSecond = System.currentTimeMillis();
        }

        void startGame() {
            started = true;
            levelNo = 1;
            player = new Player(430, 60);
            gameOver = false;
            win = false;
            leftPressed = rightPressed = false;
            messageTicks = 0;
            startLevel();
            requestFocusInWindow();
        }

        void showMessage(String text) { message = text; messageTicks = MESSAGE_TICKS; }

        void updateGame() {
            if (started && !gameOver && !win) {
                movePlayer();
                for (Fish f : level.getFish()) f.update(40, W - 40);
                if (hook) updateHook();
                checkLevelProgress();
                updateTimer();
                if (messageTicks > 0) messageTicks--;
            }
            repaint();
        }

        private void movePlayer() {
            int dir = (rightPressed ? 1 : 0) - (leftPressed ? 1 : 0);
            if (dir != 0) player.move(dir, 10, W - 10);
        }

        private void updateHook() {
            hookY += HOOK_SPEED;
            if (hookY > HOOK_MAX_Y) { resetHook(); return; }

            Rectangle hb = new Rectangle(player.getX() + player.getWidth() / 2 - HOOK_WIDTH / 2,
                    WATER_TOP, HOOK_WIDTH, Math.max(1, hookY - WATER_TOP));

            for (Iterator<Fish> it = level.getFish().iterator(); it.hasNext(); ) {
                Fish f = it.next();
                if (hb.intersects(f.getBounds())) {
                    player.catchFish(f.getScore());
                    it.remove();
                    resetHook();
                    showMessage("Caught " + f.getName() + "  +" + f.getScore());
                    return;
                }
            }
            for (Obstacle o : level.getObstacles()) {
                if (hb.intersects(o.getBounds())) {
                    player.hitObstacle(o.getPenalty());
                    resetHook();
                    showMessage("Hit " + o.getTypeName() + "  -" + o.getPenalty());
                    return;
                }
            }
        }

        private void resetHook() { hook = false; hookY = WATER_TOP; }

        private void checkLevelProgress() {
            if (player.getFishCaught() < level.getTargetFish()) return;
            if (levelNo < MAX_LEVEL) {
                levelNo++;
                player.resetFishCaught();   // count fish per level, keep the score
                startLevel();
                showMessage("Level " + (levelNo - 1) + " clear!  Next: Level " + levelNo);
            } else {
                win = true;
            }
        }

        private void updateTimer() {
            long now = System.currentTimeMillis();
            if (now - lastSecond >= 1000) {
                lastSecond = now;
                timeLeft--;
                if (timeLeft <= 0) gameOver = true;
            }
        }

        //drawing
        @Override
        protected void paintComponent(Graphics gg) {
            super.paintComponent(gg);
            Graphics2D g = (Graphics2D) gg;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (!started) { paintHome(g); return; }

            g.setColor(level.getWaterColor()); g.fillRect(0, 0, W, H);
            g.setColor(new Color(235, 220, 150)); g.fillRect(0, 0, W, WATER_TOP);
            g.setColor(new Color(50, 140, 70)); g.fillRect(0, 75, W, 30);
            g.setColor(new Color(255, 255, 255, 150)); g.fillOval(690, 18, 60, 20); g.fillOval(720, 12, 50, 27);

            for (Obstacle o : level.getObstacles()) o.draw(g);
            for (Fish f : level.getFish()) f.draw(g);
            player.draw(g);
            if (hook) {
                int hx = player.getX() + player.getWidth() / 2;
                g.setColor(new Color(45, 45, 45));
                g.drawLine(hx, WATER_TOP, hx, hookY);
                g.fillOval(hx - 5, hookY - 3, 10, 10);
            }

            drawHud(g);
            drawFooter(g);
            if (gameOver || win) drawEndPanel(g);
        }

        
        private void drawHud(Graphics2D g) {
            int[] cx = {112, 337, 562, 787};
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            center(g, "Level " + level.getNumber() + ": " + level.getName(), cx[0], 32);
            center(g, "Fish: " + player.getFishCaught() + " / " + level.getTargetFish(), cx[1], 32);
            center(g, "Score: " + player.getScore(), cx[2], 32);
            center(g, "Time: " + timeLeft, cx[3], 32);
            if (messageTicks > 0) {
                g.setFont(new Font("SansSerif", Font.BOLD, 16));
                center(g, message, W / 2, 62);
            }
        }

        private void drawFooter(Graphics2D g) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRoundRect(W / 2 - 215, 566, 430, 28, 14, 14);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            center(g, HELP, W / 2, 585);
        }

        private void drawEndPanel(Graphics2D g) {
            g.setColor(new Color(0, 0, 0, 165));
            g.fillRoundRect(W / 2 - 215, 190, 430, 210, 24, 24);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 38));
            center(g, win ? "You win!" : "Time out!", W / 2, 258);
            g.setFont(new Font("SansSerif", Font.PLAIN, 20));
            center(g, "Total score: " + player.getScore(), W / 2, 305);
            center(g, "Press R for Home", W / 2, 345);
        }

        private void paintHome(Graphics2D g) {
            g.setPaint(new GradientPaint(0, 0, new Color(160, 222, 244), 0, H, new Color(49, 156, 190)));
            g.fillRect(0, 0, W, H);
            g.setColor(new Color(255, 224, 117)); g.fillOval(735, 45, 78, 78);
            g.setColor(new Color(255, 255, 255, 190));
            g.fillOval(90, 75, 90, 30); g.fillOval(125, 58, 72, 46); g.fillOval(165, 76, 65, 27);
            g.fillOval(565, 105, 75, 25); g.fillOval(595, 88, 60, 38); g.fillOval(630, 105, 55, 25);
            drawHills(g);
            g.setColor(new Color(48, 157, 187)); g.fillRect(0, 250, W, 350);
            g.setColor(new Color(255, 255, 255, 75));
            for (int i = 0; i < 12; i++) g.drawArc(25 + i * 78, 290 + (i % 3) * 65, 50, 15, 0, 180);

            
            g.setColor(new Color(135, 92, 55)); g.fillRect(95, 240, 210, 18); g.fillRect(115, 258, 14, 72); g.fillRect(265, 258, 14, 72);
            g.setColor(new Color(56, 72, 91)); g.fillOval(172, 172, 34, 34);
            g.setColor(new Color(241, 190, 145)); g.fillOval(178, 178, 23, 23);
            g.setColor(new Color(54, 120, 80)); g.fillRoundRect(169, 204, 43, 52, 10, 10);
            g.setColor(new Color(35, 55, 70)); g.fillRect(176, 252, 11, 30); g.fillRect(194, 252, 11, 30);
            g.setColor(new Color(90, 60, 35)); g.setStroke(new BasicStroke(3));
            g.drawLine(205, 212, 275, 150); g.drawLine(275, 150, 300, 315);
            g.setStroke(new BasicStroke(1));

            drawHomeFish(g, 420, 340, 1.0f, new Color(255, 190, 85));
            drawHomeFish(g, 610, 420, 0.75f, new Color(255, 222, 115));
            drawHomeFish(g, 735, 330, 0.6f, new Color(255, 190, 85));

           
            g.setColor(new Color(18, 68, 89, 210)); g.fillRoundRect(215, 65, 470, 125, 28, 28);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 42)); center(g, "FISHING GAME", W / 2, 120);
            g.setFont(new Font("SansSerif", Font.PLAIN, 19)); center(g, "Catch the fish, avoid the obstacles!", W / 2, 158);

            g.setColor(new Color(255, 255, 255, 225)); g.fillRoundRect(215, 220, 470, 78, 18, 18);
            g.setColor(new Color(25, 78, 95));
            g.setFont(new Font("SansSerif", Font.BOLD, 19)); center(g, "How to Play", W / 2, 251);
            g.setFont(new Font("SansSerif", Font.PLAIN, 16)); center(g, HELP, W / 2, 280);

            Point mouse = getMousePositionSafe();
            boolean hover = mouse != null && startButton.contains(mouse);
            g.setColor(hover ? new Color(35, 125, 75) : new Color(46, 151, 89));
            g.fillRoundRect(startButton.x, startButton.y, startButton.width, startButton.height, 20, 20);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 25));
            centerIn(g, "Start", startButton);
        }

        //hills
        private void drawHills(Graphics2D g) {
            Path2D far = new Path2D.Double();
            far.moveTo(0, 250);
            far.lineTo(0, 218);
            far.curveTo(80, 175, 190, 175, 290, 228);
            far.curveTo(370, 185, 470, 175, 570, 220);
            far.curveTo(660, 180, 770, 170, 900, 210);
            far.lineTo(900, 250);
            far.closePath();
            g.setColor(new Color(128, 192, 140));
            g.fill(far);

            Path2D near = new Path2D.Double();
            near.moveTo(0, 250);
            near.lineTo(0, 236);
            near.curveTo(100, 208, 210, 208, 310, 242);
            near.curveTo(410, 214, 520, 214, 620, 242);
            near.curveTo(720, 214, 820, 214, 900, 236);
            near.lineTo(900, 250);
            near.closePath();
            g.setColor(new Color(97, 172, 111));
            g.fill(near);
        }

        private Point getMousePositionSafe() { try { return getMousePosition(); } catch (Exception ex) { return null; } }

        private void drawHomeFish(Graphics2D g, int x, int y, float scale, Color c) {
            Graphics2D q = (Graphics2D) g.create();
            q.translate(x, y); q.scale(scale, scale);
            q.setColor(c); q.fillOval(0, 0, 55, 28);
            int[] fx = {4, 4, -14}; int[] fy = {14, 2, 25};
            q.fillPolygon(fx, fy, 3);
            q.setColor(new Color(30, 70, 80)); q.fillOval(39, 8, 5, 5);
            q.dispose();
        }

        
        private void center(Graphics2D g, String s, int x, int y) {
            FontMetrics fm = g.getFontMetrics();
            g.drawString(s, x - fm.stringWidth(s) / 2, y);
        }

        
        private void centerIn(Graphics2D g, String s, Rectangle r) {
            FontMetrics fm = g.getFontMetrics();
            int x = r.x + (r.width - fm.stringWidth(s)) / 2;
            int y = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(s, x, y);
        }

        //input 
        @Override
        public void keyPressed(KeyEvent e) {
            int k = e.getKeyCode();
            if (!started) {
                if (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_SPACE) startGame();
                return;
            }
            if (k == KeyEvent.VK_R) {
                started = false; gameOver = false; win = false; hook = false;
                leftPressed = rightPressed = false;
                repaint();
                return;
            }
            if (gameOver || win) return;
            if (k == KeyEvent.VK_LEFT) leftPressed = true;
            if (k == KeyEvent.VK_RIGHT) rightPressed = true;
            if (k == KeyEvent.VK_SPACE && !hook) { hook = true; hookY = WATER_TOP; }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
        }

        @Override
        public void mouseClicked(MouseEvent e) { if (!started && startButton.contains(e.getPoint())) startGame(); }

        public void keyTyped(KeyEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }
}