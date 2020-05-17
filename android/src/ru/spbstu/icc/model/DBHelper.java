package ru.spbstu.icc.model;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;
import com.badlogic.gdx.utils.ArrayMap;


public class DBHelper {

    private static Database dbHandler;

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "players.db";
    private static final String TABLE_PLAYERS = "players";

    private static final String PLAYER_ID = "_id";
    private static final String PLAYER_NAME = "name";
    private static final String PLAYER_SCORE = "score";

    private static final String DATABASE_CREATE = "create table " + TABLE_PLAYERS + "(" + PLAYER_ID
            + " integer primary key, " + PLAYER_NAME + " text, " + PLAYER_SCORE + " integer" + ")";


    public void DatabaseStart() {
        dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME,
                DATABASE_VERSION, DATABASE_CREATE, null);
        dbHandler.setupDatabase();
        try {
            dbHandler.openOrCreateDatabase();
            dbHandler.execSQL(DATABASE_CREATE);
        } catch (SQLiteGdxException e) {
        }

        DatabaseCursor cursor = null;
        try {
            cursor = dbHandler.rawQuery("SELECT score FROM players");
            if (!cursor.next()) {
                dbHandler.execSQL("INSERT INTO " + TABLE_PLAYERS + " ('name', 'score') " +
                        "VALUES ('Pavel', '5')");
                dbHandler.execSQL("INSERT INTO " + TABLE_PLAYERS + " ('name', 'score') " +
                        "VALUES ('Sema', '10')");
                dbHandler.execSQL("INSERT INTO " + TABLE_PLAYERS + " ('name', 'score') " +
                        "VALUES ('Keril', '50')");
                dbHandler.execSQL("INSERT INTO " + TABLE_PLAYERS + " ('name', 'score') " +
                        "VALUES ('Pavel', '100')");
                dbHandler.execSQL("INSERT INTO " + TABLE_PLAYERS + " ('name', 'score') " +
                        "VALUES ('Nekita', '150')");
            }
        } catch (SQLiteGdxException e) {
        }
        cursor.close();
    }

    public void addOrUpdatePlayer(String name, int score) {

        DatabaseCursor cursor = null;

        try {
            cursor = dbHandler.rawQuery("SELECT score FROM players WHERE name = '" + name + "'");
        } catch (SQLiteGdxException e) {
        }

        if (cursor.next()) {
            int scoreInTable = Integer.parseInt(cursor.getString(0));
            if (score > scoreInTable) {
                try {
                    dbHandler.execSQL("UPDATE players SET score = '" + score + "' WHERE name = '" + name + "'");
                } catch (SQLiteGdxException e) {
                }
            }
        } else {
            try {
                dbHandler.execSQL("INSERT INTO " + TABLE_PLAYERS + " ('name', 'score') " +
                        "VALUES ('" + name + "', '" + score + "')");
            } catch (SQLiteGdxException e) {
            }
        }
        cursor.close();
    }

    public ArrayMap<String, Integer> getLeaderBoard() {

        DatabaseCursor cursor = null;
        ArrayMap<String, Integer> result = new ArrayMap<>();

        try {
            cursor = dbHandler.rawQuery("SELECT * FROM players ORDER BY score DESC");
            for (int i = 0; i < 5; i++) {
                cursor.next();
                String name = cursor.getString(1);
                int score = Integer.parseInt(cursor.getString(2));
                result.put(name, score);
                Gdx.app.log("cursor: ", "" + name + ", " + score);
            }
        } catch (SQLiteGdxException e) {
        }
        cursor.close();
        return result;
    }

    public void closeDB() {
        try {
            dbHandler.closeDatabase();
        } catch (SQLiteGdxException e) {
        }
        dbHandler = null;
    }
}
