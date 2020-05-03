package ru.spbstu.icc.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.TimeUtils;
import ru.spbstu.icc.controller.MyGame;


public class MainMenuScreen implements Screen {
    private final MyGame game;
    private OrthographicCamera camera;
    private boolean spawnQuery;
    private boolean needToPlaySound;

    private Sound startOfGameSound;
    private Sound startingSound;


    public MainMenuScreen(MyGame myGame) {
        this.game = myGame;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.getWidth(), game.getHeight());

        startOfGameSound = Gdx.audio.newSound(Gdx.files.internal("startOfGame.mp3"));
        startingSound = Gdx.audio.newSound(Gdx.files.internal("startingSound.mp3"));
        
        needToPlaySound = true;
        spawnQuery = true;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        createFont();

        draw();

        checkTouch();
    }

    private void createFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("19167.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100;
        parameter.borderWidth = 1;
        parameter.color = Color.BLACK;
        game.setFont(generator, parameter);
        generator.dispose();
    }


    private void checkTouch() {
        if (!game.isPause()) {
            if (Gdx.input.justTouched() && Gdx.input.getY() < 150 && Gdx.input.getX() < 150) {
                game.setPause();
            } else if (Gdx.input.justTouched() && TimeUtils.nanoTime() - game.getTimeForDelay() > 1000000000) {
                game.saveName();
                game.setScreen(new GameScreen(game));
                startingSound.play();
                dispose();
            }
        }
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
