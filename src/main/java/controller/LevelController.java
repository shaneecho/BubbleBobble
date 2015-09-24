package controller;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import model.Level;
import model.Input;
import model.Player;
import model.Monster;
import model.Bubble;
import model.Wall;
import utility.Logger;
import utility.Settings;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Jim
 * @version 0.1
 * @since 9/5/2015
 */

/**
 * This is the level controller.
 * Here all the interactions with the level happens.
 * It's kind of the main controller.
 */
public class LevelController {

    /**
     * KeyCode for pausing the game.
     */
    private static final KeyCode PAUSE_KEY = KeyCode.P;
    
    /**
     * The list of players in the game.
     */
    @SuppressWarnings("rawtypes")
    private ArrayList players = new ArrayList<>();
    
    /**
     * The list of maps that the user is about to play.
     */
    private ArrayList<String> maps = new ArrayList<>();
    /**
     * The current index of the level the user is playing.
     */
    private int indexCurrLvl;
    /**
     * THe current level the user is playing.
     */
    private Level currLvl;
    /**
     * A boolean to see if the game is going on or not.
     */
    private boolean gameStarted = false;
    /**
     * A boolean to see if the game is going on or not.
     */
    private boolean gamePaused = false;
    /**
     * The screenController that handles all GUI.
     */
    private ScreenController screenController;

    /**
     * The gameloop timer. This timer is the main timer.
     */
    private AnimationTimer gameLoop;

    /**
     * The Main Controller.
     */
    private MainController mainController;

    /**
     * The input for the player.
     */
    private Input input;

    /**
     * The path to the maps.
     */
    private String pathMaps = "src/main/resources";

