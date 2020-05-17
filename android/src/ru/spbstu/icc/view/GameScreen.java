package ru.spbstu.icc.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import ru.spbstu.icc.controller.GameController;
import ru.spbstu.icc.model.Drone;
import ru.spbstu.icc.controller.MyGame;


public class GameScreen implements Screen {

    private final MyGame game;
    private OrthographicCamera camera;
    private GameController controller;

    private boolean spawnQuery;

    private Texture playerImage;
    private Texture watermelonImage;
    private Texture droneImage;
    private Texture droneWithFoodImage;
    private Texture landscape;

    private ScreenTemplate screenTemplate;

    public GameScreen(final MyGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.getWidth(), game.getHeight());

        spawnQuery = true;

        landscape = new Texture("landscape.jpg");
        droneImage = new Texture("drone.png");
        playerImage = new Texture("player.png");
        watermelonImage = new Texture("watermelon.png");
        droneWithFoodImage = new Texture("droneWithWatermelon.png");

        controller = new GameController(game);

        screenTemplate = new ScreenTemplate() {
            @Override
            public void checkTouch() {
                if (Gdx.input.justTouched() && Gdx.input.getY() < 150 && Gdx.input.getX() < 150) { // pause button location
                    game.setPause();
                } else if (Gdx.input.justTouched() && !game.isPause()) {
                    Rectangle drone = new Rectangle();
                    drone.x = 0;
                    drone.y = 0;
                    drone.height = 60;
                    drone.width = 139;
                    double angle = Math.atan((double) (game.getHeight() - Gdx.input.getY()) / (Gdx.input.getX()));
                    controller.addDrone(drone, angle);
                }
            }

            @Override
            public void createFont() {
                FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("19167.ttf"));
                FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                parameter.size = 40;
                parameter.borderWidth = 1;
                parameter.color = Color.BLACK;
                game.setFont(generator, parameter);
                generator.dispose();
            }
        };

        screenTemplate.createFont();
    }


    @Override
    public void render(float delta) {
        draw();
        screenTemplate.checkTouch();
        controller.changeGameState();

        if (!controller.isNeedToContinue()) {
            game.setScreen(new EndOfGameScreen(game, controller.getTotalScore()));
            game.saveName();
            dispose();
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.startDrawing(camera.combined);

        game.drawBatch(landscape, 0, 0);
        game.drawBatch(playerImage, 0, 0);
        for (Rectangle food : controller.getFlyingObj()) {
            game.drawBatch(watermelonImage, food.x, food.y);
        }
        for (Drone drone : controller.getDrones()) {
            game.drawBatch(droneImage, drone.getX(), drone.getY());
        }
        for (Drone drone : controller.getDronesWithFood()) {
            game.drawBatch(droneWithFoodImage, drone.getX(), drone.getY());
        }
        if (!game.isPause()) {
            game.drawPause();
            game.drawFont("Score: " + controller.getTotalScore(), game.getWidth() / 2f - 20, game.getHeight() - 10);
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
    }

    @Override
    public void show() {

    }
}
