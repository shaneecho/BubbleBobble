package controller;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.Input;
import model.Level;
import model.Monster;
import model.Player;
import org.junit.Before;
import org.junit.Test;
import utility.Settings;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;


/**
 * Tests the creation of a LevelController.
 *
 * @author Lili
 */
public class LevelControllerTest {

    private LevelController levelController;
    private MainController mainController;
    private Pane pane;

    /**
     * The setup before every test.
     */
    @Before
    public void setUp() {
        mainController = mock(MainController.class);
        pane = mock(Pane.class);
        when(mainController.getPlayfieldLayer()).thenReturn(pane);
        levelController = new LevelController(mainController);
        levelController.setScreenController(new ScreenController(new Pane()));
    }

    /**
     * Tests the initialization.
     */
    @Test
    public void testInitialisation() {
        assertTrue(levelController != null);
    }

    /**
     * Tests the findmaps function.
     */
    @Test
    public void testFindMaps() {
        ArrayList<String> maps = levelController.getMaps();
        assertEquals(Settings.AMOUNT_MAPS, maps.size());
        levelController.findMaps();
        assertEquals(2 * Settings.AMOUNT_MAPS, maps.size());
        maps.forEach(map -> assertTrue(map.matches("map[0-9]*.txt")));
    }

    /**
     * Tests the findmaps function when there is an empty directory.
     */
    @Test
    public void testFindMapsEmptyDir() {
        ArrayList<String> maps = levelController.getMaps();
        levelController.setPathMaps("src/main/resources/emptyDir");
        levelController.findMaps();
        assertEquals(Settings.AMOUNT_MAPS, maps.size());
        maps.forEach(map -> assertTrue(map.matches("map[0-9]*.txt")));
    }

    /**
     * Tests the createTimer() method.
     */
    @Test
    public void testCreateTimer() {
        AnimationTimer timer = levelController.createTimer();
        assertTrue(timer != null);
    }

    /**
     * The test that tests the createPlayer() method.
     */
	@Test
	public void testCreatePlayer() {
		assertTrue(levelController.getPlayers().isEmpty());
        levelController.setScreenController(mock(ScreenController.class));
		levelController.createPlayer(mock(Input.class));
		ArrayList<Player> players = levelController.getPlayers();
		assertTrue(!players.isEmpty());
		assertEquals(200, players.get(0).getX(), 0.001);
		assertEquals(200, players.get(0).getY(), 0.001);
		assertEquals(Settings.PLAYER_SPEED, players.get(0).getSpeed(), 0.001);
	}

    /**
     * Tests the startLevel() method without maps.
     */
    @Test
    public void testStartLevelNoMaps() {
        levelController.setMaps(new ArrayList<>());
        levelController.startLevel(mock(AnimationTimer.class));
        assertNull(levelController.getPlayfieldLayer().getOnMousePressed());
    }

    /**
     * Tests the startLevel() method with maps.
     */
    @Test
    public void testStartLevel() {
        assertTrue(levelController.getPlayfieldLayer().getOnMousePressed() != null);
        assertEquals(0, levelController.getIndexCurrLvl());
    }

    /**
     * This is the test that tests the createLvl() function.
     */
	@Test
	public void testCreateLevel() {
        levelController.setScreenController(mock(ScreenController.class));
		assertNull(levelController.getCurrLvl());
		levelController.findMaps();
		levelController.createLvl();
		assertNotNull(levelController.getCurrLvl());
	}

    /**
     * This tests the mouse click event from the gameLoop.
     */
    @Test
    public void testStartLevelMouseEvent() {
        assertFalse(levelController.getGameStarted());
        assertNull(levelController.getInput());
        assertNull(levelController.getCurrLvl());

        Pane pane = new Pane();
        when(mainController.getPlayfieldLayer()).thenReturn(pane);
        levelController.setInput(mock(Input.class));
        levelController.setScreenController(mock(ScreenController.class));

        levelController.startLevel(mock(AnimationTimer.class));
        pane.fireEvent(new MouseEvent(MouseEvent.MOUSE_PRESSED,
                0, 0, 0, 0, MouseButton.PRIMARY, 1, true,
                true, true, true, true, true, true, true, true, true, null));

        assertTrue(levelController.getGameStarted());
        assertNotEquals(levelController.getPlayers().size(), 0);
        assertNotEquals(levelController.getCurrLvl(), null);
    }

