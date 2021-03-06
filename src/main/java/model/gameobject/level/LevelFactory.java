package model.gameobject.level;

import controller.LevelController;
import model.gameobject.player.Player;
import model.gameobject.enemy.BossEnemy;
import model.gameobject.enemy.Walker;
import model.support.Coordinates;
import utility.Logger;
import utility.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is for the level factory.
 */
public class LevelFactory {

    private static final int NUM_ROWS = 26;
    private static final int NUM_COLS = 26;

    /**
     * The map in a 2 dim array.
     */
    private Integer[][] map;

    private final LevelController levelController;

    /**
     * This is the constructor, that returns a levelFactory.
     * @param levelController The controller that creates the factory.
     */
    public LevelFactory(final LevelController levelController) {
        this.levelController = levelController;
    }

    /**
     * This function returns a level from a certain file.
     * @param levelTitle The name of the file.
     * @param limitOfPlayers The limit of players in the game.
     * @return A Level with all loaded sprites.
     */
    public Level makeLevel(String levelTitle, int limitOfPlayers) {
        Level level = new Level();
        readMap(levelTitle);
        drawMap(level, limitOfPlayers);
        return level;
    }

    /**
     * The function that draws the map.
     * @param level The Level that everything needs to be added to.
     * @param limitOfPlayers The limit of players.
     */
    public final void drawMap(Level level, int limitOfPlayers) {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                Coordinates coordinatesMoveable = new Coordinates(
                        col * Settings.SPRITE_SIZE / 2 - Settings.SPRITE_SIZE / 2,
                        row * Settings.SPRITE_SIZE / 2 - Settings.SPRITE_SIZE / 2,
                        0, 0, 0, 0);
                theIfofDrawMap(row, col, coordinatesMoveable, level, limitOfPlayers);
            }
        }
    }
    
    private void theIfofDrawMap(int row, int col, 
    		Coordinates coordinatesMoveable, Level level, int limitOfPlayers) {
    	 if (map[row][col] == 1) {
             Coordinates coordinatesWall =
                     new Coordinates(col * Settings.SPRITE_SIZE / 2,
                             row * Settings.SPRITE_SIZE / 2, 0, 0, 0, 0);
             level.addWall(new Wall(coordinatesWall));
         } else if (map[row][col] == 2) {
             level.addMonster(new Walker(coordinatesMoveable,
                     Settings.MONSTER_SPEED, true, levelController));
         } else if (map[row][col] == 3) {
             level.addMonster(new Walker(coordinatesMoveable,
                     Settings.MONSTER_SPEED, false, levelController));
         } else if (map[row][col] == 9) {
             Logger.log(String.format("Player found in %d, %d%n", row, col));
             int playerCounter = level.getPlayers().size();
             if (playerCounter < limitOfPlayers) {
                 level.addPlayer(new Player(levelController, coordinatesMoveable,
                         Settings.PLAYER_SPEED, Settings.PLAYER_LIVES,
                         levelController.createInput(playerCounter + 1), playerCounter + 1));
             }
         } else if (map[row][col] == 4) {
             level.addMonster(new BossEnemy(coordinatesMoveable,
                 Settings.MONSTER_SPEED, false, levelController, true, 5));
        }
    }

    /**
     * This function reads the file and translates it to a 2dim array.
     */
    private void readMap(String levelTitle) {
        int row = 0;
        map = new Integer[NUM_ROWS][NUM_COLS];
        BufferedReader reader;

        try (InputStreamReader isr = new InputStreamReader(getClass()
                .getClassLoader()
                .getResourceAsStream(levelTitle), "UTF-8")) {
            reader = new BufferedReader(isr);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(" ");
                if (cols.length == NUM_COLS) {
                    for (int column = 0; column < cols.length; column++) {
                        map[row][column] = Integer.parseInt(cols[column]);
                    }
                }
                row++;
            }
            reader.close();
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}