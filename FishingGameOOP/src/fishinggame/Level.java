package fishinggame;

import java.awt.*;
import java.util.*;

public class Level {
    private final int number;
    private final String name;
    private final Color waterColor;
    private final int targetFish;
    private final int timeLimit;
    private final ArrayList<Fish> fish = new ArrayList<>();
    private final ArrayList<Obstacle> obstacles = new ArrayList<>();

    public Level(int number) {
        this.number = number;
        if (number == 1) {
            name = "Shallow Pond"; waterColor = new Color(130,205,220); targetFish = 5; timeLimit = 45;
            obstacles.add(new Obstacle(Obstacle.Type.ROCK, 180, 430));
            obstacles.add(new Obstacle(Obstacle.Type.ROCK, 520, 390));
            addFish(4, 2);
        } else if (number == 2) {
            name = "River"; waterColor = new Color(80,170,205); targetFish = 7; timeLimit = 50;
            obstacles.add(new Obstacle(Obstacle.Type.ROCK, 130, 420));
            obstacles.add(new Obstacle(Obstacle.Type.ROCK, 450, 400));
            obstacles.add(new Obstacle(Obstacle.Type.TRASH, 650, 360));
            addFish(5, 3);
        } else {
            name = "Deep Sea"; waterColor = new Color(35,105,165); targetFish = 9; timeLimit = 55;
            obstacles.add(new Obstacle(Obstacle.Type.ROCK, 100, 420));
            obstacles.add(new Obstacle(Obstacle.Type.TRASH, 350, 390));
            obstacles.add(new Obstacle(Obstacle.Type.MINE, 590, 350));
            obstacles.add(new Obstacle(Obstacle.Type.MINE, 760, 430));
            addFish(6, 4);
        }
    }
    private void addFish(int small, int big) {
        Random r = new Random(number * 100);
        for (int i=0;i<small;i++) fish.add(new Fish("Small Fish", 100+r.nextInt(700), 120+r.nextInt(280), 10, 2+number));
        for (int i=0;i<big;i++) fish.add(new Fish("Big Fish", 120+r.nextInt(680), 150+r.nextInt(250), 20, 1+number));
    }
    public int getNumber(){return number;} public String getName(){return name;} public Color getWaterColor(){return waterColor;}
    public int getTargetFish(){return targetFish;} public int getTimeLimit(){return timeLimit;}
    public ArrayList<Fish> getFish(){return fish;} public ArrayList<Obstacle> getObstacles(){return obstacles;}
}
