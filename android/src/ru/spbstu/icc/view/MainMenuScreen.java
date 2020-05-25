package ru.spbstu.icc.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import ru.spbstu.icc.model.MyGame;


public class MainMenuScreen extends ScreenAdapter {

    private MyGame game;
    private OrthographicCamera camera;
    private boolean spawnQuery;
    private boolean needToPlaySound;

    private Sound startOfGameSound;
    private Sound startingSound;

    private ScreenTemplate screenTemplate;


    public MainMenuScreen(MyGame myGame) {
        this.game = myGame;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.getWidth(), game.getHeight());

        startOfGameSound = Gdx.audio.newSound(Gdx.files.internal("startOfGame.mp3"));
        startingSound = Gdx.audio.newSound(Gdx.files.internal("startingSound.mp3"));

        needToPlaySound = true;
        spawnQuery = true;

        screenTemplate = new ScreenTemplate(game) {

            @Override
            public void touchAction(int screenX, int screenY) {
                if (TimeUtils.nanoTime() - game.getTimeForDelay() > game.ONE_SECOND) {
                    game.saveName();
                    game.setScreen(new GameScreen(game));
                    startingSound.play();
                    dispose();
                }
            }
        };

        screenTemplate.createFont(100);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        draw();
        screenTemplate.checkTouch();
    }


    private void draw() {
        Gdx.gl.glClearColor(0.16f, 0.69f, 0.65f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        if (game.getCurrentPlayer() != null) {
            if (needToPlaySound) {
                startOfGameSound.play();
                needToPlaySound = false;
            }
            game.startDrawing(camera.combined);
            if (game.isPause()) {
                game.pause();
            } else {
                game.drawPause();
                game.drawFont("Hello, " + game.getCurrentPlayer(), game.getWidth() / 3f, game.getHeight() * 2f / 3f);
                game.drawFont("Tap to start", game.getWidth() / 3f, game.getHeight() / 3f);
            }
            game.endDrawing();
        } else {
            spawnQuery = game.nameQuery(spawnQuery);
        }
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
        startOfGameSound.dispose();
        startingSound.dispose();
    }
}
