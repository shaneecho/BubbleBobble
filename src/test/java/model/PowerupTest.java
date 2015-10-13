package model;

import controller.LevelController;
import controller.ScreenController;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * Created by Jim on 10/8/2015.
 * This class tests the Powerup class.
 *
 * @author Jim
 * @version 1.0
 * @since 10/8/2015
 */
public class PowerupTest {

    private Powerup powerup;
    private LevelController levelController;
    private ScreenController screenController;
    private double destx = 10.0;
    private double desty = 10.0;

    /**
     * This is the set up function that is called before every test.
     */
    @Before
    public void setUp() {
    	levelController = mock(LevelController.class);
        screenController = mock(ScreenController.class);
        when(levelController.getScreenController()).thenReturn(screenController);
        powerup = new Powerup(0, 0, 0, 0, 0, 0, 0, destx, desty, levelController);
    }

    /**
     * This test tests the move() function.
     */
    @Test
    public void testMove() {
        assertEquals(0, powerup.getdX(), 0.1);
        assertEquals(0, powerup.getdY(), 0.1);
        assertFalse(powerup.isAbleToPickup());

        powerup.move();

        assertEquals((destx - powerup.getxLocation()) / 20.0, powerup.getdX(), 0.1);
        assertEquals((desty - powerup.getyLocation()) / 20.0, powerup.getdY(), 0.1);

        powerup = new Powerup(0, 0, 0, 0, 0, 0, 0, 0, 0, levelController);
        powerup.move();

        assertEquals(0, powerup.getdX(), 0.1);
        assertEquals(0, powerup.getdY(), 0.1);
        assertTrue(powerup.isAbleToPickup());
    }

    /**
     * This function tests the causesCollision function.
     */
    @Test
    public void testCausesCollision() {
        Player player = new Player(levelController, 1, 1, 0, 0, 0, 0, 0, 1, mock(Input.class));
        player.getSpriteBase().setHeight(10.0);
        player.getSpriteBase().setWidth(10.0);
        powerup.getSpriteBase().setHeight(10.0);
        powerup.getSpriteBase().setWidth(10.0);

        ScreenController screenController = mock(ScreenController.class);
        when(levelController.getScreenController()).thenReturn(screenController);

        powerup.setAbleToPickup(true);
        powerup.causesCollision(player, null);

        assertTrue(powerup.isPickedUp());
        //verify(screenController, atLeastOnce()).removeSprite(any());
    }

}
