package model.gameobject.level;

import controller.LevelController;
import controller.ScreenController;
import model.gameobject.enemy.Monster;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the Level class.
 */
public class LevelTest {
    private Level level;
    
    /**
     * This is run before all the tests to initialize them.
     */
    @Before
    public void setUp() {
    	LevelController levelController = mock(LevelController.class);
        ScreenController screenController = mock(ScreenController.class);
        when(levelController.getScreenController()).thenReturn(screenController);
        LevelFactory levelFactory = new LevelFactory(levelController);
        level = levelFactory.makeLevel("map1.txt", 1);
    }
    
    /**
     * This tests the update method when all the monsters are still alive.
     */
    @Test
    public void testUpdate() {
    	ArrayList<Monster> mon = level.getMonsters();
    	assertEquals(mon.size(), 4);
    	assertFalse(level.update());
    }
    
    /**
     * This tests the update method when all the monsters have just been killed.
     */
    @Test
       public void testUpdateNoMonsters() {
       	ArrayList<Monster> mon = level.getMonsters();
       	mon.clear();
       	assertEquals(mon.size(), 0);
       	assertFalse(level.update());
       }
    
    /**
     * This tests the update method when all the monsters have been 
     * killed and the counter has reached 200.
     */
    @Test
       public void testUpdateNoMonstersTimeUp() {
       	ArrayList<Monster> mon = level.getMonsters();
       	int counter = 0;
       	mon.clear();
       	assertEquals(mon.size(), 0);
       	while (counter < 200) {
       		counter++;
       		level.update();
       }
       	assertTrue(level.update());
    }

}
