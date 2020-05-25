package ru.spbstu.icc.model;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.TimeUtils;
import ru.spbstu.icc.view.GameScreen;
import ru.spbstu.icc.view.MainMenuScreen;
import com.badlogic.gdx.utils.ArrayMap;


public class MyGame extends Game {

    private SpriteBatch batch;
    private BitmapFont font;
    private int width;
    private int height;
    private long TimeForDelay;

    private String currentPlayer;
    private boolean isPause;

    private TextInputListener listener;

    private Texture pauseButton;
    private Texture pauseMenu;

    private DBHelper dbHelper;

    private Preferences lastPlayer;

    public final int ONE_SECOND = 1000000000;

    public static class TextInputListener implements Input.TextInputListener {
        public String text;

        @Override
        public void input(String text) {
            this.text = text;
        }

        @Override
        public void canceled() {
            this.text = "KerilPlayer";
        }
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        width = 1920;
        height = 1080;
        pauseButton = new Texture("pauseButton.png");
        pauseMenu = new Texture("pauseMenu.png");

        isPause = false;

        dbHelper = DBHelper.DatabaseStart();

        lastPlayer = Gdx.app.getPreferences("lastPlayer");
        currentPlayer = lastPlayer.getString("name", null);

        listener = new TextInputListener();
        this.setScreen(new MainMenuScreen(this));
    }

    public void saveName() {
        lastPlayer.putString("name", currentPlayer);
        lastPlayer.flush();
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayMap<String, Integer> getLeaderBoard() {
        return dbHelper.getLeaderBoard();
    }

    public void addOrUpdatePlayer(String name, int score) {
        dbHelper.addOrUpdatePlayer(name, score);
    }

    public void drawBatch(Texture texture, float x, float y) {
        batch.draw(texture, x, y);
    }

    public void drawFont(String text, float x, float y) {
        font.draw(batch, text, x, y);
    }

    public void startDrawing(Matrix4 matrix) {
        batch.setProjectionMatrix(matrix);
        batch.begin();
    }

    public void endDrawing() {
        batch.end();
    }

    public boolean nameQuery(boolean spawnQuery) {
        if (spawnQuery) {
            Gdx.input.getTextInput(listener, "Enter your name", "", "need your name....");
        }
        currentPlayer = listener.text;
        return false;
    }

    public void setFont(FreeTypeFontGenerator generator, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        font = generator.generateFont(parameter);
    }

    public void drawPause() {
        batch.draw(pauseButton, 0, height - 53);
    }

    public void setOnPause() {
        isPause = true;
    }

    public void setOffPause() {
        isPause = false;
    }

    public boolean isPause() {
        return isPause;
    }

    public long getTimeForDelay() {
        return TimeForDelay;
    }

    public void determineTimeForDelay() {
        TimeForDelay = TimeUtils.nanoTime();
    }

    public void deleteCurrentPlayerName(){
        currentPlayer = null;
    }

    @Override
    public void pause() {
        batch.draw(pauseMenu, 0, 0);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();

        pauseButton.dispose();
        pauseMenu.dispose();

        dbHelper.closeDB();
    }
}