    /**
     * "Key Pressed" handler for pausing the game: register in boolean gamePaused.
     */
    private EventHandler<KeyEvent> pauseKeyEventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {

            // pause game on keypress PAUSE_KEY
            if (event.getCode() == PAUSE_KEY) {
                mainController.showPausescreen();
                gamePaused = true;
            }

            //unpause game on keypress anything except PAUSE_KEY
            if (gamePaused && event.getCode() != PAUSE_KEY) {
                mainController.hidePausescreen();
                gamePaused = false;
            }

        }
    };

    /**
     * The constructor of this class.
     * @param mainController The main controller that creates this class.
     */
    public LevelController(MainController mainController) {
        this.mainController = mainController;
        this.screenController = mainController.getScreenController();
        findMaps();
        gameLoop = createTimer();
        startLevel(gameLoop);
    }

    /**
     * This function scans the resources folder for maps.
     */
    public void findMaps() {
        File folder = new File(pathMaps);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().matches("map[0-9]*.txt")) {
                maps.add(file.getName());
            }
        }
    }

    /**
     * This function returns the gameLoop.
     *
     * @return The gameLoop.
     */
    public AnimationTimer createTimer() {
        return new AnimationTimer() {
            @SuppressWarnings("unchecked")
            @Override
            public void handle(long now) {
                if (((Player) players.get(0)).getGameOver()) {
                    stop();
                } else if (!isGamePaused()) {
                    ((ArrayList<Player>) players).forEach(player -> {
                        player.processInput();
                        player.move();
                        player.getBubbles().forEach(Bubble::move);
                    });
                    ((ArrayList<Monster>) currLvl.getMonsters()).forEach(monster -> {
                        ((ArrayList<Player>) players).forEach(player -> {
                            player.getBubbles().forEach(monster::checkCollision);
                            player.checkCollideMonster(monster);
                        });
                        monster.move();
                    });
                    screenController.updateUI();
                    if (currLvl.update()) {
                        nextLevel();
                    }
                }
            }
        };
    }

    /**
     * This function initializes the level.
     *
     * @param gameLoop is the loop of the game.
     */
    public final void startLevel(AnimationTimer gameLoop) {
        if (maps.size() > 0) {
            indexCurrLvl = 0;

            Pane playfieldLayer = mainController.getPlayfieldLayer();

            playfieldLayer.setOnMousePressed(event -> {
                if (!gameStarted) {
                    gameStarted = true;
                    createInput();

                    createLvl();

                    mainController.hideStartMessage();
                    playfieldLayer.addEventFilter(
                            KeyEvent.KEY_PRESSED, pauseKeyEventHandler);
                    gameLoop.start();
                }
            });
        } else {
            mainController.getPlayfieldLayer().setOnMousePressed(null);
            System.out.println("No maps found!");
        }
    }

    /**
     * This function creates the currLvl'th level.
     */
    @SuppressWarnings("unchecked")
    public final void createLvl() {
        currLvl = new Level(maps.get(indexCurrLvl), this);
        screenController.removeSprites();

        createPlayer(input);

        screenController.addToSprites(currLvl.getWalls());
        screenController.addToSprites(currLvl.getMonsters());
    }

    private void createInput() {
        if (input == null) {
            input = new Input(mainController.getPlayfieldLayer().getScene());
            input.addListeners();
        }
    }

    /**
     * The function that is used to create the player.
     * @param input The input.
     */
    @SuppressWarnings("unchecked")
    public void createPlayer(Input input) {
        double x = 200;
        double y = 200;

        Player player = new Player(x, y, 0, 0, 0, 0, Settings.PLAYER_SPEED, input, this);
        players.clear();
        players.add(player);
        screenController.addToSprites(players);
    }

    /**
     * This function creates the next level.
     */
    public final void nextLevel() {
        indexCurrLvl++;
        if (indexCurrLvl < maps.size()) {
            createLvl();
        } else {
            winGame();
        }
    }

    /**
     * This function checks whether a set of coordinates collide with a wall.
     *
     * @param minX The smallest X
     * @param maxX The highest X
     * @param minY The smallest Y
     * @param maxY The highest Y
     * @return True if a collision was caused.
     */
    @SuppressWarnings("unchecked")
    public boolean causesCollision(double minX, double maxX, double minY, double maxY) {

        for (Wall wall : (ArrayList<Wall>) currLvl.getWalls()) {
            double wallMinX = wall.getX();
            double wallMaxX = wallMinX + wall.getWidth();
            double wallMinY = wall.getY();
            double wallMaxY = wallMinY + wall.getHeight();
            if (((minX > wallMinX && minX < wallMaxX)
                    || (maxX > wallMinX && maxX < wallMaxX)
                    || (wallMinX > minX && wallMinX < maxX)
                    || (wallMaxX > minX && wallMaxX < maxX))
                    && ((minY > wallMinY && minY < wallMaxY)
                    || (maxY > wallMinY && maxY < wallMaxY)
                    || (wallMinY > minY && wallMinY < maxY)
                    || (wallMaxY > minY && wallMaxY < maxY))) {
                return true;
            }
        }

        return false;
    }

    /**
     * This function is called when it's game over.
     */
    public void gameOver() {
        Logger.log("Game over!");
        gameLoop.stop();
        mainController.showGameOverScreen();
    }

    /**
     * This method calls the win screen when the game has been won.
     */
    public void winGame() {
        Logger.log("Game won!");
        gameLoop.stop();
        mainController.showWinScreen();
    }

    /**
     * This function returns the maps.
     *
     * @return The maps.
     */
    public ArrayList<String> getMaps() {
        return maps;
    }

    /**
     * This function sets the maps.
     * @param maps The maps to be set.
     */
    public void setMaps(ArrayList<String> maps) {
        this.maps = maps;
    }

    /**
     * This function returns the playfield Layer.
     *
     * @return The playfield Layer.
     */
    public Pane getPlayfieldLayer() {
        return mainController.getPlayfieldLayer();
    }

    /**
     * This function returns the current level index.
     *
     * @return The current level index.
     */
    public int getIndexCurrLvl() {
        return indexCurrLvl;
    }

    /**
     * Gets the screenController.
     *
     * @return The screencontroller.
     */
    public ScreenController getScreenController() {
        return screenController;
    }

    /**
     * This sets the screen Controller.
     *
     * @param screenController The screencontroller to be set.
     */
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    /**
     * This is the boolean to check if the game is paused or not.
     *
     * @return True if the gamePaused is true.
     */
    public boolean isGamePaused() {
        return this.gamePaused;
    }

    /**
     * The function that gets the players.
     * @return The players.
     */
    public ArrayList getPlayers() {
        return players;
    }

    /**
     * The function thet returns the current level.
     * @return The current level.
     */
    public Level getCurrLvl() {
        return currLvl;
    }

    /**
     * The function that sets the path to the maps.
     * @param pathMaps The path to the maps.
     */
    public void setPathMaps(String pathMaps) {
        this.pathMaps = pathMaps;
    }

    /**
     * The function that gets if the game is started.
     * @return True if the game is started.
     */
    public boolean getGameStarted() {
        return gameStarted;
    }

    /**
     * This function returns the input.
     * @return The input.
     */
    public Input getInput() {
        return input;
    }

    /**
     * This function sets the input.
     * @param input The input.
     */
    public void setInput(Input input) {
        this.input = input;
    }

    /**
     * This function sets if the game has started.
     * @param gameStarted True if the game has started.
     */
    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    /**
     * This function sets the current index level.
     * @param indexCurrLvl The index of the curr level.
     */
    public void setIndexCurrLvl(int indexCurrLvl) {
        this.indexCurrLvl = indexCurrLvl;
    }

    /**
     * This function sets the players.
     * @param players The players.
     */
    public void setPlayers(ArrayList players) {
        this.players = players;
    }

    /**
     * This fucntion sets the current level.
     * @param currLvl The current level.
     */
    public void setCurrLvl(Level currLvl) {
        this.currLvl = currLvl;
    }

    /**
     * This function gets the pauseKeyEventHandler.
     * @return The pause key event handler.
     */
    public EventHandler<KeyEvent> getPauseKeyEventHandler() {
        return pauseKeyEventHandler;
    }

    /**
     * This function returns true if the game is paused.
     * @return True if the game is paused.
     */
    public boolean getGamePaused() {
        return gamePaused;
    }
}