    /**
     * This tests the mouse click event from the gameLoop when the game has already started.
     */
    @Test
    public void testStartLevelMouseEventGameStarted() {
        levelController.setGameStarted(true);
        assertTrue(levelController.getGameStarted());
        assertNull(levelController.getInput());
        assertNull(levelController.getCurrLvl());

        Pane pane = new Pane();
        when(mainController.getPlayfieldLayer()).thenReturn(pane);
        levelController.setInput(mock(Input.class));
        levelController.setScreenController(mock(ScreenController.class));

        levelController.startLevel(mock(AnimationTimer.class));
        pane.fireEvent(new MouseEvent(MouseEvent.MOUSE_PRESSED,
                0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true,
                true, true, true, true, true, true, true, true, null));

        assertTrue(levelController.getGameStarted());
    }

    /**
     * This tests the nextLevel() function.
     */
    @Test
    public void testNextLevel() {
        levelController.setScreenController(mock(ScreenController.class));
        int index = levelController.getIndexCurrLvl();

        levelController.nextLevel();

        assertEquals(levelController.getIndexCurrLvl(), index + 1);
    }

    /**
     * This tests the next level function when the game is out of maps.
     */
    @Test
    public void testNextLevelWon() {
        levelController.setScreenController(mock(ScreenController.class));
        levelController.setIndexCurrLvl(levelController.getMaps().size());
        int index = levelController.getIndexCurrLvl();

        levelController.nextLevel();

        assertEquals(levelController.getIndexCurrLvl(), index + 1);
        verify(mainController, atLeastOnce()).showWinScreen();
    }

    /**
     * This tests the gameOver function.
     */
    @Test
    public void testGameOver() {
        levelController.gameOver();
        verify(mainController, atLeastOnce()).showGameOverScreen();
    }


    /**
     * This tests the gameLoop.
     */
    @Test
    public void testGameLoop() {
        AnimationTimer gameLoop = levelController.createTimer();
        ArrayList players = new ArrayList();
        Player player = mock(Player.class);
        players.add(player);

        ArrayList monsters = new ArrayList();
        Monster monster = mock(Monster.class);
        monsters.add(monster);

        levelController.setPlayers(players);
        Level level = mock(Level.class);
        levelController.setCurrLvl(level);
        levelController.setScreenController(mock(ScreenController.class));
        when(level.getMonsters()).thenReturn(monsters);
        when(level.update()).thenReturn(true);
        int index = levelController.getIndexCurrLvl();

        when(player.getGameOver()).thenReturn(false);

        gameLoop.handle(1);

        verify(player, atLeastOnce()).processInput();
        verify(player, atLeastOnce()).move();
        verify(player, atLeastOnce()).getBubbles();
        verify(player, atLeastOnce()).checkCollideMonster(monster);
        verify(monster, atLeastOnce()).move();
        assertEquals(levelController.getIndexCurrLvl(), index + 1);
    }

    /**
     * This tests the gameLoop when the game is over for player 1.
     */
    @Test
    public void testGameLoopGameOver() {
        AnimationTimer gameLoop = levelController.createTimer();
        ArrayList players = new ArrayList();
        Player player = mock(Player.class);
        players.add(player);
        levelController.setPlayers(players);
        levelController.setCurrLvl(mock(Level.class));

        when(player.getGameOver()).thenReturn(true);

        gameLoop.handle(1);

        verify(player, never()).processInput();
        verify(player, never()).move();
        verify(player, never()).getBubbles();
    }

    /**
     * This tests the gameLoop when the game is over for player 1.
     */
    @Test
    public void testGameLoopNextLevel() {
        AnimationTimer gameLoop = levelController.createTimer();
        ArrayList players = new ArrayList();
        Player player = mock(Player.class);
        players.add(player);
        levelController.setPlayers(players);
        Level level = mock(Level.class);
        levelController.setCurrLvl(level);

        int index = levelController.getIndexCurrLvl();

        when(player.getGameOver()).thenReturn(true);
        when(level.update()).thenReturn(false);

        gameLoop.handle(1);

        assertEquals(levelController.getIndexCurrLvl(), index);

    }

    /**
     * This tests the pausekey handler.
     */
    @Test
    public void testPauseKeyHandler() {
        EventHandler<KeyEvent> handler = levelController.getPauseKeyEventHandler();
        handler.handle(new KeyEvent(null, null,
                null, "p", "p", KeyCode.P, false, false, false, false));
        verify(mainController, atLeastOnce()).showPausescreen();
        assertTrue(levelController.getGamePaused());

        handler.handle(new KeyEvent(null, null,
                null, "a", "a", KeyCode.A, false, false, false, false));
        verify(mainController, atLeastOnce()).hidePausescreen();
        assertFalse(levelController.getGamePaused());

    }
}
