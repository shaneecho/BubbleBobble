package utility;

import controller.HighscoreEntryController;

import java.util.ArrayList;
import javafx.scene.input.KeyCode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/**
 * This class is used for storing default settings.
 */
public final class Settings {

    public static final double SCENE_WIDTH = 832;
    public static final double SCENE_HEIGHT = 832;
    public static final double SPRITE_SIZE = 64;

    public static final int AMOUNT_MAPS = 5;
    public static final float GRAVITY_CONSTANT = 5.f;

    public static final double BUBBLE_INIT_SPEED = 7;
    public static final double BUBBLE_FLY_SPEED = 3;
    public static final double BUBBLE_LIVE_TIME = 300;
    public static final double BUBBLE_FLY_TIME = 30;
    public static final double BUBBLE_POWERUP_FLY_TIME = 3 * BUBBLE_FLY_TIME;

    public static final double PLAYER_SPEED = 5.0;
    public static final int PLAYER_LIVES = 5;

    public static final int POINTS_PLAYER_DIE = -25;
    public static final int POINTS_KILL_MONSTER = 10;
    public static final int POINTS_LEVEL_COMPLETE = 30;

    public static final double MONSTER_SPEED = 3.5;

    public static final double JUMP_SPEED = 3 * PLAYER_SPEED;
    public static final double JUMP_SPEED_WALKER = 3 * MONSTER_SPEED;
    public static final double JUMP_HEIGHT_WALKER = 200;

    /**
     * Get the name of the player.
     * @param index Which player.
     * @return The name.
     */
    public static String getName(int index) {
        return names[index];
    }

    /**
     * Set the name of the player.
     * @param name The name to be set.
     * @param index The index of the current player.
     */
    public static void setName(String name, int index) {
        names[index] = name;
    }

    private static String[] names = new String[2];

    private static String propertyFileName;
    private static String scoresFileName;
    private static Properties properties;
    private static Properties highscores;

    /**
     * The private constructor that does nothing.
     * This is a utility class.
     */
    private Settings() {

    }

    /**
     * Initialize the properties.
     * @param fileName the property file name.
     * @return true is file existed, false if not.
     */
    public static boolean initialize(String fileName) {
        properties = new Properties();
        propertyFileName = fileName;

        try (InputStream is = new FileInputStream(propertyFileName)) {
            properties.load(is);
            return true;
        } catch (IOException | NullPointerException e) {
            Logger.log(Logger.ERR,
                    String.format("Properties cannot be loaded from %s", propertyFileName));
            return false;
        }

    }

    /**
     * Initialize the highscores.
     * @param fileName the highscore file name.
     * @return true is file existed, false if not.
     */
    public static boolean initializeHighscores(String fileName) {
        highscores = new Properties();
        scoresFileName = fileName;

        try (InputStream is2 = new FileInputStream(scoresFileName)) {
            highscores.load(is2);
            return true;
        } catch (IOException | NullPointerException e) {
            Logger.log(Logger.ERR,
                    String.format("Highscores cannot be loaded from %s", scoresFileName));
            return false;
        }
    }

    /**
     * Set a highscore.
     * @param name the name of the player of the highscore.
     * @param score the score of the highscore.
     */
    public static void setHighscore(String name, String score) {
        setHighscoreProperty(name, score);
    }

    /**
     * Get a specific score.
     * @param name the name of the player of the highscore.
     * @return the score of the highscore.
     */
    public static String getHighscore(String name) {
        return getHighscoreValue(name);
    }

    /**
     * Get the ArrayList of the highscores entries.
     * @return the ArrayList of entries.
     */
    public static ArrayList<HighscoreEntryController> getHighscores() {
        ArrayList<HighscoreEntryController> tempHighscores =
                new ArrayList<HighscoreEntryController>();
        Set<String> keys = highscoreKeys();
        keys.forEach(key ->
                tempHighscores.add(
                        new HighscoreEntryController(key, Settings.getHighscore(key))));
        return tempHighscores;
    }

