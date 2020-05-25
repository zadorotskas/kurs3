package ru.spbstu.icc.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;
import ru.spbstu.icc.model.MyGame;


public class EndOfGameScreen extends ScreenAdapter {
    private final MyGame game;
    private OrthographicCamera camera;

    private boolean spawnQuery;

    private int playerScore;

    private Texture endOfGameImage;

    private ArrayMap<String, Integer> leaderBoard;

    private ScreenTemplate screenTemplate;

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


        screenTemplate = new ScreenTemplate(game) {

            @Override
            public void touchAction(int screenX, int screenY) {
                if (TimeUtils.nanoTime() - game.getTimeForDelay() > game.ONE_SECOND) {
                    game.saveName();
                    game.setScreen(new GameScreen(game));
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


    private void drawLeaderBoard() {
        float x = game.getHeight() / 7f;
        int LEADER_BOARD_LOCATION_X = 100;
        int SHIFT_FOR_NAME = 600;
        float LEADER_BOARD_LOCATION_Y = game.getHeight();
        game.drawFont("LeaderBoard:", LEADER_BOARD_LOCATION_X, LEADER_BOARD_LOCATION_Y);
        for (int i = 0; i < leaderBoard.size; i++) {
            String name = leaderBoard.getKeyAt(i);
            LEADER_BOARD_LOCATION_Y = game.getHeight() - (i + 1) * x;
            int score = leaderBoard.get(name);
            game.drawFont(name, LEADER_BOARD_LOCATION_X, LEADER_BOARD_LOCATION_Y);
            game.drawFont("" + score, LEADER_BOARD_LOCATION_X + SHIFT_FOR_NAME, LEADER_BOARD_LOCATION_Y);
        }
        game.drawFont("Your current score: " + playerScore, LEADER_BOARD_LOCATION_X, game.getHeight() - 6 * x);
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
