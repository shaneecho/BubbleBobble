package model;

import controller.LevelController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the player class. It has a sprite to display.
 */
public class Player extends GravityObject {

    /**
     * This is the minimal X coordinate.
     */
    private double playerMinX;

    /**
     * This is the maximal X coordinate.
     */
    private double playerMaxX;

    /**
     * This is the minimal Y coordinate.
     */
    private double playerMinY;

    /**
     * This is the maximal Y coordinate.
     */
    private double playerMaxY;

    /**
     * The input that defines the movement of the player.
     */
    private Input input;

    /**
     * The speed of the player.
     */
    private double speed;

    /**
     * The bubbles the player fired.
     */
    private ArrayList<Bubble> bubbles;

    /**
     * The boolean for which dir the player is facing.
     */
    private boolean facingRight;

    private int counter;

    private boolean isDead;
    
    private boolean gameOver;

    private LevelController levelController;

    /**
     * The constructor that takes all parameters and creates a SpriteBase.
     * @param layer The layer the player moves in.
     * @param imageLoc The path of the image the player takes.
     * @param x The start x coordinate.
     * @param y The start y coordinate.
     * @param r The r.
     * @param dx The dx.
     * @param dy The dy.
     * @param dr The dr.
     * @param speed The speed of the player.
     * @param input The input the player will use.
     */
    public Player(Pane layer,
                  String imageLoc,
                  double x,
                  double y,
                  double r,
                  double dx,
                  double dy,
                  double dr,
                  double speed,
                  Input input,
                  LevelController levelController) {

        super(layer, imageLoc, x, y, r, dx, dy, dr);

        this.speed = speed;
        this.input = input;
        this.bubbles = new ArrayList<>();
        this.counter = 16;
        this.isDead = false;
        this.gameOver = false;
        this.levelController = levelController;

        init();
    }

    /**
     * The function that initiates the player.
     */
    private void init() {

        // calculate movement bounds of the player
        // allow half of the player to be outside of the screen
        playerMinX = 0 - getWidth() / 2.0;
        playerMaxX = Settings.SCENE_WIDTH - getWidth() / 2.0;
        playerMinY = 0 - getHeight() / 2.0;
        playerMaxY = Settings.SCENE_HEIGHT - getHeight() / 2.0;

    }

    /**
     * The function that processes the input.
     */
    public void processInput() {

        // ------------------------------------
        // movement
        // ------------------------------------

    	if (!isDead) {
    		// vertical direction
    		moveVertical();

            // horizontal direction
            moveHorizontal();

        } else {
            checkIfGameOver();
        }

    }

    /**
     * The move function that applies the movement to the player.
     */
    @Override
    public void move() {

        if (!levelController.causesCollision(getX(), getX() + getWidth(), getY() - calculateGravity(), getY() + getHeight() - calculateGravity())) {
            setY(getY() - calculateGravity());
        }

        super.move();

        checkBounds();


    }

    /**
     * The function that checks wether the player is still within the bounds where it is allowed.
     * If it is not, move the player back in the bounds.
     */
    private void checkBounds() {

        // vertical
        if (Double.compare(getY(), playerMinY) < 0) {
            setY(playerMinY);
        } else if (Double.compare(getY(), playerMaxY) > 0) {
            setY(playerMaxY);
        }

        // horizontal
        if (Double.compare(getX(), playerMinX) < 0) {
            setX(playerMinX);
        } else if (Double.compare(getX(), playerMaxX) > 0) {
            setX(playerMaxX);
        }

    }

    /**
     * This function returns the bubble list.
     * @return the bubbles.
     */
    public ArrayList<Bubble> getBubbles() {
        return bubbles;
    }

    /**
     * This method checks if the monster has collides with the character.
     * @param monster is the monster that is being checked for collisions.
     */
    public void checkCollideMonster(final Monster monster) {
    	double monsterX = monster.getX();
    	double monsterMaxX = monsterX + monster.getWidth();
    	double monsterY = monster.getY();
    	double monsterMaxY = monsterY + monster.getHeight();

    	if (((monsterX > getX() && monsterX < getX() + getWidth())
    			|| (monsterMaxX > getX() && monsterMaxX < getX() + getWidth()))
    			&& ((monsterY > getY() && monsterY < getY() + getHeight())
    			|| (monsterMaxY > getY() && monsterMaxX < getY() + getHeight()))) {
    		die();
    	}

    }

    /**
     * This method is used when the character is killed.
     */
    public void die() {
        this.isDead = true;
        counter = 0;
        setImage("/BubbleBobbleLogo.png");
        //refresh sprite
    }


    /**
     * This method checks if the character is dead or not.
     * @return isDead when the character is dead.
     */
    public boolean getDead() {
        return isDead;
    }

    /**
     * This method  checks if there is a game over.
     * @return gameOver if the game is over.
     */
    public boolean getGameOver() {
        return gameOver;
    }

    /**
     * This function checks how to move vertically.
     */
    private void moveVertical() {
        if (input.isMoveUp()) {
            if (!levelController.causesCollision(getX(), getX() + getWidth(), getY() - speed, getY() + getHeight() - speed)) {
                setDy(-speed);
            } else {
                setDy(0);
            }

            if (facingRight) {
                setImage("/BubRight.png");
            } else {
                setImage("/BubLeft.png");
            }
        } else if (input.isMoveDown()) {

            if (!levelController.causesCollision(getX(), getX() + getWidth(), getY() + speed, getY() + getHeight() + speed)) {
                setDy(speed);
            } else {
                setDy(0);
            }

            if (facingRight) {
                setImage("/BubRight.png");
            } else {
                setImage("/BubLeft.png");
            }
        } else {
            setDy(0d);
        }
    }

    /**
     * This function checks how to move horizontally.
     */
    private void moveHorizontal() {
        if (input.isMoveLeft()) {
            if (!levelController.causesCollision(getX() - speed, getX() + getWidth() - speed, getY(), getY() + getHeight())) {
                setDx(-speed);
            } else {
                setDx(0);
            }

            setImage("/BubLeft.png");
            facingRight = false;
        } else if (input.isMoveRight()) {
            if (!levelController.causesCollision(getX() + speed, getX() + getWidth() + speed, getY(), getY() + getHeight())) {
                setDx(speed);
            } else {
                setDx(0);
            }

            setImage("/BubRight.png");
            facingRight = true;
        } else {
            setDx(0d);
        }

        if (input.isFirePrimaryWeapon() && counter > 30) {
            bubbles.add(new Bubble(Bubble.BUBBLE_SPRITE,
                    getX(), getY(), 0, 0, 0, 0, facingRight));
            counter = 0;
        } else {
            counter++;
        }
    }

    /**
     * This function checks if the game is over. And if so, loads the gamover screen.
     */
    private void checkIfGameOver() {
        if (counter > 50) {
            gameOver = true;
            Stage stage = (Stage) levelController.getPlayfieldLayer().getScene().getWindow();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../gameOver.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            counter++;
        }
    }
}