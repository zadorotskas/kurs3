package ru.spbstu.icc.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;
import ru.spbstu.icc.controller.MyGame;


public class EndOfGameScreen implements Screen {
    private final MyGame game;
    private OrthographicCamera camera;

    private boolean spawnQuery;

    private int playerScore;

    private Texture endOfGameImage;

    private ArrayMap<String, Integer> leaderBoard;

    private ScreenTemplate screenTemplate;

    private final int ONE_SECOND = 1000000000;

    public EndOfGameScreen(MyGame myGame, int playerScore) {
        this.game = myGame;
        this.playerScore = playerScore;
        this.endOfGameImage = new Texture("endOfGame.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.getWidth(), game.getHeight());

        spawnQuery = true;

        game.addOrUpdatePlayer(game.getCurrentPlayer(), playerScore);
        leaderBoard = game.getLeaderBoard();
        game.determineTimeForDelay();

        screenTemplate = new ScreenTemplate() {
            @Override
            public void checkTouch() {
                if (!game.isPause()) {
                    if (Gdx.input.justTouched() && Gdx.input.getY() < 150 && Gdx.input.getX() < 150) { // pause button location
                        game.setPause();
                    } else if (Gdx.input.justTouched() && TimeUtils.nanoTime() - game.getTimeForDelay() > ONE_SECOND) {
                        game.saveName();
                        game.setScreen(new GameScreen(game));
                        dispose();
                    }
                }
            }

            @Override
            public void createFont() {
                FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("19167.ttf"));
                FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                parameter.size = 100;
                parameter.borderWidth = 1;
                parameter.color = Color.BLACK;
                game.setFont(generator, parameter);
                generator.dispose();
            }
        };

        screenTemplate.createFont();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        draw();
        screenTemplate.checkTouch();
    }


    private void drawLeaderBoard() {
        float x = game.getHeight() / 7f;
        game.drawFont("Leaderboard:", 100, game.getHeight());
        for (int i = 0; i < leaderBoard.size; i++) {
            String name = leaderBoard.getKeyAt(i);
            int score = leaderBoard.get(name);
            game.drawFont(name, 100, game.getHeight() - (i + 1) * x);
            game.drawFont("" + score, 700, game.getHeight() - (i + 1) * x);
        }
        game.drawFont("Your current score: " + playerScore, 100, game.getHeight() - 6 * x);
    }


    private void draw() {
        Gdx.gl.glClearColor(0.16f, 0.69f, 0.65f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.startDrawing(camera.combined);
        game.drawBatch(endOfGameImage, 0, 0);
        drawLeaderBoard();
        if (game.isPause()) {
            game.pause();
            if (game.getCurrentPlayer() == null) spawnQuery = game.nameQuery(spawnQuery);
        } else {
            game.drawPause();
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
        endOfGameImage.dispose();
    }
}
