package ru.spbstu.icc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameController {
    private final MyGame game;

    private long lastFoodSpawn;
    private int totalScore;
    private int speedOfFood;
    private int speedOfDrones;
    private boolean needToContinue;

    private Sound firstCatch;
    private Sound millionaire;
    private Sound chikibriki;

    private Array<Rectangle> flyingObj;
    private Array<Drone> drones;
    private Array<Drone> dronesWithFood;

    public GameController(MyGame game) {
        this.game = game;
        flyingObj = new Array<>();
        spawnFood();
        drones = new Array<>();
        dronesWithFood = new Array<>();
        speedOfFood = 400;
        speedOfDrones = 1000;

        firstCatch = Gdx.audio.newSound(Gdx.files.internal("firstCatch.mp3"));
        millionaire = Gdx.audio.newSound(Gdx.files.internal("millionaire.mp3"));
        chikibriki = Gdx.audio.newSound(Gdx.files.internal("chikibriki.mp3"));

        needToContinue = true;
    }

    private void moveFood() {
        if (!game.isPause()) {
            for (Rectangle food : flyingObj) {
                food.x -= speedOfFood * Gdx.graphics.getDeltaTime();
                if (food.x < 0) {
                    needToContinue = false;
                    dispose();
                }
            }
        }
    }

    private void moveDrones() {
        if (!game.isPause()) {
            Iterator<Drone> j = drones.iterator();
            while (j.hasNext()) {
                Drone drone = j.next();
                double angle = drone.getAngle();
                drone.setX(drone.getX() + speedOfDrones * Math.cos(angle) * Gdx.graphics.getDeltaTime());
                drone.setY(drone.getY() + speedOfDrones * Math.sin(angle) * Gdx.graphics.getDeltaTime());
                if (drone.getX() > game.getWidth() || drone.getY() > game.getHeight()) j.remove();
            }
        }
    }

    private void moveDroneWithFood() {
        if (!game.isPause()) {
            Iterator<Drone> k = dronesWithFood.iterator();
            while (k.hasNext()) {
                Drone drone = k.next();
                double angle = drone.getAngle();
                drone.setX(drone.getX() - speedOfDrones * Math.cos(angle) * Gdx.graphics.getDeltaTime());
                drone.setY(drone.getY() - speedOfDrones * Math.sin(angle) * Gdx.graphics.getDeltaTime());
                if (drone.getX() < 0 || drone.getY() < 0) k.remove();
            }
        }
    }

    private void checkCollision() {
        if (!game.isPause()) {
            Iterator<Drone> d = drones.iterator();
            Iterator<Rectangle> f = flyingObj.iterator();
            try {
                while (f.hasNext()) {
                    Rectangle food = f.next();
                    while (d.hasNext()) {
                        Drone currentDrone = d.next();
                        Rectangle rectangleOfDrone = currentDrone.getRectangle();
                        if (food.overlaps(rectangleOfDrone)) {
                            f.remove();
                            d.remove();
                            totalScore++;
                            if (totalScore == 1) {
                                firstCatch.play();
                            } else if (totalScore == 10) {
                                chikibriki.play();
                            } else if (totalScore == 50) {
                                millionaire.play();
                            }
                            spawnFoodWithDrone(currentDrone.getAngle(), rectangleOfDrone.x, rectangleOfDrone.y);
                            speedOfFood = changeSpeed();
                        }
                    }
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }

    private void spawnFoodWithDrone(double angle, float x, float y) {
        Rectangle drone = new Rectangle();
        drone.x = x;
        drone.y = y;
        drone.height = 60;
        drone.width = 139;
        dronesWithFood.add(new Drone(drone, angle));
    }

    private void spawnFood() {
        if (!game.isPause()) {
            Rectangle food = new Rectangle();
            food.x = game.getWidth();
            food.y = MathUtils.random(0, game.getHeight() - 64);
            food.height = 64;
            food.width = 82;
            flyingObj.add(food);
            lastFoodSpawn = TimeUtils.nanoTime();
        }
    }

    private int changeSpeed() {
        if (totalScore < 10) {
            return 400;
        } else if (totalScore < 20) {
            return 500;
        } else if (totalScore < 30) {
            return 600;
        } else if (totalScore < 40) {
            return 700;
        } else if (totalScore < 50) {
            return 900;
        } else if (totalScore < 60) {
            return 1100;
        } else if (totalScore < 80) {
            return 1300;
        } else if (totalScore < 100) {
            return 1500;
        } else return 2000;
    }

    public Array<Drone> getDrones() {
        return drones;
    }

    public Array<Drone> getDronesWithFood() {
        return dronesWithFood;
    }

    public Array<Rectangle> getFlyingObj() {
        return flyingObj;
    }

    public boolean isNeedToContinue() {
        return needToContinue;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void addDrone(Rectangle drone, double angle) {
        drones.add(new Drone(drone, angle));
    }

    public void changeGameState() {
        if (TimeUtils.nanoTime() - lastFoodSpawn > game.ONE_SECOND) spawnFood();
        moveFood();
        moveDrones();
        moveDroneWithFood();
        checkCollision();
    }

    public void dispose() {
        firstCatch.dispose();
        millionaire.dispose();
        chikibriki.dispose();
    }
}
