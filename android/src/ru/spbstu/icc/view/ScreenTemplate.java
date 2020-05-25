package ru.spbstu.icc.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import ru.spbstu.icc.model.MyGame;

public abstract class ScreenTemplate {
    MyGame game;

    public ScreenTemplate(MyGame game) {
        this.game = game;
    }

    public void checkTouch() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (!game.isPause()) {
                    int PAUSE_BUTTON_LOCATION = 150;
                    if (screenY < PAUSE_BUTTON_LOCATION
                            && screenX < PAUSE_BUTTON_LOCATION) {
                        game.setOnPause();
                    } else {
                        touchAction(screenX, screenY);
                    }
                } else {
                    int LEFTMOST_POSITION = 446;
                    int RIGHTMOST_POSITION = 1476;
                    int EXTREME_BOTTOM_POSITION_OF_CHANGE_NAME_BUTTON = 655;
                    int EXTREME_UPPER_POSITION_OF_CHANGE_NAME_BUTTON = 836;
                    int EXTREME_BOTTOM_POSITION_OF_GAME_CONTINUE_BUTTON = 356;
                    int EXTREME_UPPER_POSITION_OF_GAME_CONTINUE_BUTTON = 597;

                    if (screenY > EXTREME_BOTTOM_POSITION_OF_GAME_CONTINUE_BUTTON &&
                            screenY < EXTREME_UPPER_POSITION_OF_GAME_CONTINUE_BUTTON &&
                            screenX > LEFTMOST_POSITION &&
                            screenX < RIGHTMOST_POSITION) {
                        game.determineTimeForDelay();
                        game.setOffPause();
                    } else if (screenY > EXTREME_BOTTOM_POSITION_OF_CHANGE_NAME_BUTTON &&
                            screenY < EXTREME_UPPER_POSITION_OF_CHANGE_NAME_BUTTON &&
                            screenX > LEFTMOST_POSITION &&
                            screenX < RIGHTMOST_POSITION) {
                        game.deleteCurrentPlayerName();
                    }
                }
                return true;
            }
        });
    }

    public abstract void touchAction(int screenX, int screenY);

    public void createFont(int fontSize) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("19167.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.borderWidth = 1;
        parameter.color = Color.BLACK;
        game.setFont(generator, parameter);
        generator.dispose();
    }
}
