package ru.spbstu.icc.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import ru.spbstu.icc.model.Drone;
import ru.spbstu.icc.controller.MyGame;


import java.util.Iterator;

public class GameScreen implements Screen {

    private final MyGame game;
    private OrthographicCamera camera;

    private boolean spawnQuery;

    private Texture playerImage;
    private Texture watermelonImage;
    private Texture droneImage;
    private Texture droneWithFoodImage;
    private Texture landscape;

    private Sound firstCatch;
    private Sound millionaire;
    private Sound chikibriki;

    private long lastFoodSpawn;
    private int totalScore;
    private int speed;

    private Array<Rectangle> flyingObj;
    private Array<Drone> drones;
    private Array<Drone> dronesWithFood;


    public GameScreen(final MyGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        spawnQuery = true;

        landscape = new Texture("landscape.jpg");
        droneImage = new Texture("drone.png");
        playerImage = new Texture("player.png");
        watermelonImage = new Texture("watermelon.png");
        droneWithFoodImage = new Texture("droneWithWatermelon.png");

        firstCatch = Gdx.audio.newSound(Gdx.files.internal("firstCatch.mp3"));
        millionaire = Gdx.audio.newSound(Gdx.files.internal("millionaire.mp3"));
        chikibriki = Gdx.audio.newSound(Gdx.files.internal("chikibriki.mp3"));

        flyingObj = new Array<>();
        spawnFood();
        drones = new Array<>();
        dronesWithFood = new Array<>();
        speed = 400;
        createFont();
    }


    @Override
    public void render(float delta) {

        draw();

        checkTouch();

        if (TimeUtils.nanoTime() - lastFoodSpawn > 1000000000) spawnFood();

        moveFood();

        moveDrones();

        moveDroneWithFood();

        checkCollision();
    }

    private void moveFood() {
        if (!game.isPause()) {
            for (Rectangle food : flyingObj) {
                food.x -= speed * Gdx.graphics.getDeltaTime();
                if (food.x < 0) {
                    game.setScreen(new EndOfGameScreen(game, totalScore));
                    game.saveName();
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
                drone.setX(drone.getX() + 1000 * Math.cos(angle) * Gdx.graphics.getDeltaTime());
                drone.setY(drone.getY() + 1000 * Math.sin(angle) * Gdx.graphics.getDeltaTime());
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
                drone.setX(drone.getX() - 1000 * Math.cos(angle) * Gdx.graphics.getDeltaTime());
                drone.setY(drone.getY() - 1000 * Math.sin(angle) * Gdx.graphics.getDeltaTime());
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
                            speed = changeSpeed();
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

    private void checkTouch() {
        if (Gdx.input.justTouched() && Gdx.input.getY() < 150 && Gdx.input.getX() < 150) {
            game.setPause();
        } else if (Gdx.input.justTouched() && !game.isPause()) {
            Rectangle drone = new Rectangle();
            drone.x = 0;
            drone.y = 0;
            drone.height = 60;
            drone.width = 139;
            double angle = Math.atan((double) (game.getHeight() - Gdx.input.getY()) / (Gdx.input.getX()));
            drones.add(new Drone(drone, angle));
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

    private void createFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("19167.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.borderWidth = 1;
        parameter.color = Color.BLACK;
        game.setFont(generator, parameter);
        generator.dispose();
    }

    private void draw() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.startDrawing(camera.combined);

        game.drawBatch(landscape, 0, 0);
        game.drawBatch(playerImage, 0, 0);
        for (Rectangle food : flyingObj) {
            game.drawBatch(watermelonImage, food.x, food.y);
        }
        for (Drone drone : drones) {
            game.drawBatch(droneImage, drone.getX(), drone.getY());
        }
        for (Drone drone : dronesWithFood) {
            game.drawBatch(droneWithFoodImage, drone.getX(), drone.getY());
        }
        if (!game.isPause()) {
            game.drawPause();
            game.drawFont("Score: " + totalScore, game.getWidth() / 2f - 20, game.getHeight() - 10);
        } else {
            game.pause();
            if (game.getCurrentPlayer() == null) spawnQuery = game.nameQuery(spawnQuery);
        }
        game.endDrawing();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        playerImage.dispose();
        watermelonImage.dispose();
        droneImage.dispose();
        droneWithFoodImage.dispose();
        landscape.dispose();

        firstCatch.dispose();
        millionaire.dispose();
        chikibriki.dispose();
    }

    @Override
    public void show() {

    }
}
