package utility;

import javafx.scene.input.KeyCode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * This tests the Settings class.
 */
public class SettingsTest {

    private static final String FILENAME = "test.properties";
    private static final String FILENAME_SCORES = "testHighscores.properties";

    /**
     * Initiliaze the properties.
     */
    @Before
    public void setUp() {
        Settings.initialize(FILENAME);
        Settings.initializeHighscores(FILENAME_SCORES);
    }

    /**
     * Remove the properties file if it exists.
     */
    @After
    public void breakDown() {
        try {
            Files.delete(Paths.get(FILENAME));
            Files.delete(Paths.get(FILENAME_SCORES));
        } catch (IOException e) {
            return;
        }
    }

    /**
     * Test initialization without existing file.
     */
    @Test
    public void testInitialize1() {
        try {
            Files.delete(Paths.get(FILENAME));
        } catch (IOException e) {
            System.err.println("The properties file did not exist.");
        } finally {
            boolean success = Settings.initialize(FILENAME);
            assertFalse(success);
        }
    }

    /**
     * Test initialization with existing file.
     */
    @Test
    public void testInitialize2() {
        try {
            Files.createFile(Paths.get(FILENAME));
        } catch (IOException e) {
            System.err.println("The properties file did not exist.");
        } finally {
            boolean success = Settings.initialize(FILENAME);
            assertTrue(success);
        }
    }

    /**
     * Test initialization without existing file.
     */
    @Test
    public void testInitialize3() {
        try {
            Files.delete(Paths.get(FILENAME_SCORES));
        } catch (IOException e) {
            System.err.println("The highscores file did not exist.");
        } finally {
            boolean success = Settings.initializeHighscores(FILENAME_SCORES);
            assertFalse(success);
        }
    }

    /**
     * Test initialization without existing file.
     */
    @Test
    public void testInitialize4() {
        try {
            Files.createFile(Paths.get(FILENAME_SCORES));
        } catch (IOException e) {
            System.err.println("The highscores file did not exist.");
        } finally {
            boolean success = Settings.initializeHighscores(FILENAME_SCORES);
            assertTrue(success);
        }
    }

    /**
     * Test the listing of keys.
     */
    @Test
    public void testKeys() {
        Set<String> keys = new HashSet<>();

        Settings.set("KEY_ONE", "STRING");
        Settings.setBoolean("KEY_TWO", true);
        keys.add("KEY_ONE");
        keys.add("KEY_TWO");

        assertEquals(keys, Settings.keys());

        Settings.setKeyCode("KEY_THREE", KeyCode.NUMPAD3);
        keys.add("KEY_THREE");

        assertEquals(keys, Settings.keys());
    }

    /**
     * Test the listing of keys.
     */
    @Test
    public void testHighscoreKeys() {
        Set<String> keys = new HashSet<>();

        Settings.setHighscore("TEST_SCORE_KEY", 10);
        Settings.setHighscore("TEST_SCORE_KEY_2", 20);
        keys.add("TEST_SCORE_KEY");
        keys.add("TEST_SCORE_KEY_2");

        assertEquals(keys, Settings.highscoreKeys());
    }

    /**
     * Test getting the filename.
     */
    @Test
    public void testGetFileName() {
        assertEquals(FILENAME, Settings.getFileName());
    }

    /**
     * Test get default KeyCode.
     */
    @Test
    public void testGetKeyCode1() {
        KeyCode key = Settings.getKeyCode("UP_KEY", KeyCode.B);
        assertEquals(KeyCode.B, key);
    }

    /**
     * Test get stored KeyCode.
     */
    @Test
    public void testGetKeyCode2() {
        Settings.setKeyCode("UP_KEY", KeyCode.UP);
        KeyCode key = Settings.getKeyCode("UP_KEY", KeyCode.B);
        assertNotEquals(KeyCode.ALPHANUMERIC, key);
        assertEquals(KeyCode.UP, key);
    }

    /**
     * Test setting the KeyCode.
     */
    @Test
    public void testSetKeyCode() {
        Settings.setKeyCode("TEST_KEY_CODE", KeyCode.TAB);
        assertEquals(KeyCode.TAB, Settings.getKeyCode("TEST_KEY_CODE", KeyCode.A));
    }

    /**
     * Test getting default boolean.
     */
    @Test
    public void testGetBoolean1() {
        boolean bool = Settings.getBoolean("IS_IMMORTAL", true);
        assertTrue(bool);
    }

    /**
     * Test getting stored boolean.
     */
    @Test
    public void testGetBoolean2() {
        Settings.setBoolean("IS_IMMORTAL", false);
        boolean bool = Settings.getBoolean("IS_IMMORTAL", true);
        assertFalse(bool);
    }

    /**
     * Test setting boolean.
     */
    @Test
    public void testSetBoolean() {
        Settings.setBoolean("TEST_BOOLEAN", false);
        assertFalse(Settings.getBoolean("TEST_BOOLEAN", true));
    }

    /**
     * Test getting the highscore.
     */
    @Test
    public void testGetHighscore() {
        Settings.setHighscore("TEST_PLAYER", 100);
        String string = Settings.getHighscore("TEST_PLAYER");
        assertEquals("100", string);
    }

    /**
     * Test SetHighscores method.
     */
    @Test
    public void testSetHighscores() {
        Settings.setName("TEST_PLAYER", 0);

        Settings.setHighscores(1, 100);
        String string = Settings.getHighscore("TEST_PLAYER");
        assertEquals("100", string);

        Settings.setHighscores(1, 50);
        String string2 = Settings.getHighscore("TEST_PLAYER");
        assertEquals("100", string2);

        Settings.setHighscores(1, 200);
        String string3 = Settings.getHighscore("TEST_PLAYER");
        assertEquals("200", string3);
    }

    /**
     * Test getting highscore value.
     */
    @Test
    public void testGetHighscoreValue() {
        Settings.setHighscoreProperty("PLAYER_TEST", 50);
        String string = Settings.getHighscoreValue("PLAYER_TEST");
        assertEquals("50", string);
    }

    /**
     * Test getting default int.
     */
    @Test
    public void testGetInt() {
        int i = Settings.getInt("TEST_COUNT", 3);
        assertEquals(3, i);
    }

    /**
     * Test getting default char.
     */
    @Test
    public void testGetChar() {
        char c = Settings.getChar("TEST_CHAR", 'x');
        assertEquals('x', c);
    }

    /**
     * This tests that the height settings are correct.
     */
    @Test
    public void testSettingsHeight() {
        assertEquals(832.d, Settings.SCENE_HEIGHT, 0.001);
    }

    /**
     * This tests that the width settings are correct.
     */
    @Test
    public void testSettingsWidth() {
        assertEquals(832.d, Settings.SCENE_WIDTH, 0.001);
    }

    /**
     * This tests that the health settings are correct.
     */
    @Test
    public void testSettingsLives() {
        assertEquals(5, Settings.PLAYER_LIVES);
    }

    /**
     * This tests that the player speed settings are correct.
     */
    @Test
    public void testSettingsPlayerSpeed() {
        assertEquals(5.d, Settings.PLAYER_SPEED, 0.001);
    }

    /**
     * This tests that the monster height settings are correct.
     */
    @Test
    public void testSettingsMonsterSpeed() {
        assertEquals(3.5, Settings.MONSTER_SPEED, 0.001);
    }
}
