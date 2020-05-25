package ru.spbstu.icc.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import ru.spbstu.icc.model.GameController;
import ru.spbstu.icc.model.Drone;
import ru.spbstu.icc.model.MyGame;


public class GameScreen extends ScreenAdapter {

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

    private final int DRONE_WIDTH = 139;
    private final int DRONE_HEIGHT = 60;

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

        screenTemplate = new ScreenTemplate(game) {

            @Override
            public void touchAction(int screenX, int screenY) {
                Rectangle drone = new Rectangle();
                drone.x = 0;
                drone.y = 0;
                drone.height = DRONE_HEIGHT;
                drone.width = DRONE_WIDTH;
                double angle = Math.atan((double) (game.getHeight() - screenY) / screenX);
                controller.addDrone(drone, angle);
            }
        };

        screenTemplate.createFont(40);
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