    /**
     * Set the highscores property with the highscores.
     * @param scoresList The ArrayList with the highscore entries.
     */
    public static void setHighscores(ArrayList<HighscoreEntryController> scoresList) {
        highscores.clear();
        for (HighscoreEntryController entry : scoresList) {
            setHighscore(entry.getName(), entry.getScoreString());
        }
    }

    /**
     * Set a property.
     * @param key the key of the property.
     * @param value the value of the property.
     */
    public static void set(String key, String value) {
        properties.setProperty(key, value);

        try (FileOutputStream fos = new FileOutputStream(propertyFileName)) {
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                    Locale.getDefault());
            String comment = String.format("Properties saved on %s", timestamp.format(new Date()));
            properties.store(fos, comment);
        } catch (IOException | NullPointerException e) {
            Logger.log(Logger.ERR, "Properties cannot be stored");
        }
    }

    /**
     * Set a property.
     * @param key the key of the property.
     * @param value the value of the property.
     */
    public static void setHighscoreProperty(String key, String value) {
        highscores.setProperty(key, value);

        try (FileOutputStream fos = new FileOutputStream(scoresFileName)) {
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                    Locale.getDefault());
            String comment = String.format("Highscore saved on %s", timestamp.format(new Date()));
            highscores.store(fos, comment);
        } catch (IOException | NullPointerException e) {
            Logger.log(Logger.ERR, "Highscore cannot be stored");
        }
    }

    /**
     * Get a specific value.
     * @param key the key of the property.
     * @return the value of the property.
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get a specific value of a highscore.
     * @param key the key of the property.
     * @return the value of the property.
     */
    public static String getHighscoreValue(String key) {
        return highscores.getProperty(key);
    }

    /**
     * Get a specific value.
     * @param key the key of the property.
     * @param def the default value.
     * @return the default value if the property does not exist, the property value otherwise.
     */
    public static String get(String key, String def) {
        String value = Settings.get(key);
        if (null != value) {
            return value;
        } else {
            Settings.set(key, def);
            return def;
        }
    }

    /**
     * Get a KeyCode property.
     * @param key the key of the property.
     * @param def the default value.
     * @return the default value if the property does not exist, the property value otherwise.
     */
    public static KeyCode getKeyCode(String key, KeyCode def) {
        return KeyCode.valueOf(get(key, def.toString()));
    }

    /**
     * Set a KeyCode property.
     * @param key the key of the property.
     * @param value the value of the property.
     */
    public static void setKeyCode(String key, KeyCode value) {
        set(key, value.toString());
    }

    /**
     * Get a boolean property.
     * @param key the key of the property.
     * @param def the default value.
     * @return the default value if the property does not exist, the property value otherwise.
     */
    public static boolean getBoolean(String key, boolean def) {
        return Boolean.parseBoolean(get(key, Boolean.toString(def)));
    }

    /**
     * Set a boolean property.
     * @param key the key of the property.
     * @param value the value of the property.
     */
    public static void setBoolean(String key, boolean value) {
        set(key, Boolean.valueOf(value).toString());
    }

    /**
     * Get an int property.
     * @param key the key of the property.
     * @param def the default value.
     * @return the default value if the property does not exist, the property value otherwise.
     */
    public static int getInt(String key, int def) {
        return Integer.parseInt(get(key, Integer.toString(def)));
    }

    /**
     * Get a char property.
     * @param key the key of the property.
     * @param def the default value.
     * @return the default value if the property does not exist, the property value otherwise.
     */
    public static char getChar(String key, char def) {
        return get(key, Character.toString(def)).charAt(0);
    }

    /**
     * Get the Set of property keys.
     * @return the Set of keys.
     */
    public static Set<String> keys() {
        return properties.stringPropertyNames();
    }

    /**
     * Get the Set of property keys for the highscore.
     * @return the Set of keys.
     */
    public static Set<String> highscoreKeys() {
        return highscores.stringPropertyNames();
    }

    /**
     * Get the property filename.
     * @return the filename
     */
    public static String getFileName() {
        return propertyFileName;
    }
}
